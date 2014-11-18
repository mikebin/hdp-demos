package spark

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext._

object ScalaWhiteHouseVisitorAnalysis {
  def main(args: Array[String]) {
    val optionArgs = args.lift
    val conf = new SparkConf().setAppName("Scala wh-visitor-analysis")
    val sc = new SparkContext(conf)
    val file = sc.textFile(optionArgs(1).getOrElse("hdfs:///user/root/whitehouse_visits.txt"))

    // Filter in records containing POTUS in the comments field
    val potus = file.filter { line =>
      val fields = line.split(",")
      fields.length > 20 && fields(19).contains("POTUS")
    }

    // Project out (name, 1) tuples from the POTUS visitor records
    val projection = potus.map { line =>
      val fields = line.split(",")
      ((fields(0) + ", " + fields(1)), 1)
    }

    // Aggregate visitor counts by key (name)
    val counts = projection.reduceByKey(_ + _)

    // Retrieve the top 50 visitor counts by name as the final result
    val result = counts.takeOrdered(50)(new Ordering[(String, Int)] {
      override def compare(x: (String, Int), y: (String, Int)) = { -x._2.compareTo(y._2) }
    });

    // Print results
    for ((name, count) <- result) {
      println("%-35s%d".format(name, count))
    }

  }
}