package fr.polytechnique.cmap.cnam

import java.io.File
import java.util.{Locale, TimeZone}
import org.apache.commons.io.FileUtils
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.scalatest._

abstract class SharedContext extends FlatSpecLike with BeforeAndAfterAll with BeforeAndAfterEach {
    self: Suite =>

  Logger.getRootLogger.setLevel(Level.WARN)
  Logger.getLogger("org").setLevel(Level.WARN)
  Logger.getLogger("akka").setLevel(Level.WARN)
  Logger.getLogger("/executors").setLevel(Level.FATAL)

  Locale.setDefault(Locale.US)
  TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

  private var _spark: SparkSession = _

  protected def spark: SparkSession = _spark
  protected def sqlContext: SQLContext = _spark.sqlContext
  protected def sc = _spark.sparkContext

  protected override def beforeAll(): Unit = {
    if (_spark == null) {
      _spark = SparkSession
        .builder()
        .appName("Tests")
        .master("local[4]")
        .config("spark.sql.testkey", "true")
        .getOrCreate()
    }
    // Ensure we have initialized the context before calling parent code
    super.beforeAll()
  }

    override def beforeEach(): Unit = {
      FileUtils.deleteDirectory(new File("target/test/output"))
      super.beforeEach()
    }

    override def afterAll() {
      try {
        if (_spark != null) {
          _spark.stop()
          _spark = null
        }
        FileUtils.deleteDirectory(new File("target/test/output"))
      } finally {
        super.afterAll()
      }
    }
}