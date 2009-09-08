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


public class BSimVesicleAcceptor extends BSimParticle {
		
	private int fusionCount;

	public BSimVesicleAcceptor(Vector3d newPosition, double newRadius, BSimScene newScene) {				
		super(newPosition, newRadius, newScene);
	}
	
	public void action() {}
	
	public void interaction(BSimVesicle vesicle) {
		double od = outerDistance(vesicle);		
		if(od < 0) {
			this.fusionCount++;			
			getScene().removeVesicle(vesicle);
		}		
	}	
	
	public void interaction(BSimBacterium b) {
		double od = outerDistance(b);
		if(od < 0) this.reaction(b,od*BSimBacterium.reactForceGradient);				
	}	
		
}
