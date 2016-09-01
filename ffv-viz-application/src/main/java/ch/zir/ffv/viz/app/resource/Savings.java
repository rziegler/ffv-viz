package ch.zir.ffv.viz.app.resource;

import java.util.List;
import java.util.stream.Collectors;

public class Savings {

	public static Saving compute(List<FlightInformation> filtered) {

		List<Saving> savings = filtered.stream().map(f -> {
			double absoult = f.getMaxPrice() - f.getMinPrice();
			double relative = (f.getMaxPrice()  - f.getMinPrice())/ f.getMaxPrice();
			return new Saving(absoult, relative);
		}).collect(Collectors.toList());
		
		double absolute = savings.stream().mapToDouble(f -> f.getAbsolut()).average().getAsDouble();
		double relative = savings.stream().mapToDouble(f -> f.getRelative()).average().getAsDouble();

		return new Saving(absolute, relative);
	}
}
