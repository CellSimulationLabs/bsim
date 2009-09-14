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
import java.util.Random;



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
	* Returns gamma distributed random variable
	* http://vyshemirsky.blogspot.com/2007/11/sample-from-gamma-distribution-in-java.html
	*/
	public static double sampleGamma(double k, double theta) {
		Random rng = new Random();
		boolean accept = false;
		if (k < 1) {
			// Weibull algorithm
			double c = (1 / k);
			double d = ((1 - k) * Math.pow(k, (k / (1 - k))));
			double u, v, z, e, x;
			do {
				u = rng.nextDouble();
				v = rng.nextDouble();
				z = -Math.log(u);
				e = -Math.log(v);
				x = Math.pow(z, c);
				if ((z + e) >= (d + x)) {
					accept = true;
				}
			} while (!accept);
			return (x * theta);
		} else {
			// Cheng's algorithm
			double b = (k - Math.log(4));
			double c = (k + Math.sqrt(2 * k - 1));
			double lam = Math.sqrt(2 * k - 1);
			double cheng = (1 + Math.log(4.5));
			double u, v, x, y, z, r;
			do {
				u = rng.nextDouble();
				v = rng.nextDouble();
				y = ((1 / lam) * Math.log(v / (1 - v)));
				x = (k * Math.exp(y));
				z = (u * v * v);
				r = (b + (c * y) - x);
				if ((r >= ((4.5 * z) - cheng)) ||
						(r >= Math.log(z))) {
					accept = true;
				}
			} while (!accept);
			return (x * theta);
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
