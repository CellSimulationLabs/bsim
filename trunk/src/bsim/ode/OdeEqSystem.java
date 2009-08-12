/**
 * Interface used for defining a system of ODEs.
 * Defines the ODEs, the number of equations and the initial conditions.
 */

package bsim.ode;

//TODO Should this and single ode be an abstract class instead?
//TODO Improve implementation of numEq, and ICs (like in single ode interface)

public interface OdeEqSystem {
	
	/** 
	 * Define a system of derivatives
	 * dy[0] = ..., dy[1] = ..., etc
	 * then return dy[]
	 */
	double[] derivativeSystem(double x, double[] y);
	
    /**
     * Should return the number of equations in the system
     * (Corresponding to the number in derivativeSystem)
     */
	int getNumEq();
	
	/**
	 *  Returns the initial conditions: y1(0), y2(0), etc..
	 */
	double[] getICs();
	
}
