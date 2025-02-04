package fr.polytechnique.cmap.cnam.etl.extractors.sources.ssr

import java.sql.Timestamp
import org.apache.spark.sql.Row
import fr.polytechnique.cmap.cnam.etl.extractors.EventRowExtractor

/**
 *  Gets the following fields for SSR sourced events: patientID, start, groupId.
 */
trait SsrRowExtractor extends SsrSource with EventRowExtractor  {

  override def usedColumns: List[String] = ColNames.core ++ super.usedColumns

  def extractPatientId(r: Row): String = {
    r.getAs[String](ColNames.PatientID)
  }

  override def extractGroupId(r: Row): String = {
    r.getAs[String](ColNames.EtaNum) + "_" +
      r.getAs[String](ColNames.RhaNum) + "_" +
      r.getAs[String](ColNames.RhsNum) + "_" +
      r.getAs[Int](ColNames.Year).toString
  }

  def extractStart(r: Row): Timestamp = r.getAs[Timestamp](NewColumns.EstimatedStayStart)
}
