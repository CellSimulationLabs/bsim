package BSimParser;

import java.util.HashMap;
import java.util.Vector;
import javax.vecmath.Vector3d;
import bsim.particle.BSimParticle;
import bsim.BSim;

/**
 * Factory to generate set of particles having been given a
 * set of parameters from the file.
 */
class BSimParticleFactory {
	
	public static Vector<BSimParticle> parse (String paramStr, BSim sim) {
		
		// Grab the attribute value pairs that we need
		HashMap<String,String> params = BSimParser.parseAttributeValuePairs(paramStr);
		
		// Parameters for the particle population
		int      popSize     = 1;
		double   partSize    = 1.0;
		Vector3d bndStartVec = new Vector3d(0.0, 0.0, 0.0);
		Vector3d bndEndVec   = new Vector3d(10.0, 10.0, 10.0);
		
		// Update the population size
		BSimParser.assignParamToInt(params, "Population", popSize);
				
		// Update the particle size (microns)
		BSimParser.assignParamToDouble(params, "ParticleSize", partSize);
		
		// Positions of the form BoundStart=0.1;2.4;5.1
		BSimParser.assignParamToVector3d(params, "BoundStart", bndStartVec);
		
		// Positions of the form BoundEnd=0.1;2.4;5.1
		BSimParser.assignParamToVector3d(params, "BoundEnd", bndEndVec);
		
		// Generate the population
		Vector<BSimParticle> particles = new Vector<BSimParticle>(popSize);
		for (int i=0; i<popSize; i++) {
			BSimParticle p = new BSimParticle(sim, BSimParser.randomVector3d(bndStartVec, bndEndVec), partSize);
			particles.add(p);
		}

		return particles;
	}
}
