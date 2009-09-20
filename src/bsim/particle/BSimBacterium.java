package bsim.particle;

import java.util.Vector;

import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.BSimUtils;

/**
 * Class representing a growing, vesiculating bacterium whose run-tumble motion 
 * is affected in a simple way by a single goal chemical 
 */
public class BSimBacterium extends BSimParticle {
	
	/* 
	 * MOVEMENT including CHEMOTAXIS
	 */
	public static enum MotionState {
		/* Quotes from 'Motile behavior of bacteria', Berg */
		/**
		 * "When the motors turn counterclockwise, the filaments rotate in parallel in a bundle that
		 * pushes the cell body steadily forward, and the cell is said to 'run'"
		 */ RUNNING,		
		/**
		 * "When the motors turn clockwise, the flagellar filaments work independently, and the cell body
		 * moves erratically with little net displacement; the cell is then said to 'tumble'"
		 */ TUMBLING }	
	protected MotionState motionState;			
	
	/**
	 * Magnitude of the flagellar force produced by the cell whilst RUNNING. 
	 * Calculated from Stokes law F = 6*PI*radius*viscosity*speed with a radius of 1 micron, 
	 * a viscosity of 2.7e-3 Pa s and a speed of 20 microns/s (conditions of
	 * 'Chemotaxis in Escherichia Coli', Berg et al.)
	 */
	protected double forceMagnitude = 1; // pN
	/** 
	 * Direction that the cell exerts its flagellar force
	 */
	protected Vector3d direction;
	
	/** Bacteria tend to swim towards higher concentrations of this chemical field */
	protected BSimChemicalField goal;
	/** Memory of previous concentrations of the goal field */ 
	protected double[] memory; // molecules/(micron)^3
	/*
	 * 'Temporal comparisons in bacterial chemotaxis', Segall, Berg et al.:
	 * Cells continuously compare the stimulus experienced during past second with 
	 * that experienced during the previous 3 seconds and respond to the difference	 
	 */
	protected double shortTermMemoryDuration = 1; // seconds
	protected double longTermMemoryDuration = 3; // seconds
	/** sim.timesteps(shortTermMemoryDuration) */
	protected double shortTermMemoryLength;
	/** sim.timesteps(longTermMemoryDuration) */
	protected double longTermMemoryLength;
	/** Sensitivity to differences in sequential averages */
	protected double sensitivity = 1; //molecules/(micron)^3
			
	/*
	 * 
	 * Bottom p86, 'Random Walks in Biology', Berg: (no chemical fields)
	 * "The distribution of run (or tumble) intervals is exponential.. 
	 * the probability per unit time that a run (or tumble) will end is constant."	
	 * 
	 * 'Chemotaxis in Escherichia Coli', Berg et al.:
	 * "When a bacterium moves up the gradient the probability per unit time of the 
	 * termination of a run decreases; when it moves down the gradient the probability
	 * reverts to the value appropriate to an isotropic concentration of similar concentration."
	 * 
	 * The probabilities per unit time for ending a run/tumble could plausibly depend on the value
	 * of the concentration at the particle's location and the derivatives with respect to time and
	 * space. Here we allow only a boolean test for whether the particle is moving
	 * up a chemical gradient in time. This corresponds to the model of Schnizter, Berg et al., 
	 * 'Strategies  for Chemotaxis', p23 but instead of reducing the run termination probability in 
	 * the case of an increasing chemical gradient by an amount proportional to the difference 
	 * between the sequential averages, we set it to a constant pEndRunUp (distinct from pEndRunElse).
	 * 
	 * Values from 'Chemotaxis in Escherichia Coli', Berg et al.
	 */
	/** Probability per per unit time of ending a run when moving up a chemical gradient */
	protected double pEndRunUp = 1/1.07; // 1/seconds
	// mean time to end a run when moving up a gradient = 1/pEndRunUp = 1.07 seconds
	/** Probability per per unit time of ending a run otherwise */
	protected double pEndRunElse = 1/0.86; // 1/seconds
	// mean time to end a run otherwise = 1/pEndRunElse = 0.86 seconds
	/** Probability per per unit time of ending a tumble */
	protected double pEndTumble = 1/0.14; // 1/seconds
	// mean time to end a tumble = 1/pEndTumble = 0.14 seconds
	/** Probability per per unit time of ending a run */
	public double pEndRun() {
		if(goal != null && movingUpGradient()) return pEndRunUp;
		else return pEndRunElse;
	}
	/** Probability per per unit time of ending a tumble */
	public double pEndTumble() { return pEndTumble; }
	/* Setters */
	public void pEndRunUp(double d) { pEndRunUp = d; }
	public void pEndRunElse(double d) { pEndRunElse = d; }
	public void pEndTumble(double d) { pEndTumble = d; }
	
	public void setMotionState(MotionState s) { motionState = s; }
	public void setForceMagnitude(double d) { forceMagnitude = d; }
	/**
	 * Set the direction of the cell to the direction of the vector v
	 */
	public void setDirection(Vector3d v) {
		Vector3d x = new Vector3d(v); 
		x.normalize();
		this.direction = x;
	}		
	/**
	 * Set this chemical field as the goal field
	 */
	public void setGoal(BSimChemicalField goal) { 
		this.goal = goal; 	
		setMemoryDuration(shortTermMemoryDuration, longTermMemoryDuration);
		memory = new double[sim.timesteps(getMemoryDuration())];
		for(int i=0;i<memory.length;i++) memory[i] = goal.getConc(position);
	} 
	public void setMemoryDuration(double shortTermMemoryDuration, double longTermMemoryDuration) {
		this.shortTermMemoryDuration = shortTermMemoryDuration;
		this.shortTermMemoryLength = sim.timesteps(shortTermMemoryDuration);
		this.longTermMemoryDuration = longTermMemoryDuration;		
		this.longTermMemoryLength = sim.timesteps(longTermMemoryDuration);
	}
	
	public Vector3d getDirection() { return direction; }
	public MotionState getMotionState() { return motionState; }	
	public double getMemoryDuration() { return shortTermMemoryDuration + longTermMemoryDuration; }
	
	/**
	 * Applies the flagellar force 
	 */
	public void flagellarForce() {				
		Vector3d f = new Vector3d();		
		f.scale(forceMagnitude, direction);
		addForce(f);	
	}	
	
	/**
	 * Return a tumble angle in radians distributed according to Fig. 3, 'Chemotaxis 
	 * in Escherichia Coli', Berg et al. (claim from 'AgentCell: a digital single-cell 
	 * assay for bacterial chemotaxis', Emonet et al.) 
	 */
	public double tumbleAngle() {	
		double tumbleShape = 4;
		double tumbleScale = 18.32;
		double tumbleLocation = -4.60;
		
		double tumbleAngle;
		do {
			tumbleAngle = BSimUtils.sampleGamma(tumbleShape, tumbleScale) + tumbleLocation;
		} while (tumbleAngle > 180);		
		
		return Math.toRadians(tumbleAngle);
	}	
		
	/**
	 * p23, 'Strategies for Chemotaxis', Schnizter, Berg et al.
	 * Compare two sequential averages
	 */
	public boolean movingUpGradient() {
		double shortTermCounter = 0, shortTermMean = 0;
		double longTermCounter = 0, longTermMean = 0;
		
		System.arraycopy(memory, 0, memory, 1, memory.length - 1);
		memory[0] = goal.getConc(position);
		
		for(int i=0; i<memory.length; i++) {
			if(i < shortTermMemoryLength) {
				shortTermCounter = shortTermCounter + memory[i];				
			}
			else {
				assert (i < shortTermMemoryLength + longTermMemoryLength);
				longTermCounter = longTermCounter + memory[i];
			}
		}		
		shortTermMean = shortTermCounter/shortTermMemoryLength;
		longTermMean = longTermCounter/longTermMemoryLength;
        
		return shortTermMean - longTermMean > sensitivity;
	}
	
	
	
	
	/* 
	 * GROWTH and VESICULATION 
	 */		
	protected double radiusGrowthRate = 1e-3; // microns/s;
	// corresponds to a surface area growth of about 5 vesicle surface areas / second
	/** Probability per typical vesicle surface area growth (0.005 microns^2) of producing a vesicle */
	protected double pVesicle = 0.1; // 1/(typical vesicle surface areas)
	// mean growth before producing a vesicle = 1/pVesicle = 10 vesicle surface areas ~ 2 seconds 
	protected Vector<BSimVesicle> vesicleList; // the external list of vesicles (sorry, it's the cleanest way)

	public void setRadiusGrowthRate(double d) { radiusGrowthRate = d; }
	public void pVesicle(double d) { pVesicle = d; }
	public void setVesicleList(Vector<BSimVesicle> v) { vesicleList = v; }
	
	public void grow() {
		double oldSurfaceArea = getSurfaceArea();
		radius += radiusGrowthRate*sim.getDt();
		double newSurfaceArea = getSurfaceArea();
		double dS = newSurfaceArea - oldSurfaceArea;
		if(vesicleList != null && Math.random() < pVesicle*(dS/0.005))
			vesiculate();		
	}
	
	public void vesiculate() {
		double r = vesicleRadius();
		vesicleList.add(new BSimVesicle(sim, new Vector3d(position), r));
		setRadiusFromSurfaceArea(getSurfaceArea()-surfaceArea(r));
	}
	
	public double vesicleRadius() {
		return 0.02;
	}
	
	
	
			
	/**
	 * Creates a RUNNING bacterium of radius 1 micron at the specified position, facing in a
	 * random direction
	 */
	public BSimBacterium(BSim sim, Vector3d position) {
		super(sim, position, 1); // default radius 1 micron
		setMotionState(MotionState.RUNNING);
		setDirection(new Vector3d(0.5-Math.random(),0.5-Math.random(),0.5-Math.random()));	
	}				
		
	@Override
	public void action() {
		switch(motionState) {
		case RUNNING:
			if(Math.random() < pEndRun()*sim.getDt())
				motionState = MotionState.TUMBLING;
			break;
		case TUMBLING:
			if(Math.random() < pEndTumble()*sim.getDt()) {
				/* Change the direction at the end of a tumble phase */
				BSimUtils.rotate(direction, tumbleAngle());
				motionState = MotionState.RUNNING;
			}
			break;
		default:
			assert false : motionState;
		}
		
		if(motionState == MotionState.RUNNING) flagellarForce();	
		
		grow();
	}
	

}