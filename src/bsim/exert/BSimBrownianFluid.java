package bsim.exert;
import java.util.Random;

import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimExerter;
import bsim.BSimParticle;

public class BSimBrownianFluid extends BSimExerter {
	
	protected double forceMagnitude;
	protected static Random rng = new Random();
	
	public BSimBrownianFluid(BSim sim, BSimParticle particle) {
		super(sim, particle); 		
		setForceMagnitude();
	}
	
	public void setForceMagnitude() {
		forceMagnitude = Math.sqrt(2*particle.stokesCoefficient()*BSim.BOLTZMANN*sim.getTemperature()/sim.getDt())*Math.pow(10,9);
	}
	
	/**
	 * Exerts a Brownian force to the particle. The applied force is a function of 
	 * radius, viscosity and temperature; if any of these are changed externally, you should call
	 * setForceMagnitude() again
	 */
	public void exert() {						
		Vector3d f = new Vector3d(rng.nextGaussian(), rng.nextGaussian(), rng.nextGaussian());
		f.scale(forceMagnitude); 
		particle.addForce(f);
	}
	
}
