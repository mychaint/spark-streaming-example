package com.mychaint.order

import com.google.inject.{Guice, Stage}
import com.mychaint.order.inject.{ProductionModule, TestModule}

object AppContext {

  def main(args: Array[String]) = {
    val devEmails = args(1)
    val processor = args.head match {
      case "test" =>
        Guice.createInjector(Stage.DEVELOPMENT, new TestModule(devEmails))
          .getInstance(classOf[OrderStreamingProcessor])
      case "prod" | "production" =>
        Guice.createInjector(new ProductionModule(devEmails))
          .getInstance(classOf[OrderStreamingProcessor])
    }
    processor.execute()
  }
}
