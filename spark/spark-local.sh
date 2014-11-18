#!/bin/bash

if [[ -z $1 ]]; then
  echo "Usage: $0 className <additional args>"
  exit -1
fi

spark-submit --class $1 --master local[4] spark-app.jar $@
