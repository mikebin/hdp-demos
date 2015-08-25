package spark

import kafka.serializer.StringDecoder
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._

case class Audit(date: String, time: String, ip: String, command: String)

object KafkaLogAnalysis {
  def main(args: Array[String]) {
    val optionArgs = args.lift
    val conf = new SparkConf().setAppName("Log Analysis with Spark Streaming and Kafka")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(2))
    val regex = """(\d\d\d\d-\d\d-\d\d)\s+(\d\d:\d\d:\d\d).*ip=(\S+).*cmd=(\S+).*""".r

    val topicsSet = Set[String](optionArgs(1).getOrElse("hdfs-audit"))
    val kafkaParams = Map[String, String]("metadata.broker.list" -> optionArgs(2).getOrElse("sandbox:6667"))
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)

    val filtered = messages.filter { case (key, message) => {
      regex.findFirstIn(message) match {
        case Some(m) => true
        case None => false
      }
    }
    }

    val commands = filtered.map { case (key, message) => {
      val matched = regex.findFirstMatchIn(message).get
      Audit(matched.group(1), matched.group(2),
        matched.group(3), matched.group(4))
    }
    }

    commands.window(Seconds(20)).foreachRDD { (rdd, time) => {
      val sqlContext = SQLContext.getOrCreate(rdd.sparkContext)
      import sqlContext.implicits._
      rdd.toDF().registerTempTable("commands")
      println(s"========= $time =========")
      sqlContext.sql("select command, count(*) as invocation_count from commands group by command").show()
    }
    }

    ssc.start()
    ssc.awaitTermination()
  }
}
