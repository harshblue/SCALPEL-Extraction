// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.etl.extractors.acts

import fr.polytechnique.cmap.cnam.SharedContext
import fr.polytechnique.cmap.cnam.etl.events.{Event, MedicalAct, SsrCEAct}
import fr.polytechnique.cmap.cnam.etl.sources.Sources
import fr.polytechnique.cmap.cnam.util.functions.makeTS
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.types._

class SsrCEMedicalActsSuite extends SharedContext {

  import SsrCeActExtractor.ColNames

  val schema = StructType(
    StructField(ColNames.PatientID, StringType) ::
      StructField(ColNames.CamCode, StringType) ::
      StructField(ColNames.Date, DateType) :: Nil
  )

  "isInStudy" should "return true when a study code is found in the row" in {

    // Given
    val codes = Set("AAAA", "BBBB")
    val inputArray = Array[Any]("Patient_A", "AAAA", makeTS(2010, 1, 1))
    val inputRow = new GenericRowWithSchema(inputArray, schema)

    // When
    val result = SsrCeActExtractor.isInStudy(codes)(inputRow)

    // Then
    assert(result)
  }

  it should "return false when no code is found in the row" in {

    // Given
    val codes = Set("AAAA", "BBBB")
    val inputArray = Array[Any]("Patient_A", "CCCC", makeTS(2010, 1, 1))
    val inputRow = new GenericRowWithSchema(inputArray, schema)

    // When
    val result = SsrCeActExtractor.isInStudy(codes)(inputRow)

    // Then
    assert(!result)
  }

  "extract" should "return a Dataset of Ssr CE Medical Acts" in {

    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    // Given
    val codes = Set("AAAA", "CCCC")

    val input = Seq(
      ("Patient_A", "AAAA", makeTS(2010, 1, 1)),
      ("Patient_A", "BBBB", makeTS(2010, 2, 1)),
      ("Patient_B", "CCCC", makeTS(2010, 3, 1)),
      ("Patient_B", "CCCC", makeTS(2010, 4, 1)),
      ("Patient_C", "BBBB", makeTS(2010, 5, 1))
    ).toDF(
      ColNames.PatientID, ColNames.CamCode, ColNames.Date
    )

    val sources = Sources(ssrCe = Some(input))

    val expected = Seq[Event[MedicalAct]](
      SsrCEAct("Patient_A", "ACE", "AAAA", 0.0, makeTS(2010, 1, 1)),
      SsrCEAct("Patient_B", "ACE", "CCCC", 0.0, makeTS(2010, 3, 1)),
      SsrCEAct("Patient_B", "ACE", "CCCC", 0.0, makeTS(2010, 4, 1))
    ).toDS

    // When
    val result = SsrCeActExtractor.extract(sources, codes)

    // Then
    assertDSs(result, expected)
  }

}
