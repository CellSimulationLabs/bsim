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
package bsim.logic;

import javax.vecmath.Vector3d;


public interface BSimLogic {

	public abstract Vector3d runLogic ( boolean contactBac, 
	                                    boolean contactBead,
	                                    boolean contactBoundary);

}
