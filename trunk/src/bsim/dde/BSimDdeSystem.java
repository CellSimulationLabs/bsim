package bsim.dde;

import java.util.Vector;

/**
 * Interface used for defining a system of DDEs.
 * Defines the DDEs, the number of equations and the initial conditions.
 */
public interface BSimDdeSystem {
	
	/** 
	 * Defines a system of derivatives - </br>
	 * dy[0] = ..., dy[1] = ..., etc</br>
	 * and returns dy[]
	 */
	public double[] derivativeSystem(double x, double[] y, Vector<double[]> ys);
	
    /**
     * Get the number of equations in the system
     * (Corresponding to the number in derivativeSystem)
     */
	public int getNumEq();
	
	/**
	 * Get the maximum delay for the system
	 * @return maximum delay
	 */
	public double getMaxDelay();
	
	/**
	 * Get the initial conditions: y1(0), y2(0), etc..
	 */
	public double[] getICs();
	
	/**
	 * Sets the initial history when solving. 
	 * @param ys Vector of historic states (double[])
	 */
	public void setInitialHistory(Vector<double[]> ys);
}
