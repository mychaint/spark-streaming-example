package com.mychaint.order

import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.types._

@Singleton
private[order] final class DataUtils @Inject()
(
  private val spark: SparkSession,
  private val mySQLWriter: MySQLWriter,
  @Named("KAFKA BROKERS") private val BROKERS: String,
  @Named("KAFKA TOPICS") private val TOPICS: String
) {

  import spark.implicits._

  private val schema =
    new StructType()
      .add(new StructField("itemid", IntegerType))
      .add(new StructField("count", IntegerType))
      .add(new StructField("timestamp", TimestampType))
      .add(new StructField("price", FloatType))
      .add(new StructField("orderid", LongType))
      .add(new StructField("shopid", LongType))
      .add(new StructField("region", StringType))

  def getTestDataSource: DataFrame = {
    this.getProductionDataSource
//    spark
//      .readStream
//      .format("socket")
//      .option("host", "localhost")
//      .option("port", 9999)
//      .load()
//      .selectExpr("CAST(value AS STRING)")
//      .select(from_json($"value", schema).alias("value"))
//      .select("value.*")
  }

  def getProductionDataSource: DataFrame = {
    spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", this.BROKERS)
      .option("subscribe", this.TOPICS)
      .load()
      .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .select(from_json($"value", schema).alias("value"))
      .select("value.*")
  }

  def getTestDataTransformation(df: DataFrame): DataFrame = {
    this.getProductionTransformation(df)
  }

  def getProductionTransformation(df: DataFrame): DataFrame = {
    df.select("orderid", "itemid", "shopid", "region", "price", "count", "timestamp")
      .withColumn("processing_time", current_timestamp)
      .groupBy(
        window($"timestamp", "1 minutes"),
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

  def getTestDataSink(df: DataFrame): StreamingQuery = {
    this.getProductionDataSink(df)
//    df.writeStream
//      .outputMode("update")
//      .format("console")
//      .start()
  }

  def getProductionDataSink(df: DataFrame): StreamingQuery = {
    df.writeStream
      .outputMode("update")
      .foreach(mySQLWriter)
      .start()
  }
}
