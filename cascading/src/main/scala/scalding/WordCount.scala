package scalding

import java.io.StringReader

import com.twitter.scalding._
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.en.EnglishAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute

import scala.collection.mutable.ArrayBuffer

object WordCount {
  val analyzer = new EnglishAnalyzer
}

class WordCount(args: Args) extends Job(args) {

  TextLine(args("input"))
    .flatMap('line -> 'word) { line: String => analyzeLine(line, WordCount.analyzer) }
    .groupBy('word) {
    _.size
  }
    .write(Tsv(args("output")))

  override def config: Map[AnyRef, AnyRef] = {
    val config = super.config
    if (args.boolean("tez")) {
      config ++ Map("cascading.flow.runtime.gather.partitions.num" -> "4",
        "tez.lib.uris" -> "hdfs:///hdp/apps/2.2.0.0-2041/tez/tez.tar.gz",
        "cascading.app.appjar.class" -> this.getClass())
    } else {
      config ++ Map("cascading.app.appjar.class" -> this.getClass())
    }
  }

  override def run: Boolean = {
    val flow = buildFlow
    flow.complete
    true
  }

  def analyzeLine(line: String, analyzer: Analyzer) = {
    val result = new ArrayBuffer[String]
    val tStream = analyzer.tokenStream("contents", new StringReader(line))
    val term: CharTermAttribute = tStream.addAttribute(classOf[CharTermAttribute])

    tStream.reset
    while (tStream.incrementToken) {
      result += term.toString
    }
    tStream.close

    result
  }

}

