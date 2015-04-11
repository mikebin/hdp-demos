#!/bin/bash

export HADOOP_CLASSPATH=$(hcat -classpath)

hive -e "drop table if exists sqoop_credentials_test"

sqoop import \
  -Dhadoop.security.credential.provider.path=jceks://hdfs/user/root/credentials/credentials.jceks \
  --verbose \
  --connect 'jdbc:mysql://localhost/hive' \
  --table TBLS \
  --username hive \
  --password-alias mysql.password \
  --create-hcatalog-table \
  --hcatalog-table sqoop_credentials_test \
  --hcatalog-storage-stanza "stored as orc" \
  -m 1
