package com.mychaint.order

import java.io.FileOutputStream
import java.io.ObjectOutputStream

import com.google.inject.{Guice, Stage}
import com.mychaint.order.inject.TestModule
import org.scalatest.FunSuite

class SerializableTest extends FunSuite {

  test("test serializable mysql writer") {
    val mySQLWriter = Guice.createInjector(Stage.DEVELOPMENT, new TestModule(""))
      .getInstance(classOf[MySQLWriter])
    val fileOut =
      new FileOutputStream("/tmp/employee.ser");
    val out = new ObjectOutputStream(fileOut)
    out.writeObject(mySQLWriter)
    out.flush()
    out.close()
  }
}
