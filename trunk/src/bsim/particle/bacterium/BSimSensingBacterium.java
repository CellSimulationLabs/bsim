/**
 * BSimSensingBacterium.java
 *
 * Class that represents a bacterium that by default will move randomly, until contact 
 * with a bead is made at which time it will follow the goal chemoattractant.
 *
 * Authors: Thomas Gorochowski
 * Created: 28/08/2008
 * Updated: 28/08/2008
 */
package bsim.particle.bacterium;

import javax.vecmath.Vector3d;

import bsim.BSimParameters;
import bsim.scene.BSimScene;

public class BSimSensingBacterium extends BSimBacterium {

	protected int beadContactTimer = 0;
	protected double switchSpeed = 2.0;

	public void action() {
		if(beadContactTimer > 0) beadContactTimer--;			
		super.action();
	}

	protected double runProb() {
		if(beadContactTimer > 0) return super.runProb();		
		else return runProbIso; // Perform random walk		
	}
		
	public void setBeadContactTimer() {
		beadContactTimer = (int)(switchSpeed / BSimParameters.dt);
	}	
	
}
