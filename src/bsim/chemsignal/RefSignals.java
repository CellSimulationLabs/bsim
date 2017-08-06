package bsim.chemsignal;

import bsim.*;

/**
 * The Class RefSignals contains methods that assist in the implementation of
 * chemical signals for PID control.
 */ 
 
public class RefSignals {

	double ref_val;
	
	/**
	 * Creates a steady signal at given value
	 * @param value_of_ref_point Value of steady constant offset signal
	 */ 
	public double refSignalConst(double value_of_ref_point){
		return ref_val=value_of_ref_point;
		
	}
	
	/**
	 * Creates a sinusoidal signal
	 * @param sim BSim object that the signal will be created in
	 * @param period Value of the period of the sinusoidal signal
	 * @param amplitude Value of the amplitude of the sinusoidal signal
	 * @param disp Value of the constant offset of the sinusoidal signal
	 */ 
	public double refSignalSin(BSim sim, double period, double amplitude, double disp){
		double timepoint=sim.getTime();
		
		
		ref_val = (amplitude*Math.sin(timepoint*2*Math.PI/period)) + disp ;
		return ref_val;
		
	}
	
	/**
	 * Creates a trapezoidal, ramp-like, signal
	 * @param sim BSim object that the signal will be created in
	 * @param ymax Value of the signal plateau (ie max value of signal)
	 * @param ymin Value of the signal at start (ie min value of signal)
	 * @param tstart Time in seconds that the signal will start ascend towards ymax
	 * @param t1 Time in seconds for the signal to reach ymax
	 * @param tend Time in seconds that the signal will start descend towards ymin
	 * @param t2 Time in seconds for the signal to reach ymin
	 */ 
	public double refSignalRamp(BSim sim, double ymax, double ymin, double tstart, double t1, double t2, double tend){
		
		double timepoint=sim.getTime();
		double m1=(ymax-ymin)/(t1-tstart);
		double m2=(ymin-ymax)/(tend-t2);
		double m3=0;
		double interceptforuppart=ymin - m1*tstart;
		double interceptfordownpart=ymax - m2*t2;
		
		
		if(timepoint>=tstart && timepoint<t1){
			ref_val= interceptforuppart+timepoint*m1;
			  }
		else if(timepoint>=t1 && timepoint<t2){
			ref_val= ymax+m3*timepoint;
		}
	    else if (timepoint>=t2 && timepoint<tend){
	    	ref_val= interceptfordownpart+timepoint*m2;
	    }
		else{  
			ref_val=0;
		}
		
		
		return ref_val;
		
		
	}
	
	/**
	 * Returns value of the reference signal at given time point
	 */ 
	public double getrefsignal(){
		return ref_val;
	}
	
	
	
	
}
