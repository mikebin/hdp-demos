Phoenix Demo
=============

This demo shows how to use Apache Phoenix to support interactive SQL with HBase. Phoenix can be used with any version of HDP >= 2.1, but the instructions here are written for HDP 2.2+ and Phoenix 4.2+. 

Installation
------------

To install Phoenix in HDP 2.2, run the following command on any node(s) in your cluster where you want the Phoenix client to be installed:

```
sudo yum -y install phoenix
```

Configuration
-------------

Phoenix is primarily a client-side tool, but it does require some coprocessors to be installed in the HBase RegionServer(s), and some minor modifications to the HBase configuration (`hbase-site.xml`).

- Link the Phoenix coprocessors into the HBase RegionServer classpath. Run the following command on all RegionServer hosts:

  ```
  ln -sf `ls /usr/hdp/current/phoenix-client/lib/phoenix-core*jar | \
  grep -vi sources | grep -vi tests` /usr/hdp/current/hbase-regionserver/lib/phoenix.jar 
  ```
  
  This assumes that Phoenix is installed on all RegionServer hosts. If that's not the case, you'll need to distribute the jar file in a different way.

- Modify `/etc/hbase/conf/hbase-site.xml` on all HBase cluster nodes for Phoenix. Specifically, the following two properties must be set as shown:

  ```
  <property>
    <name>hbase.defaults.for.version.skip</name>
    <value>true</value>
  </property>
  <property>
    <name>hbase.regionserver.wal.codec</name>
    <value>org.apache.hadoop.hbase.regionserver.wal.IndexedWALEditCodec</value>
  </property> 
  ```

- Restart all RegionServers to apply the changes made above.

The HDP documentation has more detail on installing and configuring Phoenix, including information on how to configure Phoenix in a secure cluster.

Running Phoenix SQLLine
-----------------------

Phoenix provides a command line shell called SQLLine to connect to HBase and execute SQL queries. You can run this command as follows:

```
/usr/hdp/current/phoenix-client/bin/sqlline.py <zk_node>:/hbase-unsecure
```

This assumes you're running a non-secure HBase cluster. Replace `<zk_node>` above with the hostname and port of a Zookeeper quorum node used by your HBase cluster. For example:

```
/usr/hdp/current/phoenix-client/bin/sqlline.py zk1:2181:/hbase-unsecure
``` 

If all goes well, you should see a SQLLine shell prompt appear after a few moments:

```
14/11/12 03:28:35 WARN impl.MetricsConfig: Cannot locate configuration: tried hadoop-metrics2-phoenix.properties,hadoop-metrics2.properties
Connected to: Phoenix (version 4.2)
Driver: PhoenixEmbeddedDriver (version 4.2)
Autocommit status: true
Transaction isolation: TRANSACTION_READ_COMMITTED
Building list of tables and columns for tab-completion (set fastconnect to true to skip)...
68/68 (100%) Done
Done
sqlline version 1.1.2
0: jdbc:phoenix:namenode:2181:/hbase-unsecure> !tables
+------------+-------------+------------+------------+------------+------------+
| TABLE_CAT  | TABLE_SCHEM | TABLE_NAME | TABLE_TYPE |  REMARKS   | TYPE_NAME  |
+------------+-------------+------------+------------+------------+------------+
| null       | SYSTEM      | CATALOG    | SYSTEM TABLE | null       | null     |
| null       | SYSTEM      | SEQUENCE   | SYSTEM TABLE | null       | null     |
| null       | SYSTEM      | STATS      | SYSTEM TABLE | null       | null     |
+------------+-------------+------------+------------+------------+------------+
0: jdbc:phoenix:namenode:2181:/hbase-unsecure> 
```

From here, you can enter SQL DDL and/or DML commands to manipulate HBase tables. SQLLine also provides various shell-specific commands - for a complete list, run the `!help` command from the shell prompt. For example, as shown above, the `!tables` command shows a list of all Phoenix tables in HBase. The tables above are system tables automatically created and managed by Phoenix.

Demo
------

Let's create a table, load some data, and execute some SQL queries against the data in Phoenix. For this demo, start up a SQLLine session connected to your HBase cluster.

- Create a table called `salaries`

  ```
  create table salaries (id integer not null primary key, gender char(1), 
    age integer, salary integer, zip integer);
  ```

  Notice, just like with a traditional database, we define columns with datatypes, and a primary key for the table. The primary key column(s) map to the rowkey in HBase.

- Create a sequence called `salaries_sequence` 

  ```
  create sequence salaries_sequence; 
  ```

  Sequences can be useful for generating unique primary keys for tables. Oracle RDBMS has a similar concept. We will use this sequence to populate the primary key for our `salaries` table.

- Insert some data into the `salaries` table

  ```
  upsert into salaries values (next value for salaries_sequence, 'M', 23, 50000, 60616);
  upsert into salaries values (next value for salaries_sequence, 'M', 43, 100000, 98390);
  upsert into salaries values (next value for salaries_sequence, 'F', 50, 125000, 98390);
  upsert into salaries values (next value for salaries_sequence, 'F', 29, 70000, 60616);
  upsert into salaries values (next value for salaries_sequence, 'M', 18, 10000, 60616);
  ```

  Notice the use of the `upsert` statement here. HBase does not distinguish between and insert and an update. We can use the same type of statement to update a record. For example, execute the following:

  ```
  upsert into salaries values (4, 'F', 28, 70100, 60616);
  ```

  In this case, we used an explicit key value in the statement to force an update to an existing HBase table row, instead of generating a new sequence number (which would have resulted in an insert).

  Run a quick query to view the data you just inserted/updated:

  ```
  select * from salaries;
  ```

  You should see 5 total records.


