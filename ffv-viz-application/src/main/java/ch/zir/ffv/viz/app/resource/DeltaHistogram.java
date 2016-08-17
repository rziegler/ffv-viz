package ch.zir.ffv.viz.app.resource;

import java.util.List;

public class DeltaHistogram {
	
	public static int[] createHistogram(List<FlightInformation> flights) {
		int[] result = new int[FullFlightFilter.DELTA_DAYS];

		for (FlightInformation fi : flights) {
			double min = fi.getMinPrice();
			for (int i = 0; i < FullFlightFilter.DELTA_DAYS; i++) {
				if (fi.getPrices()[i].getPrice() == min) {
					result[i] = result[i] + 1;
					break;
				}
			}
		}
		return result;
	}

}
