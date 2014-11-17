#!/bin/bash

cd /tmp

/var/lib/ambari-server/resources/scripts/configs.sh set localhost hadoop-pig-hive hive-site hive.txn.manager org.apache.hadoop.hive.ql.lockmgr.DbTxnManager
/var/lib/ambari-server/resources/scripts/configs.sh set localhost hadoop-pig-hive hive-site hive.compactor.initiator.on true
/var/lib/ambari-server/resources/scripts/configs.sh set localhost hadoop-pig-hive hive-site hive.compactor.worker.threads 1
/var/lib/ambari-server/resources/scripts/configs.sh set localhost hadoop-pig-hive hive-site hive.support.concurrency true
/var/lib/ambari-server/resources/scripts/configs.sh set localhost hadoop-pig-hive hive-site hive.enforce.bucketing true
/var/lib/ambari-server/resources/scripts/configs.sh set localhost hadoop-pig-hive hive-site hive.exec.dynamic.partition.mode nonstrict
curl -u admin:admin -i -H 'X-Requested-By: ambari' -X PUT -d '{"RequestInfo": {"context" :"Stop Hive via REST"}, "Body": {"ServiceInfo": {"state": "INSTALLED"}}}' http://localhost:8080/api/v1/clusters/hadoop-pig-hive/services/HIVE
curl -u admin:admin -i -H 'X-Requested-By: ambari' -X PUT -d '{"RequestInfo": {"context" :"Start Hive via REST"}, "Body": {"ServiceInfo": {"state": "STARTED"}}}' http://localhost:8080/api/v1/clusters/hadoop-pig-hive/services/HIVE

