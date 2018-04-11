package fr.polytechnique.cmap.cnam.etl.extractors.acts

import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.types._
import fr.polytechnique.cmap.cnam.SharedContext
import fr.polytechnique.cmap.cnam.etl.events.{DcirAct, Event, MedicalAct}
import fr.polytechnique.cmap.cnam.util.functions._

class DcirMedicalActsSuite extends SharedContext {

  import DcirMedicalActs.ColNames

  val schema = StructType(
    StructField(ColNames.PatientID, StringType) ::
    StructField(ColNames.CamCode, StringType) ::
    StructField(ColNames.InstitutionCode, DoubleType) ::
    StructField(ColNames.GHSCode, DoubleType) ::
    StructField(ColNames.Sector, DoubleType) ::
    StructField(ColNames.Date, DateType) :: Nil
  )

  "medicalActFromRow" should "return a Medical Act event when it's found in the row" in {

    // Given
    val codes = List("AAAA", "BBBB")
    val inputArray = Array[Any]("Patient_A", "AAAA", null, null, null, makeTS(2010, 1, 1))
    val inputRow = new GenericRowWithSchema(inputArray, schema)
    val expected = Some(DcirAct("Patient_A", DcirAct.groupID.Liberal, "AAAA", makeTS(2010, 1, 1)))

    // When
    val result = DcirMedicalActs.medicalActFromRow(codes)(inputRow)

    // Then
    assert(result == expected)
  }

  it should "return None when no code is found in the row" in {

    // Given
    val codes = List("AAAA", "BBBB")
    val inputArray = Array[Any]("Patient_A", "CCCC", 1D, 0D, 1D, makeTS(2010, 1, 1))
    val inputRow = new GenericRowWithSchema(inputArray, schema)

    // When
    val result = DcirMedicalActs.medicalActFromRow(codes)(inputRow)

    // Then
    assert(result.isEmpty)
  }

  "getGHS" should "return the value in the correct column" in {
    // Given
    val schema = StructType(StructField(ColNames.GHSCode, DoubleType)::Nil)
    val inputArray = Array[Any](3D)
    val input = new GenericRowWithSchema(inputArray, schema)
    val expected = 3D

    // When
    val result = DcirMedicalActs.getGHS(input)

    // Then
    assert(result == expected)
  }

  "getSector" should "return the expected value" in {
    // Given
    val schema = StructType(StructField(ColNames.Sector, DoubleType)::Nil)
    val inputArray = Array[Any](3D)
    val input = new GenericRowWithSchema(inputArray, schema)
    val expected = 3D

    // When
    val result = DcirMedicalActs.getSector(input)

    // Then
    assert(result == expected)
  }

  "getInstitutionCode" should "return the value in the correct column" in {
    // Given
    val schema = StructType(StructField(ColNames.InstitutionCode, DoubleType)::Nil)
    val inputArray = Array[Any](52D)
    val input = new GenericRowWithSchema(inputArray, schema)
    val expected = 52D

    // When
    val result = DcirMedicalActs.getInstitutionCode(input)

    // Then
    assert(result == expected)

  }

  "getGroupID" should "return correct status of private ambulatory" in {
    // Given
    val schema = StructType(
      StructField(ColNames.GHSCode, DoubleType) ::
      StructField(ColNames.Sector, StringType) ::
      StructField(ColNames.InstitutionCode, DoubleType) :: Nil
    )
    val array = Array[Any](0D, 2D, 6D)
    val input = new GenericRowWithSchema(array, schema)
    val expected = Some(DcirAct.groupID.PrivateAmbulatory)

    // When
    val result = DcirMedicalActs.getGroupId(input)

    // Then
    assert(result == expected)

  }

  it should "return None if it is public related" in {
    // Given
    val schema = StructType(StructField(ColNames.Sector, StringType) :: Nil)
    val array = Array[Any](1D)
    val input = new GenericRowWithSchema(array, schema)
    val expected = None

    // When
    val result = DcirMedicalActs.getGroupId(input)

    // Then
    assert(result == expected)
  }

  "extract" should "return a Dataset of Medical Acts" in {

    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    // Given
    val codes = List("AAAA", "CCCC")

    val input = Seq(
      ("Patient_A", "AAAA", makeTS(2010, 1, 1), None, None, None),
      ("Patient_A", "BBBB", makeTS(2010, 2, 1), Some(1D), Some(0D), Some(1D)),
      ("Patient_B", "CCCC", makeTS(2010, 3, 1), None, None, None),
      ("Patient_B", "CCCC", makeTS(2010, 4, 1), Some(7D), Some(0D), Some(2D)),
      ("Patient_C", "BBBB", makeTS(2010, 5, 1), Some(1D), Some(0D), Some(2D))
    ).toDF(ColNames.PatientID, ColNames.CamCode, ColNames.Date,
      ColNames.InstitutionCode, ColNames.GHSCode, ColNames.Sector)

    val expected = Seq[Event[MedicalAct]](
      DcirAct("Patient_A", DcirAct.groupID.Liberal, "AAAA", makeTS(2010, 1, 1)),
      DcirAct("Patient_B", DcirAct.groupID.Liberal, "CCCC", makeTS(2010, 3, 1)),
      DcirAct("Patient_B", DcirAct.groupID.PrivateAmbulatory, "CCCC", makeTS(2010, 4, 1))
    ).toDS

    // When
    val result = DcirMedicalActs.extract(input, codes)

    // Then
    assertDSs(result, expected)
  }
}
