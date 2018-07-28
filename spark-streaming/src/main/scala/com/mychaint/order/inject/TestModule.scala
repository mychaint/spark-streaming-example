package com.mychaint.order.inject

import com.google.inject.AbstractModule

private[order] final class TestModule(devEmails: String) extends AbstractModule {

  override def configure(): Unit = {
    install(new DefaultModule(devEmails))
  }
}
