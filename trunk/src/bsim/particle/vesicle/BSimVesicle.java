package bsim.particle.vesicle;

import java.awt.Graphics;
import java.util.Random;

import javax.vecmath.Vector3d;

import bsim.BSimParameters;
import bsim.BSimScene;
import bsim.particle.BSimParticle;
import bsim.particle.bacterium.BSimBacterium;
import bsim.particle.bead.BSimBead;

public class BSimVesicle extends BSimParticle {
	
	// The simulation scene that will be updated
	protected BSimScene scene;
	// Parameters for the simulation
	protected BSimParameters params;

	/**
	 * General constructor.
	 */
	public BSimVesicle(Vector3d newPosition, double newRadius, 
			BSimScene newScene, BSimParameters newParams) {
		
		// Call the parent constructor with the basic properties	
		super(newPosition, newRadius);
		
		// The simulation scene that will be updated
		scene = newScene;
		// Parameters for the simulation
		params = newParams;

		
	}

	public void action() {
		
		Random r = new Random();
		
		double resistance = 6.0*Math.PI*(this.getRadius()*Math.pow(10, -6))*params.getViscosity();
		double boltzmann = 1.38 * Math.pow(10,-23);
		double temperature = 300;
		double amplitude = Math.sqrt(2*resistance*boltzmann*temperature/params.getDtSecs())*Math.pow(10,12);
		
		Vector3d f = new Vector3d(r.nextGaussian()*amplitude, r.nextGaussian()*amplitude, r.nextGaussian()*amplitude);		
		this.addForce(f);
	}
	

}
