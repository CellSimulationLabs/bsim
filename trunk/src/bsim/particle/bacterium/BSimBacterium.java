/**
 * BSimBacterium.java
 *
 * Class that represents a bacterium in our simulation. This class implements attraction to
 * the goal chemoattractant. This class should be sub-classed to implement other GRNs,
 * such as the recruitment, or time delays, etc.
 *
 * Authors: Thomas Gorochowski
 *          Ian Miles
 *          Charlie Harrison
 *          Mattia Fazzini(Update)
 * Created: 12/07/2008
 * Updated: 07/08/2009
 */
package bsim.particle.bacterium;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import bsim.BSimParameters;
import bsim.BSimUtils;
import bsim.field.BSimChemicalField;
import bsim.particle.BSimParticle;
import bsim.scene.BSimScene;


public class BSimBacterium extends BSimParticle {
	
	protected Vector3d direction;
	protected Vector<Double> memory; // memory of the concentration of the goal field
	
	// Motion states
	protected static int RUNNING  = 1;
	protected static int TUMBLING = 2;
	protected int motionState = RUNNING; // Start off running
	
	// Set at onset of tumbling phase:
	protected int tumbleSteps; 	// Number of time steps remaining in tumble phase
	protected double tumbleAngle; // Angle remaining in tumble phase		
				
	// Static constants
	protected static double shortTermMemoryDuration = 1.0; // seconds
	protected static double longTermMemoryDuration = 3.0; // seconds
	protected static double sensitivity = 0.000001; // to differences in long term vs short term mean concentrations
	protected static double runProbUp = 1 - BSimParameters.dt/BSimParameters.runLengthUp;
	protected static double runProbDown = 1 - BSimParameters.dt/BSimParameters.runLengthDown;
	protected static double runProbIso = 1 - BSimParameters.dt/BSimParameters.runLengthIso;
	protected static double[] gammaVals = readGammaVals();	

	/**
	 * General constructor.
	 */
	public BSimBacterium(Vector3d newPosition, double newRadius, Vector3d newDirection) {
		super(newPosition, newRadius);		
		direction = newDirection;	
		
		double memorySize = (shortTermMemoryDuration + longTermMemoryDuration) / BSimParameters.dt;
		memory = new Vector((int)(memorySize));		
	}
	
	public void action() {			
		if(motionState == RUNNING) {
			memory.remove(0);
			memory.add(BSimScene.getGoalField().getConcentration(this.getPosition()));			
			run();			
		}
		else if(motionState == TUMBLING) {
			tumble();
		}
	}

	protected void run() {				
		double shortTermMean = BSimUtils.mean(shortTermMemory());
		double longTermMean = BSimUtils.mean(longTermMemory());
		
		if(Math.random() < runProb(shortTermMean, longTermMean)) {
			Vector3d f = new Vector3d();
						
			if(shortTermMean - longTermMean > sensitivity) f.scale(BSimParameters.bactForceUp, direction);			
			else f.scale(BSimParameters.bactForceDown, direction);
			
			this.addForce(f);
			direction.set(getForce());
			direction.normalize();				
		}
		else switchMotionState();
	}
	
	protected double runProb(double shortTermMean, double longTermMean) {				
		if(shortTermMean - longTermMean > sensitivity) return runProbUp;		
		else if(longTermMean - shortTermMean > sensitivity) return runProbDown;		
		else return runProbIso;
	}	
		
	protected Vector<Double> longTermMemory() {
		return (Vector<Double>)memory.subList(0, (int)(longTermMemoryDuration/BSimParameters.dt));
	}
	
	protected Vector<Double> shortTermMemory() {
		return (Vector<Double>)memory.subList((int)(longTermMemoryDuration/BSimParameters.dt), memory.size());
	}	
	
	protected void tumble() {		
		// Obtain a random direction perpendicular to curDirection		
		Vector3d randomVector = new Vector3d(Math.random(),Math.random(),Math.random());
		Vector3d crossVector = new Vector3d();
		crossVector.cross(direction, randomVector);		
		
		// Generate the rotation matrix for rotating about this direction by the tumble angle
		Matrix3d r = new Matrix3d();
		r.set(new AxisAngle4d(crossVector, tumbleAngle/tumbleSteps));
		
		// Apply the rotation			
		r.transform(direction);		
		
		// Update the remaining tumble steps and tumble angle
		tumbleSteps-= 1;
		tumbleAngle-= tumbleAngle/tumbleSteps;
		
		if (tumbleSteps == 0) switchMotionState();		
	}
	
		
	protected void switchMotionState() {			
		if (motionState == RUNNING) {					
			tumbleSteps = (int)Math.ceil(BSimUtils.expRandVar(0.14)/BSimParameters.dt);			
			tumbleAngle = approxTumbleAngle();
			motionState = TUMBLING;
		} else if (motionState == TUMBLING) {
			motionState = RUNNING;		
		}
	}
			
	/**
	 * Returns the current direction of the bacterium
	 */
	public Vector3d getDirection (){ return direction; }
	
	/**
	 * Approximates the new tumble angle based on gamma distributed RV.
	 */
	protected double approxTumbleAngle() {
		double angle;
		
		// Select a random element from the array gammaVals
		int r = (int)Math.floor(Math.random()*1000.0);
		if(r>999) r=999;
		
		angle = Math.toRadians(gammaVals[r]);
		
		// check size and update sign if required
		if(Math.random()>=0.5) return angle;
		else return -angle;
	}
		
	/**
     * Read in file containing gamma distributed values to precision 0.001
     */
    public static double[] readGammaVals() {
    	double[] g = new double[1000];
    	int i = 0;    	
    	try {
    		Scanner scan = new Scanner(new File("src/bsim/resource/gammaVals.txt"));
    		while (scan.hasNextLine()) {
    			g[i] = Double.parseDouble(scan.nextLine());		 
    			i++;
    		}
    	} catch (IOException ex){
    		ex.printStackTrace();
    	}
    	return g;
    }	
}