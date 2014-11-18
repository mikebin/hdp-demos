#!/bin/bash

if [[ -z $1 ]]; then
  echo "Usage: $0 className"
  exit -1
fi

/usr/lib/spark/bin/spark-submit --class $1 --master yarn-client --num-executors 3 --driver-memory 512m --executor-memory 512m --executor-cores 1 spark-app.jar 
