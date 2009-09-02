/**
 * BSimCoordBacterium.java
 *
 * Class that represents a bacterium that by default will move randomly, until contact 
 * with a bead is made at which time it will follow the goal chemoattractant. A 
 * co-ordination signal will also be released that will cause any bacteria in a high 
 * enough concentration to also follow the chemoattractant.
 *
 * Authors: Thomas Gorochowski
 * Created: 28/08/2008
 * Updated: 28/08/2008
 */
package bsim.particle.bacterium;

import javax.vecmath.Vector3d;

import bsim.BSimParameters;
import bsim.scene.BSimScene;

public class BSimCoordBacterium extends BSimSensingBacterium {

	// Threshold for detecting co-ordination signal (AHL)
	protected double coordThreshold = 0.1;

	public void action() {		
		if(beadContactTimer > 0) scene.getCoordinationField().addChemical (1.0, this.getPosition());	
		super.action();
	}
	
	protected double runProb() {		
		// Check to see if the bacteria has been in contact with a bead
		if(beadContactTimer > 0 || 
		   scene.getCoordinationField().getConcentration(this.getPosition()) > coordThreshold) {
			//BSimBacterium.runContinueProb();
		}
		else {
			return runProbIso;
		}
	}
	
}
