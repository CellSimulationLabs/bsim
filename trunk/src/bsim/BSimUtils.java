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

import java.text.DecimalFormat;
import java.util.List;



public class BSimUtils {
	
	/**	 
	 * Returns the mean of a List<Double>
	 */
	public static synchronized double mean(List<Double> v) {
		double c = 0;
		for(double i : v) {
			c += i;
		}
		return c/v.size();
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
	 * Formats a time in seconds to a string (HH:MM:SS)
	 */	
	public static synchronized String formatTime(double secs){
		String a1, a2, a3;
		a1 = "";
		a2 = "";
		
		// Calculate the parts of the time		
		int mins = (int)(secs/60);
		int hrs  = (int)(mins/60);
		secs = secs - (mins * 60);
		mins = mins - (hrs * 60);
		
		// Check if zero padding required 
		// (could use format string but quicker to do manually)
		if(mins < 10){ a2 = "0"; }
		if(hrs  < 10){ a1 = "0"; }
		        
        DecimalFormat df = new DecimalFormat("00.00");		
		// Return the formatted time
		return "Time: " + a1 + hrs + ":" + a2 + mins + ":" + df.format(secs);
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
