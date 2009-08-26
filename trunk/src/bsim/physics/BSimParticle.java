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

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import bsim.drawable.bacteria.BSimBacterium;
import bsim.drawable.bead.BSimBead;
import bsim.drawable.vesicle.BSimVesicle;

public abstract class BSimParticle {		

	protected Point3d position = new Point3d(); // microns		
	protected Vector3d force = new Vector3d(); // piconewtons
	protected double radius; // microns	
	
	protected double visc = 1e-3;
	protected double dt = 0.01;
	
	
	/**
	 * General constructor.
	 */
	public BSimParticle(double[] newPosition, double newRadius) {
		super();	
		position.set(newPosition);		
		radius = newRadius;
	}
		
	public void updatePosition() {
		System.out.println(position);
		System.out.println(force);
		position.scaleAdd(dt/(6.0*Math.PI*radius*visc), force, position);		
	}
		
	public void collide(BSimBacterium b) { }	
	public void collide(BSimBead b) { }
	public void collide(BSimVesicle v) { }	
	public void deNovo() { }
	
	public double[] getPosition() { double[] p = new double[3]; position.get(p); return p; }	
	public double[] getForce() { double[] f = new double[3]; force.get(f); return f; }
	public double getRadius() { return radius; }
	public void setPosition(double[] p) { position.set(p); }
	public void setForce(double[] f) { force.set(f); }
	public void setRadius(double r) { this.radius = r; }
	
}

