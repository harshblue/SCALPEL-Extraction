// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.etl.events

import org.scalatest.flatspec.AnyFlatSpecLike
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.types.{StringType, StructField, StructType, TimestampType}
import fr.polytechnique.cmap.cnam.util.functions._


class ClassificationSuite extends AnyFlatSpecLike {

  "apply" should "return correct Event" in {
    // Given
    val patientID = "Stevie"
    val groupID = "42"
    val name = "GHMDA233"
    val date = makeTS(2016, 1, 1)
    val expected = Event("Stevie", "ghm", "42", "GHMDA233", 0.0, makeTS(2016, 1, 1), None)

    // When
    val result = GHMClassification(patientID, groupID, name, date)

    // Then
    assert(result == expected)
  }
}
