package bsim;


/**
 * Represents a modular particle behaviour, for example, running and tumbling 
 * like a bacterium or wiggling like a Brownian particle. The main method of a mixin
 * (likely to be called in BSimParticle#action()) should be named the same as the class, 
 * for example BSimRunTumbleMixin#runTumble(). 
 */
public abstract class BSimMixin {
	
	protected BSim sim;
	protected BSimParticle particle;
	
	public BSimMixin(BSim sim, BSimParticle particle) {
		this.sim = sim;
		this.particle = particle;
	}

}
