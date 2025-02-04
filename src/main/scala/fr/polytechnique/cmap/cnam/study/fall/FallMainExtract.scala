// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.study.fall

import scala.collection.mutable
import org.apache.spark.sql.{Dataset, SQLContext}
import fr.polytechnique.cmap.cnam.Main
import fr.polytechnique.cmap.cnam.etl.events.DcirAct
import fr.polytechnique.cmap.cnam.etl.extractors.codes.SimpleExtractorCodes
import fr.polytechnique.cmap.cnam.etl.extractors.events.hospitalstays.McoHospitalStaysExtractor
import fr.polytechnique.cmap.cnam.etl.extractors.patients.{AllPatientExtractor, PatientsConfig}
import fr.polytechnique.cmap.cnam.etl.implicits
import fr.polytechnique.cmap.cnam.etl.patients.Patient
import fr.polytechnique.cmap.cnam.etl.sources.Sources
import fr.polytechnique.cmap.cnam.etl.transformers.patients.PatientFilters
import fr.polytechnique.cmap.cnam.study.fall.codes._
import fr.polytechnique.cmap.cnam.study.fall.config.FallConfig
import fr.polytechnique.cmap.cnam.study.fall.extractors._
import fr.polytechnique.cmap.cnam.study.fall.statistics.DiagnosisCounter
import fr.polytechnique.cmap.cnam.util.Path
import fr.polytechnique.cmap.cnam.util.reporting._

object FallMainExtract extends Main with FractureCodes {

  override def appName: String = "fall study extract"

  override def run(sqlContext: SQLContext, argsMap: Map[String, String]): Option[Dataset[_]] = {
    import implicits.SourceReader
    val format = new java.text.SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
    val startTimestamp = new java.util.Date()
    val fallConfig = FallConfig.load(argsMap("conf"), argsMap("env"))
    val sources = Sources.sanitize(sqlContext.readSources(fallConfig.input, fallConfig.readFileFormat))
    val dcir = sources.dcir.get.repartition(4000).persist()
    val mco = sources.mco.get.repartition(4000).persist()
    val meta = mutable.HashMap[String, OperationMetadata]()
    computeHospitalStays(meta, sources, fallConfig)
    computeControls(meta, sources, fallConfig)
    computeOutcomes(meta, sources, fallConfig)
    computeExposures(meta, sources, fallConfig)
    OperationMetadata.serialize(argsMap("meta_bin"), meta)

    // Write Metadata
    val metadata = MainMetadata(
      this.getClass.getName,
      startTimestamp,
      new java.util.Date(),
      meta.values.toList
    )

    OperationReporter.writeMetaData(metadata.toJsonString(), "metadata_fall_extract_" + format.format(startTimestamp) + ".json", argsMap("env"))

    dcir.unpersist()
    mco.unpersist()

    None
  }

  def computeHospitalStays(meta: mutable.HashMap[String, OperationMetadata], sources: Sources, fallConfig: FallConfig):
  mutable.HashMap[String, OperationMetadata] = {

    if (fallConfig.runParameters.hospitalStays) {
      val hospitalStays = McoHospitalStaysExtractor.extract(sources).cache()
      meta += {
        "extract_hospital_stays" ->
          OperationReporter
            .reportAsDataSet(
              "extract_hospital_stays",
              List("MCO"),
              OperationTypes.HospitalStays,
              hospitalStays,
              Path(fallConfig.output.outputSavePath),
              fallConfig.output.saveMode,
              fallConfig.writeFileFormat
            )
      }
    }
    meta
  }

  def computeExposures(meta: mutable.HashMap[String, OperationMetadata], sources: Sources, fallConfig: FallConfig):
  mutable.HashMap[String, OperationMetadata] = {

    if (fallConfig.runParameters.drugPurchases) {
      val drugPurchases = new DrugsExtractor(fallConfig.drugs).extract(sources).cache()
      meta += {
        "drug_purchases" ->
          OperationReporter
            .reportAsDataSet(
              "drug_purchases",
              List("DCIR"),
              OperationTypes.Dispensations,
              drugPurchases,
              Path(fallConfig.output.outputSavePath),
              fallConfig.output.saveMode,
              fallConfig.writeFileFormat
            )
      }
      val controlDrugPurchases = ControlDrugs.extract(sources).cache()
      meta += {
        "control_drugs_purchases" ->
          OperationReporter
            .reportAsDataSet(
              "control_drugs_purchases",
              List("DCIR"),
              OperationTypes.Dispensations,
              controlDrugPurchases,
              Path(fallConfig.output.outputSavePath),
              fallConfig.output.saveMode,
              fallConfig.writeFileFormat
            )
      }
    }

    val optionAllPatients = if (fallConfig.runParameters.patients) {
      val allpatients: Dataset[Patient] = AllPatientExtractor.extract(sources).cache()
      meta += {
        "extract_raw_patients" ->
          OperationReporter
            .reportAsDataSet(
              "raw_patients",
              List("DCIR", "MCO", "IR_BEN_R", "MCO_CE"),
              OperationTypes.Patients,
              allpatients,
              Path(fallConfig.output.outputSavePath),
              fallConfig.output.saveMode,
              fallConfig.writeFileFormat
            )
      }

      Some(allpatients)
    } else {
      None
    }

    if (fallConfig.runParameters.patients) {
      val filteredpatients: Dataset[Patient] = new PatientFilters(PatientsConfig(fallConfig.base.studyStart)).filterPatients(optionAllPatients.get).cache()
      meta += {
        "extract_filtered_patients" ->
          OperationReporter
            .reportAsDataSet(
              "filtered_patients",
              List("DCIR", "MCO", "IR_BEN_R", "MCO_CE"),
              OperationTypes.Patients,
              filteredpatients,
              Path(fallConfig.output.outputSavePath),
              fallConfig.output.saveMode,
              fallConfig.writeFileFormat
            )
      }
    }
    meta
  }

  def computeOutcomes(meta: mutable.HashMap[String, OperationMetadata], sources: Sources, fallConfig: FallConfig):
  mutable.HashMap[String, OperationMetadata] = {

    if (fallConfig.runParameters.diagnoses) {
      val diagnoses = new DiagnosisExtractor(fallConfig.diagnoses).extract(sources).persist()
      val diagnosesPopulation = DiagnosisCounter.process(diagnoses)
      val diagnosesReport = OperationReporter.reportDataAndPopulationAsDataSet(
        "diagnoses",
        List("MCO", "IR_IMB_R"),
        OperationTypes.Diagnosis,
        diagnoses,
        diagnosesPopulation,
        Path(fallConfig.output.outputSavePath),
        fallConfig.output.saveMode,
        fallConfig.writeFileFormat
      )
      meta += {
        diagnosesReport.name -> diagnosesReport
      }
    }

    if (fallConfig.runParameters.acts) {
      val (acts, surgeries) = new ActsExtractor(fallConfig.medicalActs).extract(sources)
      acts.persist()
      surgeries.persist()
      val actsReport = OperationReporter.reportAsDataSet(
        "acts",
        List("DCIR", "MCO", "MCO_CE"),
        OperationTypes.MedicalActs,
        acts,
        Path(fallConfig.output.outputSavePath),
        fallConfig.output.saveMode,
        fallConfig.writeFileFormat
      )
      meta += {
        actsReport.name -> actsReport
      }

      val surgeriesReport = OperationReporter.reportAsDataSet(
        "surgeries",
        List("MCO"),
        OperationTypes.MedicalActs,
        surgeries,
        Path(fallConfig.output.outputSavePath),
        fallConfig.output.saveMode,
        fallConfig.writeFileFormat
      )
      meta += {
        surgeriesReport.name -> surgeriesReport
      }

      val liberalActs = acts
        .filter(act => act.groupID == DcirAct.groupID.Liberal && !CCAMExceptions.contains(act.value)).persist()
      val liberal_acts_report = OperationReporter.reportAsDataSet(
        "liberal_acts",
        List("acts"),
        OperationTypes.MedicalActs,
        liberalActs,
        Path(fallConfig.output.outputSavePath),
        fallConfig.output.saveMode,
        fallConfig.writeFileFormat
      )
      meta += {
        liberal_acts_report.name -> liberal_acts_report
      }

      val hospitalDeaths = new FallHospitalStayExtractor(SimpleExtractorCodes(List(Death.value))).extract(sources)
      val hospitalDeathsReport = OperationReporter.reportAsDataSet(
        "hospital_deaths",
        List("MCO"),
        OperationTypes.HospitalStays,
        hospitalDeaths,
        Path(fallConfig.output.outputSavePath),
        fallConfig.output.saveMode,
        fallConfig.writeFileFormat
      )
      meta += {
        hospitalDeathsReport.name -> hospitalDeathsReport
      }
    }
    meta
  }

  def computeControls(
    meta: mutable.HashMap[String, OperationMetadata],
    sources: Sources,
    fallConfig: FallConfig): mutable.Buffer[OperationMetadata] = {
    val operationsMetadata = mutable.Buffer[OperationMetadata]()

    val opioids = OpioidsExtractor.extract(sources).cache()
    meta += {
      "opioids" -> {
        OperationReporter
          .report(
            "opioids",
            List("DCIR"),
            OperationTypes.Dispensations,
            opioids.toDF,
            Path(fallConfig.output.outputSavePath),
            fallConfig.output.saveMode,
            fallConfig.writeFileFormat
          )
      }
    }

    val ipp = IPPExtractor.extract(sources).cache()
    meta += {
      "IPP" -> OperationReporter
        .report(
          "IPP",
          List("DCIR"),
          OperationTypes.Dispensations,
          ipp.toDF,
          Path(fallConfig.output.outputSavePath),
          fallConfig.output.saveMode,
          fallConfig.writeFileFormat
        )
    }

    val cardiac = CardiacExtractor.extract(sources).cache()
    meta += {
      "cardiac" -> OperationReporter
        .report(
          "cardiac",
          List("DCIR"),
          OperationTypes.Dispensations,
          cardiac.toDF,
          Path(fallConfig.output.outputSavePath),
          fallConfig.output.saveMode,
          fallConfig.writeFileFormat
        )
    }

    val epileptics = EpilepticsExtractor.extract(sources).cache()
    meta += {
      "epileptics" ->
        OperationReporter
          .report(
            "epileptics",
            List("MCO", "IMB"),
            OperationTypes.Diagnosis,
            epileptics.toDF,
            Path(fallConfig.output.outputSavePath),
            fallConfig.output.saveMode,
            fallConfig.writeFileFormat
          )
    }

    val hta = HTAExtractor.extract(sources).cache()
    meta += {
      "HTA" ->
        OperationReporter
          .report(
            "HTA",
            List("DCIR"),
            OperationTypes.Dispensations,
            hta.toDF,
            Path(fallConfig.output.outputSavePath),
            fallConfig.output.saveMode,
            fallConfig.writeFileFormat
          )
    }
    operationsMetadata
  }
}
