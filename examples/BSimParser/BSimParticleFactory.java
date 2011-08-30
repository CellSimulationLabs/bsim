package BSimParser;

import java.awt.Color;
import java.util.HashMap;
import java.util.Vector;
import javax.vecmath.Vector3d;
import bsim.BSim;

/**
 * Factory to generate set of particles having been given a
 * set of parameters from the file.
 */
class BSimParticleFactory {
	
	public static Vector<BSimFromFileParticle> parse (String paramStr, BSim sim) {
		
		// Grab the attribute value pairs that we need
		HashMap<String,String> params = BSimParser.parseAttributeValuePairs(paramStr);
		
		// Parameters for the particle population
		int      popSize     = 1;
		double   partSize    = 1.0;
		Vector3d bndStartVec = new Vector3d(0.0, 0.0, 0.0);
		Vector3d bndEndVec   = new Vector3d(10.0, 10.0, 10.0);
		Color    partCol     = new Color(205,197,191);
		
		// Update the population size
		if (params.containsKey("Population")) { 
			popSize = BSimParser.parseToInt(params.get("Population"));
		}
		
		// Update the particle size (microns)
		if (params.containsKey("ParticleSize")) {
			partSize = BSimParser.parseToDouble(params.get("ParticleSize"));
		}
		
		// Positions of the form BoundStart=0.1;2.4;5.1
		BSimParser.assignParamToVector3d(params, "BoundStart", bndStartVec);
		
		// Positions of the form BoundEnd=0.1;2.4;5.1
		BSimParser.assignParamToVector3d(params, "BoundEnd", bndEndVec);
		
		// Update the colour of the particles	
		Color tempCol = BSimParser.getColorFromParam(params, "Color");
		if (tempCol != null) { partCol = tempCol; }
		
		// Generate the population
		Vector<BSimFromFileParticle> particles = new Vector<BSimFromFileParticle>(popSize);
		for (int i=0; i<popSize; i++) {
			BSimFromFileParticle p = new BSimFromFileParticle(sim, BSimParser.randomVector3d(bndStartVec, bndEndVec), partSize, partCol);
			particles.add(p);
		}

		return particles;
	}
}
