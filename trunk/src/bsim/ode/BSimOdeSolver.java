package bsim.ode;

/*
 * Most likely these will have to be updated or overloaded (some more)
 * to be able to cope with stochastic odes [honeycutt: stochastic rk algorithms]
 */

/**
 * Solver routines for numerical simulation of ODEs (Fixed time-step):
 * <ul>
 * <li>Euler's method</li>
 * <li>second order Runge-Kutta</li>
 * <li>fourth order Runge-Kutta</li>
 * </ul>
 * 
 * Each method will estimate the change of the dependent variable based
 * on the previous value of the dependent (y) and independent (x) variables, 
 * and return the new value of the dependent variable.</br>
 * 
 * The methods are in order of increasing accuracy for a given time-step;
 * Euler's method is the most basic, but the fastest as a result of having to 
 * perform relatively few calculations, while the Runge-Kutta methods use an
 * intermediate trial step at the midpoint of an interval to cancel lower order 
 * error terms. </br>
 * 
 * If the time step is too large an Euler solution will quickly diverge from
 * the true solution, therefore it is recommended to use a higher order solution 
 * if the time-step cannot reasonably be decreased.
 */
public class BSimOdeSolver {
	// ----------------- Euler solvers ------------------
	// --------------------------------------------------
	
	/**
	 *  Numerically solve an ODE system with Euler's method.
	 *  
	 * @param odes The {@link BSimOdeSystem} to solve.
	 * @param t Independent variable.
	 * @param y Vector of dependent variables.
	 * @param h Time step for ode solution.
	 * @return Vector of dependent variables at next time step.
	 */
	public static double[] euler(BSimOdeSystem odes, double t, double[] y, double h){
		int numEq = odes.getNumEq();
		double[] dydx = odes.derivativeSystem(t, y);
		
		for(int i = 0;i<numEq;i++){
			y[i] += h*dydx[i];
		}
		
		return y;
	}
	

	// ---------- Runge-Kutta 2nd order solvers ---------
	// --------------------------------------------------
	
	/** 
	 * Numerically solve an ODE system with 2nd order Runge-Kutta method.
	 * 
	 * @param odes The {@link BSimOdeSystem} to solve.
	 * @param t Independent variable.
	 * @param y Vector of dependent variables.
	 * @param h Time step for ode solution.
	 * @return Vector of dependent variables at next time step.
	 */
	public static double[] rungeKutta23(BSimOdeSystem odes, double t, double[] y, double h){
		int numEq = odes.getNumEq();
		double[] dydx = new double[numEq];
		double[] yNew = new double[numEq];
		double[] k1 = new double[numEq];
		double[] k2 = new double[numEq];	
	
		// Calculate all k1 values
		dydx = odes.derivativeSystem(t, y);
		for(int i=0;i<numEq;i++){ k1[i] = h*dydx[i]; }
	    
		// Calculate all k2 values
	    for(int i=0;i<numEq;i++){ yNew[i] = y[i] + 0.5*k1[i]; }
		dydx = odes.derivativeSystem(t + 0.5*h, yNew);
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
	 * Numerically solve an ODE system with 4th order Runge-Kutta method
	 *  
	 * @param odes The {@link BSimOdeSystem} to solve.
	 * @param t Independent variable.
	 * @param y Vector of dependent variables.
	 * @param h Time step for ode solution.
	 * @return Vector of dependent variables at next time step.
	 */
	public static double[] rungeKutta45(BSimOdeSystem odes, double t, double[] y, double h){
		int numEq = odes.getNumEq();
		double[] dydx = new double[numEq];
		double[] yNew = new double[numEq];
		double[] k1 = new double[numEq];
		double[] k2 = new double[numEq];
		double[] k3 = new double[numEq];
		double[] k4 = new double[numEq];		
	
		// Calculate all k1 values
		dydx = odes.derivativeSystem(t, y);
		for(int i=0;i<numEq;i++){ k1[i] = h*dydx[i]; }
	    
		// Calculate all k2 values
	    for(int i=0;i<numEq;i++){ yNew[i] = y[i] + 0.5*k1[i]; }
		dydx = odes.derivativeSystem(t + 0.5*h, yNew);
		for(int i=0;i<numEq;i++){ k2[i] = h*dydx[i]; }
		
		// Calculate all k3 values
	    for(int i=0;i<numEq;i++){ yNew[i] = y[i] + 0.5*k2[i]; }
		dydx = odes.derivativeSystem(t + 0.5*h, yNew);
		for(int i=0;i<numEq;i++){ k3[i] = h*dydx[i]; }

		// Calculate all k4 values 
	    for(int i=0;i<numEq;i++){ yNew[i] = y[i] + k3[i]; }
		dydx = odes.derivativeSystem(t + h, yNew);
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
