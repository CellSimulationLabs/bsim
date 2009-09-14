package bsim.mixin;

import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimMixin;
import bsim.BSimParticle;
import bsim.BSimUtils;

/**
 * Public methods:
 * 	BSimRunTumbleMixin#getMotionState()
 * 	BSimRunTumbleMixin#runTumble() 
 */
public class BSimRunTumbleMixin extends BSimMixin {
	
	protected enum MotionState { RUNNING, TUMBLING }	
	protected MotionState motionState = MotionState.RUNNING;	
				
	/* 
	 * Bottom of p86, 'Random Walks in Biology', Howard C. Berg:
	 * 'The distribution of run (or tumble) intervals is exponential, and
	 * the duration of a given interval does not depend on the durations
	 * of intervals that precede it. Evidently, the probability per unit time
	 * that a run (or tumble) will end is constant.'
	 * 
	 * Probability of ending an Xxx in t -> t+dt = lambdaXxx*dt
	 * so lambdaXxx is the probability per unit time of ending an Xxx
	 * 
	/* 1/(mean run length) for AW405 Wild type (Chemotaxis in Escherichia Coli, Berg et al.) */
	protected double lambdaRun = 1/0.86; // 1/seconds
	/* 1/(mean twiddle length) for AW405 Wild type (Chemotaxis in Escherichia Coli, Berg et al.) */
	protected double lambdaTumble = 1/0.14; // 1/seconds
		
	protected Motor motor = new Motor();
	protected class Motor {
		/*
		 * Magnitude of the force exerted by the bacteria whilst running (see BSimRunTumbleMixin#motor()). 
		 * Calculated from Stokes law F = 6*PI*radius*viscosity*speed with a radius of 1 micron, 
		 * a viscosity of 1e-3 Pa s and a speed of 20 microns/s 
		 */
		protected double forceMagnitude = 0.37; // pN
		protected Vector3d direction;
		
		/**
		 * Applies the motor force on the particle
		 */
		protected void apply() {				
			Vector3d f = new Vector3d();		
			f.scale(forceMagnitude, direction);
			particle.addForce(f);	
		}
		
		/**
		 * Sets the motor direction to the direction of the vector v
		 */
		protected void setDirection(Vector3d v) {
			Vector3d x = new Vector3d(v); 
			x.normalize();
			this.direction = x;
		}
		
	}			
	public BSimRunTumbleMixin(BSim sim, BSimParticle particle) {
		super(sim, particle);
		motor.setDirection(new Vector3d(Math.random(),Math.random(),Math.random()));
	}
		
	public MotionState getMotionState() {
		return motionState;
	}		
	
	/**
	 * Causes the particle to run and tumble like a bacterium
	 */
	public void runTumble() {
		switch(motionState) {
		case RUNNING:
			if(Math.random() < lambdaRun*sim.getDt())
				motionState = MotionState.TUMBLING;
			break;
		case TUMBLING:
			if(Math.random() < lambdaTumble*sim.getDt()) {
				BSimUtils.rotate(motor.direction, tumbleAngle());
				motionState = MotionState.RUNNING;
			}
			break;
		default:
			assert false : motionState;
		}
		
		if(motionState == MotionState.RUNNING) {
			motor.apply();
			/* Sets the motor direction from the total force on the particle at this stage, which
			 * may include external contributions in addition to the motor force (if it doesn't, 
			 * the new direction is the same as the old) 
			 */
			motor.setDirection(particle.getForce());
		}
	}

	/**
	 * Return a tumble angle distributed according to Fig. 3, 
	 * Chemotaxis in Escherichia Coli, Berg et al.)
	 */
	protected double tumbleAngle() {	
		double tumbleShape = 4;
		double tumbleScale = 18.32;
		double tumbleLocation = -4.60;
		
		double tumbleAngle;
		do {
			tumbleAngle = BSimUtils.sampleGamma(tumbleShape, tumbleScale) + tumbleLocation;
		} while (tumbleAngle > 180);		
		
		return Math.toRadians(tumbleAngle);
	}	

}