package udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Description(name = "tminus3hours",
    value = "_FUNC_(str) - Subtract 3 hours from a timestamp in MM-dd-yyyy-HH:mm:ss date and reformat to yyyy-MM-dd HH:mm:ss format ",
    extended = "Example:\n  > SELECT _FUNC_('05-18-2014-09:24:00') FROM tab a;\n  2014-05-18 06:24:00")
public class TimeMinusThreeHours extends UDF {
  private static final String DEFAULT_VALUE = "2014-01-01 00:00:00";

  public String evaluate(String s) {
    if (s != null) {
      try {
        DateTimeFormatter inputFormat =
            DateTimeFormat.forPattern("MM-dd-yyyy-HH:mm:ss");

        DateTimeFormatter formatOutput =
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        DateTime dt = inputFormat.parseDateTime(s);
        Long timestamp = Long.valueOf(dt.getMillis() - 10800000L);
        return formatOutput.print(timestamp.longValue());
      }
      catch (Exception e) {
        return DEFAULT_VALUE;
      }
    }
    else {
      return DEFAULT_VALUE;
    }
  }

  public static void main(String[] args) {
    String result = new TimeMinusThreeHours().evaluate("05-18-2014-09:24:00");
    System.out.println(result);
  }
}