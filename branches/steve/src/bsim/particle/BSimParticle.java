package bsim.particle;

import java.util.Vector;

import javax.vecmath.Vector3d;

import bsim.BSim;

public abstract class BSimParticle {	
		
	protected Vector3d position ; // microns		
	protected Vector3d force = new Vector3d(); // piconewtons	
	protected double radius; // microns	
	protected BSim sim; // the environment that the particle exists in	
		
	public BSimParticle(BSim sim, Vector3d position, double radius) {	
		this.sim = sim;
		this.position = position;
		this.radius = radius;
	}	
	
	/**
	 * Actions independent of other obstacles: flagellar forces, adding chemicals, etc
	 */		
	public abstract void action();
	
	/**
	 * Update the position of the particle according to Stokes' law
	 */
	public void updatePosition() {
		Vector3d velocity = new Vector3d();
		velocity.scale(1/stokesCoefficient(), force); // pN/(micrometers*Pa sec) = micrometers/sec 
		position.scaleAdd(sim.getDt(), velocity, position);
		force.set(0,0,0); // Payable in force, yarr
		
		if(position.x > sim.getBound().x) xAbove();
		if(position.x < 0) xBelow();
		if(position.y > sim.getBound().y) yAbove();
		if(position.y < 0) yBelow();
		if(position.z > sim.getBound().z) zAbove();
		if(position.z < 0) zBelow();
	}
		
	public Vector3d getPosition() { return position; }	
	public double getRadius() { return radius; }	
	public double stokesCoefficient() { return 6.0*Math.PI*radius*sim.getVisc(); } // micrometers*Pa sec

	/**
	 * Distance between particle centres (always positive)
	 */
	public double distance(BSimParticle p) {
		Vector3d d = new Vector3d();
        d.sub(this.position, p.position);
        return d.length();
	}
	
	/**
	 * Distance between particle edges (can be negative)
	 */
	public double outerDistance(BSimParticle p) {
		return this.distance(p) - (this.radius + p.radius);
	}		
	
	/**
	 * Tests if this particle is intersecting with any in the vector
	 */
	public boolean intersection(Vector particles) {
		for(BSimParticle p : (Vector<BSimParticle>)particles)
			if (outerDistance(p) < 0) return true;	
		return false;
	}	
        	
    /**
     * Applies a force on this of magnitude m towards this,
     * and a force on p of magnitude m towards p.
     */
	public void reaction(BSimParticle p, double m) {
		Vector3d f = new Vector3d();
		f.sub(this.position, p.position);			
		f.normalize();
		f.scale(m);
		this.force.add(f);
		f.negate();
		p.force.add(f);
	}
	
	/*
	 * Called when the particle goes above or below the bounds of the simulation.
	 * Overwrite these methods if you want different behaviour; you can use the 
	 * bounce...() methods below for solid boundaries
	 */	
	public void xAbove() { position.x = wrapAbove(position.x,sim.getBound().x); }
	public void xBelow() { position.x = wrapBelow(position.x,sim.getBound().x); }
	public void yAbove() { position.y = wrapAbove(position.y,sim.getBound().y); }
	public void yBelow() { position.y = wrapBelow(position.y,sim.getBound().y); }
	public void zAbove() { position.z = wrapAbove(position.z,sim.getBound().z); }
	public void zBelow() { position.z = wrapBelow(position.z,sim.getBound().z); }	
	
	/*
	 * Methods returning the appropriate coordinate for wrapping/bouncing a particle 
	 * that has gone above/below a bound 
	 */
	protected double wrapAbove(double coord, double edge) {
		return coord - edge;		
	}
	protected double wrapBelow(double coord, double edge) {
		return coord + edge;		
	}
	protected double bounceAbove(double coord, double edge) {
		return coord - 2*(coord - edge);	
	}
	protected double bounceBelow(double coord, double edge) {
		return coord + 2*-coord;		
	}
	
		
}
