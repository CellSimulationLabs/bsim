package bsim;

/**
 * Notifier used for multi-threaded tickers.
 * Threads wait on this notifier to ensure that they are all synchronised
 * where necessary.
 */
public class BSimNotifier {
	
	/**
	 * Constructor of a notifier (no options available).
	 */
	public BSimNotifier () {
		super();
	}
	
	/**
	 * Places object that calls this method in wait cycle.
	 * @throws InterruptedException Thrown when object has been notified.
	 */
	public synchronized void waitForNotify() throws InterruptedException {
		try{
			wait();
		}
		catch (InterruptedException e) {
			throw(e);
		}
	}
	
	/**
	 * Notifies the a single object waiting on this notifier.
	 */
	public synchronized void notifyWaiter() {
		notify();
	}
	
	/**
	 * Notifies all objects waiting on this notifier.
	 */
	public synchronized void notifyAllWaiters() {
		notifyAll();
	}	
}
