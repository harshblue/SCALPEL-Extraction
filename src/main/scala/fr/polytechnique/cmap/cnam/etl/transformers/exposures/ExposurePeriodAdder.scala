// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.etl.transformers.exposures

import me.danielpes.spark.datetime.Period
import org.apache.spark.sql.DataFrame

trait ExposurePeriodAdder {

  val exposurePeriodStrategy: ExposurePeriodStrategy

  implicit def exposurePeriodImplicits(data: DataFrame): ExposurePeriodAdderImpl = {

    exposurePeriodStrategy match {
      case ExposurePeriodStrategy.Limited => new LimitedExposurePeriodAdder(data)
      case ExposurePeriodStrategy.Unlimited => new UnlimitedExposurePeriodAdder(data)
    }
  }
}

abstract class ExposurePeriodAdderImpl(data: DataFrame) {
  def withStartEnd(
    minPurchases: Int,
    startDelay: Period,
    purchasesWindow: Period,
    endThresholdGc: Option[Period],
    endThresholdNgc: Option[Period],
    endDelay: Option[Period]): DataFrame
}
