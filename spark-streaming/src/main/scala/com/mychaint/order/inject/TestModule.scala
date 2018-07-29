package com.mychaint.order.inject

import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Provides}
import com.mychaint.order.DataUtils
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.{DataFrame, SparkSession}

private[order] final class TestModule(devEmails: String) extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[String])
      .annotatedWith(Names.named("MYSQL HOST"))
      .toInstance("localhost")

    bind(classOf[String])
      .annotatedWith(Names.named("KAFKA BROKERS"))
      .toInstance("localhost:9092")
    bind(classOf[String])
      .annotatedWith(Names.named("KAFKA TOPICS"))
      .toInstance("order-streaming")

    install(new DefaultModule(devEmails))
  }

  @Provides
  def createSparkSession(): SparkSession = {
    val spark = SparkSession
      .builder
      .master("local[2]")
      .appName("test")
      .getOrCreate()
    spark
  }

  @Provides
  def getTestDataSourceFunction(dataUtils: DataUtils): () => DataFrame = {
    dataUtils.getTestDataSource _
  }

  @Provides
  def getTestDataTransformationFunction(dataUtils: DataUtils): (DataFrame) => DataFrame = {
    dataUtils.getTestDataTransformation _
  }

  @Provides
  def getTestDataSinkFunction(dataUtils: DataUtils): (DataFrame) => StreamingQuery = {
    dataUtils.getTestDataSink _
  }
}
