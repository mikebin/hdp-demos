HBase and Hadoop
================

These demos will walk through a few simple examples of accessing/manipulating HBase data with traditional Hadoop tools such as Sqoop, Pig, and Hive.

The instructions are written for HDP 2.2+ (HBase 0.98.4+, Pig 0.14.0+, Hive 0.14.0+). You may be able to run these demos on older versions, but you'll likely be required to perform more manual steps to setup classpaths for Sqoop, Pig, and Hive, and use MR instead of Tez.

To make this demo simpler, run all the steps from a node which has all of the following client tools installed:

- Pig
- Hive
- Sqoop
- HBase

Create an HBase Table
---------------------

For this demo, we will create a single HBase table called `salaries`. 

- Open the HBase Shell

  ```
  hbase shell
  ```

- Create a table `salaries` with one column family `cf1`.

  ```
  create 'salaries', 'cf1'
  ```

- Keep this HBase Shell session open in a terminal window for later

Load Data into the HBase Table with Sqoop
-----------------------------------------

In this step, we're going to import some data from a RDBMS into the `salaries` table in HBase. We'll use MySQL, since it's most likely already available in your cluster as the Hive metastore DB. Let's create a table and load it up with some data. We've provided a script and sample data for you to use.

- Create and load a new table in MySQL - `test.salaries`. Review the `salaries.sql` file provided, then run the following command:

  ```
  mysql < salaries.sql
  ``` 

  Verify that the data loaded successfully in MySQL:

  ```
  [root@sandbox ~]# mysql -u root
  mysql> use test;
  Database changed
  mysql> select * from salaries limit 10;
  +----+--------+------+--------+---------+
  | id | gender | age  | salary | zipcode |
  +----+--------+------+--------+---------+
  |  1 | M      |   57 |  39000 |   95050 |
  |  2 | F      |   63 |  41000 |   95102 |
  |  3 | M      |   55 |  99000 |   94040 |
  |  4 | M      |   51 |  58000 |   95102 |
  |  5 | M      |   75 |  43000 |   95101 |
  |  6 | M      |   94 |  11000 |   95051 |
  |  7 | M      |   28 |   6000 |   94041 |
  |  8 | M      |   14 |      0 |   95102 |
  |  9 | M      |    3 |      0 |   95101 |
  | 10 | M      |   25 |  26000 |   94040 |
  +----+--------+------+--------+---------+
  10 rows in set (0.04 sec)

  mysql> quit;
  ```

- Run a Sqoop import job to load the data from MySQL into the HBase `salaries` table.

  ```
   sqoop import --connect jdbc:mysql://localhost/test --username root \
   --table salaries --hbase-table salaries --column-family cf1 --hbase-row-key id -m 1
  ``` 

- After the Sqoop import completes, go back to your HBase Shell session, and run the following command to verify the data in the `salaries` table:

  ```
  scan 'salaries', {LIMIT=>5}
  ```

  You should see output that looks something like this:

  ```
  ROW                   COLUMN+CELL                                               
   1                    column=cf1:age, timestamp=1415930019486, value=57         
   1                    column=cf1:gender, timestamp=1415930019486, value=M       
   1                    column=cf1:salary, timestamp=1415930019486, value=39000.0 
   1                    column=cf1:zipcode, timestamp=1415930019486, value=95050  
   10                   column=cf1:age, timestamp=1415930019486, value=25         
   10                   column=cf1:gender, timestamp=1415930019486, value=M       
   10                   column=cf1:salary, timestamp=1415930019486, value=26000.0 
   10                   column=cf1:zipcode, timestamp=1415930019486, value=94040  
   100                  column=cf1:age, timestamp=1415930019486, value=99         
   100                  column=cf1:gender, timestamp=1415930019486, value=M       
   100                  column=cf1:salary, timestamp=1415930019486, value=71000.0 
   100                  column=cf1:zipcode, timestamp=1415930019486, value=95105  
   1000                 column=cf1:age, timestamp=1415930019486, value=4          
   1000                 column=cf1:gender, timestamp=1415930019486, value=M       
   1000                 column=cf1:salary, timestamp=1415930019486, value=0.0     
   1000                 column=cf1:zipcode, timestamp=1415930019486, value=95102  
   10000                column=cf1:age, timestamp=1415930020647, value=82         
   10000                column=cf1:gender, timestamp=1415930020647, value=F       
   10000                column=cf1:salary, timestamp=1415930020647, value=5000.0  
   10000                column=cf1:zipcode, timestamp=1415930020647, value=95050  
  5 row(s) in 0.5550 seconds
  ```

Manipulate HBase data with Pig
------------------------------

Next, let's perform some simple ETL and derive a new column for the `salaries` table using Pig. We've provided you a simple script containing code which:

- Loads data from the `salaries` HBase table using Pig's `HBaseStorage`.

- Groups the salary data by `zipcode`

- Computes the average age for each `zipcode`

- Computes the mean age difference for each record in the salaries table

- Stores the newly computed mean age differential as a new column in the `salaries` HBase table

Review the code in `compute-age-diff.pig`, then execute it on the cluster with the Tez execution engine as follows:

```
pig -x tez compute-age-diff.pig
```

After a few moments, the process should complete. Go back to your HBase Shell window, and run the same scan operation as before. You should see a new column `meanAgeDiffForZip` in column family `cf1` for each row.

Create a Hive table around the HBase table
------------------------------------------

Now that we've completed the ETL on our `salaries` table in HBase using Pig, we'd like to be able to run ad-hoc analytical SQL queries against the data. One option would be to use Apache Phoenix, which provides a direct, interactive SQL query API for HBase. However, Phoenix is more suited for record-level operations. Hive, on the other hand, allows us to leverage the distributed resources of the cluster when executing SQL queries, which might provide higher throughput when running large scale OLAP-style queries against the data.

So let's create a Hive table which maps to the HBase table. Provided for you is a script - `salaries.hive` - which creates the Hive table, then runs a simple SQL query using the Tez execution engine. Review the statements in this script:

```
create external table salaries (id int, gender char(1), age int, 
                                salary double, zipcode int, meanAgeDiffForZip int) 
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' 
WITH SERDEPROPERTIES ('hbase.columns.mapping' = 
                      ':key,cf1:gender,cf1:age,cf1:salary,cf1:zipcode,cf1:meanAgeDiffForZip') 
TBLPROPERTIES ('hbase.table.name' = 'salaries');
```

This creates a Hive table called `salaries` which maps to a table with the same name in HBase. Notice the Hive to HBase column mappings in the DDL.

```
set hive.execution.engine=tez;
select gender, zipcode, avg(salary) from salaries group by gender, zipcode;
```

After setting the execution engine to Tez (MR is the default in Hive 0.14), we run a simple grouping/aggregate computation across all the data in the HBase table, computing average salary by `zipcode` and `gender`. 

Let's run the Hive script:

```
hive -f salaries.hive
``` 

After a few moments, you should see the results:

```
F	94040	39256.68449197861
F	94041	40428.77906976744
F	95050	37236.541598694945
F	95051	39652.03761755486
F	95101	39744.186046511626
F	95102	38480.53892215569
F	95103	38330.63209076175
F	95105	39221.28378378379
M	94040	37796.208530805685
M	94041	38752.50836120401
M	95050	38629.80030721966
M	95051	37920.792079207924
M	95101	38306.990881458965
M	95102	38759.13621262459
M	95103	37517.79935275081
M	95105	40642.1568627451
```
