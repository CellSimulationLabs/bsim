package BSimLogic;

import javax.vecmath.Vector3d;

import bsim.*;

/** 
 * Buffer bacterium logic gate 
 */	
class BSimBufferBacterium extends BSimLogicBacterium {	
	// Redefine the constructor as only single input and no output into the chemical fields
	public BSimBufferBacterium(BSim sim, Vector3d position, BSimChemicalField chemIn, double threshold,
										double reporterDelay) {
		super(sim, position, chemIn, threshold, null, 0.0, null, 0.0, 0.0, reporterDelay);
	}		
	
	@Override
	public void action() {				
		if(chemIn1.getConc(position) > threshold1) {
			activated = true;
			lastActivated = sim.getTime();
			if(lastInActivated == -1 || (sim.getTime() - lastInActivated) > productionDelay) {
				reporter = true;				
			}
		}
		else {
			activated = false;
			lastInActivated = sim.getTime();
			if(lastActivated == -1 || (sim.getTime() - lastActivated) > reporterDelay) reporter = false;
		}
	}
}
