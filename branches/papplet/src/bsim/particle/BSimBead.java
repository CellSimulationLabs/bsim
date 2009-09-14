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
package bsim.particle;

import javax.vecmath.Vector3d;

import bsim.scene.BSimScene;


public class BSimBead extends BSimParticle {
		
	public BSimBead(Vector3d newPosition, double newRadius, BSimScene newScene) {				
		super(newPosition, newRadius, newScene);
	}
	
	public void action() {}
	
}
