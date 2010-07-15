package bsim.export;

import bsim.BSim;

/**
 * @see BSim#export()
 */
public abstract class BSimExporter {
	
	protected BSim sim;
	protected double dt;
	
	public BSimExporter(BSim sim) {
		this.sim = sim;
		this.dt = sim.getDt();
	}
	
	public abstract void before();
	public abstract void during();
	public abstract void after();
	
	/**
	 * The exporter is called every d seconds in simulation time. Defaults to sim.getDt()  
	 */
	public void setDt(double d) { dt = d;  }
	
	public double getDt() { return dt; }
	
}
