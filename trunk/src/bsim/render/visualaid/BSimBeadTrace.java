/**
 * BSimBeadTrace.java
 *
 * Class 
 *
 * Authors: Thomas Gorochowski
 *          Ian Miles
 *          Charlie Harrison
 * Created: 14/08/2008
 * Updated: 14/08/2008
 */
package bsim.render.visualaid;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

import bsim.particle.bead.BSimBead;
import bsim.scene.BSimScene;


public class BSimBeadTrace implements BSimVisualAid {
	
	// Variables for tracing the movement of beads
	private Vector traces;
	private Color traceStartCol = Color.BLUE;
	private Color traceEndCol = Color.WHITE;
	
	private Vector beads;
	
	// Number of frames to skip for each line section
	private int frameSkip = 1;
	
	// Simulation scene to gain access to bacertia
	private BSimScene scene;
	
	
	/**
	 * General constructor
	 */
	public BSimBeadTrace (BSimScene newScene, int newFrameSkip) {
		
		// Update the internal variables
		frameSkip = newFrameSkip;
		scene = newScene;
		
		// Create new traces
		createNewTraces();
	}
	
	private void createNewTraces() {
		
		// Create a trace vector for each beads
		traces = new Vector(scene.getBeads().size());
		for(int i=0; i<scene.getBeads().size(); i++){
			traces.add(new Vector());
		}
	}
	
	
	
	
	/**
	 * Update the bead trace
	 */
	public void updateState() {
		int j;
		Vector trace, beads;
		
		if(scene.getTimeStep() % frameSkip == 0){ 
			
			beads = scene.getBeads();
			
			for(j=0; j<traces.size(); j++){
				
				trace = (Vector)traces.elementAt(j);
				
				double[] point = new double[2];
				point[0] = ((BSimBead)beads.elementAt(j)).getPosition()[0];
				point[1] = ((BSimBead)beads.elementAt(j)).getPosition()[1];
				trace.add(0,(double[])point);
			}
		}
	}
	
	
	/**
	 * Draw the vector containing the trace of the beads position
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