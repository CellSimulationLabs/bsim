package bsim.mixin;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimParticle;
import bsim.BSimUtils;


public class BSimRunTumbleMixin {
	
	private BSim sim;
	private BSimParticle particle;

	private enum MotionState { RUNNING, TUMBLING }	
	private MotionState motionState = MotionState.RUNNING;
	private Vector3d direction;
			
	private double motorForce = 0.41; // pN
	
	// Probability of switching state in t -> t+dt = lambda(state)*dt
	private double lambdaRun = 1/0.86; // 1/s
	private double lambdaTumble = 1/0.14; // 1/s	
		
	// Parameters for the tumbling distribution
	private double tumbleShape = 4;
	private double tumbleScale = 18.32;
	private double tumbleLocation = -4.60;	
	
	public BSimRunTumbleMixin(BSim sim, BSimParticle particle) {
		this.sim = sim;
		this.particle = particle;
		setDirection(new Vector3d(Math.random(),Math.random(),Math.random()));
	}
	
	private void setDirection(Vector3d d) {
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

	private void run() {				
		Vector3d f = new Vector3d();		
		f.scale(motorForce, direction);
		particle.addForce(f);
		direction.set(particle.getForce());
		direction.normalize();	
	}

	private void tumble() {		
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