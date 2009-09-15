package bsim;

/**
 * Represents an object than may exert a force on a particle
 */
public abstract class BSimExerter {
	
	protected BSim sim;
	protected BSimParticle particle;
	
	public BSimExerter(BSim sim, BSimParticle particle) {
		this.sim = sim;
		this.particle = particle;
	}	

	public abstract void exert();
}
