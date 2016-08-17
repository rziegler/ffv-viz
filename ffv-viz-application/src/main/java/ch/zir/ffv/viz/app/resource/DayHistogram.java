package ch.zir.ffv.viz.app.resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.zir.ffv.viz.app.jdbi.AggFlightRecord;

public class DayHistogram {

	public static Map<String, Integer> createDayHistogram(List<AggFlightRecord> records) {
		FullFlightFilter filter = new FullFlightFilter();
		List<FlightInformation> flights = filter.filterFlights(records);
		Map<String, Integer> result = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE");

		for (FlightInformation flight : flights) {
			for (Price price : flight.getPrices()) {
				if (price.getPrice() == flight.getMinPrice()) {
					String day = sdf.format(new Date(price.getRequest()));
					if (result.containsKey(day)) {
						result.put(day, result.get(day) + 1);
					} else {
						result.put(day, 1);
					}
				}
			}
		}
		return result;
	}

}
