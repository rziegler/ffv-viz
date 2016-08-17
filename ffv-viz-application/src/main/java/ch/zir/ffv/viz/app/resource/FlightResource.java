package ch.zir.ffv.viz.app.resource;

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

	private DbAccess store;
	private FullFlightFilter filter = new FullFlightFilter();

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
	@Path("carrier/{destination}")
	public List<String> fetchCarrier(@PathParam("destination") String destination) {
		return store.getCarriers(destination);
	}
	
	@GET
	@Path("destination/{destination}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public List<FlightInformation> fetchFlights(@PathParam("destination") String destination) {
		return filter.filterFlights(store.getFlightsForDestination(destination, FullFlightFilter.DELTA_DAYS));
	}

	@GET
	@Path("destination/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public List<FlightInformation> fetchFlights(@PathParam("destination") String destination, @PathParam("destination") String carrier) {
		return filter.filterFlights(store.getFlightsForDestinationAndCarrier(destination, carrier, FullFlightFilter.DELTA_DAYS));
	}

	@GET
	@Path("minhist/{destination}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public int[] minhist(@PathParam("destination") String destination) {
		List<FlightInformation> flights = filter.filterFlights(store.getFlightsForDestination(destination, FullFlightFilter.DELTA_DAYS));
		return DeltaHistogram.createHistogram(flights);
	}

	@GET
	@Path("minhist/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public int[] minhist(@PathParam("destination") String destination, @PathParam("carrier") String carrier) {
		List<FlightInformation> flights = filter.filterFlights(store.getFlightsForDestinationAndCarrier(destination, carrier, FullFlightFilter.DELTA_DAYS));
		return DeltaHistogram.createHistogram(flights);
	}

	@GET
	@Path("min/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public int minPrice(@PathParam("destination") String destination, @PathParam("carrier") String carrier) {
		List<AggFlightRecord> flightsForDestinationAndCarrier = store.getFlightsForDestinationAndCarrier(destination, carrier, FullFlightFilter.DELTA_DAYS);
		return MinPriceDelta.minPrice(flightsForDestinationAndCarrier);
	}

	@GET
	@Path("min/{destination}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public int minPrice(@PathParam("destination") String destination) {
		List<AggFlightRecord> flightsForDestinationAndCarrier = store.getFlightsForDestination(destination, FullFlightFilter.DELTA_DAYS);
		return MinPriceDelta.minPrice(flightsForDestinationAndCarrier);
	}
	
	@GET
	@Path("dayHistogramBook/{destination}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Map<String, Integer> dayHistogramBook(@PathParam("destination") String destination) {
		List<AggFlightRecord> records = store.getFlightsForDestination(destination, FullFlightFilter.DELTA_DAYS);
		return DayHistogram.createDayHistogram(records);	
	}

	@GET
	@Path("dayHistogramBook/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Map<String, Integer> dayHistogramBook(@PathParam("destination") String destination, @PathParam("carrier") String carrier) {
		List<AggFlightRecord> records = store.getFlightsForDestinationAndCarrier(destination, carrier,FullFlightFilter.DELTA_DAYS);
		return DayHistogram.createDayHistogram(records);	
	}
	
	@GET
	@Path("minWeekdayBook/{destination}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public String cheapestWeekDay(@PathParam("destination") String destination) {
		List<AggFlightRecord> records = store.getFlightsForDestination(destination, FullFlightFilter.DELTA_DAYS);
		return MinWeekdayBook.minWeekdayBook(records);
	}

	@GET
	@Path("minWeekdayBook/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public String cheapestWeekDay(@PathParam("destination") String destination, @PathParam("carrier") String carrier) {
		List<AggFlightRecord> records = store.getFlightsForDestinationAndCarrier(destination, carrier, FullFlightFilter.DELTA_DAYS);
		return MinWeekdayBook.minWeekdayBook(records);
	}
	
	@GET
	@Path("minWeekdayFlight/{destination}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public String cheapestWeekDayFlight(@PathParam("destination") String destination) {
		List<AggFlightRecord> records = store.getFlightsForDestination(destination, FullFlightFilter.DELTA_DAYS);
		return MinWeekdayFlight.minWeekdayFlight(records);
	}

	@GET
	@Path("minWeekdayFlight/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public String cheapestWeekDayFlight(@PathParam("destination") String destination, @PathParam("carrier") String carrier) {
		List<AggFlightRecord> records = store.getFlightsForDestinationAndCarrier(destination, carrier, FullFlightFilter.DELTA_DAYS);
		return MinWeekdayFlight.minWeekdayFlight(records);
	}
	

}