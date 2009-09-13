package bsim;


public abstract class BSimExporter {
	
	protected BSim sim;
	protected double dt;
	
	public BSimExporter(BSim sim) {
		this.sim = sim;
		this.dt = sim.getDt();
	}
	
	/**
	 * The exporter is called every dt seconds, defaults to sim.getDt()  
	 */
	public void setDt(double d) { dt = d;  }
	
	public double getDt() { return dt; }
	
	public abstract void before();
	public abstract void during();
	public abstract void after();
	
}
