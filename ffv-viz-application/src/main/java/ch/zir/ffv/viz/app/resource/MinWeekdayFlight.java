package ch.zir.ffv.viz.app.resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import ch.zir.ffv.viz.app.jdbi.AggFlightRecord;

public class MinWeekdayFlight {
	
	public static String minWeekdayFlight(List<AggFlightRecord> records) {
		FullFlightFilter filter = new FullFlightFilter();
		List<FlightInformation> filtered = filter.filterFlights(records);
		Long minDate = filtered.stream().map(x->x.getDeparture()).min(Long::compareTo).get();
		int first = getDayInYear(new Date(minDate));
		
		Multimap<Integer, FlightInformation> groupedByWeek = ArrayListMultimap.create();
		for(FlightInformation fi:filtered){
			int dayInYear = getDayInYear(new Date(fi.getDeparture())) - first;
			int week = dayInYear / 7;
			groupedByWeek.put(week, fi);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("EEE");
		Map<String, Integer> result = new HashMap<>();
		for(Integer week:groupedByWeek.keySet()){
			String weekDay = "";
			double min = Double.MAX_VALUE;
			
			for(FlightInformation record:groupedByWeek.get(week)){
				if(record.getMinPrice() < min){
					min = record.getMinPrice();
					weekDay = sdf.format(new Date(record.getDeparture()));
				}
			}
			
			if (result.containsKey(weekDay)) {
				result.put(weekDay, result.get(weekDay) + 1);
			} else {
				result.put(weekDay, 1);
			}	
		}
		
		String weekDay = "";
		int count = 0;
		for(Entry<String, Integer> entry:result.entrySet()){
			if(entry.getValue() > count){
				weekDay = entry.getKey();
				count = entry.getValue();
			}
		}
		
		return weekDay;
	}

	private static int getDayInYear(Date minDate) {
		SimpleDateFormat dayInYear = new SimpleDateFormat("D");
		return Integer.valueOf(dayInYear.format(minDate));
	}
	
}
