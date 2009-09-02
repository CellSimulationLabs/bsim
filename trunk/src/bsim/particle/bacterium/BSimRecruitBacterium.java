/**
 * BSimRecruitBacterium.java
 *
 * Class that represents a bacterium that by default will move randomly, until contact 
 * with a bead is made at which time it will follow the goal chemoattractant. A 
 * co-ordination signal will also be released that will cause any bacteria in a high 
 * enough concentration to also follow the chemoattractant. Also, a recruitment signal is
 * produced on contact with a bead that all bacteria will follow by default.
 *
 * Authors: Thomas Gorochowski
 * Created: 01/09/2008
 * Updated: 01/09/2008
 */
package bsim.particle.bacterium;

import javax.vecmath.Vector3d;

import bsim.BSimParameters;
import bsim.scene.BSimScene;


public class BSimRecruitBacterium extends BSimCoordBacterium  {


	public void action () {
		
		int newChemo = 0;
		
		// Need to check if a switch to chemo has been made.
		if(beadContactTimer > 0 || 
		   scene.getCoordinationField().getConcentration(this.getPosition()) > coordThreshold) {
			newChemo = BAC_CHEMO_GOAL;
		}
		else{
			newChemo = BAC_CHEMO_RECRUIT;
		}
		
		if(newChemo != chemo){
			// Reset the memory
			memToReset = true;
		}
		
		// Update the gradient to use
		chemo = newChemo;
		
		if(beadContactTimer > 0){
				// Generate some recruitment chemical at current location
				scene.getRecruitmentField().addChemical (1.0, this.getPosition());
		}
		
		super.action();
	}

	protected double runProb() {
		//BSimBacterium.runContinueProb();
	}
		
}
