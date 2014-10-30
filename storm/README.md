storm-samples
=============

This project contains some simple examples for Apache Storm. In order to run the samples, you'll need to have access to at minimum a local Storm installation (with the storm CLI), and ideally a full Storm cluster. Examples can all be executed either in local mode, or in cluster mode. If executing an application on the cluster, you will need a local Storm configuration which points to the host and port of the Nimbus server.

Running the Samples
-------------------

1. Run `./gradlew build` to compile and package the application jar. Storm needs all dependencies in the application jar, so the build script creates a "far jar", shading all depedendent libraries.

2. Run one of the following commands to execute the process. If the `-local` option is specified, the topology will run in a single process on your local machine; otherwise, the topology will be submitted to the Nimbus for your Storm cluster, and execute as a distributed process.

| Sample | Command | Description |
------- | -------- | ----------- |
| Simple WordCount |`storm jar storm-app.jar storm.WordCount [-local]` | Simple non-relible Storm topology using canned text spout|
| Multilang WordCount |`storm jar storm-app.jar storm.WordCountMultilang [-local]` | Simple non-relible Storm topology using canned text spout and bolt written in Python.|
| HDFS Persistence |`storm jar storm-app.jar storm.HdfsPersistence [-local]` | Simple non-relible Storm topology using canned text spout, which persists data to HDFS. HDFS path/connection details are hardcoded in this class, so you will likely need to make changes and recompile for your environment. |
| Simple Trident WordCount |`storm jar storm-app.jar storm.TridentWordCount [-local]` | Simple Trident topology using canned fixed batch text spout. Also demonstrates the use of DRPC to execute a query against the in-memory state of the Trident topology.|
|Kafka/Trident WordCount |`storm jar storm-app.jar storm.TridentKafkaWordCount [-local]` | Trident topology using a transactional Kafka spout. This example has hardcoded ZK quorum locations and Kafka topic name, so some modifications may be needed for your environment. There is also a `SentencesProducer` class which can be used as a standalone program to load data into a Kafka topic before or while running the Storm topology, and some example scripts in `kafka-scripts/` which can be used to help create and view contents of a Kafka topic.|

For topologies submitted to the cluster, use the Storm UI to view metrics and logs for the executing topology. You can kill the topology from the Storm UI, or from the command line with `storm kill <topology name>`.
