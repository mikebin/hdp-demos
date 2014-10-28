import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;


public class FindCentroid extends EvalFunc<Double> {
    double[] centroids;
    public FindCentroid(String initialCentroid) {
        String[] centroidStrings = initialCentroid.split(":");
        centroids = new double[centroidStrings.length];
        for (int i=0;i<centroidStrings.length;i++)
            centroids[i] = Double.parseDouble(centroidStrings[i]);
    }
    @Override
    public Double exec(Tuple input) throws IOException {
        double min_distance = Double.MAX_VALUE;
        double closest_centroid = 0;
        for (double centroid : centroids) {
            double distance = Math.abs(centroid - (Double)input.get(0));
            if (distance < min_distance) {
                min_distance = distance;
                closest_centroid = centroid;
            }
        }
        return closest_centroid;
    }

}

