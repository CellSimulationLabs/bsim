package bsim.mixin;
import java.util.Random;

import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimMixin;
import bsim.BSimParticle;

/**
 * Public methods:
 *	BSimBrownianForceMixin#setBrownianForceMagnitude()
 * 	BSimBrownianForceMixin#brownianForce() 
 */
public class BSimBrownianForceMixin extends BSimMixin {
	
	protected double brownianForceMagnitude;
	protected static Random rng = new Random();
	
	public BSimBrownianForceMixin(BSim sim, BSimParticle particle) {
		super(sim, particle); 		
		setBrownianForceMagnitude();
	}
	
	public void setBrownianForceMagnitude() {
		brownianForceMagnitude = Math.sqrt(2*particle.stokesCoefficient()*BSim.BOLTZMANN*sim.getTemperature()/sim.getDt())*Math.pow(10,9);
	}
	
	/**
	 * Applies a Brownian force to the particle. The applied force is a function of 
	 * radius, viscosity and temperature; if any of these are changed externally, you should call
	 * setBrownianForceMagnitude() again
	 */
	public void brownianForce() {						
		Vector3d f = new Vector3d(rng.nextGaussian(), rng.nextGaussian(), rng.nextGaussian());
		f.scale(brownianForceMagnitude); 
		particle.addForce(f);
	}
	
}
