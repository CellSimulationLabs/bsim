package bsim;


public abstract class BSimThreadedTicker extends BSimTicker implements Runnable {
	
	int threads;
	
	/**
	 * Run each timestep, this is where you should update particle properties
	 * by calling interaction(), action() and updatePosition() methods
	 */
	
	BSimThreadedTicker (int totalThreads) {
		threads = totalThreads;
	}
	
	@Override
	public void tick() {
		
		
		
	}
	
	public void run() {
		
		
	}
	
	public void sequentialStart() {}
	public abstract void threadedTick(int threadID, int threads);
	public void sequentialEnd() {}
}
