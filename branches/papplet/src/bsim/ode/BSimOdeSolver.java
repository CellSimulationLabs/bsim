/**
 * BSimOdeSolver.java
 * 
 * Solver routines for numerical simulation of ODEs (Fixed time-step):
 * - Euler's method
 * - second order Runge-Kutta
 * - fourth order Runge-Kutta.
 * 
 * Each method will estimate the change of the dependent variable based
 * on the previous value of the dependent (y) and independent (x) variables, 
 * and return the new value of the dependent variable.
 * 
 * The methods are in order of increasing accuracy for a given time-step;
 * Euler's method is the most basic, but the fastest as a result of having to 
 * perform relatively few calculations, while the Runge-Kutta methods use an
 * intermediate trial step at the midpoint of an interval to cancel lower order 
 * error terms. 
 * 
 * If the time step is too large an Euler solution will quickly diverge from
 * the true solution, therefore it is recommended to use a higher order solution 
 * if the time-step cannot reasonably be decreased.
 * 
 * Author: Antoni Matyjaszkiewicz
 * 
 * Created: 10/08/2009
 * Updated: 14/08/2009
 */

package bsim.ode;

/*
 * Most likely these will have to be updated or overloaded (some more)
 * to be able to cope with stochastic odes [honeycutt: stochastic rk algorithms]
 * as it looks like this is the phase where random variables must be integrated.
 */

public class BSimOdeSolver {
	// ----------------- Euler solvers ------------------
	// --------------------------------------------------

	/** 
	 * Numerically solve a single ODE with Euler's method
	 * y = euler(ODE, x, y, step_size)
	*/ 
	public static double euler(BSimOdeSingle ode, double x, double y, double h){
		// Next y value = time-step * (dy/dx)
		y += h*ode.derivative(x, y);
		
		return y;
	}
	
	/**
	 *  Numerically solve an ODE system with Euler's method
	 *  y = euler(ODE_system, x, y_vector, step_size)
	 */
	public static double[] euler(BSimOdeSystem odes, double x, double[] y, double h){
		int numEq = odes.getNumEq();
		double[] dydx = odes.derivativeSystem(x, y);
		
		for(int i = 0;i<numEq;i++){
			y[i] += h*dydx[i];
		}
		
		return y;
	}
	

	// ---------- Runge-Kutta 2nd order solvers ---------
	// --------------------------------------------------

	/** 
	 * Numerically solve a single ODE with 2nd order Runge-Kutta method
	 * y = rungeKutta23(ODE, x, y, step_size)
	 */
	public static double rungeKutta23(BSimOdeSingle ode, double x, double y, double h){
		double k1 = 0.0, k2 = 0.0;
		
		// Intermediate trial step coefficients (k values)
	    k1 = h*ode.derivative(x, y);
		k2 = h*ode.derivative(x + 0.5*h, y + 0.5*k1);	
		
		// New y value
		y += k2;
		
		return y;
	}
	
	/** 
	 * Numerically solve an ODE system with 2nd order Runge-Kutta method
	 * y = rungeKutta23(ODE_system, x, y_vector, step_size)
	 */
	public static double[] rungeKutta23(BSimOdeSystem odes, double x, double[] y, double h){
		int numEq = odes.getNumEq();
		double[] dydx = new double[numEq];
		double[] yNew = new double[numEq];
		double[] k1 = new double[numEq];
		double[] k2 = new double[numEq];	
	
		// Calculate all k1 values
		dydx = odes.derivativeSystem(x, y);
		for(int i=0;i<numEq;i++){ k1[i] = h*dydx[i]; }
	    
		// Calculate all k2 values
	    for(int i=0;i<numEq;i++){ yNew[i] = y[i] + 0.5*k1[i]; }
		dydx = odes.derivativeSystem(x + 0.5*h, yNew);
		for(int i=0;i<numEq;i++){ k2[i] = h*dydx[i]; }
		
		// Compute vector of new y values
		for(int i=0;i<numEq;i++){
			y[i] += k2[i];
		}
		
		return y;
	}

	// ---------- Runge-Kutta 4th order solvers ---------
	// --------------------------------------------------

	/** 
	 * Numerically solve a single ODE function with 4th order Runge-Kutta method
	 * y = rungeKutta45(ODE, x, y, step_size)
	 */
	public static double rungeKutta45(BSimOdeSingle ode, double x, double y, double h){
		double k1 = 0.0, k2 = 0.0, k3 = 0.0, k4 = 0.0;

		// Intermediate trial step coefficients (k values)
	    k1 = h*ode.derivative(x, y);
		k2 = h*ode.derivative(x + 0.5*h, y + 0.5*k1);
		k3 = h*ode.derivative(x + 0.5*h, y + 0.5*k2);
		k4 = h*ode.derivative(x + h, y + k3);
		
		// New y value based on the trial steps
		y += k1/6 + k2/3 + k3/3 + k4/6;
		
		return y;
	}
	
	/**
	 *  Numerically solve an ODE system with 4th order Runge-Kutta method
	 *  y = rungeKutta45(ODE_system, x, y_vector, step_size)
	 */
	public static double[] rungeKutta45(BSimOdeSystem odes, double x, double[] y, double h){
		int numEq = odes.getNumEq();
		double[] dydx = new double[numEq];
		double[] yNew = new double[numEq];
		double[] k1 = new double[numEq];
		double[] k2 = new double[numEq];
		double[] k3 = new double[numEq];
		double[] k4 = new double[numEq];		
	
		// Calculate all k1 values
		dydx = odes.derivativeSystem(x, y);
		for(int i=0;i<numEq;i++){ k1[i] = h*dydx[i]; }
	    
		// Calculate all k2 values
	    for(int i=0;i<numEq;i++){ yNew[i] = y[i] + 0.5*k1[i]; }
		dydx = odes.derivativeSystem(x + 0.5*h, yNew);
		for(int i=0;i<numEq;i++){ k2[i] = h*dydx[i]; }
		
		// Calculate all k3 values
	    for(int i=0;i<numEq;i++){ yNew[i] = y[i] + 0.5*k2[i]; }
		dydx = odes.derivativeSystem(x + 0.5*h, yNew);
		for(int i=0;i<numEq;i++){ k3[i] = h*dydx[i]; }

		// Calculate all k4 values 
	    for(int i=0;i<numEq;i++){ yNew[i] = y[i] + k3[i]; }
		dydx = odes.derivativeSystem(x + h, yNew);
		for(int i=0;i<numEq;i++){ 
			k4[i] = h*dydx[i];
		}
		
		// Compute vector of new y values
		for(int i=0;i<numEq;i++){
			y[i] += k1[i]/6 + k2[i]/3 + k3[i]/3 + k4[i]/6;
		}

		return y;
	}
}


