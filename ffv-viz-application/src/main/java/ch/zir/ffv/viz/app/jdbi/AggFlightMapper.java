package ch.zir.ffv.viz.app.jdbi;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class AggFlightMapper implements ResultSetMapper<AggFlightRecord> {
	
	
	public AggFlightRecord map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
		AggFlightRecord afr = new AggFlightRecord();
		afr.setCarrier(rs.getString("carrier"));
		afr.setNumber(rs.getString("number"));
		afr.setRequest(rs.getDate("request"));
		afr.setDeparture(rs.getDate("departure"));
		afr.setPrice(rs.getDouble("price"));
		afr.setDestination(rs.getString("destination"));
		afr.setDelta(rs.getInt("delta"));
		return afr;
	}
	
	
}