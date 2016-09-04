package ch.zir.ffv.viz.app.jdbi;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(AggFlightMapper.class)
public interface DbAccess {

	@SqlQuery("select count(*) from ffv.aggflights f where f.carrier = :carrier")
	long countCarrier(@Bind("carrier") String carrier);
	
	@SqlQuery("select distinct destination from ffv.aggflights")
	List<String> getDestinations();
	
	@SqlQuery("select distinct carrier from ffv.aggflights")
	List<String> getCarriers();
	
	@SqlQuery("select distinct carrier from ffv.aggflights f where f.destination = :destination")
	List<String> getCarriers(@Bind("destination") String destination);
	
	@SqlQuery("select * from ffv.aggflights f where f.destination = :destination and f.delta <= :delta")
	List<AggFlightRecord> getFlights(@Bind("destination") String destination, @Bind("delta") int delta);
	
	@SqlQuery("select * from ffv.aggflights f where f.destination = :destination and f.carrier = :carrier and f.delta <= :delta")
	List<AggFlightRecord> getFlights(@Bind("destination") String destination, @Bind("carrier") String carrier, @Bind("delta") int delta);
	

}
