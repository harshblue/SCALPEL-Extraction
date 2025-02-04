package fr.polytechnique.cmap.cnam.etl.extractors.sources.ssr

import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{LongType, TimestampType}
import org.apache.spark.sql.{Column, DataFrame}
import fr.polytechnique.cmap.cnam.etl.extractors.ColumnNames
import fr.polytechnique.cmap.cnam.util.ColumnUtilities.parseTimestamp

trait SsrSource extends ColumnNames {

  final object ColNames extends Serializable {
    val PatientID: ColName = "NUM_ENQ"
    val StayStartMonth: ColName = "MOI_LUN_1S"
    val StayStartYear: ColName = "ANN_LUN_1S"
    val StayStartDate: ColName = "ENT_DAT"
    val StayEndDate: ColName = "SOR_DAT"
    val StartDate: ColName = "EXE_SOI_DTD"
    val EndDate: ColName = "EXE_SOI_DTF"
    val EtaNum: ColName = "ETA_NUM"
    val RhaNum: ColName = "RHA_NUM"
    val RhsNum: ColName = "RHS_NUM"
    val Year: ColName = "year"

    val core = List(
      PatientID, StayStartMonth, StayStartYear, StayStartDate, StayStartDate, StayEndDate, StartDate, EndDate,
      EtaNum, RhaNum, RhsNum, Year, NewColumns.EstimatedStayStart
    )
    val StayLength: ColName = "SSR_B__RHS_ANT_SEJ_ENT"
    val DP: ColName = "SSR_B__MOR_PRP"

    val DR: ColName = "SSR_B__ETL_AFF"

    val DA: ColName = "SSR_D__DGN_COD"

    val CCAM: ColName = "SSR_CCAM__CCAM_ACT" // present only in 2014-2015-2016, should be addeed for the studies on the echantillon

    val CSARR: ColName = "SSR_CSARR__CSARR_COD"

    val FP_PEC: ColName = "SSR_B__FP_PEC"
    // MOI_ANN_SOR_SEJ ?
    //val GHM: ColName = "SSR_B__GRG_GHM" -> GME TODO

    val all = List(
      PatientID, DP, DR, DA, CCAM, CSARR, FP_PEC, EtaNum, RhaNum, RhsNum, StayLength, //CSARR,
      StayStartDate, StayEndDate, StartDate, EndDate, Year, StayStartMonth, StayStartYear
    )

    val hospitalStayPart = List(
      PatientID, EtaNum, RhaNum, RhsNum, Year, StartDate, EndDate
    )
  }

  implicit class SsrDataFrame(df: DataFrame) {

    /**
      *
      * Estimate the stay starting date according to the different versions of PMSI SSR
      * Please note that in the case of early SSR (i.e. < 2009), the estimator is
      * date(01/month/year) - number of days of the stay.
      * This estimator is quite imprecise, and if one patient has several stays of the same
      * length in one month, it results in duplicate events.
      */
    def estimateStayStartTime: DataFrame = {
      /*
      val dayInMs = 24L * 60 * 60
      val timeDelta: Column = coalesce(ColNames.StayLength.toCol, lit(0)) * dayInMs
      val estimate: Column = {
        val endDate = parseTimestamp(ColNames.StayEndDate.toCol, "ddMMyyyy")
        (endDate.cast(LongType) - timeDelta).cast(TimestampType)
      }*/

      val roughEstimate: Column = unix_timestamp(
        concat_ws("-", ColNames.StayStartYear.toCol, ColNames.StayStartMonth.toCol, lit("01 00:00:00"))
      ).cast(LongType).cast(TimestampType)

      val givenDate: Column = parseTimestamp(ColNames.StayStartDate.toCol, "ddMMyyyy")

      df.withColumn(
        NewColumns.EstimatedStayStart,
        coalesce(givenDate, roughEstimate)
      )
    }
  }

  object NewColumns extends Serializable {
    val EstimatedStayStart: ColName = "estimated_start"
  }

}
