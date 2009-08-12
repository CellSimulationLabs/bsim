/**
 * BSimDeadBacterium.java
 *
 * Class that represents a bacterium that has died. It cannot produce any internal force.
 *
 * Authors: Thomas Gorochowski
 * 			Mattia Fazzini(Update)
 * Created: 01/09/2008
 * Updated: 12/08/2009
 */
package bsim.drawable.bacteria;

import java.awt.Color;
import java.awt.Graphics;

import bsim.BSimParameters;
import bsim.BSimScene;
import bsim.drawable.BSimDrawable;
import bsim.logic.BSimLogic;


public class BSimDeadBacterium extends BSimBacterium implements BSimLogic, BSimDrawable {


	/**
	 * General constructor for trilinear elongation .
	 */
	
	public BSimDeadBacterium(double newSpeed, double newMass,
			double newL0, double newR, double newTC, double newT2, double newTG, double newa1, double newa2, double newa3, int newElongationType,
			double[] newDirection, double[] newPosition, double newForceMagnitudeDown,
			double newForceMagnitudeUp,
			int newState, double newTumbleSpeed, int newRemDt, BSimScene newScene, 
		    BSimParameters newParams) {
		
		super(newSpeed, newMass,
		newL0, newR, newTC, newT2, newTG, newa1, newa2, newa3, newElongationType,
		newDirection, newPosition, newForceMagnitudeDown,
		newForceMagnitudeUp,
		newState, newTumbleSpeed, newRemDt, newScene, 
		newParams);
			
	}
	
	/**
	 * General constructor for bilinear elongation .
	 */
	public BSimDeadBacterium(double newSpeed, double newMass,
			double newL0, double newLTC, double newLTG, double newR, double newTC, double newTG, int newElongationType,
			double[] newDirection, double[] newPosition, double newForceMagnitudeDown,
			double newForceMagnitudeUp,
			int newState, double newTumbleSpeed, int newRemDt, BSimScene newScene, 
		    BSimParameters newParams) {
		
		super(newSpeed, newMass,
				newL0, newLTC, newLTG, newR, newTC, newTG, newElongationType,
				newDirection, newPosition, newForceMagnitudeDown,
				newForceMagnitudeUp,
				newState, newTumbleSpeed, newRemDt, newScene, 
			    newParams);
	}

	/**
	 * Implements the BSimLogic interface. In this case it merely carries out
	 * the standard chemotaxis toward fGoal gradient. The internal force of the bacterium
	 * at a timestep is returned.
	 */
	public double[] runLogic ( boolean contactBac, 
	                           boolean contactBead,
	                           boolean contactBoundary ) {
		
		double[] f = {0.0, 0.0, 0.0};
		
		return  f;
	}
	
	
	/*
	 * Function to increase the lifeTime
	 * This method must be empty because a dead bacterium has no lifeTime anymore
	 */
	public void increaseLifeTime(){
	}
    
	/*
	 * Function to increase the Size
	 * This method must be empty because a dead bacterium can not elongate anymore
	 */
	public void increaseSize(){
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
