package bsim;

import java.util.concurrent.CyclicBarrier;


public abstract class BSimThreadedTickerWorker implements Runnable {

	protected int threadID;
	protected int threads;
	private boolean alive = true;
	protected static CyclicBarrier barrier = null;
	protected BSimNotifier myNotifier;
	
	public BSimThreadedTickerWorker(int threadID, int threads) {
		this.threadID = threadID;
		this.threads = threads;
		myNotifier = new BSimNotifier();
		if (barrier == null) barrier = new CyclicBarrier(threads);
	}
	
	final public void run() {
		if (threadID == 0) {
			// Run the worker
			threadedTick(threadID, threads);
			try {
				// Wait until other threads are done
				barrier.await();
				barrier.reset();
			}
			catch (Exception e) {
				// Do nothing
			}
		}
		else {
			while (true) {
				try {
					// Wait on trigger
					myNotifier.waitForNotify();
					// Check if worker has been killed
					if (!alive) break;
					// Run the worker
					threadedTick(threadID, threads);
					// Wait until other threads are done
					barrier.await();
				}
				catch (Exception e) {
					// Break cleanly ensuring any waiting threads are released
					break;
				}
			}
		}
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
