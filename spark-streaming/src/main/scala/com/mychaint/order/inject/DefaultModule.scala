package com.mychaint.order.inject

import com.google.inject.AbstractModule
import com.google.inject.name.Names

private[order] final class DefaultModule(devEmails: String) extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[String])
      .annotatedWith(Names.named("MYSQL PORT"))
      .toInstance("3306")
    bind(classOf[String])
      .annotatedWith(Names.named("MYSQL DB"))
      .toInstance("mychaint")
    bind(classOf[String])
      .annotatedWith(Names.named("MYSQL USER"))
      .toInstance("")
    bind(classOf[String])
      .annotatedWith(Names.named("MYSQL PASSWORD"))
      .toInstance("")
    bind(classOf[String])
      .annotatedWith(Names.named("MYSQL TABLE"))
      .toInstance("agg_order_minute_tab")
  }
}
