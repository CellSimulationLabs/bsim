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
package bsim.particle;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import bsim.particle.bacterium.BSimBacterium;
import bsim.particle.bead.BSimBead;
import bsim.particle.vesicle.BSimVesicle;

public abstract class BSimParticle {		

	protected Point3d position = new Point3d(); // microns		
	protected Vector3d force = new Vector3d(); // piconewtons
	protected double radius; // microns	
	
	protected double visc = 1e-3;
	protected double dt = 0.01;
	
	public BSimParticle(Point3d newPosition, double newRadius) {
		super();	
		position.set(newPosition);		
		radius = newRadius;
	}
	
	/*
	 * Update the position of the particle according to Stokes' law	
	 */
	public void updatePosition() {
		position.scaleAdd(dt/(6.0*Math.PI*radius*visc), force, position);		
	}

	/*
	 * Interactions with other obstacles: reaction forces, fusions, etc 
	 */
	public void interaction(BSimBacterium b) { }	
	public void interaction(BSimBead b) { }
	public void interaction(BSimVesicle v) { }
	
	/*
	 * Actions independent of other obstacles: flagellar forces, adding chemicals, etc
	 */		
	public void action() { }
	
	public Point3d getPosition() { return position; }	
	public Vector3d getForce() { return force; }
	public double getRadius() { return radius; }
	public void setPosition(Point3d p) { position.set(p); }
	public void setForce(Vector3d f) { force.set(f); }
	public void setRadius(double r) { this.radius = r; }
	
}

