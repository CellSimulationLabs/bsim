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
 * Created: 12/07/2008
 * Updated: 14/08/2008
 */


//Define the location of the class in the bsim package
package bsim.object;

//Import the bsim packages used
import bsim.*;
import bsim.object.*;
import bsim.logic.*;

//Standard packages required by the application
import java.awt.*;
import java.lang.Math;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class BSimBacterium extends BSimObject implements BSimLogic {

	// Propulsive force that the bacterium can produce; this is a function of
	// size, as well as viscosity and speed (which are fixed)
	protected double forceMagnitudeDown = 0.0;
	protected double forceMagnitudeUp = 0.0;

	// Variables related to the state of the bacterium
	public static int BAC_STATE_RUNNING  = 1;
	public static int BAC_STATE_TUMBLING = 2;
	protected int state = BAC_STATE_RUNNING;
	protected int memoryLength = 4;

	// Speed of tumbling
	protected double tumbleSpeed = 0.0;

	// Number of time steps remaining in current phase
	protected int remDt = 0;

	// Variable related to chemotactic state
	public static int BAC_CHEMO_ISO = 1;
	public static int BAC_CHEMO_GOAL = 2;
	public static int BAC_CHEMO_RECRUIT = 3;
	protected int chemo = BAC_CHEMO_GOAL;
	
	// Concentration of chemical field of interest at previous position
	protected double previousConc;
	protected Vector concMemory;
	
	protected static double[] gammaVals = new double[1000];
	
	// The simulation scene that will be updated
	protected BSimScene scene;
	
	// Probabilities for determining whether a run will continue this time step
	protected static double isoRunProb;
	protected static double upRunProb;
	protected static double downRunProb;
	
	// Parameters for the simulation
	protected BSimParameters params;
	
	protected boolean memToReset = true;
	
	protected boolean runUp = false;


	/**
	 * General constructor.
	 */
	public BSimBacterium(double newSpeed, double newMass, double newSize,
			double[] newDirection, double[] newPosition, double newForceMagnitudeDown,
			double newForceMagnitudeUp,
			int newState, double newTumbleSpeed, int newRemDt, BSimScene newScene, 
		    BSimParameters newParams) {

		// Call the parent constructor with the basic properties	
		super(newSpeed, newMass, newSize, newDirection, newPosition, BSimObject.OBTYPE_BACT);

		// Update extended properties
		forceMagnitudeDown = newForceMagnitudeDown;
		forceMagnitudeUp = newForceMagnitudeUp;
		state          = newState;
		tumbleSpeed    = newTumbleSpeed;
		remDt          = newRemDt;
		previousConc   = 0.0;
		concMemory 	   = new Vector();
		scene = newScene;
		params = newParams;
		
		// Calcuate the run probabilities from the run lengths
		isoRunProb = 1 - newScene.getDtSec()/params.getIsoRunLength(); //Math.pow(0.5, newScene.getDtSec()/params.getIsoRunLength());
		upRunProb = 1 - newScene.getDtSec()/params.getUpRunLength(); //Math.pow(0.5, newScene.getDtSec()/params.getUpRunLength());
		downRunProb = 1 - newScene.getDtSec()/params.getDownRunLength(); //Math.pow(0.5, newScene.getDtSec()/params.getDownRunLength());
		
		// Check to see if the gamma distribution has been read in 
		if(gammaVals[0] == 0){
			// Read distribution values from external text file
			readGammaVals();
		}
		
		
		// It needs to be reset to the current concentration
		memToReset = true;
		
	}


	/**
	 * Implements the BSimLogic interface. In this case it merely carries out
	 * the standard chemotaxis toward fGoal gradient. The internal force of the bacterium
	 * at a timestep is returned.
	 */
	public double[] runLogic ( boolean contactBac, 
	                           boolean contactPart,
	                           boolean contactBoundary ) {
		
		if(memToReset){
			// Set the initial memory for the bacteria to current concentration of
			// goal attractant
			double curConc;
			if(chemo == BAC_CHEMO_GOAL) curConc = scene.getGoalField().getConcentration(this.getCentrePos());
			else if(chemo == BAC_CHEMO_RECRUIT) curConc = scene.getRecruitmentField().getConcentration(this.getCentrePos());
			else curConc = scene.getGoalField().getConcentration(this.getCentrePos());
			concMemory = new Vector();
			for(int i=0; i<=(4.0 / params.getDtSecs()); i++) {concMemory.add(curConc);}
			memToReset = false;
		}
		
		return iterateBacterium();
	}


	/**
	 * Iterate the motion of the bacterium.
	 */
	protected double[] iterateBacterium() {

		// Check to see if current phase still has remaining time; if not start new phase
		if (this.remDt == 0) startNewPhase();

		// Perform tumbling if applicable
		if (this.state == BAC_STATE_TUMBLING) {
			iterateTumble();
			// Update the remaining time steps for the current phase
			this.remDt = remDt - 1;
		}

		// Find the swimming force vector of the bacterium, if any
		double[] internalForce = {0.0, 0.0};

		if (this.state == BAC_STATE_RUNNING) internalForce = this.doRun();
		
		return internalForce;
	}
	
	
	/**
	 * Iterate a bacterium's tumbling phase
	 */
	protected void iterateTumble() {
		double[] curDirection = this.getDirection();
		double[] newDirection = new double[2];

		// Get the angle (direction)
		double theta = Math.atan2(curDirection[1],curDirection[0]);

		// Find and update the new direction of the bacterium
		newDirection[0] = Math.cos(theta + tumbleSpeed);
		newDirection[1] = Math.sin(theta + tumbleSpeed);
		setDirection(newDirection);
	}


	/**
	 * Start a new phase (running or tumbling)
	 */
	protected void startNewPhase() {

		if (state == BAC_STATE_RUNNING) {
			// Change state; Switch from run to tumble

			// Calculate tumble angle (only approximates distribution)
			double tumbleAngle = approxTumbleAngle();

			// Update the state and other properties of the bacterium
			this.setRemDt(BSimUtils.roundToDtSteps(BSimUtils.expRandVar(0.14), scene.getDtSec()));
			this.setTumbleSpeed(tumbleAngle/remDt);
			this.setState(BAC_STATE_TUMBLING);

		} else if (state == BAC_STATE_TUMBLING) {
			// Change state; Switch from tumble to run
			// Update the state and other properties of the bacterium
			this.setRemDt(1);
			this.setState(BAC_STATE_RUNNING);

		} else {System.err.println("State conflict in run and tumble");}
	}
	
	
	/**
	 *  Method determining whether the bacterium senses it is moving up/down a gradient
	 *  or thinks it is in an isotropic environment. Returns the probability of the
	 *  run continuing in the next time step. Works by measuring the difference between
	 *  the the average aspartate concentration of the last second and the previous 3 seconds.
	 */
	protected double senseRunContinueProb() {
		double shortTermMean;
		double longTermMean;
		double shortTermCounter = 0.0;
		double longTermCounter = 0.0;
		double shortTermMemoryLength = 1.0; // seconds
		double longTermMemoryLength = 3.0; // seconds
		double sensitivity = 0.000001;
		
		for(int i=0; i<concMemory.size();i++) {
			if(i <= (longTermMemoryLength/params.getDtSecs())) {
				longTermCounter = longTermCounter + (Double)concMemory.elementAt(i);
			} else shortTermCounter = shortTermCounter + (Double)concMemory.elementAt(i);
		}
		
		shortTermMean = shortTermCounter / (1 + (shortTermMemoryLength/params.getDtSecs()));
		longTermMean = longTermCounter / (longTermMemoryLength/params.getDtSecs());
		
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
	
	
	/**
	 * Decide whether to continue run
	 */
	protected double[] doRun() {
		
		int chemoState = chemo;
		double prevConc = previousConc;
		BSimChemicalField field;
		double currConc, prob;
		double[] internalForce = new double[2];

		// Check the chemotaxis state, i.e. which to follow - goal or recruitment
		if(chemoState == BAC_CHEMO_GOAL) field = scene.getGoalField();
		else if(chemoState == BAC_CHEMO_RECRUIT) field = scene.getRecruitmentField();
		else field = scene.getGoalField();
		
		currConc = field.getConcentration(this.getCentrePos());
		
		this.addToConcMemory(currConc);
		
		prob = senseRunContinueProb();
		
		// Test whether to continue or terminate run
		if(Math.random()<prob) {	// CONTINUE RUN
			if(runUp){
				internalForce[0] = direction[0] * forceMagnitudeUp;
				internalForce[1] = direction[1] * forceMagnitudeUp;
			}
			else{
				internalForce[0] = direction[0] * forceMagnitudeDown;
				internalForce[1] = direction[1] * forceMagnitudeDown;
			}
		} else {					// TERMINATE RUN
			internalForce[0] = 0.0;
			internalForce[1] = 0.0;
			startNewPhase();
		}
		this.setLastConc(currConc);

		return internalForce;
	}
	
	
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
	
	
	/*
     * Read in file containing gamma distributed values to precision 0.001
     */
    public static void readGammaVals() {
    	
    	int i = 0;
    	
		// Read in the file to the specified array
    	try {
    		Scanner scan = new Scanner(new File("bsim/gammaVals.txt"));
    		while (scan.hasNextDouble()) {
    			gammaVals[i] = scan.nextDouble();		 
    			i++;
    		}
    	} catch (IOException ex){
    		ex.printStackTrace();
    	}
    }


	/**
	 * Redraws the bacterium. A small red circle is also drawn to represent the direction
	 * of the bacteria.
	 */
	public void redraw(Graphics g) {

		// Draw the main shape of bacterium
		g.setColor(Color.GREEN);
		g.fillOval((int)position[0],(int)position[1],(int)(size),(int)(size));

		// Draw an indicator of bacterium's direction
		int x1,x2;
		double littleR = size/5.0;
		x1 = (int)(position[0] + (size/2.0)*(1+direction[0]) - (littleR/Math.sqrt(2.0)));
		x2 = (int)(position[1] + (size/2.0)*(1+direction[1]) - (littleR/Math.sqrt(2.0)));
		g.setColor(Color.RED);
		g.fillOval(x1,x2,(int)(littleR*2.0),(int)(littleR*2.0));
	}
	
	
	/**
	 *  Add a concentration to the bacterium's memory, removing the last 
	 *  element if it was too long ago for the bacterium to remember
	 */
	public void addToConcMemory(double currentConc) {
		concMemory.remove(0);
		concMemory.add(currentConc);
	}
	  
	                                                  
	/**
	 * Standard set methods for the class.
	 */
	public void setState (int newState){ state = newState; }
	public void setTumbleSpeed (double newTumbleSpeed){ tumbleSpeed = newTumbleSpeed; }
	public void setRemDt (int newRemDt){ remDt = newRemDt; }
	public void setForceMagnitudeUp (double newForceMagnitudeUp){ forceMagnitudeUp = newForceMagnitudeUp; }
	public void setForceMagnitudeDown (double newForceMagnitudeDown){ forceMagnitudeDown = newForceMagnitudeDown; }
	public void setLastConc (double newLastConc){ previousConc = newLastConc; }
	public void setChemo (int newChemo){ chemo = newChemo; }
	public void setMemToReset (boolean newReset) { memToReset = newReset; }


	/**
	 * Standard get methods for the class.
	 */
	public int getState (){ return state; }
	public double getTumbleSpeed (){ return tumbleSpeed; }
	public int getRemDt (){ return remDt; }
	public double getForceMagnitudeUp (){ return forceMagnitudeUp; }
	public double getForceMagnitudeDown (){ return forceMagnitudeDown; }
	public double getLastConc (){ return previousConc; }
	public int getChemo (){ return chemo; }
	public Vector getConcMemory() {return concMemory;}
	public boolean getMemToReset() {return memToReset; }
}