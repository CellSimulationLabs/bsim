package bsim.particle;

import javax.vecmath.Vector3d;

import bsim.BSim;

public abstract class BSimParticle {	
		
	private Vector3d position ; // microns		
	private Vector3d force = new Vector3d(); // piconewtons	
	private double radius; // microns	
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
		
	public void setPosition(Vector3d p) { position = p; }
	
	public Vector3d getPosition() { return position; }	
	public Vector3d getForce() { return force; }	
	public double getRadius() { return radius; }	
	public double stokesCoefficient() { return 6.0*Math.PI*radius*sim.getVisc(); } // micrometers*Pa sec
			
	public double distance(BSimParticle p) {
		Vector3d d = new Vector3d();
        d.sub(this.position, p.position);
        return d.length();
	}
	
	public double outerDistance(BSimParticle p) {
		return this.distance(p) - (this.radius + p.radius);
	}	
		
	public void addForce(Vector3d f) { force.add(f); }
        	
    /**
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
	
	public void xAbove() { position.x = bounceAbove(position.x,sim.getBound().x); }
	public void xBelow() { position.x = bounceBelow(position.x,sim.getBound().x); }
	public void yAbove() { position.y = bounceAbove(position.y,sim.getBound().y); }
	public void yBelow() { position.y = bounceBelow(position.y,sim.getBound().y); }
	public void zAbove() { position.z = bounceAbove(position.z,sim.getBound().z); }
	public void zBelow() { position.z = bounceBelow(position.z,sim.getBound().z); }	
	
	private double wrapAbove(double coord, double edge) {
		return coord - edge;		
	}
	private double wrapBelow(double coord, double edge) {
		return coord + edge;		
	}
	private double bounceAbove(double coord, double edge) {
		return coord - 2*(coord - edge);	
	}
	private double bounceBelow(double coord, double edge) {
		return coord + 2*-coord;		
	}
	
		
}

