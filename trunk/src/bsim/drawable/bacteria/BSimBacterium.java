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
 * Updated: 12/08/2009
 */
package bsim.drawable.bacteria;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import bsim.BSimParameters;
import bsim.BSimScene;
import bsim.BSimUtils;
import bsim.drawable.BSimDrawable;
import bsim.drawable.bacteria.BSimBacterium;
import bsim.drawable.field.BSimChemicalField;
import bsim.logic.BSimLogic;
import bsim.physics.BSimParticle;


public class BSimBacterium extends BSimParticle implements BSimLogic, BSimDrawable {

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
	
	public static int TRILINEAR_ELONGATION=1;
	public static int BILINEAR_ELONGATION=2;
	
	protected int elongationType = 0;
	protected double width = 0;
	
	//values in num of timestep
	public int lifeTime = 0;	
	public int lifeCycleTime = 0;
	protected int tc = 0;
	protected double timeC = 0;
	protected int t2 = 0;
	protected double time2 = 0;
	protected int tg = 0;
	protected double timeG = 0;
	
	protected double a1minute = 0;
	protected double a2minute = 0;
	protected double a3minute = 0;
	protected double a1 = 0;
	protected double a2 = 0;
	protected double a3 = 0;
	
	protected double ltg = 0;
	protected double ltc = 0;
	protected double m1 = 0;
	protected double m2 = 0;
	protected double q1 = 0;
	protected double q2 = 0;



	/**
	 * General constructor for a trilinear elongation bacteria.
	 */
	public BSimBacterium(double newSpeed, double newMass,
			double newL0, double newR, double newTC, double newT2, double newTG, double newa1, double newa2, double newa3, int newElongationType,
			double[] newDirection, double[] newPosition, double newForceMagnitudeDown,
			double newForceMagnitudeUp,
			int newState, double newTumbleSpeed, int newRemDt, BSimScene newScene, 
		    BSimParameters newParams) {

		// Call the parent constructor with the basic properties	
		super(newSpeed, newMass, newL0, newDirection, newPosition, BSimParticle.PART_BACT);
		
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
		
		lifeTime=0;
		lifeCycleTime=0;
		width = newR;
		timeC=newTC;
		time2=newT2;
		timeG=newTG;
		//numb of time step in second
		tc=(int)((timeC*60)/params.getDtSecs());
		t2=(int)((time2*60)/params.getDtSecs());
		tg=(int)((timeG*60)/params.getDtSecs());
		a1minute=newa3;
		a2minute=newa2;
		a3minute=newa3;
		//from minute to second
		a1=a1minute/60;
		a2=a1minute/60;
		a3=a1minute/60;
		elongationType = newElongationType;
	}
	
	/**
	 * General constructor for a bilinear elongation bacteria.
	 */
	public BSimBacterium(double newSpeed, double newMass,
			double newL0, double newLTC, double newLTG, double newR, double newTC, double newTG, int newElongationType,
			double[] newDirection, double[] newPosition, double newForceMagnitudeDown,
			double newForceMagnitudeUp,
			int newState, double newTumbleSpeed, int newRemDt, BSimScene newScene, 
		    BSimParameters newParams) {

		// Call the parent constructor with the basic properties	
		super(newSpeed, newMass, newL0, newDirection, newPosition, BSimParticle.PART_BACT);
		
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
		
		
		lifeTime=0;
		lifeCycleTime=0;
		ltc=newLTC;
		ltg=newLTG;
		width = newR;
		timeC=newTC;
		timeG=newTG;
		//number of time step in second
		tc=(int)((timeC*60)/params.getDtSecs());
		tg=(int)((timeG*60)/params.getDtSecs());
		m1 = (ltc-newL0)/((timeC*60)-0);
		m2 = (ltg-ltc)/((timeG*60)-(timeC*60));
		q1 = newL0;
		q2 = ltg-(m2*(timeG*60));	
		elongationType = newElongationType;
	}


	/**
	 * Implements the BSimLogic interface. In this case it merely carries out
	 * the standard chemotaxis toward fGoal gradient. The internal force of the bacterium
	 * at a timestep is returned.
	 */
	public double[] runLogic ( boolean contactBac, 
	                           boolean contactBead,
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
		double[] internalForce = {0.0, 0.0, 0.0};

		if (this.state == BAC_STATE_RUNNING) internalForce = this.doRun();
		
		return internalForce;
	}
	
	
	/**
	 * Iterate a bacterium's tumbling phase
	 */
	protected void iterateTumble() {
		double[] curDirection = this.getDirection();
		double[] newDirection = new double[3];

		// TODO make curDirection a vector in the first place
		Vector3d curDirectionVector = new Vector3d(curDirection);
		
		// Obtain a random direction perpendicular to curDirection
		Vector3d randomVector = new Vector3d(Math.random(),Math.random(),Math.random());
		Vector3d crossVector = new Vector3d();
		crossVector.cross(curDirectionVector, randomVector);		
		
		// Generate the rotation matrix for rotating about this direction by the tumble angle
		Matrix3d r = new Matrix3d();
		r.set(new AxisAngle4d(crossVector, tumbleSpeed));
		
		// Apply the rotation
		Vector3d newDirectionVector = new Vector3d();		
		r.transform(curDirectionVector,newDirectionVector);
		newDirectionVector.get(newDirection);
		
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
		double[] internalForce = new double[3];

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
				internalForce[2] = direction[2] * forceMagnitudeUp;
			}
			else{
				internalForce[0] = direction[0] * forceMagnitudeDown;
				internalForce[1] = direction[1] * forceMagnitudeDown;
				internalForce[2] = direction[2] * forceMagnitudeDown;
			}
		} else {					// TERMINATE RUN
			internalForce[0] = 0.0;
			internalForce[1] = 0.0;
			internalForce[2] = 0.0;
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
    		Scanner scan = new Scanner(new File("src/bsim/gammaVals.txt"));
    		while (scan.hasNextLine()) {
    			gammaVals[i] = Double.parseDouble(scan.nextLine());		 
    			i++;
    		}
    	} catch (IOException ex){
    		ex.printStackTrace();
    	}
    }
    
	/*
	 * Function to increase Time
	 */
	public void increaseLifeTime(){
		//increase lifeTime every timeStep
		lifeTime++;
		lifeCycleTime++;
	}
	
	public void newLifeCycle(){
		lifeCycleTime=0;
	}
    
	/*
	 * Function to increase the Size
	 */
	public void increaseSize(){
		if(elongationType==BSimBacterium.TRILINEAR_ELONGATION){
			if(lifeCycleTime>t2){
				//third linear phase
				size=(size)+((a3)*(params.getDtSecs()));
				setPosition(position);
			}
			else{
				if(lifeCycleTime>tc){
					//second linear phase
					size=(size)+((a2)*(params.getDtSecs()));
					setPosition(position);
				}
				else{
					//first linear phase
					size=(size)+((a1)*(params.getDtSecs()));
					setPosition(position);
					
				}
			}
		}
		else{
			if(lifeCycleTime>tc){
				//second linear phase
				size= (m2*(lifeCycleTime*params.getDtSecs()))+q2;
				setPosition(position);
			}
			else{
				//second linear phase
				size=(m1*(lifeCycleTime*params.getDtSecs()))+q1;
				setPosition(position);
			}
		}
	}
	
	/*
	 * Replication function
	 */
	public BSimBacterium replicate(BSimScene scene, BSimParameters params){
		double[] newCentrePosBact2 = new double[3];
		double[] newPosition = new double[3];
		double beta = Math.atan2(direction[1],direction[0]);
		double alpha = Math.acos((direction[2])/Math.sqrt(Math.pow(direction[0], 2.0)+Math.pow(direction[1], 2.0)+Math.pow(direction[2], 2.0)));
		this.size=this.size/2;
		newCentrePosBact2[0] = centrePos[0]+((this.size/2)*Math.sin(alpha)*Math.cos(beta));
		newCentrePosBact2[1] = centrePos[1]+((this.size/2)*Math.sin(alpha)*Math.sin(beta));
		newCentrePosBact2[2] = centrePos[2]+((this.size/2)*Math.cos(alpha));
		newPosition[0]=newCentrePosBact2[0]-(this.size/2);
		newPosition[1]=newCentrePosBact2[0]-(this.size/2);;
		newPosition[2]=newCentrePosBact2[0]-(this.size/2);;
		centrePos[0] = centrePos[0]-((this.size/2)*Math.sin(alpha)*Math.cos(beta));
		centrePos[1] = centrePos[1]-((this.size/2)*Math.sin(alpha)*Math.sin(beta));
		centrePos[2] = centrePos[2]-((this.size/2)*Math.cos(alpha));
		setCentrePos(centrePos);
		BSimBacterium newBact = null;
		if(elongationType==BSimBacterium.TRILINEAR_ELONGATION){
			//inherit the trilinear elongation
			newBact = new BSimBacterium(this.speed, this.mass, this.size, this.width, this.timeC, this.time2, this.timeG, this.a1minute, this.a2minute, this.a3minute, this.elongationType, this.direction, newPosition, this.forceMagnitudeDown, this.forceMagnitudeUp, this.state, this.speed, this.remDt, scene, params);
		}
		else{
			//inherit the bilinear elongation
			newBact = new BSimBacterium(this.speed, this.speed, this.size, this.ltc, this.ltg, this.width, this.timeC, this.timeG, this.elongationType, this.direction, newPosition, this.forceMagnitudeDown, this.forceMagnitudeUp, this.state, this.speed, this.remDt, scene, params);
		}
		newBact.startNewPhase();
		scene.setReallocateNewForceMat(true);
		return newBact;
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
		//int x1,x2;
		//double littleR = size/5.0;
		//x1 = (int)(position[0] + (size/2.0)*(1+direction[0]) - (littleR/Math.sqrt(2.0)));
		//x2 = (int)(position[1] + (size/2.0)*(1+direction[1]) - (littleR/Math.sqrt(2.0)));
		//g.setColor(Color.RED);
		//g.fillOval(x1,x2,(int)(littleR*2.0),(int)(littleR*2.0));
		//System.out.println(position[0]+" "+position[1]+" "+position[2]);
		//System.out.println(centrePos[0]+" "+centrePos[1]+" "+centrePos[2]);	
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
	public int getLifeCycleTime() {return lifeCycleTime;}
	public int getTg() {return tg;}
}