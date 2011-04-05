package bsim;

import java.util.concurrent.CyclicBarrier;


public abstract class BSimThreadedTickerWorker implements Runnable {

	protected int threadID;
	protected int threads;
	private boolean alive = true;
	protected static CyclicBarrier barrier = null;
	protected BSimNotifier tickerNotifier;
	protected BSimNotifier myNotifier;
	
	public BSimThreadedTickerWorker(int threadID, int threads, BSimNotifier notifier) {
		this.threadID = threadID;
		this.threads = threads;
		this.tickerNotifier = notifier;
		myNotifier = new BSimNotifier();
		if (barrier == null) barrier = new CyclicBarrier(threads);
	}
	
	final public void run() {
		while (true) {
			try {
				// Wait on trigger
				myNotifier.waitForNotify();
				// Check if worker has been killed
				if (!alive) break;
				// Run the worker
				threadedTick(threadID, threads);
				// Only the first thread needs to notify the threaded ticker
				if (barrier.await() == 0) {
					// Notify threaded ticker that all workers are done
					tickerNotifier.notifyAllWaiters();
				}
			}
			catch (Exception e) {
				// Break cleanly ensuring any waiting threads are released
				tickerNotifier.notifyAllWaiters();
				break;
			}
		}		
	}
	
	final public static void resetBarrier() {
		barrier.reset();
	}
	
	final public void trigger() {
		// Kick off another cycle of the worker
		myNotifier.notifyWaiter();
	}
	
	final public void kill() {
		alive = false;
		myNotifier.notifyWaiter();
	}
	
	/**
	 * Run each timestep in parallel, use the threadID to figure
	 * out which part of the problem to work on. threads is the
	 * total number of threads created.
	 */
	public abstract void threadedTick(int threadID, int threads);
}
