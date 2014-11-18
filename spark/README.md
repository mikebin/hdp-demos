Spark Overview
==============

This project contains a few simple examples of data processing with Apache Spark:

- basic word counting application
- simple application to find the top-N most frequent visits to the President from a White House visitor data set
- Spark SQL from a text file
- Spark SQL from a Hive table

The examples are written in both Java and Scala for comparison (except for the Spark SQL examples, which are Scala-only). Spark also has a Python API which is currently not demonstrated in this project.

A couple of simple scripts are provided to help launch the applications either in local execution mode (`spark-local.sh`) or distributed on YARN (`spark-yarn.sh`). These examples require Spark 1.1.0+, and have been tested with the Spark 1.1.0 Technical Preview on HDP 2.1.

Running the Samples
-------------------

* Run `./gradlew build` to compile and package the application jar. The build script creates a "fat jar" called `spark-app.jar`, shading any necessary non-provided depedendency libraries into a single application jar which can be submitted and executed on the cluster

* For running samples in local execution mode, install Spark locally. On OSX, a simple way to install Spark with with Brew: `brew install spark`

* For running the samples on YARN, set the following environment variables if needed on the node where you plan to run the Spark YARN application:

  ```
  export YARN_CONF_DIR=/etc/hadoop/conf
  export SPARK_JAR=/usr/lib/spark/lib/spark-assembly-1.1.0.2.1.5.0-702-hadoop2.4.0.2.1.5.0-695.jar
  export SPARK_HOME=/usr/lib/spark
  export PATH=$PATH:$SPARK_HOME/bin
  ```
  You will also need `spark-app.jar`, `spark-yarn.sh`, and any necessary input file(s) available on the node where you plan to launch the Spark on YARN application.
  
  
* Run one of the following examples:

| Sample | Local Command | YARN command | Prerequisites
------- | -------- | ----------- | -------------|
| Java Word Count | `./spark-local.sh spark.WordCount constitution.txt out` | `./spark-yarn.sh spark.WordCount hdfs:///<dir>/constitution.txt hdfs:///<dir>/out` | For YARN, copy `constitution.txt` to `<dir>` in HDFS first |
| Java White House Visitor Analysis | `spark-local.sh spark.WhiteHouseVisitorAnalysis whitehouse_visits.txt` | `spark-yarn.sh spark.WhiteHouseVisitorAnalysis hdfs:///<dir>/whitehouse_visits.txt` | Unzip `whitehouse_visits.zip`. For YARN, copy `whitehouse_visits.txt` to `<dir>` in HDFS first |
| Scala Word Count | `./spark-local.sh spark.ScalaWordCount constitution.txt out` | `./spark-yarn.sh spark.ScalaWordCount hdfs:///<dir>/constitution.txt hdfs:///<dir>/out` | For YARN, copy `constitution.txt` to `<dir>` in HDFS first |
| Scala White House Visitor Analysis | `spark-local.sh spark.ScalaWhiteHouseVisitorAnalysis whitehouse_visits.txt` | `spark-yarn.sh spark.ScalaWhiteHouseVisitorAnalysis hdfs:///<dir>/whitehouse_visits.txt` | Unzip `whitehouse_visits.zip`. For YARN, copy `whitehouse_visits.txt` to `<dir>` in HDFS first |
| Spark SQL from a text file | `./spark-local.sh spark.SparkSqlFromFile salarydata.txt` | `./spark-yarn.sh spark.SparkSqlFromFile hdfs:///<dir>/salarydata.txt` | For YARN, copy `salarydata.txt` to `<dir>` in HDFS first |
| Spark SQL from a Hive table | N/A | `./spark-yarn.sh spark.SparkSqlFromHive` | Run `hive -f salaries.sql` to create and load the `salaries` table in Hive first |
