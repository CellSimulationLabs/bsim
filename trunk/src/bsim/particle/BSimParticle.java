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

import javax.vecmath.Vector3d;

import bsim.particle.bacterium.BSimBacterium;
import bsim.particle.bead.BSimBead;
import bsim.particle.vesicle.BSimVesicle;

public abstract class BSimParticle {		

	private Vector3d position = new Vector3d(); // microns		
	private Vector3d force = new Vector3d(); // piconewtons
	private double radius; // microns	
	
	private double visc = 1e-3;
	private double dt = 0.01;
	
	public BSimParticle(Vector3d newPosition, double newRadius) {
		super();	
		position.set(newPosition);		
		radius = newRadius;
	}
	
	/*
	 * Interactions with other obstacles: reaction forces, fusions, etc 
	 */
	public abstract void interaction(BSimBacterium b);	
	public abstract void interaction(BSimBead b);
	public abstract void interaction(BSimVesicle v);
	
	/*
	 * Actions independent of other obstacles: flagellar forces, adding chemicals, etc
	 */		
	public abstract void action();
	
	/*
	 * Update the position of the particle according to Stokes' law	
	 */
	public void updatePosition() {
		position.scaleAdd(dt/(6.0*Math.PI*radius*visc), force, position);
		force.set(0,0,0);
	}
		
	public Vector3d getPosition() { return position; }	
	public Vector3d getForce() { return force; }
	public double getRadius() { return radius; }
	public void addForce(Vector3d f) { force.add(f); }
	public void setRadius(double r) { radius = r; }
	
	public static double distance(BSimParticle a, BSimParticle b) {
		Vector3d d = new Vector3d();
        d.sub(a.getPosition(), b.getPosition());
        return d.length();
	}
	
    public static boolean particlesIntersecting(BSimParticle a, BSimParticle b) {
        if(distance(a,b) < (a.radius + b.radius)) return true;
        else return false;
}
	
	
}

