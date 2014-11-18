package spark

import org.apache.spark.{SparkConf, SparkContext}

object SparkSqlFromHive {

   case class SalaryData(gender: String, age: Int, salary: Double, zip: Int)

   def main(args: Array[String]) {
     val conf = new SparkConf().setAppName("SalaryData Spark SQL from Hive")
     val sc = new SparkContext(conf)
     val sqlContext = new org.apache.spark.sql.hive.HiveContext(sc)

     val genderBreakdown = sqlContext.sql("select gender, count(*) from salaries group by gender")
     genderBreakdown.collect().foreach(println)
   }

 }
