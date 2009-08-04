/**
 * BSimParticle.java
 *
 * Class that represents a particle in our simulation.
 *
 * Authors: Thomas Gorochowski
 *          Ian Miles
 * Created: 12/07/2008
 * Updated: 19/07/2008
 */
package bsim.object.particle;

import java.awt.Color;
import java.awt.Graphics;

import bsim.object.BSimObject;


public class BSimParticle extends BSimObject {
	
	
	/**
	 * General constructor.
	 */
	public BSimParticle(double newSpeed, double newMass, double newSize, 
			double[] newDirection, double[] newPosition) {
				
		// Call the parent constructor
		super(newSpeed, newMass, newSize, newDirection, newPosition, BSimObject.OBTYPE_PART);
	}
	
	
	/**
	 * Redraws the particle.
	 */
	public void redraw(Graphics g) {
		
		// Draw the particle on the given graphics context
		g.setColor(Color.RED);
		g.fillOval((int)position[0],(int)position[1],(int)size,(int)size);
	}
}
