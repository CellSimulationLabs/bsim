/**
 * BSimSceneClock.java
 *
 * Class that displays a clock on a simulations top left corner
 *
 * Authors: Thomas Gorochowski
 * Created: 14/08/2008
 * Updated: 24/08/2008
 */


// Define the location of the class in the bsim package
package bsim.object;

// Import the bsim packages used
import bsim.*;
import bsim.object.*;

// Standard packages required by the application
import java.util.*;
import java.awt.*;
import java.awt.geom.*;


public class BSimSceneClock implements BSimVisualAid {
	
	
	// Simulation scene to gain access to bacertia
	private BSimScene scene;
	
	
	/**
	 * General constructor
	 */
	public BSimSceneClock (BSimScene newScene) {
		
		// Update the internal variables
		scene = newScene;
	}
	
	
	/**
	 * Update the particle trace
	 */
	public void updateState() {
		// No internal state to update
	}
	
	
	/**
	 * Draw the vector containing the trace of the particles position
	 */
	public void redraw(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		// Get the current transformation (return to this at end)
		AffineTransform saveXform = g2d.getTransform();
		
		// New transformation for our text
		AffineTransform toBottomRightScaled = new AffineTransform();
		
		// Move to the top left corner 
		//(order of transformations important, think matrix multiplication!)
		toBottomRightScaled.scale(1/scene.getScale(), 1/scene.getScale());
		toBottomRightScaled.translate(-scene.getTransX()-70, 
		                              -scene.getTransY()-70);
		
		// Perform the transformation
		g2d.transform(toBottomRightScaled);
		
		// Change colour to white
		g.setColor(Color.WHITE);
		
		// Use anti-alised text
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Draw the time to the output
		g.drawString(scene.getFormatedTimeSecs(), 100, 100);
		
		// Reset the transform on the graphics context
		g2d.setTransform(saveXform);
	}
	
	
	/**
	 * Reset the visual aid
	 */
	public void reset() {
		// No internal state to reset
	}
}