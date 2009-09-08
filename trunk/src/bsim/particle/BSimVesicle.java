package bsim.particle;
import java.util.Random;

import javax.vecmath.Vector3d;

import bsim.scene.BSimScene;

public class BSimVesicle extends BSimParticle {
		
	private static double boltzmann = 1.38 * Math.pow(10,-23);
	private static double temperature = 300;	
	
	public BSimVesicle(Vector3d newPosition, double newRadius, BSimScene newScene) {
		super(newPosition, newRadius, newScene);	
	}

	public void action() {						
		double brownianAmplitude = Math.sqrt(2*stokesCoefficient()*boltzmann*temperature/BSimScene.dt)*Math.pow(10,9);

		Random r = new Random();
		Vector3d f = new Vector3d(r.nextGaussian(), r.nextGaussian(), r.nextGaussian());
		f.scale(brownianAmplitude);
		this.addForce(f);
	}
	
}
