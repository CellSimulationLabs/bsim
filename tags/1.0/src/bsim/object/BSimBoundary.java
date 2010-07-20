/**
 * BSimBoundary.java
 *
 * Class that represents simulation boundaries. These are solid lines that act as walls
 * restricting the movement that both particles and bacteria can make.
 *
 * Authors: Thomas Gorochowski
 * Created: 23/08/2008
 * Updated: 23/08/2008
 */


// Define the location of the class in the bsim package
package bsim.object;

// Import the bsim packages used
import bsim.*;

// Standard packages required by the application
import java.util.*;
import java.awt.*;


public class BSimBoundary {

	
	// Start and end points of the boundary
	protected double[] p1 = {0.0,0.0};
	protected double[] p2 = {0.0,0.0};

	// Colour of all boundaries
	protected Color boundColour = Color.GRAY;

	protected double mod_p2_sub_p1_sq;
	protected double px2_sub_px1;
	protected double py2_sub_py1;
	
	/**
	 * General constructor.
	 */
	public BSimBoundary(double[] newStartPoint, double[] newEndPoint) {
		
		// Update the internal variables
		p1[0] = newStartPoint[0];
		p1[1] = newStartPoint[1];
		p2[0] = newEndPoint[0];
		p2[1] = newEndPoint[1];
		
		// u denominator
		mod_p2_sub_p1_sq = Math.pow((p2[0] - p1[0]) + (p2[1] - p1[1]), 2.0); 
		
		// Differences
		px2_sub_px1 = p2[0] - p1[0];
		py2_sub_py1 = p2[1] - p1[1];
	}

	
	/**
	 * Calculates the location along the boundary that a perpendicular distance will give.
	 * If the distance is in [0, 1] then on the line segment.
	 */
	protected synchronized double calcDistOnBoundary(double[] p3) {
		
		// Calculate the distance
		return (((p3[0] - p1[0])*(p2[0] - p1[0])) +
		        ((p3[1] - p3[1])*(p2[1] - p1[1]))) / mod_p2_sub_p1_sq;
	}
	
	
	/**
	 * Calculates the distance from the boundary. If the point falls within the line
	 * segment then perpendicular distance returned, otherwise shortest distance to end
	 * points is returned.
	 */
	protected synchronized double calcDistFromBoundary(double[] p3) {
		double u, d1, d2;
		double[] p4 = new double[2];
		
		// Calculate distance on the line segment
		u = (((p3[0] - p1[0])*(p2[0] - p1[0])) +
		        ((p3[1] - p3[1])*(p2[1] - p1[1]))) / mod_p2_sub_p1_sq;
		
		// Check to see if point falls on line segment
		if(u >= 0 || u <= 1 ) {
			
			// Find the point on the line segment
			p4[0] = p1[0] + (u * (p2[0] - p1[0]));
			p4[1] = p1[1] + (u * (p2[1] - p1[1]));
			
			// Return the perpendicular distance
			return distBetweenPoints(p3, p4);
		}
		else{
			// Calculate the distances to end points
			d1 = distBetweenPoints(p1, p3);
			d2 = distBetweenPoints(p2, p3);
			
			// Return the smallest
			if(d1 <= d2) { return d1; }
			else { return d2; }
		}
	}
	
	
	/**
	 * Calculate the distance between two points.
	 */
	protected synchronized double distBetweenPoints(double[] p1, double[] p2) {
		
		// Length = sqrt(a^2 + b^2)
		return Math.sqrt(Math.pow(p1[0] - p2[0], 2) + Math.pow(p1[1] - p2[1], 2));
	}
	
	
	/**
	 * Find the reaction force for the boundary.
	 */
	public synchronized double distToBoundary(BSimObject x) {

		// Return the distance of the object to the bounday (we use edge of object not centre)
		return calcDistFromBoundary(x.getCentrePos()) - (x.size / 2);
	}

	
	/**
	 * Standard get methods
	 */
	public double[] getP1() { return p1; }
	public double[] getP2() { return p2; }
	
	
	/**
	 * Draw the boundary.
	 */
	public void redraw(Graphics g) {
		g.setColor(boundColour);
		g.drawLine((int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1]);
	}
}
