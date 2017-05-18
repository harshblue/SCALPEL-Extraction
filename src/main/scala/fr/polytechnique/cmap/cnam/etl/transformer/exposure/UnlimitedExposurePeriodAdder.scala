package fr.polytechnique.cmap.cnam.etl.transformer.exposure

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.TimestampType
import org.apache.spark.sql.{Column, DataFrame}
import fr.polytechnique.cmap.cnam.etl.events.Event.Columns._

private class UnlimitedExposurePeriodAdder(data: DataFrame) extends ExposurePeriodAdderImpl(data) {

  // todo: add first-only parameter, similar to mlpp
  def withStartEnd(minPurchases: Int = 2, startDelay: Int = 3, purchasesWindow: Int = 6): DataFrame = {

    val window = Window.partitionBy(PatientID, Value)

    val exposureStartRule: Column = when(
      months_between(col(Start), col("previousStartDate")) <= purchasesWindow,
      add_months(col(Start), startDelay).cast(TimestampType)
    )

    val potentialExposureStart: Column = if(minPurchases == 1)
      col(Start)
    else
      lag(col(Start), minPurchases - 1).over(window.orderBy(Start))

    data
      .withColumn("previousStartDate", potentialExposureStart)
      .withColumn("exposureStart", exposureStartRule)
      .withColumn("exposureStart", when(col("exposureStart") < col("followUpStart"),
        col("followUpStart")).otherwise(col("exposureStart"))
      )
      .withColumn("exposureStart", min("exposureStart").over(window))
      .withColumn("exposureEnd", col("followUpEnd"))
      .drop("previousStartDate")
  }
}
