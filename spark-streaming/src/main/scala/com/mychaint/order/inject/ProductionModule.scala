package com.mychaint.order.inject

import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Provides}
import com.mychaint.order.DataUtils
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.{DataFrame, SparkSession}

private[order] final class ProductionModule(devEmails: String) extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[String])
      .annotatedWith(Names.named("MYSQL HOST"))
      .toInstance("slave01")

    bind(classOf[String])
      .annotatedWith(Names.named("KAFKA BROKERS"))
      .toInstance("slave00:9092")
    bind(classOf[String])
      .annotatedWith(Names.named("KAFKA TOPICS"))
      .toInstance("order-streaming")

    install(new DefaultModule(devEmails))
  }

  @Provides
  def createSparkSession(): SparkSession = {
    SparkSession
      .builder
      .master("yarn")
      .appName("Order streaming process pipeline")
      .getOrCreate()
  }

  @Provides
  def getDataSourceFunction(dataUtils: DataUtils): () => DataFrame = {
    dataUtils.getProductionDataSource _
  }

  @Provides
  def getDataTransformationFunction(dataUtils: DataUtils): (DataFrame) => DataFrame = {
    dataUtils.getProductionTransformation _
  }

  @Provides
  def getDataSinkFunction(dataUtils: DataUtils): (DataFrame) => StreamingQuery = {
    dataUtils.getProductionDataSink _
  }
}
