/**
 * BSimLogic.java
 *
 * Abstract class that bacteria use to implement differing internal dynamics that can
 * can be simple (delays) or more advanced (full GRNs).
 *
 * Authors: Thomas Gorochowski
 * Created: 11/08/2008
 * Updated: 11/08/2008
 */


//Define the location of the class in the bsim package
package bsim.logic;


public interface BSimLogic {

	public abstract double[] runLogic ( boolean contactBac, 
	                                    boolean contactPart,
	                                    boolean contactBoundary);

}
