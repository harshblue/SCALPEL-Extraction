// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.study.rosiglitazone

/*
 * The codes needed for this study's outcomes are listed in Confluence.
 * Link: https://datainitiative.atlassian.net/wiki/spaces/CFC/pages/55738376/Rosiglitazone+infarctus
 */

trait RosiglitazoneStudyCodes {

  val infarctusDiagnosisCodes: List[String] = List(
    "I2100", "I21000", "I2110", "I21100", "I2120", // Infarctus
    "I21200", "I2130", "I21300", "I2140", "I21400", // Infarctus
    "I2190", "I21900", "I2200", "I22000", "I2210", // Infarctus
    "I22100", "I2280", "I22800", "I2290", "I22900" // Infarctus
  )

  val diagCodeHeartFailure: List[String] = List("I50")

  val diagCodeHeartComplication: List[String] = List("I110", "I130", "I132", "I139", "K761", "J81")
}
