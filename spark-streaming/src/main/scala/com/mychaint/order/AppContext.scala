package com.mychaint.order

import com.google.inject.Guice
import com.mychaint.order.inject.{ProductionModule, TestModule}
import org.apache.spark.sql.SparkSession

object AppContext {

  private val spark = SparkSession
    .builder
    .appName("Order streaming process")
    .getOrCreate()

  def main(args: Array[String]) = {
    val devEmails = args(1)
    val processor = args.head match {
      case "test" =>
        Guice.createInjector(new TestModule(devEmails))
          .getInstance(classOf[OrderStreamingProcessor])
      case "prod" | "production" =>
        Guice.createInjector(new ProductionModule(devEmails))
          .getInstance(classOf[OrderStreamingProcessor])
    }
    processor.execute()
  }
}
