// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.etl.sources

import org.apache.spark.sql.DataFrame

private[sources] trait SourceSanitizer {
  self: SourceManager =>

  /**
    * Used to separate reading a source from running sanitizing filters
    *
    * @return a new instance of the Source, with the sanitized data
    */
  def sanitize(sourceData: DataFrame): DataFrame = sourceData
}
