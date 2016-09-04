package ch.zir.ffv.viz.app.resource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ch.zir.ffv.viz.app.jdbi.DbAccess;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FlightResource {

	private static final String DELTA_DEFAULT = "49";

	private DbAccess store;
	private FullFlightFilter filter = new FullFlightFilter();
	private Cache<String, List<FlightInformation>> fiCache = CacheBuilder.newBuilder().maximumSize(512).concurrencyLevel(5).expireAfterAccess(5, TimeUnit.HOURS)
			.build();
	private Cache<String, Object> objCache = CacheBuilder.newBuilder().maximumSize(512).concurrencyLevel(5).expireAfterAccess(5, TimeUnit.HOURS).build();

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
		return (Long) objCache.get("count-" + cacheKey(carrier), () -> store.countCarrier(carrier));
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("destination")
	public List<String> fetchDestination() throws ExecutionException {
		return (List<String>) objCache.get(cacheKey("destinations"), () -> store.getDestinations());
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("carrier")
	public List<String> fetchCarrier() throws ExecutionException {
		return (List<String>) objCache.get(cacheKey("carriers"), () -> store.getCarriers());
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("carrier/{destination}")
	public List<String> fetchCarrier(@PathParam("destination") String destination) throws ExecutionException {
		return (List<String>) objCache.get("carrier-" + cacheKey(destination), () -> store.getCarriers(destination));
	}

	@GET
	@Path("destination/{destination}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public List<FlightInformation> fetchFlights(@PathParam("destination") String destination, @QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) {
		return getFlightInformation(destination, delta);
	}

	@GET
	@Path("destination/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public List<FlightInformation> fetchFlights(@PathParam("destination") String destination, @PathParam("carrier") String carrier,
			@QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) {
		return getFlightInformation(destination, carrier, delta);
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("flights/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public List<UIFlight> fetchFlightsUi(@PathParam("destination") String destination, @PathParam("carrier") String carrier,
			@QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) throws ExecutionException {
		return (List<UIFlight>) objCache.get("fetchFlightsUi" + cacheKey(destination, carrier, delta), () -> {
			UIConverter converter = new UIConverter();
			return converter.convert(getFlightInformation(destination, carrier, delta));
		});
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("flights/{destination}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public List<UIFlight> fetchFlightsUi(@PathParam("destination") String destination, @QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta)
			throws ExecutionException {
		return (List<UIFlight>) objCache.get("fetchFlightsUi" + cacheKey(destination, delta), () -> {
			UIConverter converter = new UIConverter();
			return converter.convert(getFlightInformation(destination, delta));
		});
	}

	@GET
	@Path("minhist/{destination}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public int[] minhist(@PathParam("destination") String destination, @QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) {
		return DeltaHistogram.createHistogram(getFlightInformation(destination, delta), delta);
	}

	@GET
	@Path("minhist/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public int[] minhist(@PathParam("destination") String destination, @PathParam("carrier") String carrier,
			@QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) {
		return DeltaHistogram.createHistogram(getFlightInformation(destination, carrier, delta), delta);
	}

	@GET
	@Path("min/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Statistic minPrice(@PathParam("destination") String destination, @PathParam("carrier") String carrier,
			@QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) {
		return MinPriceDelta.minPrice(getFlightInformation(destination, carrier, delta), delta);
	}

	@GET
	@Path("min/{destination}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Statistic minPrice(@PathParam("destination") String destination, @QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) {
		return MinPriceDelta.minPrice(getFlightInformation(destination, delta), delta);
	}

	@GET
	@Path("dayHistogramBook/{destination}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Map<String, Integer> dayHistogramBook(@PathParam("destination") String destination, @QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) {
		return DayHistogram.createDayHistogram(getFlightInformation(destination, delta));
	}

	@GET
	@Path("dayHistogramBook/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Map<String, Integer> dayHistogramBook(@PathParam("destination") String destination, @PathParam("carrier") String carrier,
			@QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) {
		return DayHistogram.createDayHistogram(getFlightInformation(destination, carrier, delta));
	}

	@GET
	@Path("minWeekdayBook/{destination}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Statistic cheapestWeekDayBook(@PathParam("destination") String destination, @QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) {
		return MinWeekdayBook.minWeekdayBook(getFlightInformation(destination, delta), delta);
	}

	@GET
	@Path("minWeekdayBook/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Statistic cheapestWeekDayBook(@PathParam("destination") String destination, @PathParam("carrier") String carrier,
			@QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) {
		return MinWeekdayBook.minWeekdayBook(getFlightInformation(destination, carrier, delta), delta);
	}

	@GET
	@Path("minWeekdayFlight/{destination}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Statistic cheapestWeekDayFlight(@PathParam("destination") String destination, @QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) {
		return MinWeekdayFlight.minWeekdayFlight(getFlightInformation(destination, delta));
	}

	@GET
	@Path("minWeekdayFlight/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Statistic cheapestWeekDayFlight(@PathParam("destination") String destination, @PathParam("carrier") String carrier,
			@QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) {
		return MinWeekdayFlight.minWeekdayFlight(getFlightInformation(destination, carrier, delta));
	}
	
	@GET
	@Path("savings/{destination}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Saving savings(@PathParam("destination") String destination, @QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) {
		return Savings.compute(getFlightInformation(destination, delta));
	}

	@GET
	@Path("savings/{destination}/{carrier}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Saving savings(@PathParam("destination") String destination, @PathParam("carrier") String carrier,
			@QueryParam("delta") @DefaultValue(DELTA_DEFAULT) int delta) {
		return Savings.compute(getFlightInformation(destination, carrier, delta));
	}

	private List<FlightInformation> getFlightInformation(String destination, String carrier, int delta) {
		try {
			return fiCache.get(cacheKey(destination, carrier, delta), () -> filter.filterFlights(store.getFlights(destination, carrier, delta), delta));
		} catch (ExecutionException e) {
			throw new RuntimeException("Could not update Cache.", e);
		}
	}

	private List<FlightInformation> getFlightInformation(String destination, int delta) {
		try {
			return fiCache.get(cacheKey(destination, delta), () -> filter.filterFlights(store.getFlights(destination, delta), delta));
		} catch (ExecutionException e) {
			throw new RuntimeException("Could not update Cache.", e);
		}
	}

	private String cacheKey(String destination, String carrier, int delta) {
		return (destination + "#" + carrier + "#" + delta).toLowerCase();
	}

	private String cacheKey(String destination, int delta) {
		return cacheKey(destination, "all", delta);
	}
	
	private String cacheKey(String destination) {
		return cacheKey(destination, "all", 0);
	}

}