/**
* BSimUtils.java
*
* Class that holds static methods that are used by all components of the system.
*
* Authors: Thomas Gorochowski
* Created: 20/07/2008
* Updated: 20/07/2008
*/


// Define the location of the class in the bsim package
package bsim;

import bsim.object.BSimObject;


public class BSimUtils {
	
	
	/**
	* Calculate the number of timesteps in a given number of seconds.
	*/
	public static synchronized int roundToDtSteps(double t, double dt) {
		double tOverDt = t/dt;
		int nDt;
		
		// Calculate the number of timesteps
		if(tOverDt%1 >= 0.5) nDt = (int)(Math.ceil(tOverDt));
		else                 nDt = (int)(Math.floor(tOverDt));
		
		// Return the result (we do not allow events to take 0 timesteps)
		if(nDt==0) return 1;
		else       return nDt;
	}
	
	
	/**
	* Returns an exponentially distributed random variable.
	*/
	public static synchronized double expRandVar(double mean) {
		return -mean * Math.log(1-Math.random());
	}
	
	
	/**
	* Returns approximate gamma distributed random variable (Parameters for bacteria).
	*/
	public static synchronized double approxGammaRandVar() {
		double x = (Math.random()*2.0)-1;
		if(x>0) {
			return (221.64*Math.pow(x,4.0)) - (109.34*Math.pow(x,3.0)) - 
			(137.84*Math.pow(x,2.0) + (177.58*x) + 12.736);
		}
		else {
			return -(221.64*Math.pow(-x,4.0)) - (109.34*Math.pow(-x,3.0)) - 
			(137.84*Math.pow(-x,2.0) + (177.58*-x) + 12.736);
		}
	}
	
	public static synchronized double dragForce(double speed, double radius, double visc)
	{
		return 6.0 * Math.PI * radius * speed * visc;
	}
	
	public static synchronized double dragForceInv(double force, double radius, double visc) {
		return force / ( 6.0 * Math.PI * radius * visc);
	}
	
	/**
	* Returns the 2D velocity vector of an object by solving Stokes' Law; force applied
	* to an object is assumed to equal drag.
	* N.B. Units are S.I.; e.g. for F in micro Newtons, velocity is in microns per metre
	*/
	public static synchronized double[] force2Velocity2D(double[] F, double r, double visc) {
		double[] v = new double[2];
		v[0] = F[0]/(6.0*Math.PI*r*visc);
		v[1] = F[1]/(6.0*Math.PI*r*visc);
		return v;
	}
	
	
	public static synchronized boolean objectsIntersecting(BSimObject a, BSimObject b) {
		double centreDist = get2Ddist(a.getCentrePos(),b.getCentrePos());
		double aRad = a.getRadius(), bRad = b.getRadius();
		
		if(centreDist < (aRad + bRad)) return true;
		else return false;
	}
	
	
	/**
	* Returns the distance between a pair of 2 dimensional points
	*/
	public static synchronized double get2Ddist(double[] a, double[] b) {
		double distSqrd = Math.pow(a[0]-b[0],2) + Math.pow(a[1]-b[1],2);
		return Math.sqrt(distSqrd);
	}
	
	
	/**
	* Returns a padded version of the number to a size of two
	*/
	public static synchronized String padInt2(int val) {
		String outStr = "";
		
		// Pad with zero if needed
		if(val<10) {
			outStr = "0" + val;
		}
		else{
			outStr = "" + val;
		}
		
		return outStr;
	}
	
	
	/**
	* Returns a padded version of the number to a size of two
	*/
	public static synchronized String padInt4(int val) {
		String outStr = "";
		
		// Pad with zero if needed
		if(val<10) {
			outStr = "000" + val;
		}
		else if (val<100) {
			outStr = "00" + val;
		}
		else if (val<1000) {
			outStr = "0" + val;
		}
		else {
			outStr = "" + val;
		}
		
		return outStr;
	}
}
