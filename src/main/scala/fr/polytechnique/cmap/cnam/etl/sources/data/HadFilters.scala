package fr.polytechnique.cmap.cnam.etl.sources.data

import fr.polytechnique.cmap.cnam.etl.sources.data.DoublonFinessPmsi.specialHospitalCodes
import org.apache.spark.sql.{Column, DataFrame}

private[data] class HadFilters(rawHad: DataFrame) {
  /** Remove geographic finess doublons from APHP, APHM and HCL.
    *
    * @return dataframe without finess doublons
    */
  def filterSpecialHospitals: DataFrame = {
    rawHad.where(!HadSource.ETA_NUM_EPMSI.isin(specialHospitalCodes: _*))
  }

  /** Filter out shared stays (between hospitals).
    *
    * @return
    */
  def filterSharedHospitalStays: DataFrame = {
    val duplicateHospitalsFilter: Column = !(HadSource.ENT_MOD === 1 and HadSource.SOR_MOD === 1)
    rawHad.filter(duplicateHospitalsFilter)
  }

  /** Filter out Had corrupted stays as returned by the ATIH.
    *
    * @return dataframe cleaned of HAD corrupted stays
    */
  def filterHadCorruptedHospitalStays: DataFrame = {
    val fictionalAndFalseHospitalStaysFilter: Column = HadSource
      .NIR_RET === "0" and HadSource.SEJ_RET === "0" and HadSource
      .FHO_RET === "0" and HadSource.PMS_RET === "0" and HadSource
      .DAT_RET === "0"
    rawHad.filter(fictionalAndFalseHospitalStaysFilter)
  }
}

