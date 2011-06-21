package bsim;


public class BSimNotifier {
	
	public BSimNotifier () {
		super();
	}
	
	public synchronized void waitForNotify() throws InterruptedException {
		try{
			wait();
		}
		catch (InterruptedException e) {
			throw(e);
		}
	}
	
	public synchronized void notifyWaiter() {
		notify();
	}
	
	public synchronized void notifyAllWaiters() {
		notifyAll();
	}	
}
