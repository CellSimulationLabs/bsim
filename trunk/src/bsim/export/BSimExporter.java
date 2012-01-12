package bsim.export;

import bsim.BSim;

/**
 * Exporter base class. @see BSim#export()
 */
public abstract class BSimExporter {
	
	/** Associated simulation. */
	protected BSim sim;
	/** Timestp. */
	protected double dt;
	
	/**
	 * Constructor of a basic exporter. Abstract class to be extended
	 * for particular need.
	 * @param sim Associated simulation.
	 */
	public BSimExporter(BSim sim) {
		this.sim = sim;
		this.dt = sim.getDt();
	}
	
	/** Called before a simulation starts (overwrite). */
	public abstract void before();
	/** Called each timestep (overwrite). */
	public abstract void during();
	/** Called after a simulation finishes (overwrite). */
	public abstract void after();
	
	/**
	 * Set the time interval that the exporter is called. 
	 * The exporter is called every d seconds in simulation time. 
	 * Defaults to sim.getDt().
	 */
	public void setDt(double d) { dt = d; }
	
	/** Return the timestep. */
	public double getDt() { return dt; }
	
}
