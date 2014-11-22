package spark

import org.apache.spark.{SparkConf, SparkContext}

object SparkSqlFromFile {

  case class SalaryData(gender: String, age: Int, salary: Double, zip: Int)

  def main(args: Array[String]) {
    val optionArgs = args.lift
    val conf = new SparkConf().setAppName("SalaryData Spark SQL from file")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    import sqlContext.createSchemaRDD

    val salaries = sc.textFile(optionArgs(1).getOrElse("hdfs:///user/root/salarydata.txt")).map(_.split(","))
      .map(s => SalaryData(s(0).trim, s(1).trim.toInt, s(2).trim.toDouble, s(3).trim.toInt))

    salaries.registerTempTable("salaries")

    val genderBreakdown = sqlContext.sql("select gender, count(*) from salaries group by gender")
    genderBreakdown.collect().foreach(println)
  }

}
