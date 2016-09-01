package ch.zir.ffv.viz.app.resource;

public class Saving {

	private double absolut;
	private double relative;
	
	public Saving(double absolut, double relative){
		this.absolut = absolut;
		this.relative = relative;
	}

	public double getAbsolut() {
		return absolut;
	}
	
	public double getRelative() {
		return relative;
	}

}
