/**
 * BSimParticle.java
 *
 * Abstract class which defines the basic properties of an object in our simulation. 
 * It cannot be used directly and should instead be extended to include additional
 * properties of a real object.
 *
 * Authors: Thomas Gorochowski
 *          Ian Miles
 *          Mattia Fazzini(Update)
 * Created: 12/07/2008
 * Updated: 07/08/2009
 */
package bsim.physics;



public abstract class BSimParticle {
	
	public static int PART_PART = 0;
	public static int PART_BACT = 1;
	public static int PART_BEAD = 2;
	public static int PART_VES = 3;
	
	protected int partType = PART_PART;
	
	// General properties of all objects in a simulation
	protected double radius  = 0, // microns
	                 mass  = 0, 
	                 speed = 0; // microns per second	
	protected double position[] = {0,0,0};
	protected double direction[] = {0,0,0};
	
	/**
	 * General constructor.
	 */
	public BSimParticle(double newSpeed, double newMass, double newRadius, 
			double[] newDirection, double[] newPosition, int newType) {
		super();
		speed        = newSpeed;
		mass         = newMass;
		radius         = newRadius;
		direction[0] = newDirection[0];
		direction[1] = newDirection[1];
		direction[2] = newDirection[2];
		position[0] = newPosition[0];
		position[1] = newPosition[1];
		position[2] = newPosition[2];
		
		partType = newType;
	}
		
	
	/**
	 * Standard set methods for the class.
	 */	
	public void setRadius(double r) {radius = r;}
	public void setMass(double m) {mass = m;}
	public void setSpeed(double s) {speed = s;}
	public void setType(int t) {partType = t;}
	public void setPosition(double p[]) {
		position[0] = p[0];
		position[1] = p[1];
		position[2] = p[2];
	}
	public void setDirection(double[] d) {
		double[] normD = normalise3DVector(d);
		direction[0] = normD[0]; 
		direction[1] = normD[1];
		direction[2] = normD[2];
	}
	
	
	/**
	 * Standard get methods for the class.
	 */	
	public double getRadius() {return radius;}
	public double getMass () { return mass; }
	public double getSpeed () { return speed; }
	public int getType() {return partType;}	
	public double[] getPosition () { return position; }
	public double[] getDirection () { return direction; }
	
	
	/**
	 * Normalises the length of a given 3D vector.
	 */
	private double[] normalise3DVector(double[] newVector) {
		
		// Variables holding normalising constants
		double xPow2 = Math.pow(newVector[0],2.0);
		double yPow2 = Math.pow(newVector[1],2.0);
		double zPow2 = Math.pow(newVector[2],2.0);
		
		// Calculate the normalised components
		newVector[0] = newVector[0] / Math.sqrt(xPow2 + yPow2 + zPow2);
		newVector[1] = newVector[1] / Math.sqrt(xPow2 + yPow2 + zPow2);
		newVector[2] = newVector[2] / Math.sqrt(xPow2 + yPow2 + zPow2);
		
		// Return the new normalised vector
		return newVector;
	}
}

