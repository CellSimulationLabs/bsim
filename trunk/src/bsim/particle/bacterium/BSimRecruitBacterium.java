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

import java.awt.Graphics;

import javax.vecmath.Vector3d;

import bsim.BSimParameters;
import bsim.BSimScene;


public class BSimRecruitBacterium extends BSimCoordBacterium  {

	protected boolean foundRecruit = false;

	/**
	 * General constructor.
	 */
	public BSimRecruitBacterium(Vector3d newPosition, double newRadius,
			Vector3d newDirection,  double newForceMagnitudeDown,
			double newForceMagnitudeUp,
			int newState, double newTumbleSpeed, int newRemDt, BSimScene newScene, 
		    double newSwitchSpeed, double newCoordThreshold) {

		// Call the parent constructor with the basic properties	
		super(newPosition, newRadius, newDirection, newForceMagnitudeDown,
		newForceMagnitudeUp, newState,
		      newTumbleSpeed, newRemDt, newScene, newSwitchSpeed, newCoordThreshold);
	}


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

	
	/**
	 * This is an updated version of the BSimBacterium method to only allow for the
	 * sensed goal concentration to be used if in contact with a bead.
	 */
	protected double senseRunContinueProb() {
		double shortTermMean;
		double longTermMean;
		double shortTermCounter = 0.0;
		double longTermCounter = 0.0;
		double shortTermMemoryLength = 1.0; // seconds
		double longTermMemoryLength = 3.0; // seconds
		double sensitivity = 0.000001;
		
		// Perform the normal attraction to the goal chemoattractant
		for(int i=0; i<concMemory.size();i++) {
			if(i <= (longTermMemoryLength/BSimParameters.dt)) {
				longTermCounter = longTermCounter + (Double)concMemory.elementAt(i);
			} else shortTermCounter = shortTermCounter + (Double)concMemory.elementAt(i);
		}
		shortTermMean = shortTermCounter / (1 + (shortTermMemoryLength/BSimParameters.dt));
		longTermMean = longTermCounter / (longTermMemoryLength/BSimParameters.dt);
	
		if(shortTermMean - longTermMean > sensitivity) {
			foundRecruit = true;
			runUp = true;
			return upRunProb;
		}
		else if(longTermMean - shortTermMean > sensitivity){
			foundRecruit = false;
			runUp = false;
			return downRunProb;
		}
		else {
			foundRecruit = false;
			runUp = false;
			return isoRunProb;
		}
	}
		
}
