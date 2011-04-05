package bsim;


public abstract class BSimThreadedTickerWorker {

	/**
	 * Run each timestep, this is where you should update particle properties
	 * by calling interaction(), action() and updatePosition() methods
	 */
	public abstract void threadedTick(int threadID, int threads);
}
