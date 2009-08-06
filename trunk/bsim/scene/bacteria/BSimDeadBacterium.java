/**
 * BSimDeadBacterium.java
 *
 * Class that represents a bacterium that has died. It cannot produce any internal force.
 *
 * Authors: Thomas Gorochowski
 * Created: 01/09/2008
 * Updated: 01/09/2008
 */
package bsim.scene.bacteria;

import java.awt.Color;
import java.awt.Graphics;

import bsim.BSimParameters;
import bsim.BSimScene;
import bsim.logic.BSimLogic;


public class BSimDeadBacterium extends BSimBacterium implements BSimLogic {


	/**
	 * General constructor.
	 */
	public BSimDeadBacterium(double newSpeed, double newMass, double newSize,
			double[] newDirection, double[] newPosition, double newForceMagnitudeDown,
			 double newForceMagnitudeUp,
			int newState, double newTumbleSpeed, int newRemDt, BSimScene newScene, 
		    BSimParameters newParams) {

		// Call the parent constructor with the basic properties	
		super(newSpeed, newMass, newSize, newDirection, newPosition, newForceMagnitudeDown,
		newForceMagnitudeUp, newState,
		      newTumbleSpeed, newRemDt, newScene, newParams);
	}


	/**
	 * Implements the BSimLogic interface. In this case it merely carries out
	 * the standard chemotaxis toward fGoal gradient. The internal force of the bacterium
	 * at a timestep is returned.
	 */
	public double[] runLogic ( boolean contactBac, 
	                           boolean contactBead,
	                           boolean contactBoundary ) {
		
		double[] f = {0.0, 0.0};
		
		return  f;
	}
	
	
	/**
	 * Redraws the bacterium. A small red circle is also drawn to represent the direction
	 * of the bacteria.
	 */
	public void redraw(Graphics g) {

		g.setColor(Color.GRAY);
		g.fillOval((int)position[0],(int)position[1],(int)(size),(int)(size));
	}
}
