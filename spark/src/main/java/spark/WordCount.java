package spark;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class WordCount {

  @SuppressWarnings("serial")
  public static void main(String... args) {
    SparkConf conf = new SparkConf();
    conf.setAppName("word-count");
    JavaSparkContext sc = new JavaSparkContext(conf);

    JavaRDD<String> file = sc
        .textFile(args.length > 1 ? args[1] : "hdfs:///user/root/constitution.txt");
    JavaRDD<String> words = file.flatMap(new FlatMapFunction<String, String>() {
      public Iterable<String> call(String s) {
        return Arrays.asList(s.split(" "));
      }
    });

    JavaPairRDD<String, Integer> pairs = words
        .mapToPair(new PairFunction<String, String, Integer>() {
          public Tuple2<String, Integer> call(String s) {
            return new Tuple2<String, Integer>(s, 1);
          }
        });

    JavaPairRDD<String, Integer> counts = pairs
        .reduceByKey(new Function2<Integer, Integer, Integer>() {
          @Override
          public Integer call(Integer i1, Integer i2) {
            return i1 + i2;
          }
        });

    counts.saveAsTextFile(args.length > 2 ? args[2] : "hdfs:///user/root/sparkwordcount");
  }
}