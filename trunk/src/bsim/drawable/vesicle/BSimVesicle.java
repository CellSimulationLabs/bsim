package bsim.drawable.vesicle;

import java.awt.Graphics;
import java.util.Random;

import javax.vecmath.Point3d;

import bsim.BSimParameters;
import bsim.BSimScene;
import bsim.drawable.BSimDrawable;
import bsim.physics.BSimParticle;

public class BSimVesicle extends BSimParticle implements BSimDrawable {
	
	// The simulation scene that will be updated
	protected BSimScene scene;
	// Parameters for the simulation
	protected BSimParameters params;

	/**
	 * General constructor.
	 */
	public BSimVesicle(Point3d newPosition, double newRadius, 
			BSimScene newScene, BSimParameters newParams) {
		
		// Call the parent constructor with the basic properties	
		super(newPosition, newRadius);
		
		// The simulation scene that will be updated
		scene = newScene;
		// Parameters for the simulation
		params = newParams;

		
	}
	

	public double[] runLogic ( boolean contactBead, 
	                           boolean contactBoundary) {
		
		Random r = new Random();
		
		double resistance = 6.0*Math.PI*(radius*Math.pow(10, -6))*params.getViscosity();
		double boltzmann = 1.38 * Math.pow(10,-23);
		double temperature = 300;
		double amplitude = Math.sqrt(2*resistance*boltzmann*temperature/params.getDtSecs())*Math.pow(10,12);
		
		double[] f = {r.nextGaussian()*amplitude, r.nextGaussian()*amplitude, r.nextGaussian()*amplitude};
		
		return  f;
	}
	
	
	
	
	/**
	 * Redraws the vesicle. A small red circle is also drawn to represent the direction
	 * of the vesicle.
	 */
	public void redraw(Graphics g) {

//		g.setColor(Color.PINK);
//		g.fillOval((int)position[0],(int)position[1],(int)(radius),(int)(radius));
	}
}
