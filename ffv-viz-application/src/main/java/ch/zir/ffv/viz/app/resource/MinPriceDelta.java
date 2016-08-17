package ch.zir.ffv.viz.app.resource;

import java.util.List;

import ch.zir.ffv.viz.app.jdbi.AggFlightRecord;

public class MinPriceDelta {
	
	public static int minPrice(List<AggFlightRecord> records) {
		FullFlightFilter filter = new FullFlightFilter();
		List<FlightInformation> flights = filter.filterFlights(records);
		int[] histogram = DeltaHistogram.createHistogram(flights);
		return minDelta(histogram);
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
