Using the Hadoop Credential Store with Sqoop
============================================

Hadoop 2.6.0 adds support for a general-purpose credential store: https://issues.apache.org/jira/browse/HADOOP-10607. Sqoop 1.4.5+ in HDP 2.2+ supports using a credential store to provide database passwords. This demo will walk through a simple example of creating a credential store, then using it in a Sqoop import job.

Creating a Credential Store
---------------------------

Use the following command-line utility to create a new credential store in HDFS, or add a new credential to an existing store:

```
hadoop credential create mysql.password -provider jceks://hdfs/user/root/credentials/credentials.jceks
```

Modify the HDFS filesystem path accordingly in this command for your environment. You will be prompted to enter the password you want to store twice. The example Sqoop job below imports a table from the Hive metastore DB, so you can enter the hive DB password here. 

The stored credential can subsequently be accessed by applications or tools like Sqoop using the *alias* name you provided, which in this case is `mysql.password`.

After running this command, you should see a new credential file in HDFS:

```
hadoop fs -ls /user/root/credentials
```

Notice the permissions are set to only allow access by the owner (700). If you try to cat the content of this file, you'll see that it's binary encoded - your credentials are not human-readable:

```
hadoop fs -cat credentials/credentials.jceks
```

You can view a list of credential aliases in the credential store by running the `hadoop credential list` command:

```
hadoop credential list -provider jceks://hdfs/user/root/credentials/credential.jceks
```

Using the Credential Store in a Sqoop job
-----------------------------------------

Let's run a simple Sqoop import job using the credential store created in the previous step. View the contents of the file called sqoop-secure.sh in this project, and modify the file system paths and database details as needed for your environment. The example script simply imports one of the Hive metastore DB tables into a new Hive table. The parts of the `sqoop import` command which are relevant to using the credential store are:

```
-Dhadoop.security.credential.provider.path=jceks://hdfs/user/root/credentials/credentials.jceks
```
This tells Sqoop where to find the credential store in HDFS.

```
--password-alias mysql.password
```
This tells Sqoop which credential alias in the credential store contains the database password to use.

After making any needed modifications, run the script:

```
./sqoop-secure.sh
```

The import should complete successfully. We can verify the result by running a Hive command against the new table:

```
hive -e "select * from sqoop_credentials_test limit 10" 
```

