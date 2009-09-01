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

import java.awt.Graphics;

import javax.vecmath.Vector3d;

import bsim.BSimParameters;
import bsim.BSimScene;
import bsim.particle.BSimParticle;
import bsim.particle.bead.BSimBead;


public class BSimSensingBacterium extends BSimBacterium {

	protected int beadContactTimer = 0;
	
	protected double switchSpeed = 2.0;

	/**
	 * General constructor.
	 */
	public BSimSensingBacterium(Vector3d newPosition, double newRadius,
			Vector3d newDirection, double newForceMagnitudeDown,
			double newForceMagnitudeUp,
			int newState, double newTumbleSpeed, int newRemDt, BSimScene newScene, 
		    double newSwitchSpeed) {

		// Call the parent constructor with the basic properties	
		super(newPosition, newRadius, newDirection, newForceMagnitudeDown,
		      newForceMagnitudeUp, newState, newTumbleSpeed, newRemDt, newScene);
		
		switchSpeed = newSwitchSpeed;
	}

	public void action() {
		if(beadContactTimer > 0){
			beadContactTimer--;
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
		
		// Check to see if the bacteria has been in contact with a bead
		if(beadContactTimer > 0){
			// Perform the normal attraction to the goal chemoattractant
			for(int i=0; i<concMemory.size();i++) {
				if(i <= (longTermMemoryLength/BSimParameters.dt)) {
					longTermCounter = longTermCounter + (Double)concMemory.elementAt(i);
				} else shortTermCounter = shortTermCounter + (Double)concMemory.elementAt(i);
			}
			shortTermMean = shortTermCounter / (1 + (shortTermMemoryLength/BSimParameters.dt));
			longTermMean = longTermCounter / (longTermMemoryLength/BSimParameters.dt);
		
			if(shortTermMean - longTermMean > sensitivity) {
				runUp = true;
				return upRunProb;
			}
			else if(longTermMean - shortTermMean > sensitivity) {
				runUp = false;
				return downRunProb;
			}
			else {
				runUp = false;
				return isoRunProb;
			}
		}
		else{
			// If not perform random walk
			return isoRunProb;
		}
	}
		
	public void setBeadContactTimer() {
		beadContactTimer = (int)(switchSpeed / BSimParameters.dt);
	}	
	
}
