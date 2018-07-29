package com.mychaint.order

import java.sql.{Connection, DriverManager, Statement}

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

  var connection: Connection = null

  override def open(partitionId: Long, version: Long) = {
    this.connection = {
      val JDBC_DRIVER = "com.mysql.jdbc.Driver"
      val DB_URL = s"jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$MYSQL_DB"
      try {
        DriverManager.getConnection(DB_URL)
      } catch {
        case e: Throwable =>
          e.printStackTrace
          null
      }
    }
    true
  }

  override def process(value: Row): Unit = {
    val columns = value.schema.fieldNames.mkString(",")
    val values =
      0 until value.length map { i =>
        val v = value.get(i)
        v match {
          case Int => v.toString
          case Long => v.toString
          case _ => s"'${v.toString}'"
        }
      } mkString(",")
    var statement: Statement = null
    try {
      statement = this.connection.createStatement()
      val query =
        s"""
          |REPLACE INTO $MYSQL_TABLE ($columns) values ($values)
        """.stripMargin
//      statement.execute(query)
    } catch {
      case e: Throwable =>
        e.printStackTrace
    } finally {
      statement.close()
    }

  }

  override def close(errorOrNull: Throwable): Unit = {
    this.connection.close()
  }
}

