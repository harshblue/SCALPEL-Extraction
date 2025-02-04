// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.etl.events

import java.sql.Timestamp

trait HospitalStay extends AnyEvent with EventBuilder {

  override val category: EventCategory[HospitalStay] = "hospital_stay"

  def apply(patientID: String, hospitalID: String, start: Timestamp, end: Timestamp): Event[HospitalStay] =
    apply(patientID, hospitalID, hospitalID, 0D, start, Some(end))

  def apply(
    patientID: String,
    hospitalID: String,
    weight: Double,
    start: Timestamp,
    end: Timestamp): Event[HospitalStay] =
    apply(patientID, hospitalID, hospitalID, weight, start, Some(end))

}

object HospitalStay extends HospitalStay

object McoHospitalStay extends HospitalStay {
  override val category: EventCategory[HospitalStay] = "mco_hospital_stay"
}

object McoceEmergency extends HospitalStay {
  override val category: EventCategory[HospitalStay] = "mco_ce_emergency"
}

/** Hospital Stay in the SSR PMSI are one type of hospital stays,  see :
 * https://documentation-snds.health-data-hub.fr/glossaire/ssr.html
 */
object SsrHospitalStay extends HospitalStay {
  override val category: EventCategory[HospitalStay] = "ssr_hospital_stay"
}

/** HAD Hospital Stay in the HAD PMSI are one type of hospital stays, see :
 * https://documentation-snds.health-data-hub.fr/glossaire/had.html
 */
object HadHospitalStay extends HospitalStay {
  override val category: EventCategory[HospitalStay] = "had_hospital_stay"
}