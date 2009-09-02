/**
* BSimUtils.java
*
* Class that holds static methods that are used by all components of the system.
*
* Authors: Thomas Gorochowski
*			Mattia Fazzini(Update)	
* Created: 20/07/2008
* Updated: 12/08/2009
*/
package bsim;



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
