package bsim;

import java.util.Vector;


public abstract class BSimThreadedTicker extends BSimTicker {
	
	protected int threads;
	protected Vector<BSimThreadedTickerWorker> workers;
	protected BSimNotifier notifier;
	
	public BSimThreadedTicker (int totalThreads) {
		threads = totalThreads;
		// Create a pool of worker threads
		notifier = new BSimNotifier();
		workers = new Vector<BSimThreadedTickerWorker>(totalThreads);
		for (int i = 0; i < totalThreads; i++) { 
			BSimThreadedTickerWorker worker = createWorker(i, totalThreads, notifier);
			workers.add(worker);
			Thread t = new Thread(worker);
			t.start();
		}
	}
	
	@Override
	final public void tick() {
		sequentialBefore();
		try {
			for (int i = 0; i < threads; i++) {
				workers.get(i).trigger();
			}
			// Wait until all threads have finished their work
			notifier.waitForNotify();
		}
		catch (InterruptedException e) {
			// Do Nothing
		}
		BSimThreadedTickerWorker.resetBarrier();
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
	public abstract BSimThreadedTickerWorker createWorker (int threadID, int threads, BSimNotifier notifier);
}
