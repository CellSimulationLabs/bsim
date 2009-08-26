/**
 * BSimBeadsCreate.java
 *
 * Class to hold static methods to generate inital bead positions
 *
 * Authors: Thomas Gorochowski
 * 			Mattia Fazzini(Update)
 * Created: 25/08/2008
 * Updated: 07/08/2009
 */
package bsim.drawable.bead;

import java.util.Vector;

import javax.vecmath.Point3d;

import bsim.BSimParameters;


public class BSimBeadsCreate {
	
	
	/**
	 * Create a bead set with given parameters (uniform random distribution). No 
	 * checking of overlapping beads is performed meaning initial simulation output 
	 * should be investigated if strange movement occurs.
	 */
	public static Vector createBeadSet (double[] args, BSimParameters params) {
		
		// Variables for each bead property
		double[] newDirection = {0,0,0};
		double newRadius, newSpeed;
		Point3d newPosition;
		Vector beadVec = new Vector();
		
		if(args[8] == 0) {
			newRadius = params.getBeadRadius();
		}
		else{
			newRadius = args[8];
		}
				
		newSpeed = 0.0;
		
		// Loop through the number of beads to create
		for(int i=0; i<args[6]; i++){
			
			// Randomly select a new position for the bead
			newPosition = new Point3d(
					args[0] + (args[3] * Math.random()),
					args[1] + (args[4] * Math.random()),
					args[2] + (args[5] * Math.random())
				);

			
			// Create the type of bead required
			switch((int)args[7]){
				// Standard bead
				case 1: beadVec.add(new BSimBead(newPosition, newRadius)); break;
			}
		}

		// Return the new bead set
		return beadVec;
	}
	
	
	/**
	 * Create a single bead with given parameters.
	 */
	public static BSimBead createBead (double[] args, BSimParameters params) {
		
		// Variables for each bead property
		double[] newDirection = {0,0,0};
		double newRadius, newSpeed;
		Point3d newPosition;
		
		newPosition = new Point3d(args[0], args[1], args[2]);
		
		if(args[3] == 0) {
			newRadius = params.getBeadRadius();
		}
		else{
			newRadius = args[3];
		}
			
		newSpeed = 0.0;
		
		// Return the new bead
		return new BSimBead(newPosition, newRadius);
	}
}
