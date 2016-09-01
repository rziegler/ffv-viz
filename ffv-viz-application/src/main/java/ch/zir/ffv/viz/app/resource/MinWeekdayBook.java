package ch.zir.ffv.viz.app.resource;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MinWeekdayBook {
	
	public static Statistic minWeekdayBook(List<FlightInformation> records, int delta) {
		Map<String, Integer> histogram = DayHistogram.createDayHistogram(records);	
		
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
	

}
