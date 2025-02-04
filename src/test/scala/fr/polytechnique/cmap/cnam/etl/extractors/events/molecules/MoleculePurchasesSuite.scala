// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.etl.extractors.events.molecules

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import fr.polytechnique.cmap.cnam.SharedContext
import fr.polytechnique.cmap.cnam.etl.sources.Sources

class MoleculePurchasesSuite extends SharedContext {

  "extract" should "call the adequate private extractor" in {

    // Given
    val config = MoleculePurchasesConfig(List("A10"))
    val dcir: DataFrame = sqlContext.read.load("src/test/resources/test-input/DCIR.parquet")
    val irPha: DataFrame = sqlContext.read.load("src/test/resources/test-input/IR_PHA_R.parquet")
    val dosages: DataFrame = sqlContext.read
      .format("csv")
      .option("header", "true")
      .load("src/test/resources/test-input/DOSE_PER_MOLECULE.CSV")
      .select(
        col("PHA_PRS_IDE"),
        col("MOLECULE_NAME"),
        col("TOTAL_MG_PER_UNIT")
      )
    val sources = new Sources(
      dcir = Some(dcir),
      irPha = Some(irPha),
      dosages = Some(dosages)
    )
    val expected = new DcirMoleculePurchases(config).extract(sources)

    // When
    val result = new MoleculePurchases(config).extract(sources)

    // Then
    assertDSs(result, expected)
  }
}