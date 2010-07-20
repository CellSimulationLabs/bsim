package bsim;


public abstract class BSimTicker {
	/**
	 * Run each timestep, this is where you should update particle properties
	 * by calling interaction(), action() and updatePosition() methods
	 */
	public abstract void tick();
}
