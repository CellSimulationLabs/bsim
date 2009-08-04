/**
 * BSimAverageBacteriaTrace.java
 *
 * Class that will display a trace for the average position of bacteria in a simulation.
 *
 * Authors: Thomas Gorochowski
 *          Ian Miles
 *          Charlie Harrison
 * Created: 14/08/2008
 * Updated: 14/08/2008
 */


// Define the location of the class in the bsim package
package bsim.object;

// Import the bsim packages used
import bsim.*;
import bsim.object.*;

// Standard packages required by the application
import java.util.*;
import java.awt.*;


public class BSimAverageBacteriaTrace implements BSimVisualAid {
	
	// Variables for tracing mean bacteria position 
	private int frameCount = 0;
	private Vector meanBacPos = new Vector();
	private Color bacStartCol = Color.WHITE;
	private Color bacEndCol = Color.MAGENTA;
	
	private Vector bacteria;
	
	// Number of frames to skip for each line section
	private int frameSkip = 1;
	
	// Simulation scene to gain access to bacertia
	private BSimScene scene;
	
	
	/**
	 * General constructor
	 */
	public BSimAverageBacteriaTrace (BSimScene newScene, int newFrameSkip) {
		
		// Update the internal variables
		frameSkip = newFrameSkip;
		scene = newScene;
		
		// Get the list of bacteria
		bacteria = scene.getBacteria();
	}
	
	
	/**
	 * Add the current mean position of the bacteria to the vector of trace points
	 */
	public void updateState() {
		double xTot = 0, yTot = 0;
		double meanX, meanY;
		double[] meanPos = new double[2];
		
		// Make sure we are using the latest bacteria
		bacteria = scene.getBacteria();
		
		if(scene.getTimeStep() % frameSkip == 0){ 
			for(int i=0; i < bacteria.size(); i++) {
				xTot += ((BSimBacterium)bacteria.elementAt(i)).getCentrePos()[0];
				yTot += ((BSimBacterium)bacteria.elementAt(i)).getCentrePos()[1];
			}
			meanX = xTot/bacteria.size();
			meanY = yTot/bacteria.size();
		
			meanPos[0] = meanX;
			meanPos[1] = meanY;
		
			meanBacPos.addElement(meanPos);
		}
	}
	
	
	/**
	 * Draw the vector containing the trace of the bacteria's mean position
	 */
	public void redraw(Graphics g) {
		
		// The number of line segments that exist
		int n = meanBacPos.size();

		for(int i=1; i<n; i++) {

			// Set the colour for the current section
			float lineRed = (float)((bacEndCol.getRed() + ((bacStartCol.getRed() - bacEndCol.getRed())*(i-1)/(n-1)))/255.0);
			float lineGreen = (float)((bacEndCol.getGreen() + ((bacStartCol.getGreen() - bacEndCol.getGreen())*(i-1)/(n-1)))/255.0);
			float lineBlue = (float)((bacEndCol.getBlue() + ((bacStartCol.getBlue() - bacEndCol.getBlue())*(i-1)/(n-1)))/255.0);
			g.setColor(new Color(lineRed,lineGreen,lineBlue));
			
			// Draw the line section
			double[] point1 = (double[])meanBacPos.elementAt(i-1);
			double[] point2 = (double[])meanBacPos.elementAt(i);
			g.drawLine((int)point1[0],(int)point1[1],(int)point2[0],(int)point2[1]);
		}
	}
	
	
	public void reset() {
		meanBacPos = new Vector();
	}
}