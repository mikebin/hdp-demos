Importing into ORC Hive tables with Sqoop
============================================

Sqoop's HCatalog integration supports importing RDBMS data directly into Hive tables stored in ORC format. 

A key requirement to using HCatalog integration with Sqoop is setting the CLASSPATH appropriately. Fortunately, HCatalog provides a simple command-line tool which can help with this:

```
export HADOOP_CLASSPATH=$(hcat -classpath)
```

The `sqoop-orc.sh` script included in this project contains the full set of Sqoop options required to import data into Hive ORC tables. The example imports the Hive TBLS table from the MySQL Hive metastore DB, which should exist in most HDP environments.
