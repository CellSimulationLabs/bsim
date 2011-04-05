package bsim;

import java.util.concurrent.CyclicBarrier;


public abstract class BSimThreadedTickerWorker implements Runnable {

	protected int threadID;
	protected int threads;
	private boolean alive = true;
	protected static CyclicBarrier barrier = null;
	protected Object notifier;
	
	BSimThreadedTickerWorker(int threadID, int threads, Object notifier) {
		this.threadID = threadID;
		this.threads = threads;
		this.notifier = notifier;
		if (barrier == null) barrier = new CyclicBarrier(threads);
	}
	
	final public void run() {
		while (true) {
			// Wait on trigger
			try {
				this.wait();
				// Check if worker has been killed
				if (!alive) break;
				// Run the worker
				threadedTick(threadID, threads);
				if (barrier.await() == 0) {
					// Notify threaded ticker that all workers are done
					notifier.notifyAll();
				}
			}
			catch (Exception e) {
				notifier.notifyAll();
				break;
			}
		}		
	}
	
	final public static void resetBarrier() {
		barrier.reset();
	}
	
	final public void trigger() {
		// Kick off another cycle of the worker
		this.notify();
	}
	
	final public void kill() {
		alive = false;
		this.notify();
	}
	
	/**
	 * Run each timestep in parallel, use the threadID to figure
	 * out which part of the problem to work on. threads is the
	 * total number of threads created.
	 */
	public abstract void threadedTick(int threadID, int threads);
}
