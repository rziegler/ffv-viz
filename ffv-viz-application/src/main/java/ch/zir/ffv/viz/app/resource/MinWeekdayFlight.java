package ch.zir.ffv.viz.app.resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class MinWeekdayFlight {
	
	public static Statistic minWeekdayFlight(List<FlightInformation> filtered) {
		
		Long minDate = filtered.stream().map(x->x.getDeparture()).min(Long::compareTo).get();
		int first = getDayInYear(new Date(minDate));
		
		Multimap<Integer, FlightInformation> groupedByWeek = ArrayListMultimap.create();
		for(FlightInformation fi:filtered){
			int dayInYear = getDayInYear(new Date(fi.getDeparture())) - first;
			int week = dayInYear / 7;
			groupedByWeek.put(week, fi);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("EEE");
		Map<String, Integer> histogram = new HashMap<>();
		for(Integer week:groupedByWeek.keySet()){
			String weekDay = "";
			double min = Double.MAX_VALUE;
			
			for(FlightInformation record:groupedByWeek.get(week)){
				if(record.getMinPrice() < min){
					min = record.getMinPrice();
					weekDay = sdf.format(new Date(record.getDeparture()));
				}
			}
			
			if (histogram.containsKey(weekDay)) {
				histogram.put(weekDay, histogram.get(weekDay) + 1);
			} else {
				histogram.put(weekDay, 1);
			}	
		}
		
		String weekDay = "";
		int count = 0;
		for(Entry<String, Integer> entry:histogram.entrySet()){
			if(entry.getValue() > count){
				weekDay = entry.getKey();
				count = entry.getValue();
			}
		}
		
		int sum = histogram.values().stream().mapToInt(x->x).sum();
		Statistic result = new Statistic();
		result.setValue(weekDay);
		result.setPropability((double)histogram.get(weekDay)/(double)sum);
		
		return result;
	}

	private static int getDayInYear(Date minDate) {
		SimpleDateFormat dayInYear = new SimpleDateFormat("D");
		return Integer.valueOf(dayInYear.format(minDate));
	}
	
}
