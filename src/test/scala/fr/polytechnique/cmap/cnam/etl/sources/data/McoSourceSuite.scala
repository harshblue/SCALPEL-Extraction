// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.etl.sources.data

import fr.polytechnique.cmap.cnam.SharedContext

class McoSourceSuite extends SharedContext {
  "sanitize" should "return lines that are not corrupted" in {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    // Given
    val colNames = List(
      McoSource.GRG_GHM,
      McoSource.NIR_RET,
      McoSource.SEJ_RET,
      McoSource.FHO_RET,
      McoSource.PMS_RET,
      McoSource.DAT_RET,
      McoSource.ETA_NUM,
      McoSource.GHS_NUM,
      McoSource.SEJ_TYP
    ).map(col => col.toString)

    val input = Seq(
      ("90XXXX", "1", "1", "1", "1", "1", "1", "3333", Some("A")),
      ("27XXXX", "1", "1", "1", "1", "1", "2", "3424", Some("A")),
      ("76XXXX", "0", "0", "0", "0", "0", "1", "8271", Some("A")),
      ("76XXXX", "0", "0", "0", "0", "0", "1", "8271", None),
      ("76XXXX", "0", "0", "0", "0", "0", "1", "8271", Some("B")),
      ("28XXXX", "0", "0", "0", "0", "0", "1", "8271", Some("B")),
      ("76XXXX", "0", "0", "0", "0", "0", "1", "9999", Some("A")),
      ("76XXXX", "0", "0", "0", "0", "0", "910100023", "1111", Some("B")),
      ("28XXXX", "0", "0", "0", "0", "0", "1", "2222", Some("A")),
      ("28XXXX", "0", "0", "0", "0", "0", "130784234", "1981", Some("A"))
    ).toDF(colNames: _*)


    val expected = Seq(
      ("76XXXX", "0", "0", "0", "0", "0", "1", "8271", Some("A")),
      ("76XXXX", "0", "0", "0", "0", "0", "1", "8271", None),
      ("28XXXX", "0", "0", "0", "0", "0", "1", "8271", Some("B")),
      ("28XXXX", "0", "0", "0", "0", "0", "1", "2222", Some("A"))
    ).toDF(colNames: _*)

    // When
    val result = McoSource.sanitize(input)

    // Then
    assertDFs(result, expected)
  }
}
