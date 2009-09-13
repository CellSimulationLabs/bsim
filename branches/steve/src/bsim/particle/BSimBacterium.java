package bsim.particle;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimUtils;


public class BSimBacterium extends BSimParticle {

	private static int RUNNING  = 1;
	private static int TUMBLING = 2;	
	private int motionState = RUNNING; // RUNNING or TUMBLING
	private Vector3d direction;
	
	// Probability of switching state in t -> t+dt = lambda(state)*dt
	private double lambdaRun = 1/0.86; // 1/s
	private double lambdaTumble = 1/0.14; // 1/s
	private double motorForce = 0.41; // pN
		
	private double tumbleShape = 4;
	private double tumbleScale = 18.32;
	private double tumbleLocation = -4.60;
	
	public BSimBacterium(BSim sim, Vector3d position, double radius, Vector3d direction) {
		super(sim, position, radius);	
		setDirection(direction);
	}
	
	private void setDirection(Vector3d d) {
		d.normalize();
		direction = d;
	}
	
	public String getMotionState() {
		if (motionState == RUNNING) return "RUNNING";
		else return "TUMBLING"; //(motionState == TUMBLING)
	}		
	
		
	public void action() {			
		if(motionState == RUNNING) {
			if(Math.random() < lambdaRun*sim.getDt())
				motionState = TUMBLING;
		} else if(motionState == TUMBLING) {
			if(Math.random() < lambdaTumble*sim.getDt()) {
				tumble();
				motionState = RUNNING;
			}
		}
		
		if(motionState == RUNNING) run();
	}

	protected void run() {				
		Vector3d f = new Vector3d();		
		f.scale(motorForce, direction);
		addForce(f);
		direction.set(getForce());
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

	public Vector3d getDirection() { return direction; }


}