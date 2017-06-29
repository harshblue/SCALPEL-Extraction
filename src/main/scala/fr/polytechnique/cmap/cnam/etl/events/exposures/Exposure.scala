package fr.polytechnique.cmap.cnam.etl.events.exposures

import java.sql.Timestamp
import org.apache.spark.sql.Row
import fr.polytechnique.cmap.cnam.etl.events._

object Exposure extends Exposure

trait Exposure extends AnyEvent with EventBuilder {

  val category: EventCategory[Exposure] = "exposure"

  def apply(
    patientID: String, molecule: String, weight: Double, start: Timestamp, end: Timestamp
  ): Event[Exposure] = Event(patientID, category, groupID = "NA", molecule, weight, start, Some(end))

  def fromRow(
      r: Row,
      patientIDCol: String = "patientID",
      nameCol: String = "name",
      weightCol: String = "weight",
      startCol: String = "start",
      endCol: String = "end"): Event[Exposure] = {

    Exposure(
      r.getAs[String](patientIDCol),
      r.getAs[String](nameCol),
      r.getAs[Double](weightCol),
      r.getAs[Timestamp](startCol),
      r.getAs[Timestamp](endCol)
    )
  }
}
