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
import bsim.scene.BSimScene;

public abstract class BSimParticle {		

	private Vector3d position; // microns		
	private Vector3d force; // piconewtons	
	private double radius; // microns	
	private BSimScene scene; // the environment that the particle exists in
		
	public BSimParticle(Vector3d newPosition, double newRadius, BSimScene newScene) {	
		position = new Vector3d();
		force = new Vector3d();		
		radius = newRadius;
		scene = newScene;
		
		position.set(newPosition);
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
		Vector3d velocity = new Vector3d();
		velocity.scale(1/(6.0*Math.PI*radius*BSimParameters.visc), force); // pN/(micrometers*Pa sec) = micrometers/sec 
		position.scaleAdd(BSimParameters.dt, velocity, position);
		force.set(0,0,0);
		
		if(position.x > BSimParameters.xBound) position.x -= BSimParameters.xBound;
		if(position.y > BSimParameters.yBound) position.y -= BSimParameters.yBound;
		if(position.z > BSimParameters.zBound) position.z -= BSimParameters.zBound;				
	}
		
	public Vector3d getPosition() { return position; }	
	public Vector3d getForce() { return force; }	
	public double getRadius() { return radius; }
	public double getSurfaceArea() { return 4*Math.PI*Math.pow(radius,2); }
	public BSimScene getScene() { return scene; }
	public void addForce(Vector3d f) { force.add(f); }
	public void setRadius(double r) { radius = r; }
		
	public static double distance(BSimParticle a, BSimParticle b) {
		Vector3d d = new Vector3d();
        d.sub(a.position, b.position);
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
		f.sub(p.position, q.position);			
		f.normalize();
		f.scale(m);
		p.addForce(f);
		f.negate();
		q.addForce(f);
	}
		
}

