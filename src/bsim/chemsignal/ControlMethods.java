package bsim.chemsignal;
import bsim.BSim;

/**
 * The Class ControlMethods contains methods that assist in the implementation of PID control
 * on a chemical field
 */ 


public class ControlMethods {
	
	double[] errorlog = new double[2];
	double valuetoreturn_pCtrl;
	double valuetoreturn_piCtrl;
	double valuetoreturn_Dctrl;
	double k_gain;
	double epsilon;
	double integral_time;
	double derivative_time;
	BSimChemFieldExt BSimChemField;
	double delayonset;
	double signaloff;
	double chemfluxinput;


/**
 * Constructor of the ControlMethods class. Create a ControlMethods object to allow PID control on chemical field
 * Supply relative PID parameters and on which chemical field to act.
 * @param my_k_gain Value for the proportional controller parameter (ie P from PID)
 * @param my_integral_time Value for the integral controller parameter (ie I from PID)
 * @param my_derivative_time Value for the derivative controller parameter (ie D from PID)
 * @param my_epsilon Error value where PID control stops (ie difference between reference signal and actual output)
 * @param my_BSimChemField Chemical field the controller will affect (has to be of class BSimChemFieldExt)
 * @param my_delayonset Controller will start after this amount of seconds from simulation start
 * @param my_signaloff Controller will switch off after this amount of seconds from simulation start
 * 
 */
	public ControlMethods(double my_k_gain, double my_epsilon,double my_integral_time, double my_derivative_time, BSimChemFieldExt my_BSimChemField, double my_delayonset, double my_signaloff){
		
		k_gain=my_k_gain;
		epsilon=my_epsilon;
		integral_time=my_integral_time;
		derivative_time=my_derivative_time;
		BSimChemField = my_BSimChemField;
		delayonset = my_delayonset;
		signaloff= my_signaloff;
		errorlog[0]=0;
		errorlog[1]=0;
	}
	

/**
 * Keeps track of the error between reference signal and actual output of the last two timesteps
 */ 
   public void updaterrorlog(double avgOfGRNVariable, double referenceValue){
	   double instantaneouserror = avgOfGRNVariable - referenceValue;
	   errorlog[0] = errorlog[1];
	   errorlog[1] = instantaneouserror;
	   
   }
	
/**
 * Method that distributes chemical evenly in chamber. This is only valid if the chemical has high diffusive rate
 * Called by methods PCtrl, PICtrl, PIDCtrl 
 * @param sim BSim simulation object
 * @param chemicalinput Amount of chemical to distribute evenly in simulation region 
 */
	public void distribchem(BSim sim, double chemicalinput){
		int xboxes= BSimChemField.getBoxes()[0];
	    int yboxes= BSimChemField.getBoxes()[1];
		int zboxes= BSimChemField.getBoxes()[2];
		double qtyTOTAL = chemicalinput*1e15*sim.getDt() ; //do i need to multiply with the timestep here? yes if it will work in an ode fashion.
		double qtyperbox = qtyTOTAL/(xboxes*yboxes*zboxes);
		for(int i=0;i<xboxes;i++){
			 for(int j=0;j<yboxes ;j++){
				 for(int k=0;k<zboxes;k++){
					 BSimChemField.addQuantity(i, j, k, qtyperbox);
				 }
			 }
		 }
	}

/**
 * Computes the amount of input to the system based on Proportional Control
 * If the absolute error is less than the error set in the ControlMethods object then this returns as zero
 * Used by the PCtrl PICtrl and PIDCtrl methods
 * @param avgOfGRNVariable Value of actual output at the time the controller will act
 * @param Value of reference signal at the time the controller will act
 */ 
public double PCtrlRtnVal(double avgOfGRNVariable, double referenceValue){
		
		double instantaneouserror = avgOfGRNVariable - referenceValue;
		double errorabsval = Math.abs(instantaneouserror);
		if(errorabsval>epsilon){
			valuetoreturn_pCtrl = - k_gain*instantaneouserror;
		}
		else{
			valuetoreturn_pCtrl=0;
		}
			
		return valuetoreturn_pCtrl;
	}	
	
	
/**
 * Computes the amount of input to the system based on Integral Control
 * Used by the PIDCtrl and PICtrl methods
 * @param BSim BSim object that the controller is in
 * @param avgOfGRNVariable Value of actual output at the time the controller will act
 * @param Value of reference signal at the time the controller will act
 */ 

 public double ICtrlRtnVal(BSim sim, double avgOfGRNVariable, double referenceValue){
	 	 
	 	  double instantaneouserror = avgOfGRNVariable - referenceValue;
		  double timestep = sim.getDt();
		  double timepoint=sim.getTime();
		  valuetoreturn_piCtrl = -(k_gain/integral_time)*(instantaneouserror)*timepoint; //this is the integral of -(k_gain/integral_time)*instantaneouserror
		  return valuetoreturn_piCtrl;
		  
	  }

/**
 * Computes the amount of input to the system based on Derivative Control
 * Used by the PIDCtrl method
 * @param BSim Object that the controller is in
 * @param avgOfGRNVariable Value of actual output at the time the controller will act
 * @param Value of reference signal at the time the controller will act
 */ 
								
public double DCtrlRtnVal(BSim sim, double avgOfGRNVariable, double referenceValue){
		this.updaterrorlog(avgOfGRNVariable, referenceValue);
		double timestep = sim.getDt();
		double errordifference = errorlog[1] - errorlog[0];
		double errorderivative = errordifference/timestep;
		valuetoreturn_Dctrl = -(k_gain*derivative_time)*errorderivative; 
		return valuetoreturn_Dctrl;
}

/**
 * Method that acts as a PID controller on a specified chemical field
 * Calls methods PCtrlRtnVal, ICtrlRtnVal, DCtrlRtnVal to work
 * @param BSim Declare BSim object that controller is in
 * @param AHLfield Declare on which chemical field of class BSimChemFieldExt to act upon
 * @param signalref Value of the reference signal at the given timepoint
 * @param avgLI Value of the actual output at the given timepoint
 */ 
public void PIDCtrl(BSim sim,BSimChemFieldExt AHLfield, double signalref, double avgLI){
	 double timepoint = sim.getTime();
	  
	  
	  if (timepoint > delayonset && timepoint < signaloff){
		// double signalref =  refsignal.getrefsignal();
		 //double avgLI=averageLI(bacteria);
		 double chemfluxinputfromDctrl = DCtrlRtnVal(sim,avgLI,signalref);
		 double chemfluxinputfromPctrl= PCtrlRtnVal(avgLI,signalref);
		 double chemfluxinputfromPIctrl= ICtrlRtnVal(sim, avgLI,signalref);
		 chemfluxinput = chemfluxinputfromPctrl + chemfluxinputfromPIctrl + chemfluxinputfromDctrl;
		 distribchem(sim, chemfluxinput); 
	  	}
	  
	  if (timepoint >= signaloff){
		 distribchem(sim,0);
	  }
	 
 }

/**
 * Method that acts as a PI controller on a specified chemical field
 * Calls methods PCtrlRtnVal, ICtrlRtnVal to work
 * @param BSim Declare BSim object that controller is in
 * @param AHLfield Declare on which chemical field of class BSimChemFieldExt to act upon
 * @param signalref Value of the reference signal at the given timepoint
 * @param avgLI Value of the actual output at the given timepoint
 */ 
 
 public void PICtrl(BSim sim,BSimChemFieldExt AHLfield, double signalref, double avgLI){
  double timepoint = sim.getTime();
	  
	  
	  if (timepoint > delayonset && timepoint < signaloff){
		// double signalref =  refsignal.getrefsignal();
		 //double avgLI=averageLI(bacteria);
		 double chemfluxinputfromPctrl= PCtrlRtnVal(avgLI,signalref);
		 double chemfluxinputfromPIctrl= ICtrlRtnVal(sim, avgLI,signalref);
		 chemfluxinput = chemfluxinputfromPctrl + chemfluxinputfromPIctrl;
		 distribchem(sim, chemfluxinput); 
	  	}
	  
	  if (timepoint >= signaloff){
		 distribchem(sim,0);
	  }
	  
	  }


/**
 * Method that acts as a P controller on a specified chemical field
 * Calls method PCtrlRtnVal to work
 * @param BSim Declare BSim object that controller is in
 * @param AHLfield Declare on which chemical field of class BSimChemFieldExt to act upon
 * @param signalref Value of the reference signal at the given timepoint
 * @param avgLI Value of the actual output at the given timepoint
 */ 
 
  public void PCtrl(BSim sim, BSimChemFieldExt AHLfield, double signalref, double avgLI){
	  double timepoint = sim.getTime();
		  
		  
		  if (timepoint > delayonset && timepoint < signaloff){
			 //double signalref =  refsignal.getrefsignal();
			 chemfluxinput= PCtrlRtnVal(avgLI,signalref);
			 distribchem(sim, chemfluxinput); 
		  	}
		  
		  if (timepoint >= signaloff){
			 distribchem(sim,0);
		  }
		  
		  }
  
  /**
   * Returns the value computed by the I controller
   */
  public double getIctrlval(){
	  return valuetoreturn_piCtrl;
  }
	
   /**
   * Returns the value computed by the P controller
   */  

  public double getPctrlval(){
	  return valuetoreturn_pCtrl;
  }

 /**
   * Returns the value computed by the D controller
   */  

  public double getDctrlval(){
	  return valuetoreturn_Dctrl;
  }
  
  /**
   * Returns the cumulative values of the chosen control method
   * For example if PID control is used it is the sum of the P the I and the D controller
   */ 
 public double getchemfluxinput(){
	 return chemfluxinput;
	 
 }
}
