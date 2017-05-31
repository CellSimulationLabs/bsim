package bsim;

/**
 * Standard ticker.
 * Used to update the simulation state at each timestep.
 */
public abstract class BSimTicker {
	/**
	 * Run each timestep, this is where you should update particle properties
	 * by calling interaction(), action() and updatePosition() methods
	 */
	public abstract void tick();

	/**
	 * Run at the end of a simulation.
	 * This is where any clean up should take place (particularly relevant
	 * for multithreaded examples)
	 */
	public void finish(){
		System.out.println("Simulation done.");
	}
}
