package com.mychaint.order

import com.google.inject.{Inject, Singleton}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.StreamingQuery

@Singleton
private[order] final class OrderStreamingProcessor @Inject()
(
  private val getDataSource: () => DataFrame,
  private val transformDataSource: (DataFrame) => DataFrame,
  private val sinkData: (DataFrame) => StreamingQuery
) {

  implicit class DataFrameUtils(val df: DataFrame) {
    def proceedTransformation = transformDataSource(df)
    def proceedSink = sinkData(df)
  }

  def execute(): Unit = {
    this.getDataSource()
      .proceedTransformation
      .proceedSink
  }
}
