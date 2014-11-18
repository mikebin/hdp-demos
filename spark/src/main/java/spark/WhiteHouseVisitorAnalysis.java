package spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.List;

public class WhiteHouseVisitorAnalysis {

  @SuppressWarnings("serial")
  public static void main(String... args) {
    SparkConf conf = new SparkConf();
    conf.setAppName("wh-visitor-analysis");
    JavaSparkContext sc = new JavaSparkContext(conf);

    JavaRDD<String> file = sc
        .textFile(args[1] != null ? args[1] : "hdfs://namenode:8020/user/root/whitehouse_visits.txt");

    // Filter in only records containing POTUS in the comments field
    JavaRDD<String> potus = file.filter(new Function<String, Boolean>() {
      @Override
      public Boolean call(String line) throws Exception {
        String fields[] = line.split(",");
        return fields.length >= 20 && fields[19].contains("POTUS");
      }
    });

    // Project out (name, 1) tuples from the POTUS visitor records
    JavaPairRDD<String, Integer> projection = potus
        .mapToPair(new PairFunction<String, String, Integer>() {
          @Override
          public Tuple2<String, Integer> call(String line) throws Exception {
            String[] fields = line.split(",");
            return new Tuple2<String, Integer>(fields[0] + ", " + fields[1], 1);
          }
        });

    // Aggregate visitor counts by key (name)
    JavaPairRDD<String, Integer> counts = projection
        .reduceByKey(new Function2<Integer, Integer, Integer>() {
          @Override
          public Integer call(Integer v1, Integer v2) throws Exception {
            return v1 + v2;
          }
        });

    // Retrieve the top 50 visitor counts by name as the final result
    List<Tuple2<String, Integer>> result = counts.takeOrdered(50,
        new SerializableComparator<Tuple2<String, Integer>>() {
          @Override
          public int compare(Tuple2<String, Integer> lhs,
                             Tuple2<String, Integer> rhs) {
            return lhs._2().compareTo(rhs._2()) * -1;
          }
        });

    // Output the results
    for (Tuple2<String, Integer> record : result) {
      System.out.format("%-35s%d\n", record._1(), record._2());
    }

  }
}