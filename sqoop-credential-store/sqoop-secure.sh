#!/bin/bash

export HADOOP_CLASSPATH=$(hcat -classpath)
HIVE_HOME=/usr/hdp/current/hive-client
HCAT_HOME=/usr/hdp/current/hive-webhcat
HADOOP_HOME=/usr/hdp/current/hadoop-client
export LIB_JARS=$HCAT_HOME/share/hcatalog/hive-hcatalog-core.jar,$HIVE_HOME/lib/hive-metastore.jar,$HIVE_HOME/lib/libthrift-0.9.0.jar,$HIVE_HOME/lib/hive-exec.jar,$HIVE_HOME/lib/libfb303-0.9.0.jar,$HIVE_HOME/lib/jdo-api-3.0.1.jar,$HADOOP_HOME/lib/slf4j-api-1.7.5.jar,$HIVE_HOME/lib/hive-cli.jar

hive -e "drop table if exists sqoop_credentials_test"

sqoop import \
  -Dhadoop.security.credential.provider.path=jceks://hdfs/user/root/credentials/credentials.jceks \
  -libjars $LIB_JARS \
  --verbose \
  --connect 'jdbc:mysql://hd-poc-02.unix.gsm1900.org/hive' \
  --table TBLS \
  --username hive \
  --password-alias mysql.password \
  --create-hcatalog-table \
  --hcatalog-table sqoop_credentials_test \
  --hcatalog-storage-stanza "stored as orc" \
  -m 1
