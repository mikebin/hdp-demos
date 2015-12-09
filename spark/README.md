Spark Overview
==============

This project contains a few simple examples of data processing with Apache Spark:

- basic word counting application
- simple application to find the top-N most frequent visits to the President from a White House visitor data set
- Spark SQL from a text file
- Spark SQL from a Hive table
- Spark Streaming with Kafka

The examples are written in Scala. Spark also has Java, Python, and R APIs which are currently not demonstrated in this project.

A couple of simple scripts are provided to help launch the applications either in local execution mode (`spark-local.sh`) or distributed on YARN (`spark-yarn.sh`). These examples are designed to run on Spark 1.3.1+, and have been tested with Spark on HDP 2.3.

Running the Samples
-------------------

* Run `./gradlew build` to compile and package the application jar. The build script creates a "fat jar" called `spark-app.jar`, shading any necessary non-provided depedendency libraries into a single application jar which can be submitted and executed on the cluster

* For running samples in local execution mode, install Spark locally. On OSX, a simple way to install Spark is with Homebrew: `brew install apache-spark`

* For running the samples on YARN, you will need `spark-app.jar`, `spark-yarn.sh`, and any necessary input file(s) available on the node where you plan to launch the Spark on YARN application.
  
* Run one of the following examples:

| Sample | Local Command | YARN command | Prerequisites
------- | -------- | ----------- | -------------|
| Word Count | `./spark-local.sh spark.WordCount constitution.txt out` | `./spark-yarn.sh spark.WordCount hdfs:///<dir>/constitution.txt hdfs:///<dir>/out` | For YARN, copy `constitution.txt` to `<dir>` in HDFS |
| White House Visitor Analysis | `spark-local.sh spark.WhiteHouseVisitorAnalysis whitehouse_visits.txt` | `spark-yarn.sh spark.WhiteHouseVisitorAnalysis hdfs:///<dir>/whitehouse_visits.txt` | Unzip `whitehouse_visits.zip`. For YARN, copy `whitehouse_visits.txt` to `<dir>` in HDFS |
| Spark SQL from a text file | `./spark-local.sh spark.SparkSqlFromFile salarydata.txt` | `./spark-yarn.sh spark.SparkSqlFromFile hdfs:///<dir>/salarydata.txt` | For YARN, copy `salarydata.txt` to `<dir>` in HDFS |
| Spark SQL from a Hive table | N/A | `./spark-yarn.sh spark.SparkSqlFromHive` | Copy `/etc/hive/conf/hive-site.xml` to `$SPARK_HOME/conf`. Run `hive -f salaries.sql` to create and load the `salaries` table in Hive. Make sure your $SPARK_HOME/conf directory contains `hive-site.xml`.|
| Spark Streaming from Kafka | `./spark-yarn.sh spark.KafkaLogAnalysis` |`./spark-local.sh spark.KafkaLogAnalysis` | Assumes default Kafka configuration (broker port, etc.) as installed by Ambari. Create topic named 'hdfs-audit' in Kafka. Publish records from /var/log/hadoop/hdfs/hdfs-audit.log to the topic, using kafka-console-producer.sh |
