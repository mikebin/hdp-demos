#!/bin/bash

/usr/hdp/current/flume-server/bin/flume-ng agent -n agent -c $PWD -f flume.conf
