package bsim;

import java.util.Vector;


public abstract class BSimThreadedTicker extends BSimTicker {
	
	protected int threads;
	protected Vector<BSimThreadedTickerWorker> workers;
	protected BSimThreadedTickerWorker myWorker;
	
	public BSimThreadedTicker (int threads) {
		this.threads = threads;
		// Create a pool of worker threads
		workers = new Vector<BSimThreadedTickerWorker>(threads);
		myWorker = createWorker(0, threads);
		for (int i = 1; i < threads; i++) { 
			BSimThreadedTickerWorker worker = createWorker(i, threads);
			workers.add(worker);
			Thread t = new Thread(worker);
			t.start();
		}
	}
	
	@Override
	final public void tick() {
		sequentialBefore();
		// Trigger the other workers to run in parallel
		for (int i = 0; i < workers.size(); i++) {
			workers.get(i).trigger();
		}
		// This will automatically wait for all other workers to finish
		myWorker.run(); 
		sequentialAfter();
	}
	
	/**
	 * Overwrite these with sequential operations to run before and after 
	 * the parallel block.
	 */
	public abstract void sequentialBefore();
	public abstract void sequentialAfter();
	
	/**
	 * 
	 * @param threadID the unique thread ID
	 * @param threads the total number of threads that are being used
	 * @param notifier should be sent to the worker constructor to signal when done
	 * @return The worker that will be called in parallel
	 */
	public abstract BSimThreadedTickerWorker createWorker (int threadID, int threads);
}
