package com.mychaint.order

import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{current_timestamp, sum, window, round, count, min, max}

@Singleton
private[order] final class DataUtils @Inject()
(
  private val spark: SparkSession,
  @Named("KAFKA BROKERS") private val BROKERS: String,
  @Named("KAFKA TOPICS") private val TOPICS: String
) {

  def getTestDataSource: DataFrame = {
    spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "streaming-data")
      .load()
  }

  def getProductionDataSource: DataFrame = {
    spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", this.BROKERS)
      .option("subscribe", this.TOPICS)
      .load()
  }

  def getTestDataTransformation(df: DataFrame): DataFrame = {
    this.getProductionTransformation(df)
  }

  def getProductionTransformation(df: DataFrame): DataFrame = {
    import spark.implicits._

    df.select("orderid", "itemid", "shopid", "region", "price", "count", "timestamp")
      .withColumn("processing_time", current_timestamp)
      .withColumn("timestamp", round($"timestamp" / 60, 0) * 60)
      .groupBy(
        window($"timestamp", "2 minutes", "1 minutes"),
        $"region", $"shopid", $"itemid", $"timestamp"
      )
      .agg(
        sum($"price").alias("total_expenditure"),
        sum($"count").alias("total_items_sold"),
        count($"orderid").alias("total_orders"),
        min($"processing_time").alias("min_processing_time"),
        max($"processing_time").alias("max_processing_time")
      )
  }

  def getTestDataSink(df: DataFrame): Unit = {
    df.writeStream
      .format("console")
      .start()
  }

  def getProductionDataSink(df: DataFrame): DataFrame = {
    df.writeStream
      .foreach("kafka")
  }
}
