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
package bsim.scene.bead;

import java.awt.Color;
import java.awt.Graphics;

import bsim.scene.BSimObject;


public class BSimBead extends BSimObject {
	
	
	/**
	 * General constructor.
	 */
	public BSimBead(double newSpeed, double newMass, double newSize, 
			double[] newDirection, double[] newPosition) {
				
		// Call the parent constructor
		super(newSpeed, newMass, newSize, newDirection, newPosition, BSimObject.OBTYPE_BEAD);
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
