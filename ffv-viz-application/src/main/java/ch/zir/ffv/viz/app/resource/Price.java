package ch.zir.ffv.viz.app.resource;

public class Price {

	long request;
	double price;
	int delta;
 
	public long getRequest() {
		return request;
	}

	public void setRequest(long request) {
		this.request = request;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPrice() {
		return price;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}

	public int getDelta() {
		return delta;
	}

}
