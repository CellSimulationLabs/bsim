package bsim.particle;

import javax.vecmath.Vector3d;

import bsim.BSim;

public abstract class BSimParticle {	
		
	private Vector3d position ; // microns		
	private Vector3d force = new Vector3d(); // piconewtons	
	private double radius; // microns	
	protected BSim sim; // the environment that the particle exists in
	
	private double visc = 1e-3;
		
	public BSimParticle(BSim sim, Vector3d position, double radius) {	
		this.sim = sim;
		this.position = position;
		this.radius = radius;
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
		velocity.scale(1/stokesCoefficient(), force); // pN/(micrometers*Pa sec) = micrometers/sec 
		position.scaleAdd(sim.getDt(), velocity, position);
		force.set(0,0,0);
	}
		
	public Vector3d getPosition() { return position; }	
	public Vector3d getForce() { return force; }	
	public double getRadius() { return radius; }	
	public double stokesCoefficient() { return 6.0*Math.PI*radius*visc; } // micrometers*Pa sec
			
	public double distance(BSimParticle p) {
		Vector3d d = new Vector3d();
        d.sub(this.position, p.position);
        return d.length();
	}
	
	public double outerDistance(BSimParticle p) {
		return this.distance(p) - (this.radius + p.radius);
	}	
		
	public void addForce(Vector3d f) { force.add(f); }
        	
    /*
     * Applies a force on this of magnitude m towards this,
     * and a force on p of magnitude m towards p.
     */
	public void reaction(BSimParticle p, double m) {
		Vector3d f = new Vector3d();
		f.sub(this.position, p.position);			
		f.normalize();
		f.scale(m);
		this.addForce(f);
		f.negate();
		p.addForce(f);
	}
		
}

