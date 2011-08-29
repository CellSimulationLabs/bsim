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
		if (params.containsKey("Population")) { 
			popSize = BSimParser.parseToInt(params.get("Population"));
		}
		
		// Update the particle size (microns)
		if (params.containsKey("ParticleSize")) {
			partSize = BSimParser.parseToDouble(params.get("ParticleSize"));
		}
		
		// Positions of the form BoundStart=0.1;2.4;5.1
		if (params.containsKey("BoundStart")) {
			// Split the positions on ';' character
			String[] bndStart = params.get("BoundStart").split(";");
			if (bndStart.length != 3) {
				System.err.println("Problem extracting the BoundStart for a particle population");
			}
			else {
				bndStartVec.set(BSimParser.parseToDouble(bndStart[0]),
								BSimParser.parseToDouble(bndStart[1]),
								BSimParser.parseToDouble(bndStart[2]));
			}
		}
		
		// Positions of the form BoundEnd=0.1;2.4;5.1
		if (params.containsKey("BoundEnd")) {
			// Split the positions on ';' character
			String[] bndEnd = params.get("BoundEnd").split(";");
			if (bndEnd.length != 3) {
				System.err.println("Problem extracting the BoundEnd for a particle population");
			}
			else {
				bndEndVec.set(BSimParser.parseToDouble(bndEnd[0]),
						      BSimParser.parseToDouble(bndEnd[1]),
						      BSimParser.parseToDouble(bndEnd[2]));
			}
		}
		
		// Generate the population
		Vector<BSimParticle> particles = new Vector<BSimParticle>(popSize);
		for (int i=0; i<popSize; i++) {
			BSimParticle p = new BSimParticle(sim, BSimParser.randomVector3d(bndStartVec, bndEndVec), partSize);
			particles.add(p);
		}

		return particles;
	}
}
