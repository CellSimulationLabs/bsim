package bsim.particle;

import javax.vecmath.Vector3d;

import bsim.BSim;

/**
 * 
 */
public class BSimVesicle extends BSimParticle {

	/**
	 * Constructor for a vesicle at a position and of a given size.
	 * @param sim The simulation to add the vesicle to.
	 * @param position Position of the vesicle.
	 * @param radius Radius of the vesicle (microns).
	 */
	public BSimVesicle(BSim sim, Vector3d position, double radius) {
		super(sim, position, radius);
	}	
}
