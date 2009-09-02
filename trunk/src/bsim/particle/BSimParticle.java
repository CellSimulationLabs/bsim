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

import bsim.BSimParameters;
import bsim.particle.bacterium.BSimBacterium;
import bsim.particle.bead.BSimBead;
import bsim.particle.vesicle.BSimVesicle;

public abstract class BSimParticle {		

	private Vector3d position = new Vector3d(); // microns		
	private Vector3d force = new Vector3d(); // piconewtons
	private double radius; // microns	
	
	public BSimParticle(Vector3d newPosition, double newRadius) {
		super();	
		position.set(newPosition);		
		radius = newRadius;
	}
	
	/*
	* Interactions between particles: reaction forces, fusions, etc 
	*/
	public static void interaction(BSimBacterium p, BSimBacterium q) {
		double d = distance(p, q);
		if(d < 0) reaction(p,q,d*BSimParameters.reactForce);		
	}	
    
	public static void interaction(BSimBacterium bacterium, BSimBead bead) {
		
		double d = distance(bacterium, bead);
		double magnitude;
		double wellWidth = BSimParameters.wellWidthBactBead;
		double wellDepth = BSimParameters.wellDepthBactBead;
		
		if (d>wellWidth || d == 0) magnitude = 0;
		else if(d>(wellWidth/2.0)) magnitude = -wellDepth + (d-(wellWidth/2.0))*wellDepth/(wellWidth/2.0);
		else if(d>=0.0) magnitude = -(d*2.0*wellDepth/wellWidth);		
		else magnitude = d * BSimParameters.reactForce;
				
		reaction(bacterium, bead, magnitude);
	}		
	
	public static void interaction(BSimBead p, BSimBead q) {
		double d = distance(p, q);
		if(d < 0) reaction(p,q,d*BSimParameters.reactForce);		
	}
	
	public static void interaction(BSimBead bead, BSimVesicle vesicle) {
		double d = distance(bead, vesicle);
		if(d < 0) reaction(bead,vesicle,d*BSimParameters.reactForce);		
	}
	
	/*
	 * Actions independent of other obstacles: flagellar forces, adding chemicals, etc
	 */		
	public abstract void action();
	
	/*
	 * Update the position of the particle according to Stokes' law	
	 * Payable in force, yarr
	 */
	public void updatePosition() {
		position.scaleAdd(BSimParameters.dt/(6.0*Math.PI*radius*BSimParameters.visc), force, position);
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
        	
    /*
     * Applies a force on p of magnitude m towards p,
     * and a force on q of magnitude m towards q.
     */
	public static void reaction(BSimParticle p, BSimParticle q, double m) {
		Vector3d f = new Vector3d();
		f.sub(p.getPosition(), q.getPosition());			
		f.normalize();
		f.scale(m);
		p.addForce(f);
		f.negate();
		q.addForce(f);
	}
		
}

