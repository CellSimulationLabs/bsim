package bsim.exert;

import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimExerter;
import bsim.BSimParticle;
import bsim.BSimUtils;

/** 
 * 'Motile behavior of bacteria', Howard C. Berg:
 * "When the motors turn CW, the flagellar filaments work independently, and the cell body
 *  moves erratically with little net displacement; the cell is then said to 'tumble'. When 
 *  the motors turn CCW, the filaments rotate in parallel in a bundle that pushes the cell 
 *  body steadily forward, and the cell is said to 'run'. The two modes alternate. The cell 
 *  runs and tumbles, executing a three-dimensional random walk.
 *  	When different flagellar motors in the same cell are studied under conditions in 
 *  which they cannot interact mechanically, they change directions independently. Yet,
 *  when a flagellar bundle drives the cell forward, all of the motors have to rotate CCW. 
 *  The events that bring about this coordination are not yet understood. The mean run interval
 *  is about 1 s, whereas the mean tumble interval is only about 0.1 s. Both of the times are 
 *  exponentially distributed."	
 *  
 * Bottom of p86, 'Random Walks in Biology', Howard C. Berg:
 * "The distribution of run (or tumble) intervals is exponential, and
 * the duration of a given interval does not depend on the durations
 * of intervals that precede it. Evidently, the probability per unit time
 * that a run (or tumble) will end is constant."
 * 
 */
public class BSimFlagella extends BSimExerter {
	
	public static enum State { BUNDLED, APART }
	protected State state;	
				
	/*
	 * Probability of switching from state X to state Y in t -> t+dt = lambdaXY*dt
	 * lambdaXY is the probability per unit time of switching state X to state Y
	 */
	protected double lambdaBundledApart = 1/1; // 1/seconds
	protected double lambdaApartBundled = 1/0.1; // 1/seconds	

	/**
	 * Magnitude of the force exerted by the flagella in the BUNDLED state. 
	 * Calculated from Stokes law F = 6*PI*radius*viscosity*speed with a radius of 1 micron, 
	 * a viscosity of 2.7e-3 Pa s and a speed of 20 microns/s 
	 */
	protected double bundleForceMagnitude = 1; // pN
	/** 
	 * Direction of the bundle
	 */
	protected Vector3d bundleDirection;
			
	public BSimFlagella(BSim sim, BSimParticle particle) {
		super(sim, particle);
		setState(State.BUNDLED);
		setBundleDirection(new Vector3d(Math.random(),Math.random(),Math.random()));
	}	
			
	public void setState(State s) { state = s; }
	public void setBundleForceMagnitude(double d) { bundleForceMagnitude = d; }
	public void setBundleDirection(Vector3d v) {
		Vector3d x = new Vector3d(v); 
		x.normalize();
		this.bundleDirection = x;
	}	
	
	public Vector3d getBundleDirection() { return bundleDirection; }
	public State getState() { return state; }					
	
	/** Exerts the flagella force, causing the particle to run and tumble like a bacterium */
	public void exert() {
		switch(state) {
		case BUNDLED:
			if(Math.random() < lambdaBundledApart*sim.getDt())
				state = State.APART;
			break;
		case APART:
			if(Math.random() < lambdaApartBundled*sim.getDt()) {
				BSimUtils.rotate(bundleDirection, tumbleAngle());
				state = State.BUNDLED;
			}
			break;
		default:
			assert false : state;
		}
		
		if(state == State.BUNDLED) {
			bundleForce();
			/* Sets the bundle direction from the total force on the particle at this stage, which
			 * may include external contributions in addition to the bundle force (if it doesn't, 
			 * the new direction is the same as the old) 
			 */
			setBundleDirection(particle.getForce());
		}
	}

	/**
	 * Exerts the bundle force on the particle, causing it to run
	 */
	public void bundleForce() {				
		Vector3d f = new Vector3d();		
		f.scale(bundleForceMagnitude, bundleDirection);
		particle.addForce(f);	
	}	
	
	/**
	 * Return a tumble angle distributed according to Fig. 3, 'Chemotaxis in Escherichia Coli', 
	 * Berg et al. (claim from 'AgentCell: a digital single-cell assay for bacterial 
	 * chemotaxis', Emonet et al.) 
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

}