/**
 * Interface used for defining a single ODE.
 * Defines an ODE and its initial condition.
 */

package bsim.ode;

//TODO ICs should be more flexible (maybe not defined here?)

public interface OdeEqSingle {
	
	/**
	 * Define a derivative in terms of its dependent variable y 
	 * and independent variable x
	 */
	double derivative(double x, double y);
	
	/**
	 * Return the initial condition for the IVP i.e. y(0)
	 */ 
	double getIC();
	
}
