Hive Permanent Functions Demo
=============================

This demo will show how to create a permanent function in Hive, a new feature available starting in Hive 0.13. Permanent functions register UDF jar files (and their dependencies) in the Hive metastore DB, making it easier to share UDF implementations.

This demo creates a simple date-based function which converts a timestamp in one format to another, and also subtracts 3 hours from the time. It has a dependency on the Joda Time library, which is packaged in the UDF jar (resulting in a "fat jar" distribution).

This demo assumes you're using Hive 0.14+, which is available in HDP 2.2+. Certain issues exist with classloading of dependent jars from fat jars in Hive 0.13.

To run the demo, perform the following steps:

- From the project directory, run `./gradlew build` to compile and package the fat jar for the UDF. The jar file will be created in the current directory with the name `time-udf.jar`.

- Connect to Hive using the Hive CLI or Beeline. We'll show Beeline here, as it is the preferred Hive client: 

  ```beeline --silent -u "jdbc:hive2://127.0.0.1:10000/default;auth=noSasl" -n root -p ''```

  Change the hostname, database name, and userid/password as appropriate for your environment.

- Copy the UDF jar to a folder in HDFS. For example: 

  ```hadoop fs -put time-udf.jar /user/root/udfs```

- Register the new permanent function: 

  ```create function tminus3hours as 'udf.TimeMinusThreeHours' using jar 'hdfs:///user/root/udfs/time-udf.jar';```

  Change the path to the UDF accordingly.

- Describe the function: 

  ```describe function extended tminus3hours;```

- Execute the function 

  ```select tminus3hours('05-18-2014-09:24:00');```

- Create another Hive session (using Beeline or the Hive CLI) from another terminal, and verify that you are still able to see and execute the function.
