/**
 * BSimBoundaryCreate.java
 *
 * Class to hold static methods to generate solid and wrapping boundaries.
 *
 * Authors: Thomas Gorochowski
 * Created: 24/08/2008
 * Updated: 24/08/2008
 */


//Define the location of the class in the bsim package
package bsim.object;

//Import the bsim packages used
import bsim.*;
import bsim.physics.*;

//Standard packages required by the application
import java.util.*;


public class BSimBoundaryCreate {
	
	
	/**
	 * Create a solid boundary from parameter arguments
	 */
	public static BSimBoundary createSolidBoundary( double[] args ){
		
		// Create points required to create the solid boundary
		double[] p1 = new double[2];
		double[] p2 = new double[2];
		
		p1[0] = args[0];
		p1[1] = args[1];
		p2[0] = args[2];
		p2[1] = args[3];
		
		// return the new boundary
		return new BSimBoundary(p1, p2);
	}
	
	
	/**
	 * Create a wrapping boundary from parameter arguments
	 */
	public static BSimWrapBoundary createWrapBoundary( double[] args ){
		
		// Create points required to create the solid boundary
		double[] p1 = new double[2];
		double[] p2 = new double[2];
		double[] offset = new double[2];
		
		p1[0] = args[0];
		p1[1] = args[1];
		p2[0] = args[2];
		p2[1] = args[3];
		offset[0] = args[4];
		offset[1] = args[5];
		
		// return the new boundary
		return new BSimWrapBoundary(p1, p2, offset, args[6]);
	}
}
