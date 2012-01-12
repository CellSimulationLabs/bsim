package bsim.particle;

import java.util.Random;
import java.util.Vector;

import javax.vecmath.Vector3d;

import bsim.BSim;

/**
 * 
 */
public class BSimParticle {	
		
	protected static Random rng = new Random();
	
	protected Vector3d position ; // microns		
	protected Vector3d force = new Vector3d(); // piconewtons	
	protected double radius; // microns	
	protected double brownianForceMagnitude;
	protected BSim sim; // the environment that the particle exists in	
		
	public BSimParticle(BSim sim, Vector3d position, double radius) {	
		this.sim = sim;
		this.position = position;
		setRadius(radius);
	}	
	
   /**
   * Sets the magnitude of the Brownian force such that var(X(t)) = var(Y(t)) = var(Z(t)) = 2*D*t
   */
	public void setBrownianForceMagnitude() {
		brownianForceMagnitude = Math.sqrt(2*stokesCoefficient()*BSim.BOLTZMANN*sim.getTemperature()/sim.getDt())*Math.pow(10,9);
	}
	public void setRadius(double r) { radius = r; setBrownianForceMagnitude(); }
	public void setRadiusFromSurfaceArea(double s) { radius = Math.sqrt(s/(4*Math.PI)); }
	public void addForce(Vector3d f) { force.add(f); }
	
	public Vector3d getPosition() { return position; }
	public Vector3d getForce() { return force; }
	public double getRadius() { return radius; }
	public double getSurfaceArea() { return surfaceArea(radius); }
	public double surfaceArea(double r) { return 4*Math.PI*Math.pow(r,2); }	
	public double stokesCoefficient() { return 6.0*Math.PI*radius*sim.getVisc(); } // micrometers*Pa sec
	
	/**
	 * Call in BSimTicker#tick() 
	 */		
	public void action() { brownianForce(); }
	
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
	
	/**
	 * Applies a Brownian force to the particle. The applied force is a function of 
	 * radius, viscosity and temperature; if viscosity or temperature is changed externally, 
	 * you should call setBrownianForceMagnitude() again
	 */
	public void brownianForce() {						
		Vector3d f = new Vector3d(rng.nextGaussian(), rng.nextGaussian(), rng.nextGaussian());
		f.scale(brownianForceMagnitude); 
		addForce(f);
	}

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
	@SuppressWarnings("unchecked")
	public boolean intersection(@SuppressWarnings("rawtypes") Vector particles) {
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
		addForce(f);
		f.negate();
		p.addForce(f);
	}
	
    /**
     * Applies a reaction force with the properties
     * F(0) = Inf
     * F(this.radius + p.radius) = 0
     * For a particle exerting a force f, the minimum distance of approach to p is
     * d = (this.radius + p.radius) exp(-f/k)
     * i.e. if the particle exerts a force 1 pN, then k = 1 will prevent it from
     * coming closer than (this.radius + p.radius)/e to p. 
     */
	public void logReaction(BSimParticle p, double k) {
		reaction(p, -k*Math.log(this.distance(p)/(this.radius + p.radius)));
	}
	
	/*
	 * Called when the particle goes above or below the bounds of the simulation.
	 * Overwrite these methods if you want different behaviour
	 */	
	protected void xAbove() { 
		if(sim.getSolid()[0]) position.x = bounceAbove(position.x,sim.getBound().x);
		else position.x = wrapAbove(position.x,sim.getBound().x);
		// Ensure in small environments that the new position is not outside
		if (position.x > sim.getBound().x) { position.x = sim.getBound().x; }
		else { if (position.x < 0) { position.x = 0; } }
	}
	protected void xBelow() { 
		if(sim.getSolid()[0]) position.x = bounceBelow(position.x,sim.getBound().x);
		else position.x = wrapBelow(position.x,sim.getBound().x);
		// Ensure in small environments that the new position is not outside
		if (position.x > sim.getBound().x) { position.x = sim.getBound().x; }
		else { if (position.x < 0) { position.x = 0; } }
	}
	protected void yAbove() { 
		if(sim.getSolid()[1]) position.y = bounceAbove(position.y,sim.getBound().y);
		else position.y = wrapAbove(position.y,sim.getBound().y);
		// Ensure in small environments that the new position is not outside
		if (position.y > sim.getBound().y) { position.y = sim.getBound().y; }
		else { if (position.y < 0) { position.y = 0; } }
	}
	protected void yBelow() { 
		if(sim.getSolid()[1]) position.y = bounceBelow(position.y,sim.getBound().y);
		else position.y = wrapBelow(position.y,sim.getBound().y); 
		// Ensure in small environments that the new position is not outside
		if (position.y > sim.getBound().y) { position.y = sim.getBound().y; }
		else { if (position.y < 0) { position.y = 0; } }
	}
	protected void zAbove() { 
		if(sim.getSolid()[2]) position.z = bounceAbove(position.z,sim.getBound().z);
		else position.z = wrapAbove(position.z,sim.getBound().z);
		// Ensure in small environments that the new position is not outside
		if (position.z > sim.getBound().z) { position.z = sim.getBound().z; }
		else { if (position.z < 0) { position.z = 0; } }
	}
	protected void zBelow() { 
		if(sim.getSolid()[2]) position.z = bounceBelow(position.z,sim.getBound().z);
		else position.z = wrapBelow(position.z,sim.getBound().z);
		// Ensure in small environments that the new position is not outside
		if (position.z > sim.getBound().z) { position.z = sim.getBound().z; }
		else { if (position.z < 0) { position.z = 0; } }
	}
	
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

