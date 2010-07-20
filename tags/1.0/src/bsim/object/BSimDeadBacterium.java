/**
 * BSimDeadBacterium.java
 *
 * Class that represents a bacterium that has died. It cannot produce any internal force.
 *
 * Authors: Thomas Gorochowski
 * Created: 01/09/2008
 * Updated: 01/09/2008
 */


//Define the location of the class in the bsim package
package bsim.object;

//Import the bsim packages used
import bsim.*;
import bsim.object.*;
import bsim.logic.*;

//Standard packages required by the application
import java.awt.*;
import java.lang.Math;
import java.io.File;
import java.io.IOException;
import java.util.*;


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
	                           boolean contactPart,
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
