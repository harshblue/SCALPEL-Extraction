// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.study.bulk.extractors

import fr.polytechnique.cmap.cnam.etl.config.BaseConfig
import fr.polytechnique.cmap.cnam.etl.extractors.patients.{AllPatientExtractor, PatientsConfig}
import fr.polytechnique.cmap.cnam.etl.sources.Sources
import fr.polytechnique.cmap.cnam.etl.transformers.patients.PatientFilters
import fr.polytechnique.cmap.cnam.util.Path
import fr.polytechnique.cmap.cnam.util.reporting.{OperationMetadata, OperationReporter, OperationTypes}

class PatientExtractor(val path: String, val saveMode: String, val baseConfig: BaseConfig, val fileFormat: String = "parquet") {
  def extract(sources: Sources): List[OperationMetadata] = {
    val patients = new PatientFilters(PatientsConfig(baseConfig.studyStart)).filterPatients(AllPatientExtractor.extract(sources))
    List(
      OperationReporter
        .report(
          "all_patients",
          List("DCIR", "MCO", "IR_BEN_R", "MCO_CE"),
          OperationTypes.Patients,
          patients.toDF,
          Path(path),
          saveMode,
          fileFormat
        )
    )

  }
}
