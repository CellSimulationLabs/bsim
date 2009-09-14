package bsim.mixin;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimMixin;
import bsim.BSimParticle;
import bsim.BSimUtils;


public class BSimRunTumbleMixin extends BSimMixin {
	
	protected enum MotionState { RUNNING, TUMBLING }	
	protected MotionState motionState = MotionState.RUNNING;
	protected Vector3d direction;
			
	protected double motorForce = 0.41; // pN
	
	// Probability of switching state in t -> t+dt = lambda(state)*dt
	protected double lambdaRun = 1/0.86; // 1/s
	protected double lambdaTumble = 1/0.14; // 1/s	
		
	// Parameters for the tumbling distribution
	protected double tumbleShape = 4;
	protected double tumbleScale = 18.32;
	protected double tumbleLocation = -4.60;	
	
	public BSimRunTumbleMixin(BSim sim, BSimParticle particle) {
		super(sim, particle);
		setDirection(new Vector3d(Math.random(),Math.random(),Math.random()));
	}
	
	protected void setDirection(Vector3d d) {
		d.normalize();
		direction = d;
	}
		
	public MotionState getMotionState() {
		return motionState;
	}		
	
	/**
	 * Causes the particle to run and tumble like a bacterium
	 */
	public void runOrTumble() {
		switch(motionState) {
		case RUNNING:
			if(Math.random() < lambdaRun*sim.getDt())
				motionState = MotionState.TUMBLING;
			break;
		case TUMBLING:
			if(Math.random() < lambdaTumble*sim.getDt()) {
				tumble();
				motionState = MotionState.RUNNING;
			}
			break;
		default:
			assert false : motionState;
		}
		
		if(motionState == MotionState.RUNNING) run();
	}

	protected void run() {				
		Vector3d f = new Vector3d();		
		f.scale(motorForce, direction);
		particle.addForce(f);
		direction.set(particle.getForce());
		direction.normalize();	
	}

	protected void tumble() {		
		// Obtain a random direction perpendicular to current direction		
		Vector3d randomVector = new Vector3d(Math.random(),Math.random(),Math.random());
		Vector3d crossVector = new Vector3d();
		crossVector.cross(direction, randomVector);		

		// Generate the rotation matrix for rotating about this direction by the tumble angle
		Matrix3d r = new Matrix3d();
		double tumbleAngle;
		do {
			tumbleAngle = BSimUtils.sampleGamma(tumbleShape, tumbleScale) + tumbleLocation;
		} while (tumbleAngle > 180);
		
		tumbleAngle = tumbleAngle / 180 * Math.PI;
		r.set(new AxisAngle4d(crossVector, tumbleAngle));

		// Apply the rotation			
		r.transform(direction);			
	}
	

}