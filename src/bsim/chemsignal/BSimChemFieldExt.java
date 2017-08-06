package bsim.chemsignal;

import bsim.*;

public class BSimChemFieldExt extends BSimChemicalField {

	/**
	 * Inherited constructor from BSimChemicalField class
	 * @param sim
	 * @param boxes
	 * @param diffusivity
	 * @param decayRate
	 */
	public BSimChemFieldExt(BSim sim, int[] boxes, double diffusivity,
			double decayRate) {
		super(sim, boxes, diffusivity, decayRate);
		// TODO Auto-generated constructor stub
	}
	
	
/**
 * The method is to allow the addition of an external sin signal to modulate the external chemical field, including also a waiting period before the signal starts.
 * The amount added is adjusted as a per second rate according to the rate set by sim.getDt().
 * Then the function is called in the BSimTicker() method at every tick (i.e. at every timestep sim.getDt()).
 * @param period Add the period of the sinusoidal signal in seconds.
 * @param amplitude Add the amplitude of the sinusoidal signal.
 * @param disp Add the displacement from the origin for the sinusoidal signal.
 * @param signalonsetdelay Add the amount of seconds of waiting period before the signal is initiated.
 */
public void extModSignal( double period, double amplitude, double disp, double signalonsetdelay){ //add to extended chemical field class
	 
	 double timepoint = sim.getTime();
	 double timestep = sim.getDt();
	 if (timepoint > signalonsetdelay){
		 
		double qtyTOTAL = (amplitude*Math.sin(timepoint*2*Math.PI/period) + disp)*1e15*timestep ;
	 	double qtyperbox = qtyTOTAL/(this.getBoxes()[0]*this.getBoxes()[1]*this.getBoxes()[2]);
	 	for(int i=0;i<this.boxes[0];i++){
		 	for(int j=0;j<this.boxes[1] ;j++){
			 	for(int k=0;k<this.boxes[2];k++){
				 	this.addQuantity(i, j, k, qtyperbox);
			 	}
		 	}
	 	}
	 }
}

/**
 * Add a constant amount to the external chamber. The amount is adjusted as a per second rate according to the rate set by sim.getDt().
 * Then the function is called in the BSimTicker() method at every tick (i.e. at every timestep sim.getDt()).
 * @param amount The amount to add to the external chamber
 */
public void extConstantAdd(double amount){
	
	double timestep = sim.getDt();
	double qtytoadd = amount * 1e15*timestep;
	double qtyperbox = qtytoadd/(this.boxes[0]*this.boxes[1]*this.boxes[2]);
 	for(int i=0;i<this.boxes[0];i++){
	 	for(int j=0;j<this.boxes[1] ;j++){
		 	for(int k=0;k<this.boxes[2];k++){
			 	this.addQuantity(i, j, k, qtyperbox);
		 	}
	 	}
 	}
}


}
