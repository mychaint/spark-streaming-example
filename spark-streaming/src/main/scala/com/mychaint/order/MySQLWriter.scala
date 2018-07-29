package com.mychaint.order

import java.sql.{Connection, DriverManager, Statement}
import java.text.SimpleDateFormat
import java.util.Calendar

import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import org.apache.spark.sql.{ForeachWriter, Row}

@Singleton
private[order] final class MySQLWriter @Inject()
(
  @Named("MYSQL HOST") private val MYSQL_HOST:String,
  @Named("MYSQL PORT") private val MYSQL_PORT:String,
  @Named("MYSQL DB") private val MYSQL_DB:String,
  @Named("MYSQL USER") private val MYSQL_USER:String,
  @Named("MYSQL PASSWORD") private val MYSQL_PASSWORD:String,
  @Named("MYSQL TABLE") private val MYSQL_TABLE:String
) extends ForeachWriter[Row] {

  @transient var connection: Option[Connection] = None

  lazy val dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

  lazy val calendar = Calendar.getInstance()

  private def getConnection(): Unit = {
    this.connection = {
      try {
        Some(DriverManager.getConnection(
          s"jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$MYSQL_DB",
          MYSQL_USER,
          MYSQL_PASSWORD
        ))
      } catch {
        case e: Throwable =>
          e.printStackTrace()
          throw e
      }
    }
  }

  override def open(partitionId: Long, version: Long) = true

  override def process(value: Row): Unit = {
    var isExceptional = false

    var statement: Statement = null
    try {
      if (this.connection == null || this.connection.isEmpty || this.connection.get.isClosed)
        this.getConnection()
      statement = this.connection.get.createStatement()
      val columns = value.schema.fieldNames.mkString(",")
      val values =
        0 until value.length map { i =>
          val v = value.get(i)
          try {
            this.calendar.setTime(
              dateFormatter.parse(v.toString)
            )
            this.calendar.getTime.getTime / 1000
          } catch {
            case _: Throwable =>
              v
          }
        } mkString(",")
      val query =
        s"""
          |REPLACE INTO $MYSQL_TABLE ($columns) values ($values)
        """.stripMargin
//      statement.execute(query)
    } catch {
      case e: Throwable =>
        e.printStackTrace
        isExceptional = true
    } finally {
      statement.close()
      if (isExceptional)
        this.connection.get.close()
    }
  }

  override def close(errorOrNull: Throwable): Unit = {}
}

