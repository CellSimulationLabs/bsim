package BSimLogic;

import javax.vecmath.Vector3d;

import bsim.*;

/**
 * OR bacterium logic gate 
 */
class BSimORBacterium extends BSimLogicBacterium {
	public BSimORBacterium(BSim sim, Vector3d position, BSimChemicalField chemIn1, double threshold1, 
								  BSimChemicalField chemIn2, double threshold2, 
								  BSimChemicalField chemOut, double productionRate, double productionDelay,
								  double reporterDelay) {
		super(sim, position, chemIn1, threshold1, chemIn2, threshold2, chemOut, productionRate, 
				productionDelay, reporterDelay);
	}
	@Override
	public void action() {				
		if(chemIn1.getConc(position) > threshold1 || chemIn2.getConc(position) > threshold2) {
			activated = true;
			lastActivated = sim.getTime();
			if(lastInActivated == -1 || (sim.getTime() - lastInActivated) > productionDelay) {
				reporter = true;				
				chemOut.addQuantity(position, productionRate*sim.getDt());
			}
		}
		else {
			activated = false;
			lastInActivated = sim.getTime();
			if(lastActivated == -1 || (sim.getTime() - lastActivated) > reporterDelay) reporter = false;
		}
	}
}
