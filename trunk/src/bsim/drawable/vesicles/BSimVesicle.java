package bsim.drawable.vesicles;

import java.awt.Color;
import java.awt.Graphics;

import bsim.BSimParameters;
import bsim.BSimScene;
import bsim.drawable.BSimDrawable;
import bsim.drawable.bacteria.BSimBacterium;
import bsim.logic.BSimLogic;
import bsim.physics.BSimParticle;

public class BSimVesicle extends BSimParticle implements BSimLogic, BSimDrawable {
	
	// The simulation scene that will be updated
	protected BSimScene scene;
	// Parameters for the simulation
	protected BSimParameters params;
	//value of the Brownian force;
	protected double amplitude = 0;

	/**
	 * General constructor.
	 */
	public BSimVesicle(double newSpeed, double newMass,
			double newSize, double[] newDirection, double[] newPosition, 
			BSimScene newScene, BSimParameters newParams, double newAmplitude) {
		
		// Call the parent constructor with the basic properties	
		super(newSpeed, newMass, newSize, newDirection, newPosition, BSimParticle.PART_VES);
		
		// The simulation scene that will be updated
		scene = newScene;
		// Parameters for the simulation
		params = newParams;
		//value of the Brownian force;
		amplitude = newAmplitude;

		
	}
	
	/**
	 * Implements the BSimLogic interface. In this case it merely carries out
	 * the standard chemotaxis toward fGoal gradient. The internal force of the bacterium
	 * at a timestep is returned.
	 */
	public double[] runLogic ( boolean contactBead, 
	                           boolean contactBoundary) {
		
		double[] f = {0.0, 0.0, 0.0};
		
		return  f;
	}
	
	
	
	
	/**
	 * Redraws the vesicle. A small red circle is also drawn to represent the direction
	 * of the vesicle.
	 */
	public void redraw(Graphics g) {

		g.setColor(Color.PINK);
		g.fillOval((int)position[0],(int)position[1],(int)(radius),(int)(radius));
	}
}
