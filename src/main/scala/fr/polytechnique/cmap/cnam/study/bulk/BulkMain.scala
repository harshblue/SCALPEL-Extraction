// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.study.bulk

import java.io.PrintWriter
import org.apache.spark.sql.{Dataset, SQLContext}
import fr.polytechnique.cmap.cnam.Main
import fr.polytechnique.cmap.cnam.etl.implicits
import fr.polytechnique.cmap.cnam.etl.sources.Sources
import fr.polytechnique.cmap.cnam.study.bulk.extractors._
import fr.polytechnique.cmap.cnam.util.reporting.MainMetadata

object BulkMain extends Main {
  override def appName: String = "BulkMain"

  override def run(
    sqlContext: SQLContext,
    argsMap: Map[String, String]): Option[Dataset[_]] = {

    val format = new java.text.SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
    val startTimestamp = new java.util.Date()
    val bulkConfig = BulkConfig.load(argsMap("conf"), argsMap("env"))

    import implicits.SourceReader
    val sources = Sources.sanitize(sqlContext.readSources(bulkConfig.input, bulkConfig.readFileFormat))

    val sourceExtractor: List[SourceExtractor] = List(
      new DcirSourceExtractor(bulkConfig.output.root, bulkConfig.output.saveMode, bulkConfig.writeFileFormat, bulkConfig.drugs),
      new McoSourceExtractor(bulkConfig.output.root, bulkConfig.output.saveMode, bulkConfig.writeFileFormat),
      new McoCeSourceExtractor(bulkConfig.output.root, bulkConfig.output.saveMode, bulkConfig.writeFileFormat),
      new SsrSourceExtractor(bulkConfig.output.root, bulkConfig.output.saveMode, bulkConfig.writeFileFormat),
      new SsrCeSourceExtractor(bulkConfig.output.root, bulkConfig.output.saveMode, bulkConfig.writeFileFormat),
      new HadSourceExtractor(bulkConfig.output.root, bulkConfig.output.saveMode, bulkConfig.writeFileFormat)
    )

    // Write Metadata
    val metadata = MainMetadata(
      this.getClass.getName, startTimestamp, new java.util.Date(),
      sourceExtractor.flatMap(se => se.extract(sources)) ++
        new PatientExtractor(bulkConfig.output.root, bulkConfig.output.saveMode, bulkConfig.base, bulkConfig.writeFileFormat).extract(sources)
    )
    val metadataJson: String = metadata.toJsonString()

    new PrintWriter("metadata_bulk_" + format.format(startTimestamp) + ".json") {
      write(metadataJson)
      close()
    }

    None
  }
}
