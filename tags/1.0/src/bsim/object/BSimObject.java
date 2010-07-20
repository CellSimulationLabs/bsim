/**
 * BSimObject.java
 *
 * Abstract class which defines the basic properties of an object in our simulation. 
 * It cannot be used directly and should instead be extended to include additional
 * properties of a real object and imeplement a redraw method to display the item
 * as required.
 *
 * Authors: Thomas Gorochowski
 *          Ian Miles
 * Created: 12/07/2008
 * Updated: 20/07/2008
 */


// Define the location of the class in the bsim package
package bsim.object;

// Standard packages required by the application
import java.awt.*;


public abstract class BSimObject {
	
	public static int OBTYPE_OBJECT = 0;
	public static int OBTYPE_BACT = 1;
	public static int OBTYPE_PART = 2;
	protected int obType = OBTYPE_OBJECT;
	
	// General properties of all objects in a simulation
	protected double size  = 0, // microns
	                 mass  = 0, 
	                 speed = 0; // microns per second
	protected double position[]  = {0,0};
	protected double centrePos[] = {0,0};
	protected double direction[] = {0,0};
	
	/**
	 * General constructor.
	 */
	public BSimObject(double newSpeed, double newMass, double newSize, 
			double[] newDirection, double[] newPosition, int newType) {
		super();
		speed        = newSpeed;
		mass         = newMass;
		size         = newSize;
		direction[0] = newDirection[0];
		direction[1] = newDirection[1];
		position[0]  = newPosition[0];
		position[1]  = newPosition[1];
		centrePos[0] = newPosition[0] + newSize/2.0;
		centrePos[1] = newPosition[1] + newSize/2.0;
		obType = newType;
	}
	
	
	/**
	 * Abstract method that should be over written to display the object as required.
	 */
	public abstract void redraw(Graphics g);
	
	
	/**
	 * Standard set methods for the class.
	 */
	public void setSize(double s) {size = s;}
	public void setRadius(double r) {size = 2.0*r;}
	public void setMass(double m) {mass = m;}
	public void setSpeed(double s) {speed = s;}
	public void setType(int t) {obType = t;}
	public void setPosition(double[] p) {
		position[0] = p[0]; 
		position[1] = p[1];
		centrePos[0] = p[0] + size/2.0;
		centrePos[1] = p[1] + size/2.0;
	}
	public void setCentrePos(double cp[]) {
		position[0] = cp[0] - size/2.0; 
		position[1] = cp[1] - size/2.0;
		centrePos[0] = cp[0];
		centrePos[1] = cp[1];
	}
	public void setDirection(double[] d) {
		double[] normD = normalise2DVector(d);
		direction[0] = normD[0]; 
		direction[1] = normD[1];
	}
	
	
	/**
	 * Standard get methods for the class.
	 */
	public double getSize () { return size; }
	public double getRadius() {return size/2.0;}
	public double getMass () { return mass; }
	public double getSpeed () { return speed; }
	public int getType() {return obType;}
	public double[] getPosition () { return position; }
	public double[] getCentrePos () { return centrePos; }
	public double[] getDirection () { return direction; }
	
	
	/**
	 * Normalises the length of a given 2D vector.
	 */
	private double[] normalise2DVector(double[] newVector) {
		
		// Variables holding normalising constants
		double xPow2 = Math.pow(newVector[0],2.0);
		double yPow2 = Math.pow(newVector[1],2.0);
		
		// Calculate the normalised components
		newVector[0] = newVector[0] / Math.sqrt(xPow2 + yPow2);
		newVector[1] = newVector[1] / Math.sqrt(xPow2 + yPow2);
		
		// Return the new normalised vector
		return newVector;
	}
}

