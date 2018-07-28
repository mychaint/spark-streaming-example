package com.mychaint.order.inject

import com.google.inject.AbstractModule
import org.apache.spark.sql.SparkSession

private[order] final class ProductionModule(devEmails: String) extends AbstractModule {

  val spark = SparkSession
    .builder
    .master("yarn")
    .appName("Order streaming process pipeline")
    .getOrCreate()

  override def configure(): Unit = {

    install(new DefaultModule(devEmails))
  }
}
