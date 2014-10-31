spark-samples
=============

This project contains two simple examples of data processing with Apache Spark - a basic word counting application, and a simple application to find the top-N most frequent visits to the President from a White House visitor data set. The examples are written in both Java and Scala for comparison. Spark also has a Python API which is currently not demonstrated in this project.

The samples are currently hard-coded to expect a YARN execution environment, and use HDFS as the input source/output sink. Simple modifications/parameterization in this code would make it possible to run these samples locally or in other execution environments.

Running the Samples
-------------------

* Copy `constitution.txt` and `whitehouse_visits.txt` (contained in `whitehouse_visits.zip`) to your home directory in HDFS

* Modify the code as needed to reference your file locations in HDFS

* Run `./gradlew build` to compile and package the application jar. The build script creates a "fat jar", shading all depedendency libraries into a single application jar which can be submitted and executed on the cluster

* Use the `copyfiles.sh` script (or a similar variant) to copy the provided scripts and jar file to a cluster node containing the Spark framework

* Set the following environment variables if needed on the node where you plan to run the Spark YARN application (or pass these as parameters to the spark-submit script)

```
export YARN_CONF_DIR=/etc/hadoop/conf
export SPARK_JAR=/usr/lib/spark/lib/spark-assembly-1.0.1.2.1.3.0-563-hadoop2.4.0.2.1.3.0-563.jar
export SPARK_WORKER_MEMORY=512m
export SPARK_MASTER_MEMORY=512m
export MASTER=yarn-client
export SPARK_HOME=/usr/lib/spark
export PATH=$PATH:$SPARK_HOME/bin
```

* Run one of the following scripts (perhaps with minor changes, depending on your cluster configuration) to submit the process to the Spark cluster from the node used in step 4 above

| Sample | Command |
------- | -------- |
| Java Word Count |`spark-word-count.sh` |
| Java White House Visitor Analysis |`spark-potus.sh` | 
| Scala Word Count |`spark-scala-word-count.sh` |
| Scala White House Visitor Analysis |`spark-scala-potus.sh` | 
