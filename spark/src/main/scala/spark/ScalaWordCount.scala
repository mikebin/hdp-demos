package spark

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext._

object ScalaWordCount {
  def main(args: Array[String]) {
    val optionArgs = args.lift
    val conf = new SparkConf().setAppName("Scala Word Count")
    val sc = new SparkContext(conf)
    val file = sc.textFile(optionArgs(1).getOrElse("hdfs:///user/root/constitution.txt"))
    val counts = file.flatMap(line => line.split(" "))
      .map(word => (word, 1))
      .reduceByKey(_ + _)
    counts.cache()
    counts.saveAsTextFile(optionArgs(2).getOrElse("hdfs:///user/root/scala-wc-out"))
  }
}