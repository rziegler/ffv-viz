package ch.zir.ffv.viz.app.resource;

import java.util.List;

public class DeltaHistogram {

	public static int[] createHistogram(List<FlightInformation> flights, int delta) {
		int[] result = new int[delta];

		for (FlightInformation fi : flights) {
			double min = fi.getMinPrice();
			for (int i = 0; i < delta; i++) {
				if (fi.getPrices()[i].getPrice() == min) {
					result[i] = result[i] + 1;
					break;
				}
			}
		}
		return result;
	}

}
