// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.etl.extractors.events.diagnoses

import fr.polytechnique.cmap.cnam.SharedContext
import fr.polytechnique.cmap.cnam.etl.events.{Diagnosis, Event, McoAssociatedDiagnosis, McoLinkedDiagnosis, McoMainDiagnosis}
import fr.polytechnique.cmap.cnam.etl.extractors.codes.SimpleExtractorCodes
import fr.polytechnique.cmap.cnam.etl.sources.Sources
import fr.polytechnique.cmap.cnam.util.functions.makeTS

class McoDiagnosesSuite extends SharedContext {

  "extract" should "extract target MainDiagnosis" in {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    // Given
    val dpCodes = SimpleExtractorCodes(List("C67"))
    val mco = spark.read.parquet("src/test/resources/test-input/MCO.parquet")
    val sources = Sources(mco = Some(mco))

    val expected = Seq[Event[Diagnosis]](
      McoMainDiagnosis("Patient_02", "10000123_20000123_2007", "C670", makeTS(2007, 1, 29)),
      McoMainDiagnosis("Patient_02", "10000123_20000345_2007", "C671", makeTS(2007, 1, 29)),
      McoMainDiagnosis("Patient_02", "10000123_10000987_2006", "C670", makeTS(2005, 12, 29)),
      McoMainDiagnosis("Patient_02", "10000123_10000543_2006", "C671", makeTS(2005, 12, 24)),
      McoMainDiagnosis("Patient_02", "10000123_30000546_2008", "C670", makeTS(2008, 3, 8)),
      McoMainDiagnosis("Patient_02", "10000123_30000852_2008", "C671", makeTS(2008, 3, 15))
    ).toDS


    // When
    val result = McoMainDiagnosisExtractor(dpCodes).extract(sources)

    // Then
    assertDSs(result, expected)
  }

  it should "extract target LinkedDiagnosis" in {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    // Given
    val linkedCodes = SimpleExtractorCodes(List("E05", "E08"))
    val mco = spark.read.parquet("src/test/resources/test-input/MCO.parquet")
    val sources = Sources(mco = Some(mco))

    val expected = Seq[Event[Diagnosis]](
      McoLinkedDiagnosis("Patient_02", "10000123_20000123_2007", "E05", makeTS(2007, 1, 29)),
      McoLinkedDiagnosis("Patient_02", "10000123_10000543_2006", "E08", makeTS(2005, 12, 24))
    ).toDS

    // When
    val result = McoLinkedDiagnosisExtractor(linkedCodes).extract(sources)

    // Then
    assertDSs(result, expected)
  }

  it should "extract target AssocitedDiagnosis" in {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    // Given
    val associatedDiagnosis = SimpleExtractorCodes(List("C66"))
    val mco = spark.read.parquet("src/test/resources/test-input/MCO.parquet")
    val sources = Sources(mco = Some(mco))

    val expected = Seq[Event[Diagnosis]](
      McoAssociatedDiagnosis("Patient_02", "10000123_20000123_2007", "C66.5", makeTS(2007, 1, 29)),
      McoAssociatedDiagnosis("Patient_02", "10000123_10000543_2006", "C66.9", makeTS(2005, 12, 24))
    ).toDS

    // When
    val result = McoAssociatedDiagnosisExtractor(associatedDiagnosis).extract(sources)

    // Then
    assertDSs(result, expected)
  }

}

