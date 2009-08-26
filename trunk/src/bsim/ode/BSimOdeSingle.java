/**
 * BSimOdeSingle.java
 * 
 * Interface used for defining a single ODE.
 * Defines an ODE and its initial condition.
 * 
 * Author: Antoni Matyjaszkiewicz
 * 
 * Created: 10/08/2009
 * Updated: 12/08/2009
 */

package bsim.ode;

//TODO ICs should be more flexible (maybe not defined here?)

public interface BSimOdeSingle {
	
	/**
	 * Define a derivative in terms of its dependent variable y 
	 * and independent variable x
	 */
	public double derivative(double x, double y);
	
	/**
	 * Return the initial condition for the IVP i.e. y(0)
	 */ 
	public double getIC();
	
}
