Using Flume
===========

This example demonstrates the use of Flume to ingest a simple dataset into Hadoop. It makes use of the new Hive sink type to stream data directly into a bucketed ORC-format table.

The provided scripts are designed to work with HDP 2.2, but they should also work for HDP 2.1 with minor adjustments to paths in `start-flume.sh` and `flume-env.sh`.

This demo assumes that you're running on a cluster node which has Flume and Hive installed. HDP 2.2 provides an option to install Flume with Ambari. In HDP 2.1, you will need to install Flume separately through the package manager.

Running the Demo
----------------

- Create the Hive table
 
  Review the `salaries.sql` script, which creates a bucketed Hive table in ORC format, currently required for streaming data ingest. Execute the script to create the table:

  ```
  hive -f salaries.sql
  ```

- Review the Flume configuration

  Have a look at the included `flume.conf` file. It configures the following components:
  - Spooling file source: reads files from the `./spool` folder, and steams their contents to the channel as lines of text
  - Memory channel: stores events (lines of text from the spooled file(s)) in memory
  - Hive sink: streams event data into the `salaries` table. Assumes that each line of text is comma-delimited with 4 fields: gender, age, salary, zip

- Review `flume-env.sh`

  `flume-env.sh` sets environment variables pointing to the home directories for Hive and HCatalog. This is required for Flume to add the necessary jar files to the `CLASSPATH` when using the Hive sink.
  
- Start the Flume agent

  ```
  ./start-flume.sh
  ```

  Note: the current version of Flume in HDP 2.2 (1.5.x) prints a ton of `INFO` log messages for the spooling file source. This is expected until the fix in https://issues.apache.org/jira/browse/FLUME-2385.

- Copy a file to the spool directory

  In a different terminal window:

  ```
  cp salarydata.txt spool/salarydata1.txt
  ```

  You should see a bunch of logging in the other window from the Flume agent as it processes the file. After it's done, the file in `./spool` should be renamed to have a `.COMPLETED` suffix.

- Confirm that data has been inserted into the Hive table

  Run a query against the Hive table to confirm that it was updated:

  ```
  select count(*) from salaries;

  select * from salaries limit 10;
  ```

  You should see 10000 rows in the table after the streaming ingest is complete.

  **Note**: if you look in `/apps/hive/warehouse/salaries`, you'll see many delta subfolders. By default, Hive is not configured to automatically run *compaction* to combine these smaller transaction batches into larger data files. You can enable this by running the `enable-transactions.sh` script from the Ambari server node to modify the Hive configuration and restart Hive components. See https://github.com/mikebin/hdp-demos/tree/master/hive-transactions for more details.

