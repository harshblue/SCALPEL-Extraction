package fr.polytechnique.cmap.cnam.filtering

import org.apache.spark.sql.DataFrame
import fr.polytechnique.cmap.cnam.SharedContext


class ExtractorSuite extends SharedContext{

  "extract" should "return the right dataframe" in {
    // Given
    val path: String = "src/test/resources/expected/IR_BEN_R.parquet"
    val expected: DataFrame = sqlContext.read.parquet("src/test/resources/expected/IR_BEN_R.parquet")

    class TestExtractor extends Extractor(sqlContext)

    val testExtractor = new TestExtractor()

    // When
    val result = testExtractor.extract(path)

    // Then
    assert(result.schema == expected.schema)
  }

  it should "fail is path is invalid" in {
    // Given
    val path: String = "src/test/resources/expected/invalid_path.parquet"

    // Then
    intercept[org.apache.spark.sql.AnalysisException] {
      new IrImbExtractor(sqlContext).extract(path).count
    }
  }
}
