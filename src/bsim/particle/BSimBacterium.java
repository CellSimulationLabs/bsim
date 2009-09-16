package bsim.particle;

import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.BSimParticle;
import bsim.BSimUtils;

public class BSimBacterium extends BSimParticle {
	
	/* MOVEMENT */
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
	
	/* CHEMOTAXIS */
	/** The chemical field that the bacterium responds to */
	protected BSimChemicalField field;
	/** Memory of previous concentrations of the chemical field */ 
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
	/** Probability per per unit time of ending a run otherwise */
	protected double pEndRunElse = 1/0.86; // 1/seconds
	/** Probability per per unit time of ending a tumble */
	protected double pEndTumble = 1/0.14; // 1/seconds
	/** Probability per per unit time of ending a run */
	public double pEndRun() {
		if(field != null && movingUpGradient()) return pEndRunUp;
		else return pEndRunElse;
	}
	/** Probability per per unit time of ending a tumble */
	public double pEndTumble() { return pEndTumble; }	
	
			
	/**
	 * Creates a RUNNING bacterium of radius 1 micron at the specified position, facing in a
	 * random direction
	 */
	public BSimBacterium(BSim sim, Vector3d position) {
		super(sim, position, 1); // default radius 1 micron
		setMotionState(MotionState.RUNNING);
		setDirection(new Vector3d(Math.random(),Math.random(),Math.random()));	
	}	
				
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
	 * Respond to this chemical field
	 */
	public void setField(BSimChemicalField field) { 
		this.field = field; 	
		setMemoryDuration(shortTermMemoryDuration, longTermMemoryDuration);
		memory = new double[sim.timesteps(getMemoryDuration())];
		for(int i=0;i<memory.length;i++) memory[i] = field.getConc(position);
	} 
	public void setMemoryDuration(double shortTermMemoryDuration, double longTermMemoryDuration) {
		this.shortTermMemoryDuration = shortTermMemoryDuration;
		this.shortTermMemoryLength = sim.timesteps(shortTermMemoryDuration);
		this.longTermMemoryDuration = longTermMemoryDuration;		
		this.longTermMemoryLength = sim.timesteps(longTermMemoryDuration);
	}
	public void pEndRunUp(double d) { pEndRunUp = d; }
	public void pEndRunElse(double d) { pEndRunElse = d; }
	public void pEndTumble(double d) { pEndTumble = d; }
	
	public Vector3d getDirection() { return direction; }
	public MotionState getMotionState() { return motionState; }	
	public double getMemoryDuration() { return shortTermMemoryDuration + longTermMemoryDuration; }
		
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
		
		brownianForce();
		/**
		 * Sets the cell direction from the total force on the cell at this point, which may
		 * include external contributions in addition to the flagellar force (if it doesn't, 
		 * the new direction is the same as the old) 
		 */
		setDirection(force);
	}
	
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
		memory[0] = field.getConc(position);
		
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

}