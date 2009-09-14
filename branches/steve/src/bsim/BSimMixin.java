package bsim;


/**
 * Represents a modular particle behaviour, for example, running and tumbling 
 * like a bacterium or wiggling like a Brownian particle 
 */
public abstract class BSimMixin {
	
	protected BSim sim;
	protected BSimParticle particle;
	
	public BSimMixin(BSim sim, BSimParticle particle) {
		this.sim = sim;
		this.particle = particle;
	}

}
