/**
 * BSimSceneScale.java
 *
 * Class that displays a scale on the simulation
 *
 * Authors: Thomas Gorochowski
 * Created: 28/08/2008
 * Updated: 28/08/2008
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


public class BSimSceneScale implements BSimVisualAid {
	
	
	// Simulation scene to gain access to bacertia
	private BSimScene scene;
	
	private double[] pos;
	
	private double scaleLen = 0.0;
	
	/**
	 * General constructor
	 */
	public BSimSceneScale (BSimScene newScene, double p1x, double p1y, double newScaleLen) {
		
		// Update the internal variables
		scene = newScene;
		
		// Set the position of the scale (not in simulation co-ordinate space)
		pos = new double[2];
		pos[0] = p1x;
		pos[1] = p1y;
		
		// Update the scale length
		scaleLen = newScaleLen;
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
		
		
		// Draw the scale
		int screenLen = (int)(scaleLen * scene.getScale()); 
		
		g.drawLine((int)pos[0], (int)pos[1]-4, (int)pos[0]+screenLen, (int)pos[1]-4);
		g.drawLine((int)pos[0], (int)pos[1] - 6, (int)pos[0], (int)pos[1]-2);
		g.drawLine((int)pos[0]+screenLen, (int)pos[1] - 6, (int)pos[0]+screenLen, (int)pos[1]-2);
		
		// Draw the string
		g.drawString("" + scaleLen + " microns", (int)pos[0]+screenLen+5, (int)pos[1]);
		
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