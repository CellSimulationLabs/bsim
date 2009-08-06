/**
 * BSimBead.java
 *
 * Class that represents a bead in our simulation.
 *
 * Authors: Thomas Gorochowski
 *          Ian Miles
 * Created: 12/07/2008
 * Updated: 19/07/2008
 */
package bsim.drawable.bead;

import java.awt.Color;
import java.awt.Graphics;

import bsim.drawable.BSimDrawable;
import bsim.physics.BSimParticle;


public class BSimBead extends BSimParticle implements BSimDrawable {
	
	
	/**
	 * General constructor.
	 */
	public BSimBead(double newSpeed, double newMass, double newSize, 
			double[] newDirection, double[] newPosition) {
				
		// Call the parent constructor
		super(newSpeed, newMass, newSize, newDirection, newPosition, BSimParticle.PART_BEAD);
	}
	
	
	/**
	 * Redraws the bead.
	 */
	public void redraw(Graphics g) {
		
		// Draw the bead on the given graphics context
		g.setColor(Color.RED);
		g.fillOval((int)position[0],(int)position[1],(int)size,(int)size);
	}
}
