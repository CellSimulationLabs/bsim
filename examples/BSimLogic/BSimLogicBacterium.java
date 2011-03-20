package BSimLogic;

import javax.vecmath.Vector3d;

import bsim.*;
import bsim.particle.*;

/** 
 * General logic gate bacterium - holds up to two inputs and a single output with thresholds
 */
class BSimLogicBacterium extends BSimBacterium {
	// Chemical fields to act as inputs and outputs
	protected BSimChemicalField chemIn1;
	protected BSimChemicalField chemIn2;
	protected BSimChemicalField chemOut;

	// Flags for activation and delay before production occurs
	public boolean activated = false;
	public boolean reporter = false;
	protected double lastInActivated = 0.0;
	protected double lastActivated = -1;

	// Threshold for activation and production rate
	protected double threshold1 = 0.0;
	protected double threshold2 = 0.0;
	protected double productionRate = 0.0;
	protected double productionDelay = 0.0;
	protected double reporterDelay = 0.0;

	public BSimLogicBacterium(BSim sim, Vector3d position, BSimChemicalField chemIn1, double threshold1, 
	                          BSimChemicalField chemIn2, double threshold2, 
	                          BSimChemicalField chemOut, double productionRate, double productionDelay,
	                          double reporterDelay) {
		super(sim, position);
		this.chemIn1 = chemIn1;
		this.chemIn2 = chemIn2;
		this.chemOut = chemOut;
		this.threshold1 = threshold1;
		this.threshold2 = threshold2;
		this.productionRate = productionRate;
		this.productionDelay = productionDelay;
		this.reporterDelay = reporterDelay;
	}
}
