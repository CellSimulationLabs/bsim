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
package bsim.particle.bead;

import java.awt.Graphics;

import javax.vecmath.Vector3d;

import bsim.particle.BSimParticle;
import bsim.particle.bacterium.BSimBacterium;
import bsim.particle.vesicle.BSimVesicle;


public class BSimBead extends BSimParticle {
	
	
	/**
	 * General constructor.
	 */
	public BSimBead(Vector3d newPosition, double newRadius) {
				
		// Call the parent constructor
		super(newPosition, newRadius);
	}
	
	
	public void interaction(BSimBacterium bact) {}
	public void interaction(BSimBead bead) {}	
	public void interaction(BSimVesicle vesicle) {}
	public void action() {}
	
	/**
	 * Redraws the bead.
	 */
	public void redraw(Graphics g) {
//		
//		// Draw the bead on the given graphics context
//		g.setColor(Color.RED);
//		g.fillOval((int)position[0],(int)position[1],(int)size,(int)size);
	}
}
