package bsim.export;

import bsim.BSim;

public abstract class BSimExporter {
	
	protected BSim sim;
	
	public BSimExporter(BSim sim) {
		this.sim = sim;
	}
	
	public abstract void before();
	public abstract void during();
	public abstract void after();
}
