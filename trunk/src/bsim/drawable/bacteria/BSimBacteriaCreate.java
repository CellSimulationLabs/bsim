/**
 * BSimBacteriaCreate.java
 *
 * Class to hold static methods to generate inital bacteria starting conditions.
 *
 * Authors: Thomas Gorochowski
 * 			Mattia Fazzini(Update)
 * Created: 20/07/2008
 * Updated: 07/08/2009
 */
package bsim.drawable.bacteria;

import java.util.Vector;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import bsim.BSimParameters;
import bsim.BSimScene;
import bsim.BSimUtils;
import bsim.physics.BSimParticle;


public class BSimBacteriaCreate {

	
	/**
	 * Create a bacteria set with given parameters (uniform random distribution). No 
	 * checking of overlapping beads is performed meaning initial simulation output 
	 * should be investigated if strange movement occurs.
	 */
	public static Vector createBacteriaSet (double[] args, BSimScene scene, BSimParameters params) {
		
		// Variables for each bead property		
		double newRadius = params.getBactRadius();
		Point3d newPosition;
		Vector3d newDirection;
		double newForceUp, newForceDown;
		double dx,dy,dz;
		double newTumbleSpeed;
		int newRemDt = 1;
		Vector bactVec = new Vector();
		Vector beadVec = scene.getBeads();
		
		if(args[8] == 0) {
			newRadius = params.getBactRadius();
		}
		else{
			newRadius = args[8];
		}		
		
		// Loop through the number of bacteria to create
		for(int i=0; i<args[6]; i++){
			boolean invalidPlacement = false;
			
			// Randomly select a new position for the bacterium
			newPosition = new Point3d(
					args[0] + (args[3] * Math.random()),
					args[1] + (args[4] * Math.random()),
					args[2] + (args[5] * Math.random())
				);
			
			dx = (2.0 * Math.random()) - 1.0;
			dy = (2.0 * Math.random()) - 1.0;
			dz = (2.0 * Math.random()) - 1.0;

			newDirection = new Vector3d(
							dx / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)+ Math.pow(dz,2.0))),
							dy / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)+ Math.pow(dz,2.0))),
							dz / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)+ Math.pow(dz,2.0)))
						);

			newForceUp = params.getBactForceUp();
			newForceDown = params.getBactForceDown();

			newTumbleSpeed = 0.0;
			
			newRemDt = BSimUtils.roundToDtSteps(BSimUtils.expRandVar(0.83), params.getDtSecs());
			
			BSimBacterium thisBacterium = new BSimBacterium(newPosition, newRadius, newDirection, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params);
			
			for(int j=0; j<beadVec.size(); j++) {
				if(BSimUtils.particlesIntersecting((BSimParticle)beadVec.elementAt(j), (BSimParticle)thisBacterium)) {
					i = i-1;
					invalidPlacement = true;
				}
			}
			
			if(!invalidPlacement) {
				// Create the type of bacteria required
				switch((int)args[7]){
					// Standard bacteria
					case 1: bactVec.add(new BSimBacterium(newPosition, newRadius, newDirection, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params)); break;
					// Bead sensing bacteria
					case 2: bactVec.add(new BSimSensingBacterium(newPosition, newRadius, newDirection, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[9])); break;
					// Bead sensing and co-ordinating bacteria
					case 3: bactVec.add(new BSimCoordBacterium(newPosition, newRadius, newDirection, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[9], args[10])); break;
					// Bead sensing, co-ordinating and recruiting bacteria
					case 4: bactVec.add(new BSimRecruitBacterium(newPosition, newRadius, newDirection, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[9], args[10])); break;
					// GRN bacs (test)
					case 5: bactVec.add(new BSimRepBacterium(newPosition, newRadius, newDirection, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params)); break;
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
		double newRadius = params.getBactRadius();
		Point3d newPosition;;		
		Vector3d newDirection;
		double newForceUp, newForceDown;		
		double dx,dy,dz;
		double newTumbleSpeed;
		int newRemDt;
		BSimBacterium newBact = null;
		
		if(args[3] == 0) {
			newRadius = params.getBactRadius();
		}
		else{
			newRadius = args[3];
		}
		
		newPosition = new Point3d(args[0], args[1], args[2]);
				
		dx = (2.0 * Math.random()) - 1.0;
		dy = (2.0 * Math.random()) - 1.0;
		dz = (2.0 * Math.random()) - 1.0;

		newDirection = new Vector3d(
				dx / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)+ Math.pow(dz,2.0))),
				dy / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)+ Math.pow(dz,2.0))),
				dz / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)+ Math.pow(dz,2.0)))
		);

		newForceUp = params.getBactForceUp();
		newForceDown = params.getBactForceDown();

		newTumbleSpeed = 0.0;
		
		newRemDt = BSimUtils.roundToDtSteps(BSimUtils.expRandVar(0.83), params.getDtSecs());
		
		// Create the type of bacteria required
		switch((int)args[4]){
			// Standard bacteria
			case 1: newBact = new BSimBacterium(newPosition, newRadius, newDirection, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params); break;
			// Bead sensing bacteria
			case 2: newBact = new BSimSensingBacterium(newPosition, newRadius, newDirection, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[5]); break;
			// Bead sensing and co-ordinating bacteria
			case 3: newBact = new BSimCoordBacterium(newPosition, newRadius, newDirection, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[5], args[6]); break;
			// Bead sensing, co-ordinating and recruiting bacteria
			case 4: newBact = new BSimRecruitBacterium(newPosition, newRadius, newDirection, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[5], args[6]); break;
		}
		
		return newBact;
	}
}
