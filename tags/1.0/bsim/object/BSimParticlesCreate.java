/**
 * BSimParticlesCreate.java
 *
 * Class to hold static methods to generate inital particle positions
 *
 * Authors: Thomas Gorochowski
 * Created: 25/08/2008
 * Updated: 25/08/2008
 */


// Define the location of the class in the bsim package
package bsim.object;

// Import the bsim packages used
import bsim.*;
import bsim.physics.*;

// Standard packages required by the application
import java.util.*;


public class BSimParticlesCreate {
	
	
	/**
	 * Create a particle set with given parameters (uniform random distribution). No 
	 * checking of overlapping particles is performed meaning initial simulation output 
	 * should be investigated if strange movement occurs.
	 */
	public static Vector createParticleSet (double[] args, BSimParameters params) {
		
		// Variables for each particle property
		double[] newDirection = {0,0};
		double newSize, newMass, newSpeed;
		double[] newPosition = new double[2];
		Vector partVec = new Vector();
		
		if(args[6] == 0) {
			newSize = params.getPartSize();
		}
		else{
			newSize = args[6];
		}
		
		newMass = 0.0; // Not used
		newSpeed = 0.0;
		
		// Loop through the number of particles to create
		for(int i=0; i<args[4]; i++){
			
			// Randomly select a new position for the particle
			newPosition[0] = args[0] + (args[2] * Math.random());
			newPosition[1] = args[1] + (args[3] * Math.random());
			
			// Create the type of particle required
			switch((int)args[5]){
				// Standard particle
				case 1: partVec.add(new BSimParticle(newSpeed, newMass, newSize, newDirection, newPosition)); break;
			}
		}

		// Return the new particle set
		return partVec;
	}
	
	
	/**
	 * Create a single particle with given parameters.
	 */
	public static BSimParticle createParticle (double[] args, BSimParameters params) {
		
		// Variables for each particle property
		double[] newDirection = {0,0};
		double newSize, newMass, newSpeed;
		double[] newPosition = new double[2];
		newPosition[0] = args[0];
		newPosition[1] = args[1];
		
		if(args[2] == 0) {
			newSize = params.getPartSize();
		}
		else{
			newSize = args[2];
		}
		
		newMass = 0.0; // Not used
		newSpeed = 0.0;
		
		// Return the new particle
		return new BSimParticle(newSpeed, newMass, newSize, newDirection, newPosition);
	}
}
