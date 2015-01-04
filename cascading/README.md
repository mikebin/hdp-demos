**To run Cascading jobs:**

```
./gradlew build
hadoop jar cascade.jar <classname> <additional opts>
```

If you run `hadoop jar` without the <additional opts>, the available/required options will be shown.

To run the StockAnalyzer, load the data by running `load-data.sh`. Then, set `-stocks cstocks -dividends cdividends` command line options.

By default, Cascading jobs run on the cluster using MapReduce.

These samples are configured to run on HDP 2.2, which includes Tez 0.5.0 (required by Cascading when executing with Tez). It's possible to run these examples on older versions of HDP, but for Tez support you will need to manually install Tez 0.5.0. Depending on your installation, you may need to modify the `tez.lib.uris` setting in the source code to point to the HDFS installation path of the Tez package. The out-of-the-box setting should work for default HDP 2.2 installations.

Use the `--tez` CLI option to execute the Cascading job with Tez.

To run any job locally, use the -local CLI option.

**To run Scalding jobs:**

```
gradle build
./run-scalding.sh <job class> --local | --hdfs [ --tez] --input <input path> --output <output path>
```

The `--tez` option is only valid together with `--hdfs` (cluster execution).
