package bsim.ode;

/**
 * Interface used for defining a system of ODEs.
 * Defines the ODEs, the number of equations and the initial conditions.
 */
public interface BSimOdeSystem {
	
	/** 
	 * Define a system of derivatives - </br>
	 * dy[0] = ..., dy[1] = ..., etc</br>
	 * then return dy[]
	 */
	public double[] derivativeSystem(double x, double[] y);
	
    /**
     * Get the number of equations in the system
     * (Corresponding to the number in derivativeSystem)
     */
	public int getNumEq();
	
	/**
	 * Get the initial conditions: y1(0), y2(0), etc..
	 */
	public double[] getICs();
	
}
