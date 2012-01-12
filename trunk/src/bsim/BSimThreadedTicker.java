package bsim;

import java.util.Vector;

/**
 * Multi-threaded ticker.
 * This can be used instead of a standard ticker and will allow for concurrent
 * updating of appropriate parts of a simulation. Care must be taken to ensure
 * that data structures are not updated and read by two different threads. A
 * user must define a BSimThreadedTickerWorker object that captures how the
 * ticker should split up the update task.
 */
public abstract class BSimThreadedTicker extends BSimTicker {
	
	/** Total number of threads (including main one). */
	protected int threads;
	/** List of workers to call upon. */
	protected Vector<BSimThreadedTickerWorker> workers;
	/** A local working for the main thread to call directly. */
	protected BSimThreadedTickerWorker myWorker;
	
	/**
	 * Constructor that creates a threaded ticker that uses a fixed size pool of threads. These are
	 * not recreated each call due to the sizable overhead in Java, but instead
	 * a pool of worker threads is blocked and notified to carry out work each
	 * time the ticker is called.
	 * @param threads Total number of threads to use.
	 */
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
	
	/**
	 * Called at each time step by the BSim simulation object. Runs the sequential before, the
	 * workers code, and then the sequential after.
	 */
	@Override
	final public void tick() {
		sequentialBefore();
		myWorker.run(); // This will automatically trigger and wait for all other workers to finish
		sequentialAfter();
	}
	
	/**
	 * Overwrite these with sequential operations to run before and after 
	 * the parallel block.
	 */
	public abstract void sequentialBefore();
	public abstract void sequentialAfter();
	
	/**
	 * For the user to overwrite to create suitable workers for this ticker.
	 * @param threadID Unique thread ID
	 * @param threads Total number of threads
	 * @return New worker object that will be called in parallel
	 */
	public abstract BSimThreadedTickerWorker createWorker (int threadID, int threads);
}
