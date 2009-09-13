package bsim.particle;
import java.util.Random;

import javax.vecmath.Vector3d;

import bsim.BSim;

public class BSimBrownianParticle extends BSimParticle {
				
	private double brownianAmplitude;
	private static Random rng = new Random();
	
	public BSimBrownianParticle(BSim sim, Vector3d position, double radius) {
		super(sim, position, radius);	
		setBrownianAmplitude();
	}
	
	public void setBrownianAmplitude() {
		brownianAmplitude = Math.sqrt(2*stokesCoefficient()*BSim.BOLTZMANN*sim.getTemperature()/sim.getDt())*Math.pow(10,9);
	}
	
	@Override
	public void setRadius(double r) {
		radius = r;
		setBrownianAmplitude(); // brownianAmplitude is a function of radius via stokesCoefficient 
	}

	@Override
	public void action() {						
		Vector3d f = new Vector3d(rng.nextGaussian(), rng.nextGaussian(), rng.nextGaussian());
		f.scale(brownianAmplitude);
		this.force.add(f);
	}
	
}
