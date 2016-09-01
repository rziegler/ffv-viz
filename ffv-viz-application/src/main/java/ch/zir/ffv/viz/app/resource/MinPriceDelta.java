package ch.zir.ffv.viz.app.resource;

import java.util.Arrays;
import java.util.List;

public class MinPriceDelta {
	
	public static Statistic minPrice(List<FlightInformation> flights, int delta) {
		int[] histogram = DeltaHistogram.createHistogram(flights, delta);
		int minDelta = minDelta(histogram);
		int sum = Arrays.stream(histogram).sum();

		Statistic result = new Statistic();
		result.setValue(Integer.toString( minDelta));
		result.setPropability((double)histogram[minDelta-1]/(double)sum);
		return result;
	}

	private static int minDelta(int[] histogram) {
		int delta = 0;
		int max = 0;
		for (int i = 0; i < histogram.length; i++) {
			if (histogram[i] > max) {
				delta = i;
				max = histogram[i];
			}

		}
		return delta+1;
	}

}
