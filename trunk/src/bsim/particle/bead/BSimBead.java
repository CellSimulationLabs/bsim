/**
 * BSimBead.java
 *
 * Class that represents a bead in our simulation.
 *
 * Authors: Thomas Gorochowski
 *          Ian Miles
 * Created: 12/07/2008
 * Updated: 19/07/2008
 */
package bsim.particle.bead;

import javax.vecmath.Vector3d;

import bsim.particle.BSimParticle;


public class BSimBead extends BSimParticle {
	
	
	/**
	 * General constructor.
	 */
	public BSimBead(Vector3d newPosition, double newRadius) {
				
		// Call the parent constructor
		super(newPosition, newRadius);
	}
	
	public void action() {}
	
}