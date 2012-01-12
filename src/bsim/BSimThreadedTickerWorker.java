package bsim;

import java.util.concurrent.CyclicBarrier;

/**
 * Multi-threaded ticker worker.
 * This carries out the actual update task and captures how individual threads
 * should work on separate parts of the simulation.
 */
public abstract class BSimThreadedTickerWorker implements Runnable {

	/** The ID of this worker. */
	protected int threadID;
	/** Total number of threads in the pool. */
	protected int threads;
	/** Shared barrier to enable synchronisation of all threads at start of update. */
	protected static CyclicBarrier barrier1;
	/** Shared barrier to enable synchronisation of all threads at end of update. */
	protected static CyclicBarrier barrier2;
	
	/**
	 * Constructor to create a new worker for the BSimThreadedTicker.
	 * @param threadID Unique thread ID for the worker.
	 * @param threads Total number of threads.
	 */
	public BSimThreadedTickerWorker(int threadID, int threads) {
		this.threadID = threadID;
		this.threads = threads;
		if (threadID == 0) {
			barrier1 = new CyclicBarrier(threads);
			barrier2 = new CyclicBarrier(threads);
		}
	}
	
	/**
	 * Threaded function. The first (threadID = 0) is treated specially as this is 
	 * the main application thread and therefore should not enter a waiting state.
	 */
	final public void run() {
		if (threadID == 0) {
			try {
				barrier1.await(); // Make sure all threads are ready to start
				barrier1.reset();
				threadedTick(threadID, threads); // Run the worker
				barrier2.await(); // Wait until other threads are done
				barrier2.reset();
			}
			catch (Exception e) { }
		}
		else {
			while (true) {
				try {
					barrier1.await(); // Wait on trigger
					threadedTick(threadID, threads); // Run the worker
					barrier2.await(); // Wait until other threads are done
				}
				catch (Exception e) { break; }
			}
		}
	}
	
	/**
	 * Run each timestep in parallel, use the threadID to figure
	 * out which part of the problem to work on. threads is the
	 * total number of threads created. To be overwritten by user.
	 */
	public abstract void threadedTick(int threadID, int threads);
}
