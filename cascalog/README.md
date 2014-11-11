Cascalog Demo
==============

This project provides some simple examples of Cascalog, a Clojure-based framework which builds on Cascading. Following is a summary of available samples:

|Name|Description/Notes|
|----|-----------------|
|Word Count|Borrows some example WordCount code from the excellent *Cascalog for the Impatient* series, which provides much more content here: https://github.com/Cascading/Impatient-Cascalog/wiki. Some minor enhancements made to support the latest version of Cascalog, Cascading, and Hadoop 2.x. Also implements a custom function to parse lines of text using a Lucene Analyzer.|

Compiling/Packaging Samples
------------------------------

To compile the Clojure code, a Leiningen build script is provided for each sample. You will need to install Leiningen on your local machine if you don't already have it: http://leiningen.org/. Once Leiningen is available on your local machine, `cd` to the directory containing the demo you want to run, and then run `lein uberjar`. This will create a fat jar file containing all application dependencies in the `target` subfolder. You can run this jar file locally or on the cluster with the `hadoop jar` command. In addition, you can run `lein repl` and experiment with the code and functions interactively.

