Hive Transactions Demo
======================

This demo will show how to use the new ACID features in Hive 0.14+ to perform row-level operations. Note that this functionality is not intended to be used as a low-latency replacement for traditional OLTP databases - it's focused more on making corrections and minor updates to OLAP data stored in a Hive table, without having to rebuild entire partitions.

This demo requires a Hadoop environment with Hive 0.14+, such as HDP 2.2+.

Configuration
-------------

To enable ACID in Hive, ensure the following configuration settings in `hive-site.xml` and restart the HiveServer2 and Hive Metastore services:

|Property Name|Value|
|-------------|-----|
|hive.txn.manager|org.apache.hadoop.hive.ql.lockmgr.DbTxnManager|
|hive.compactor.initiator.on|true|
|hive.compactor.worker.threads|1|
|hive.support.concurrency|true|
|hive.enforce.bucketing|true|
|hive.exec.dynamic.partition.mode|true|

See https://cwiki.apache.org/confluence/display/Hive/Hive+Transactions for more details on other optional settings.

Ensure that `hadoop.proxyuser.hive.groups` and `hadoop.proxyuser.hive.hosts` properties are set accordingly in `core-site.xml` to allow the `hive` user to impersonate other users when compacting delta files.

Using ACID Features in Hive
---------------------------

Execute the following steps to demonstrate the ACID features in Hive:

- Load sample data into HDFS:

  ```hadoop fs -mkdir salarydata```
  
  ```hadoop fs -put salarydata.txt salarydata```

- Open Beeline (preferred) or the Hive shell:

  ```beeline --silent -u "jdbc:hive2://127.0.0.1:10000/default;auth=noSasl" -n root -p '' -hiveconf "hive.execution.engine=tez"```

  Change the hostname, port, username, and/or password as needed for your environment.
  
- Create an external table over the data loaded in the previous step:

  ```
  create external table salaries_stage (gender char(1), age int, salary int, zip int)
  row format delimited fields terminated by ','
  location '/user/root/salarydata';
  ```
 
  Change the data location as appropriate for your environment.
 
  Verify the data in the table by running a simple query:
  
  ```select * from salaries_stage limit 10;```

- Create a transactional, bucketed ORC table and bulk load the data staged in the previous step:

  ```
  create table salaries (gender char(1), age int, salary int, zip int)
  clustered by (age) into 4 buckets
  stored as orc
  tblproperties('transactional'='true');
  ```
  
  ```insert into table salaries select * from salaries_stage;```

  Verify the data in the table by running a simple query:

  ```select * from salaries limit 10;```

- Execute various row level DML operations against the transactional table:

  ```set hive.execution.engine=tez;```

  ```insert into table salaries values ('M', 40, 90000, 98390), ('F', 35, 50000, 98390);```

  ```select * from salaries where zip = 98390;```

  ```update salaries set salary = salary + 10000 where zip = 98390;```

  ```select * from salaries where zip = 98390;```

  ```delete from salaries where zip = 98390 and gender = 'F';```

  ```select * from salaries where zip = 98390;```

- View the HDFS filesystem for the table:

  ```dfs -ls -R /apps/hive/warehouse/salaries;```

  Notice the delta file directories

- Force a compaction to occur

  Hive will automatically perform minor and major compactions of the delta files periodically based on size and/or time constraints. In this step, we're going to force Hive to perform a major compaction, and observe the results.

  ```alter table salaries compact 'major';```

  ```show compactions```

  When the compaction is finished, view the HDFS directory for the table and notice that the data has been consolidated, eliminating the small delta file(s):

  ```dfs -ls -R /apps/hive/warehouse/salaries;```
  
  
