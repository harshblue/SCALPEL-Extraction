// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.etl.extractors.dcir

import fr.polytechnique.cmap.cnam.etl.extractors.ColumnNames

trait DcirSource extends ColumnNames {

  final object ColNames extends Serializable {
    lazy val PatientID: ColName = "NUM_ENQ"
    lazy val MSpe: ColName = "PSE_SPE_COD"
    lazy val NonMSpe: ColName = "PSE_ACT_NAT"
    lazy val ExecPSNum: ColName = "PFS_EXE_NUM"
    lazy val DcirEventStart: ColName = "EXE_SOI_DTD"
    lazy val DcirFluxDate: ColName = "FLX_DIS_DTD"
    lazy val CamCode: String = "ER_CAM_F__CAM_PRS_IDE"
    lazy val GHSCode: String = "ER_ETE_F__ETE_GHS_NUM"
    lazy val InstitutionCode: String = "ER_ETE_F__ETE_TYP_COD"
    lazy val Sector: String = "ER_ETE_F__PRS_PPU_SEC"
    lazy val Date: String = "EXE_SOI_DTD"
    lazy val all = List(
      PatientID,
      CamCode,
      GHSCode,
      InstitutionCode,
      Sector,
      Date,
      MSpe,
      NonMSpe,
      ExecPSNum,
      DcirFluxDate
    )

  }

}
