package bsim;

import java.util.Calendar;
import java.util.Random;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

public class BSimUtils {
	
	private static Random rng = new Random();
	
	/**
	 * Sample from a gamma distribution
	 * http://vyshemirsky.blogspot.com/2007/11/sample-from-gamma-distribution-in-java.html
	 */
	public static synchronized double sampleGamma(double k, double theta) {
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
	 * Rotates the vector v by an angle theta in a random direction perpendicular to v
	 */
	public static synchronized void rotatePerp(Vector3d v, double theta) {		
		/* Obtain a random direction perpendicular to v */		
		Vector3d random = new Vector3d(0.5-Math.random(),0.5-Math.random(),0.5-Math.random());
		Vector3d randomPerp = new Vector3d();
		randomPerp.cross(v, random);		
		rotate(v, randomPerp, theta);
	}
	
	/**
	 * Rotates the vector v towards the specified axis by an angle theta
	 */
	public static synchronized void rotate(Vector3d v, Vector3d axis, double theta) {
		/* Generate the rotation matrix for rotating about the axis by an angle theta */
		Matrix3d r = new Matrix3d();
		r.set(new AxisAngle4d(axis, theta));
		
		/* Apply the rotation */			
		r.transform(v);
	}
	
	public static synchronized String timeStamp(){
		// Build the time stamp of the whole batch (this helps to keep batches together)
		Calendar calNow = Calendar.getInstance();
		String yyyyStr, mmStr, ddStr, hhStr, miStr, ssStr, timestampStr;
		
		yyyyStr = "" + calNow.get(Calendar.YEAR);
		mmStr = BSimUtils.padInt2(calNow.get(Calendar.MONTH));
		ddStr = BSimUtils.padInt2(calNow.get(Calendar.DAY_OF_MONTH));
		hhStr = BSimUtils.padInt2(calNow.get(Calendar.HOUR_OF_DAY));
		miStr = BSimUtils.padInt2(calNow.get(Calendar.MINUTE));
		ssStr = BSimUtils.padInt2(calNow.get(Calendar.SECOND));
		
		timestampStr = yyyyStr + "-" + mmStr + "-" + ddStr + "_" +
			hhStr + "-" + miStr + "-" + ssStr;
		
		return timestampStr;
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

}
