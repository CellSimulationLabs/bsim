package bsim.dde;

import java.util.Vector;

import bsim.dde.BSimDdeSystem;

/**
 * Solver routines for numerical simulation of DDEs (Fixed time-step). 
 * These make use of basic ODE solvers and so stability is not ensured
 * in all cases. Be sure to run simulations with smaller time steps to
 * check that the results are accurate.
 */
public class BSimDdeSolver {
	
	/**
	 * Create an initial state, including history.
	 * @param ddes The DDE system.
	 * @param h Timestep.
	 * @return Initial state vector.
	 */
	@Deprecated
	public static Vector<double[]> getInitialState (BSimDdeSystem ddes, double h){
		// Create the initial state (including history)
		Vector<double[]> ys = new Vector<double[]>((int)(ddes.getMaxDelay()/h));
		for (int i=0; i<ys.size(); i++){
			ys.add(ddes.getICs());
		}
		ddes.setInitialHistory(ys);
		return ys;
	}
	
	/**
	 * Calculates a delayed (historic) state. Should be used by the DDE system
	 * when retrieving historic states of the system. 
	 * @param ys The state vector.
	 * @param h Timestep
	 * @param delay Required delayed state vector
	 * @return Delayed state vector.
	 */
    @Deprecated
	public static double[] getDelayedState(Vector<double[]> ys, double h, double delay){
		// Calculate the index for the delayed value (rounds down)
		int i = (int)(delay/h);
		// Return the correct delayed state
		return ys.get(i);
	}
	
	/**
	 * Shifts all elements in the state vector by one timestep.
	 * Looses last historic state vector.
	 * @param ys The state vector.
	 */
    @Deprecated
	public static void shiftState(Vector<double[]> ys){
		// Shift all elements and loose last
		double[] y = new double[ys.get(0).length];
		int s = ys.size();
		for (int i=s-1; i > 0; i--){
			ys.set(i, ys.get(i-1));
		}
		ys.set(0, y);
	}
	
	// ----------------- Euler solvers ------------------
	// --------------------------------------------------
	
	/**
	 * Numerically solve an DDE system with Euler's method.
	 * @param odes The {@link BSimDdeSystem} to solve.
	 * @param t Independent variable.
	 * @param ys Vector of dependent variables including history.
	 * @param h Time step for ode solution.
	 * @return Vector of dependent variables at next time step including history.
	 */
    @Deprecated
	public static Vector<double[]> euler(BSimDdeSystem ddes, double t, Vector<double[]> ys, double h){
		int numEq = ddes.getNumEq();
		System.out.println("Size of ys = " + ys.size());
		double[] dydx = ddes.derivativeSystem(t, ys.get(0), ys);
		BSimDdeSolver.shiftState(ys);
		double[] y = ys.get(0);
		for(int i = 0;i<numEq;i++){
			y[i] += h*dydx[i];
		}
		return ys;
	}
	

	// ---------- Runge-Kutta 2nd order solvers ---------
	// --------------------------------------------------
	
	/** 
	 * Numerically solve an DDE system with 2nd order Runge-Kutta method.
	 * @param odes The {@link BSimDdeSystem} to solve.
	 * @param t Independent variable.
	 * @param y Vector of dependent variables.
	 * @param h Time step for ode solution.
	 * @return Vector of dependent variables at next time step.
	 */
    @Deprecated
	public static Vector<double[]> rungeKutta23(BSimDdeSystem ddes, double t, Vector<double[]> ys, double h){
		int numEq = ddes.getNumEq();
		double[] dydx = new double[numEq];
		double[] yNew = new double[numEq];
		double[] k1 = new double[numEq];
		double[] k2 = new double[numEq];
		double[] y = ys.get(0);
	
		// Calculate all k1 values
		dydx = ddes.derivativeSystem(t, y, ys);
		for(int i=0;i<numEq;i++){ k1[i] = h*dydx[i]; }
	    
		// Calculate all k2 values
	    for(int i=0;i<numEq;i++){ yNew[i] = y[i] + 0.5*k1[i]; }
		dydx = ddes.derivativeSystem(t + 0.5*h, yNew, ys);
		for(int i=0;i<numEq;i++){ k2[i] = h*dydx[i]; }
		
		BSimDdeSolver.shiftState(ys);
		y = ys.get(0);
		
		// Compute vector of new y values
		for(int i=0;i<numEq;i++){
			y[i] += k2[i];
		}
		
		return ys;
	}

	// ---------- Runge-Kutta 4th order solvers ---------
	// --------------------------------------------------
	
	/**
	 * Numerically solve an DDE system with 4th order Runge-Kutta method.
	 * @param odes The {@link BSimDdeSystem} to solve.
	 * @param t Independent variable.
	 * @param y Vector of dependent variables.
	 * @param h Time step for ode solution.
	 * @return Vector of dependent variables at next time step.
	 */
    @Deprecated
	public static Vector<double[]> rungeKutta45(BSimDdeSystem ddes, double t, Vector<double[]> ys, double h){
		int numEq = ddes.getNumEq();
		double[] dydx = new double[numEq];
		double[] yNew = new double[numEq];
		double[] k1 = new double[numEq];
		double[] k2 = new double[numEq];
		double[] k3 = new double[numEq];
		double[] k4 = new double[numEq];	
		double[] y = ys.get(0);
	
		// Calculate all k1 values
		dydx = ddes.derivativeSystem(t, y, ys);
		for(int i=0;i<numEq;i++){ k1[i] = h*dydx[i]; }
	    
		// Calculate all k2 values
	    for(int i=0;i<numEq;i++){ yNew[i] = y[i] + 0.5*k1[i]; }
		dydx = ddes.derivativeSystem(t + 0.5*h, yNew, ys);
		for(int i=0;i<numEq;i++){ k2[i] = h*dydx[i]; }
		
		// Calculate all k3 values
	    for(int i=0;i<numEq;i++){ yNew[i] = y[i] + 0.5*k2[i]; }
		dydx = ddes.derivativeSystem(t + 0.5*h, yNew, ys);
		for(int i=0;i<numEq;i++){ k3[i] = h*dydx[i]; }

		// Calculate all k4 values 
	    for(int i=0;i<numEq;i++){ yNew[i] = y[i] + k3[i]; }
		dydx = ddes.derivativeSystem(t + h, yNew, ys);
		for(int i=0;i<numEq;i++){ 
			k4[i] = h*dydx[i];
		}
		
		BSimDdeSolver.shiftState(ys);
		y = ys.get(0);
		
		// Compute vector of new y values
		for(int i=0;i<numEq;i++){
			y[i] += k1[i]/6 + k2[i]/3 + k3[i]/3 + k4[i]/6;
		}

		return ys;
	}
}
