/**
 * BSimBacteriaCreate.java
 *
 * Class to hold static methods to generate inital bacteria starting conditions.
 *
 * Authors: Thomas Gorochowski
 * Created: 20/07/2008
 * Updated: 25/08/2008
 */


//Define the location of the class in the bsim package
package bsim.object;

//Import the bsim packages used
import bsim.*;
import bsim.physics.*;

//Standard packages required by the application
import java.util.*;


public class BSimBacteriaCreate {

	
	/**
	 * Create a bacteria set with given parameters (uniform random distribution). No 
	 * checking of overlapping particles is performed meaning initial simulation output 
	 * should be investigated if strange movement occurs.
	 */
	public static Vector createBacteriaSet (double[] args, BSimScene scene, BSimParameters params) {
		
		// Variables for each particle property
		double newSpeed = params.getBactSpeed();
		double newMass = 0; // Not used
		double newSize = params.getBactSize();
		double[] newPosition = new double[2];
		double[] newDirection = new double[2];
		double newForceUp, newForceDown;
		double dx,dy;
		double newTumbleSpeed;
		int newRemDt = 1;
		Vector bactVec = new Vector();
		Vector partVec = scene.getParticles();
		
		if(args[6] == 0) {
			newSize = params.getBactSize();
		}
		else{
			newSize = args[6];
		}
		
		newSpeed = params.getBactSpeed();
		
		// Loop through the number of bacteria to create
		for(int i=0; i<args[4]; i++){
			boolean invalidPlacement = false;
			
			// Randomly select a new position for the bacterium
			newPosition[0] = args[0] + (args[2] * Math.random());
			newPosition[1] = args[1] + (args[3] * Math.random());
			
			dx = (2.0 * Math.random()) - 1.0;
			dy = (2.0 * Math.random()) - 1.0;

			newDirection[0] = dx / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)));
			newDirection[1] = dy / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)));

			newForceUp = params.getBactForceUp();
			newForceDown = params.getBactForceDown();

			newTumbleSpeed = 0.0;
			
			newRemDt = BSimUtils.roundToDtSteps(BSimUtils.expRandVar(0.83), params.getDtSecs());
			
			BSimBacterium thisBacterium = new BSimBacterium(newSpeed, newMass, newSize, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params);
			
			for(int j=0; j<partVec.size(); j++) {
				if(BSimUtils.objectsIntersecting((BSimObject)partVec.elementAt(j), (BSimObject)thisBacterium)) {
					i = i-1;
					invalidPlacement = true;
				}
			}
			
			if(!invalidPlacement) {
				// Create the type of bacteria required
				switch((int)args[5]){
					// Standard bacteria
					case 1: bactVec.add(new BSimBacterium(newSpeed, newMass, newSize, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params)); break;
					// Particle sensing bacteria
					case 2: bactVec.add(new BSimSensingBacterium(newSpeed, newMass, newSize, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[7])); break;
					// Particle sensing and co-ordinating bacteria
					case 3: bactVec.add(new BSimCoordBacterium(newSpeed, newMass, newSize, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[7], args[8])); break;
					// Particle sensing, co-ordinating and recruiting bacteria
					case 4: bactVec.add(new BSimRecruitBacterium(newSpeed, newMass, newSize, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[7], args[8])); break;
					// Dead bacteria
					case 5: bactVec.add(new BSimDeadBacterium(newSpeed, newMass, newSize, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params)); break;
				}
			}
		}

		// Return the new bacteria set
		return bactVec;
	}
	
	
	/**
	 * Create a single bacterium with given parameters.
	 */
	public static BSimBacterium createBacterium (double[] args, BSimScene scene, BSimParameters params) {
		
		// Variables for each bacterium property
		double newSpeed = params.getBactSpeed();
		double newMass = 0; // Not used
		double newSize = params.getBactSize();
		double[] newPosition = new double[2];
		newPosition[0] = args[0];
		newPosition[1] = args[1];
		double[] newDirection = {0,0};
		double newForceUp, newForceDown;
		double dx,dy;
		double newTumbleSpeed;
		int newRemDt;
		BSimBacterium newBact = null;
		
		if(args[2] == 0) {
			newSize = params.getBactSize();
		}
		else{
			newSize = args[2];
		}
		
		dx = (2.0 * Math.random()) - 1.0;
		dy = (2.0 * Math.random()) - 1.0;

		newDirection[0] = dx / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)));
		newDirection[1] = dy / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)));

		newForceUp = params.getBactForceUp();
		newForceDown = params.getBactForceDown();

		newTumbleSpeed = 0.0;
		
		newRemDt = BSimUtils.roundToDtSteps(BSimUtils.expRandVar(0.83), params.getDtSecs());
		
		// Create the type of bacteria required
		switch((int)args[3]){
			// Standard bacteria
			case 1: newBact = new BSimBacterium(newSpeed, newMass, newSize, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params); break;
			// Particle sensing bacteria
			case 2: newBact = new BSimSensingBacterium(newSpeed, newMass, newSize, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[4]); break;
			// Particle sensing and co-ordinating bacteria
			case 3: newBact = new BSimCoordBacterium(newSpeed, newMass, newSize, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[4], args[5]); break;
			// Particle sensing, co-ordinating and recruiting bacteria
			case 4: newBact = new BSimRecruitBacterium(newSpeed, newMass, newSize, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[4], args[5]); break;
			// Dead bacteria
			case 5: newBact = new BSimDeadBacterium(newSpeed, newMass, newSize, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params); break;
		}
		
		return newBact;
	}
}
