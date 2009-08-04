/**
 * BSimBacteriaTrace.java
 *
 * Class that will display a trace for every bacterium in a simulation.
 *
 * Authors: Thomas Gorochowski
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


public class BSimBacteriaTrace implements BSimVisualAid {
	
	
	// Variables for tracing the movement of particles
	private Vector traces;
	private Color traceStartCol = Color.BLUE;
	private Color traceEndCol = Color.WHITE;
	
	private Vector particles;
	
	// Number of frames to skip for each line section
	private int frameSkip = 1;
	
	// Simulation scene to gain access to bacertia
	private BSimScene scene;
	
	
	/**
	 * General constructor
	 */
	public BSimBacteriaTrace (BSimScene newScene, int newFrameSkip) {
		
		// Update the internal variables
		frameSkip = newFrameSkip;
		scene = newScene;
		
		// Create new traces
		createNewTraces();
	}
	
	private void createNewTraces() {
		
		// Create a trace vector for each bacterium
		traces = new Vector(scene.getBacteria().size());
		for(int i=0; i<scene.getBacteria().size(); i++){
			traces.add(new Vector());
		}
	}
	
	
	
	
	/**
	 * Update the bacterium trace
	 */
	public void updateState() {
		int j;
		Vector trace, bacteria;
		
		if(scene.getTimeStep() % frameSkip == 0){ 
			
			bacteria = scene.getBacteria();
			
			for(j=0; j<traces.size(); j++){
				
				trace = (Vector)traces.elementAt(j);
				
				double[] point = new double[2];
				point[0] = ((BSimBacterium)bacteria.elementAt(j)).getCentrePos()[0];
				point[1] = ((BSimBacterium)bacteria.elementAt(j)).getCentrePos()[1];
				trace.add(0,(double[])point);
			}
		}
	}
	
	
	/**
	 * Draw the vector containing the trace of the bacterium position
	 */
	public void redraw(Graphics g) {
		int n, j, i;
		Vector trace;
		double[] point1 = {0, 0};
		double[] point2 = {0, 0};
		
		for(j=0; j<traces.size(); j++){
			
			trace = (Vector)traces.elementAt(j);
			n = trace.size();
			
			for(i=1; i<n; i++) {
				// set colours for fading line
				float lineRed = (float)((traceEndCol.getRed() + ((traceStartCol.getRed() - traceEndCol.getRed())*(i-1)/(n-1)))/255.0);
				float lineGreen = (float)((traceEndCol.getGreen() + ((traceStartCol.getGreen() - traceEndCol.getGreen())*(i-1)/(n-1)))/255.0);
				float lineBlue = (float)((traceEndCol.getBlue() + ((traceStartCol.getBlue() - traceEndCol.getBlue())*(i-1)/(n-1)))/255.0);
				g.setColor(new Color(lineRed,lineGreen,lineBlue));
			
				// draw line between point1 & 2
				point1 = (double[])trace.elementAt(i-1);
				point2 = (double[])trace.elementAt(i);
				g.drawLine((int)point1[0],(int)point1[1],(int)point2[0],(int)point2[1]);
			}
		}
	}
	
	
	public void reset() {
		createNewTraces();
	}
}