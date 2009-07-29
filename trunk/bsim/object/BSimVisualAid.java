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


// Define the location of the class in the bsim package
package bsim.object;

// Standard packages required by the application
import java.awt.*;


public interface BSimVisualAid {
	
	
	// Function called when the simulation is reset
	public abstract void reset();
	
	// Function called on each time step to update the visual aid internal state
	public abstract void updateState();
	
	// Function to redraw the visual aid
	public abstract void redraw( Graphics g );
}
