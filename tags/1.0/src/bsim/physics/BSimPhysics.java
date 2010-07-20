/**
 * BSimPhysics.java
 *
 * Abstract class which implements the interface that handles the physics for the
 * simulation. It maintains an internal reference to the current scene and has
 * an update method that must be implemented by any valid physical models.
 *
 * Authors: Thomas Gorochowski
 *          Ian Miles
 * Created: 12/07/2008
 * Updated: 20/07/2008
 */


// Define the location of the class in the bsim package
package bsim.physics;

// Import the bsim packages used
import bsim.*;

// Standard packages required by the application
import java.io.File;
import java.io.IOException;
import java.util.*;


public abstract class BSimPhysics{
	
	
	// The simulation scene that will be updated
	protected BSimScene scene;
	
	
	/**
	 * General constructor.
	 */
	public BSimPhysics(BSimScene newScene){
		super();
		// Update the internal reference to the scene
		scene = newScene;
	}
	
	
	/**
	 * Get/set the value of viscosity
	 */
	//public static double getViscosity() {return viscosity;}
	//public static void setViscosity(double newViscosity) {viscosity = newViscosity;}
	
	 
	/**
	 * Abstract method that models the physics for the simulation. It should update
	 * all positions and properties of every object in the simulation.
	 */
	public abstract void updateProperties();
	

}
