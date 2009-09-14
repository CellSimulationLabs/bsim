package bsim.mixin;
import java.util.Random;

import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimMixin;
import bsim.BSimParticle;

public class BSimBrownianMixin extends BSimMixin {
	
	protected double brownianAmplitude;
	protected static Random rng = new Random();
	
	public BSimBrownianMixin(BSim sim, BSimParticle particle) {
		super(sim, particle); 		
		setBrownianAmplitude();
	}
	
	public void setBrownianAmplitude() {
		brownianAmplitude = Math.sqrt(2*particle.stokesCoefficient()*BSim.BOLTZMANN*sim.getTemperature()/sim.getDt())*Math.pow(10,9);
	}
	
	/**
	 * Applies a Brownian force to the particle. The applied force is a function of 
	 * radius, viscosity and temperature; if any of these are changed externally, you must call
	 * setBrownianAmplitude() again
	 */
	public void brownianForce() {						
		Vector3d f = new Vector3d(rng.nextGaussian(), rng.nextGaussian(), rng.nextGaussian());
		f.scale(brownianAmplitude); 
		particle.addForce(f);
	}
	
}
