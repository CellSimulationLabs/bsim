/**
 * BSimVisualAid.java
 *
 * Interface for visual aids that are displayed on the simulation. This defines a single
 * function that allows for the internal state of the aid to updated on each time step.
 *
 * Authors: Thomas Gorochowski
 * Created: 14/08/2008
 * Updated: 14/08/2008
 */
package bsim.render.visualaid;



public interface BSimVisualAid {
	
	
	// Function called when the simulation is reset
	public abstract void reset();
	
	// Function called on each time step to update the visual aid internal state
	public abstract void updateState();

}
