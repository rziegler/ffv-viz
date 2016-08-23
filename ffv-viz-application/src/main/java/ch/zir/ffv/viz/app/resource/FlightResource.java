package ch.zir.ffv.viz.app.resource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ch.zir.ffv.viz.app.jdbi.DbAccess;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FlightResource {

	private DbAccess store;
	private FullFlightFilter filter = new FullFlightFilter();
	private Cache<String, List<FlightInformation>> fiCache = CacheBuilder.newBuilder().maximumSize(512).concurrencyLevel(5).expireAfterAccess(5, TimeUnit.HOURS)
			.build();
	private Cache<String, Object> objCache = CacheBuilder.newBuilder().maximumSize(512).concurrencyLevel(5).expireAfterAccess(5, TimeUnit.HOURS)
			.build();
	public FlightResource(DbAccess store) {
		this.store = store;
	}

	/**
	 * Counts the number of flights for a carrier.
	 * 
	 * @param carrier
	 *            the carrier
	 * @return
	 * @throws ExecutionException 
	 */
	@GET
	@Path("count/{carrier}")
	public long fetch(@PathParam("carrier") String carrier) throws ExecutionException {
		return (Long)objCache.get("count-"+carrier, () -> store.countCarrier(carrier));
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("destination")
	public List<String> fetchDestination() throws ExecutionException {
		return (List<String>)objCache.get("destinations", () -> store.getDestinations());
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("carrier")
	public List<String> fetchCarrier() throws ExecutionException {
		return (List<String>)objCache.get("carriers", () -> store.getCarriers());
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("carrier/{destination}")
	public List<String> fetchCarrier(@PathParam("destination") String destination) throws ExecutionException { 
		return (List<String>)objCache.get("carrier-"+destination, ()->store.getCarriers(destination));
	}

	@GET
	@Path("destination/{destination}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public List<FlightInformation> fetchFlights(@PathParam("destination") String destination) {
		return getFlightInformation(destination);
	}

	@GET
	@Path("destination/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public List<FlightInformation> fetchFlights(@PathParam("destination") String destination, @PathParam("carrier") String carrier) {
		return getFlightInformation(destination, carrier);
	}

	@GET
	@Path("flights/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public List<UIFlight> fetchFlightsUi(@PathParam("destination") String destination, @PathParam("carrier") String carrier) {
		UIConverter converter = new UIConverter();
		return converter.convert(getFlightInformation(destination, carrier));
	}

	@GET
	@Path("flights/{destination}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public List<UIFlight> fetchFlightsUi(@PathParam("destination") String destination) {
		UIConverter converter = new UIConverter();
		return converter.convert(getFlightInformation(destination));
	}

	@GET
	@Path("minhist/{destination}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public int[] minhist(@PathParam("destination") String destination) {
		return DeltaHistogram.createHistogram(getFlightInformation(destination));
	}

	@GET
	@Path("minhist/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public int[] minhist(@PathParam("destination") String destination, @PathParam("carrier") String carrier) {
		return DeltaHistogram.createHistogram(getFlightInformation(destination, carrier));
	}

	@GET
	@Path("min/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Statistic minPrice(@PathParam("destination") String destination, @PathParam("carrier") String carrier) {
		return MinPriceDelta.minPrice(getFlightInformation(destination, carrier));
	}

	@GET
	@Path("min/{destination}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Statistic minPrice(@PathParam("destination") String destination) {
		return MinPriceDelta.minPrice(getFlightInformation(destination));
	}

	@GET
	@Path("dayHistogramBook/{destination}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Map<String, Integer> dayHistogramBook(@PathParam("destination") String destination) {
		return DayHistogram.createDayHistogram(getFlightInformation(destination));
	}

	@GET
	@Path("dayHistogramBook/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Map<String, Integer> dayHistogramBook(@PathParam("destination") String destination, @PathParam("carrier") String carrier) {
		return DayHistogram.createDayHistogram(getFlightInformation(destination, carrier));
	}

	@GET
	@Path("minWeekdayBook/{destination}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Statistic cheapestWeekDayBook(@PathParam("destination") String destination) {
		return MinWeekdayBook.minWeekdayBook(getFlightInformation(destination));
	}

	@GET
	@Path("minWeekdayBook/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Statistic cheapestWeekDayBook(@PathParam("destination") String destination, @PathParam("carrier") String carrier) {
		return MinWeekdayBook.minWeekdayBook(getFlightInformation(destination, carrier));
	}

	@GET
	@Path("minWeekdayFlight/{destination}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Statistic cheapestWeekDayFlight(@PathParam("destination") String destination) {
		return MinWeekdayFlight.minWeekdayFlight(getFlightInformation(destination));
	}

	@GET
	@Path("minWeekdayFlight/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Statistic cheapestWeekDayFlight(@PathParam("destination") String destination, @PathParam("carrier") String carrier) {
		return MinWeekdayFlight.minWeekdayFlight(getFlightInformation(destination, carrier));
	}

	private List<FlightInformation> getFlightInformation(String destination, String carrier) {
		try {
			return fiCache.get(cacheKey(destination, carrier), () -> filter.filterFlights(store.getFlights(destination, carrier, FullFlightFilter.DELTA_DAYS)));
		} catch (ExecutionException e) {
			throw new RuntimeException("Could not update Cache.", e);
		}
	}

	private List<FlightInformation> getFlightInformation(String destination) {
		try {
			return fiCache.get(cacheKey(destination), () -> filter.filterFlights(store.getFlights(destination, FullFlightFilter.DELTA_DAYS)));
		} catch (ExecutionException e) {
			throw new RuntimeException("Could not update Cache.", e);
		}
	}

	private String cacheKey(String destination, String carrier) {
		return destination + "#" + carrier;
	}

	private String cacheKey(String destination) {
		return destination;
	}

}