package bsim;

import java.util.Vector;


public abstract class BSimThreadedTicker extends BSimTicker {
	
	protected int threads;
	protected Vector<BSimThreadedTickerWorker> workers;
	protected Object notifier;
	
	BSimThreadedTicker (int totalThreads) {
		threads = totalThreads;
		// Create a pool of worker threads
		notifier = new Object();
		workers = new Vector<BSimThreadedTickerWorker>(totalThreads);
		for (int i = 0; i < totalThreads; i++) { 
			BSimThreadedTickerWorker worker = createWorker(i, totalThreads, notifier);
			workers.add(worker);
			Thread t = new Thread(worker);
			t.run();
		}
	}
	
	@Override
	final public void tick() {
		sequentialBefore();
		for (int i = 0; i < threads; i++) {
			workers.get(i).trigger();
		}
		// Wait until all threads have finished their work
		try {
			notifier.wait();
			BSimThreadedTickerWorker.resetBarrier();
		}
		catch (InterruptedException e) { /* Do Nothing */ }
		sequentialAfter();
	}
	
	/**
	 * Overwrite these if you wish to do some sequential operations
	 * before or after the parallel block. Empty by default.
	 */
	public void sequentialBefore() {}
	public void sequentialAfter() {}
	
	/**
	 * 
	 * @param threadID the unique thread ID
	 * @param threads the total number of threads that are being used
	 * @param notifier should be sent to the worker constructor to signal when done
	 * @return The worker that will be called in parallel
	 */
	public abstract BSimThreadedTickerWorker createWorker (int threadID, int threads, Object notifier);
}
