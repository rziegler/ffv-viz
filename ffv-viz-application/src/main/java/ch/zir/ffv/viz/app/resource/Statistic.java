package ch.zir.ffv.viz.app.resource;

public class Statistic {

	private String value;
	private double propability;

	public double getPropability() {
		return propability;
	}

	public String getValue() {
		return value;
	}

	public void setPropability(double propability) {
		this.propability = propability;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
