package ch.zir.ffv.viz.app.resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UIConverter {
	
	public List<UIFlight> convert(List<FlightInformation> flights){
		List<UIFlight> result = new ArrayList<>();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat sdfDay = new SimpleDateFormat("EEE", Locale.GERMAN);
		
		for(FlightInformation flight:flights){
			double diff = flight.getMaxPrice()-flight.getMinPrice();
			double factor = Math.pow(2, 6) / diff;
			
			for(Price price:flight.getPrices()){
				UIFlight f = new UIFlight();
				double bin = (price.getPrice() - flight.getMinPrice()) * factor;
				f.setBin((int)(Math.log(bin+1)/Math.log(2))+1);
				f.setCarrier(flight.getCarrier());
				f.setDeltaTime(price.getDelta());
				f.setDepartureDate(sdfDate.format(new Date(flight.getDeparture())));
				f.setDepartureTime(sdfTime.format(new Date(flight.getDeparture())));
				f.setDepartureWeekday(sdfDay.format(new Date(flight.getDeparture())));
				f.setDestination(flight.getDestination());
				f.setFlightNumber(flight.getNumber());
				f.setOrigin(flight.getOrigin());
				f.setPrice(price.getPrice());
				f.setRequestDate(sdfDate.format(new Date(price.getRequest())));
				result.add(f);
			}
		}
		return result;
	}

}
