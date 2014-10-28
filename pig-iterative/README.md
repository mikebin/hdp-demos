Iterative Pig 
========================

This demo executes a simple iterative process - K-Means Clustering - using embedded Pig. The driver program which manages the iterative execution is a simple Jython script, which internally invokes Pig Latin with Tez for each iteration. The Pig Latin in turn invokes a Java-based UDF to compute the nearest centroid for each data point.

Running the Demo
----------------

1. Run ```./gradlew build``` to compile and package the Java UDF.

2. Put the test data set - student.txt - into your home directory in HDFS

3. Run one of the following commands to execute the process:

Pig with MapReduce: ```PIG_OPTS="-Dpython.cachedir.skip=true" pig kmeans.py```

Pig with Tez: ```PIG_OPTS="-Dpython.cachedir.skip=true" pig -x tez kmeans.py```

Local Mode Pig: ```PIG_OPTS="-Dpython.cachedir.skip=true" pig -x local kmeans.py```

Local mode will by far be the fastest option with the small sample data set, but the interesting comparison is between Pig on MR and Pig on Tez. Expect to see Pig on Tez perform 3-5x faster than Pig on MR. The larger and more iterative the process, the more significant this performance boost will be, due to session/container re-use and in-memory caching performed by Tez.

Note: Pig on Tez requires Pig 0.14+, available in HDP 2.2+ 
