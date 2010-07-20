/**
 * BSimSemaphore.java
 *
 * Implementation of a semaphore in the form of a Java notifiable object. It is used to
 * all executing threads to wait on a signal, required when for example the simulation
 * is paused. In this case the thread will wait on this object until the the signal
 * method is called causing the animation to play once again. Both the functions in this
 * class are inherited from the base Object.
 *
 * Authors: Thomas Gorochowski
 * Created: 13/07/2008
 * Updated: 15/07/2008
 */


// Define the location of the class in the bsim package
package bsim;

public class BSimSemaphore{
	
	
	/**
	 * Method that stops the current thread and makes it wait.
	 */
	public synchronized void waitOn(){
		// Stop the calling threads execution and wait on this object
		try{
			wait();
		}
		catch(InterruptedException e){}
	}
	
	
	/**
	 * Notifies any waiting threads to continue execution.
	 */
	public synchronized void signal(){
		// Signal to all threads waiting on this object to continue execution
		notifyAll();
	}
}
