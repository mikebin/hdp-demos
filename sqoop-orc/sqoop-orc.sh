#!/bin/bash

export HADOOP_CLASSPATH=$(hcat -classpath)

hive -e "drop table if exists sqoop_orc"

sqoop import \
  --verbose \
  --connect 'jdbc:mysql://localhost/hive' \
  --table TBLS \
  --username hive \
  --password hive \
  --create-hcatalog-table \
  --hcatalog-table sqoop_orc \
  --hcatalog-storage-stanza "stored as orc" \
  -m 1
