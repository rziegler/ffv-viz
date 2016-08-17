package ch.zir.ffv.viz.app.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.zir.ffv.viz.app.jdbi.AggFlightRecord;

public class FullFlightFilter {
	
	public static final int DELTA_DAYS = 49;
	
	public List<FlightInformation> filterFlights(Collection<AggFlightRecord> records) {
		List<FlightInformation> result = new ArrayList<>();
		Map<String, FlightInformation> m = new HashMap<>();
		for (AggFlightRecord afr : records) {

			FlightInformation cf = m.get(afr.getId());
			if (cf == null) {
				cf = new FlightInformation(DELTA_DAYS);
				cf.setCarrier(afr.getCarrier());
				cf.setNumber(afr.getNumber());
				cf.setDestination(afr.getDestination());
				cf.setOrigin("ZHR");
				cf.setDeparture(afr.getDeparture().getTime());
				m.put(afr.getId(), cf);
			}
			cf.addPrice(afr.getPrice(), afr.getRequest().getTime(), afr.getDelta());

		}
		for (FlightInformation cf : m.values()) {
			cf.compute();
			if (cf.isComplete()) {
				result.add(cf);
			}
		}
		return result;
	}

}
