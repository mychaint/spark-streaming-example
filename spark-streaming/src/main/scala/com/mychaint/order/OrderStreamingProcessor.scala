package com.mychaint.order

import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import org.apache.spark.sql.DataFrame

@Singleton
private[order] final class OrderStreamingProcessor @Inject()
(
  @Named("DATA SOURCE PROVIDER") private val getDataSource: () => DataFrame,
  @Named("DATA TRANSFORMATION") private val transformDataSource: (DataFrame) => DataFrame,
  @Named("DATA SINK") private val sinkData: (DataFrame) => Unit
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
