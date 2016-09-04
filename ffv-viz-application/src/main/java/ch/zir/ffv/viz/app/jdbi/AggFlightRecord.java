package ch.zir.ffv.viz.app.jdbi;

import java.util.Date;

public class AggFlightRecord {

	private String carrier;
	private String number;
	private Date departure;
	private Date request;
	private double price;
	private String destination;
	private int delta;

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

	public Date getDeparture() {
		return departure;
	}

	public void setDeparture(Date departure) {
		this.departure = departure;
	}

	public Date getRequest() {
		return request;
	}

	public void setRequest(Date request) {
		this.request = request;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getId() {
		return carrier + number + departure.getTime();
	}

	public void setDestination(String destination) {
		this.destination = destination;

	}

	public String getDestination() {
		return destination;
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}

}
