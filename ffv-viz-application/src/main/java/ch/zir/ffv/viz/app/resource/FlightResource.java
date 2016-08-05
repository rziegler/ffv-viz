package ch.zir.ffv.viz.app.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import ch.zir.ffv.viz.app.jdbi.AggFlightRecord;
import ch.zir.ffv.viz.app.jdbi.DbAccess;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FlightResource {

	private static final int DELTA_DAYS = 49;
	private DbAccess store;

	public FlightResource(DbAccess store) {
		this.store = store;
	}

	@GET
	@Path("count/{carrier}")
	public long fetch(@PathParam("carrier") String carrier) {
		return store.countCarrier(carrier);
	}
	
	@GET
	@Path("destination")
	public List<String> fetchDestination() {
		return store.getDestinations();
	}

	@GET
	@Path("carrier")
	public List<String> fetchCarrier() {
		return store.getCarriers();
	}
	
	@GET
	@Path("destination/{destination}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public List<FlightInformation> fetchFlights(@PathParam("destination") String destination) {
		List<FlightInformation> result = processFlightsForDestination(destination);
		return result;
	}
	
	@GET
	@Path("minhist/{destination}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public int[] minhist(@PathParam("destination") String destination) {
		List<FlightInformation> flights = processFlightsForDestination(destination);
		int[] result = new int[DELTA_DAYS];
		
		for (FlightInformation fi : flights) {
			double min = fi.getMinPrice();
			for(int i = 0; i < DELTA_DAYS; i++){
				if(fi.getPrices()[i].getPrice() == min){
					result[i] = result[i]+1;
					break;
				}
			}
		}
		return result;	
	}
	

	private List<FlightInformation> processFlightsForDestination(String destination) {
		List<FlightInformation> result = new ArrayList<>();
		Map<String, FlightInformation> m = new HashMap<>();
		for (AggFlightRecord afr : store.getFlightsForDestination(destination, DELTA_DAYS)) {

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