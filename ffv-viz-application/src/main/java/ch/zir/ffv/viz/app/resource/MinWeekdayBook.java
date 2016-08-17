package ch.zir.ffv.viz.app.resource;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.zir.ffv.viz.app.jdbi.AggFlightRecord;

public class MinWeekdayBook {
	
	public static String minWeekdayBook(List<AggFlightRecord> records) {
		Map<String, Integer> histogram = DayHistogram.createDayHistogram(records);	
		
		String weekDay = "";
		int count = 0;
		for(Entry<String, Integer> entry:histogram.entrySet()){
			if(entry.getValue() > count){
				weekDay = entry.getKey();
				count = entry.getValue();
			}
		}
		
		return weekDay;
	}
	

}
