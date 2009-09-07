package bsim.particle.vesicle;
import java.util.Random;

import javax.vecmath.Vector3d;

import bsim.BSimParameters;
import bsim.particle.BSimParticle;
import bsim.particle.bacterium.BSimBacterium;
import bsim.scene.BSimScene;

public class BSimVesicle extends BSimParticle {
		
	static double boltzmann = 1.38 * Math.pow(10,-23);
	static double temperature = 300;
	
	public BSimVesicle(Vector3d newPosition, double newRadius, BSimScene newScene) {
		super(newPosition, newRadius, newScene);	
	}

	public void action() {						
		double brownianAmplitude = Math.sqrt(2*stokesCoefficient()*boltzmann*temperature/BSimParameters.dt)*Math.pow(10,9);

		Random r = new Random();
		Vector3d f = new Vector3d(r.nextGaussian(), r.nextGaussian(), r.nextGaussian());
		f.scale(brownianAmplitude);
		this.addForce(f);
	}
	
}
