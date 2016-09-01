package ch.zir.ffv.viz.app.resource;

import java.util.Arrays;

public class FlightInformation {
 
	private String carrier;
	private String number;
	private String destination;
	private String origin;
	private long departure;
	private Price[] prices;
	private boolean isComplete;
	private double minPrice;
	private double maxPrice;

	public FlightInformation(int delta) {
		prices = new Price[delta];
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public long getDeparture() {
		return departure;
	}

	public void setDeparture(long departure) {
		this.departure = departure;
	}

	public void addPrice(double price, long request, int delta) {
		if (delta > prices.length) {
			return;
		}
		Price priceInfo = new Price();
		priceInfo.setDelta(delta);
		priceInfo.setRequest(request);
		priceInfo.setPrice(price);
		prices[delta - 1] = priceInfo;
	}

	public Price[] getPrices() {
		return prices;
	}

	public double getMinPrice() {
		return minPrice;
	}

	public double getMaxPrice() {
		return maxPrice;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void compute() {
		this.isComplete = true;
		for (int i = 1; i <= prices.length; i++) {
			if (prices[i - 1] == null) {
				this.isComplete = false;
				break;
			}
		}
		if (isComplete) {
			minPrice = Arrays.stream(prices).map(x -> x.getPrice()).min(Double::compareTo).get();
			maxPrice = Arrays.stream(prices).map(x -> x.getPrice()).max(Double::compareTo).get();
		}
	}

}
