/**
 * BSimBacteriaCreate.java
 *
 * Class to hold static methods to generate inital bacteria starting conditions.
 *
 * Authors: Thomas Gorochowski
 * 			Mattia Fazzini(Update)
 * Created: 20/07/2008
 * Updated: 12/08/2009
 */
package bsim.drawable.bacteria;

import java.util.Random;
import java.util.Vector;

import bsim.BSimParameters;
import bsim.BSimScene;
import bsim.BSimUtils;
import bsim.physics.BSimParticle;


public class BSimBacteriaCreate {
	
	//Confidence at 90%
	private static double MEAN_L0=2.5;
	private static double SD_L0=0.35;
	private static double CONFIDENCE_L0_MIN=2.456;
	private static double CONFIDENCE_L0_MAX=2.544;
	//Confidence at 90%
	private static double MEAN_LTC=3.23;
	private static double SD_LTC=0.45;
	private static double CONFIDENCE_LTC_MIN=3.1722;
	private static double CONFIDENCE_LTC_MAX=3.2878;
	//Confidence at 90%
	private static double MEAN_LTG=5.08;
	private static double SD_LTG=0.85;
	private static double CONFIDENCE_LTG_MIN=4.9732;
	private static double CONFIDENCE_LTG_MAX=5.1868;
	//Confidence at 1%
	private static double MEAN_R=0.481;
	private static double SD_R=0.024;
	private static double CONFIDENCE_R_MIN=0.4192;
	private static double CONFIDENCE_R_MAX=0.5428;
	//Confidence at 1%
	private static double MEAN_A2=0.854;
	private static double SD_A2=0.077;
	private static double CONFIDENCE_A2_MIN=0.6557;
	private static double CONFIDENCE_A2_MAX=1.0523;
	//Confidence at 90%
	//tg is in minute
	private static double MEAN_TG=25.08;
	private static double SD_TG=7.97;
	private static double CONFIDENCE_TG_MIN=24.0785;
	private static double CONFIDENCE_TG_MAX=26.0815;
	//Confidence at 90%
	private static double MEAN_AC=0.428;
	private static double SD_AC=0.107;
	private static double CONFIDENCE_AC_MIN=0.4146;
	private static double CONFIDENCE_AC_MAX=0.4414;
	//Confidence at 90%
	//a1 is in micrometer/minute
	private static double MEAN_a1=0.067;
	private static double SD_a1=0.027;
	private static double CONFIDENCE_a1_MIN=0.0636;
	private static double CONFIDENCE_a1_MAX=0.0704;
	//Confidence at 90%
	//a2 is in micrometer/minute
	private static double MEAN_a2=0.122;
	private static double SD_a2=0.035;
	private static double CONFIDENCE_a2_MIN=0.1176;
	private static double CONFIDENCE_a2_MAX=0.1264;
	//Confidence at 90%
	//a3 is in micrometer/minute
	private static double MEAN_a3=0.174;
	private static double SD_a3=0.049;
	private static double CONFIDENCE_a3_MIN=0.1678;
	private static double CONFIDENCE_a3_MAX=0.1802;
	
	/**
	 * Create a bacteria set with given parameters (uniform random distribution). No 
	 * checking of overlapping beads is performed meaning initial simulation output 
	 * should be investigated if strange movement occurs.
	 */
	public static Vector createBacteriaSet (double[] args, BSimScene scene, BSimParameters params) {
		
		// Variables for each bead property
		double newSpeed = params.getBactSpeed();
		double newMass = 0; // Not used
		double[] newPosition = new double[3];
		double[] newCentrePos = new double[3];
		double[] newDirection = new double[3];
		double newForceUp, newForceDown;
		double dx,dy,dz;
		double newTumbleSpeed;
		int newRemDt = 1;
		Vector bactVec = new Vector();
		Vector beadVec = scene.getBeads();
		int elongationType = 0;
		boolean invalidPlacement = false;
		boolean standardValues = true;
		boolean trueRValue = false;
		boolean trueA2Value = false;
		boolean trueL0Value = false;
		boolean trueLTCValue = false;
		boolean trueLTGValue = false;
		boolean trueACValue = false;
		boolean trueTGValue = false;
		boolean truea1Value = false;
		boolean truea2Value = false;
		boolean truea3Value = false;
		Random rR = new Random();
		Random rA2 = new Random();
		Random rL0 = new Random();
		Random rLTC = new Random();
		Random rLTG = new Random();
		Random rAC = new Random();
		Random rTG = new Random();
		Random ra1 = new Random();
		Random ra2 = new Random();
		Random ra3 = new Random();
		double RValue = 0;
		double A2Value = 0;
		double L0Value = 0;
		double LTCValue = 0;
		double LTGValue = 0;
		double ACValue = 0;
		double TCValue = 0;
		double TGValue = 0;
		double a1Value = 0;
		double a2Value = 0;
		double a3Value = 0;
		double T2Value = 0;
		
		if(args[8] == 0) {
			standardValues = true;
		}
		else{
			standardValues = false;
		}
		
		newSpeed = params.getBactSpeed();
		
		// Loop through the number of bacteria to create
		for(int i=0; i<args[6]; i++){
			invalidPlacement = false;
			trueRValue = false;
			trueA2Value = false;
			trueL0Value = false;
			trueLTCValue = false;
			trueACValue = false;
			trueLTGValue = false;
			truea1Value = false;
			truea2Value = false;
			truea3Value = false;
			
			
			if(standardValues){
				//determination of R
				while(!trueRValue){
					RValue=(SD_R*rR.nextGaussian())+MEAN_R;
					if( (RValue>CONFIDENCE_R_MIN) && (RValue<CONFIDENCE_R_MAX) ){
						trueRValue=true;
					}
				}
				
				//determination of A2
				while(!trueA2Value){
					A2Value=(SD_A2*rA2.nextGaussian())+MEAN_A2;
					if( (A2Value>CONFIDENCE_A2_MIN) && (A2Value<CONFIDENCE_A2_MAX) ){
						trueA2Value=true;
					}
				}
				if(A2Value>1){
					//Bilinear elongation
					elongationType=BSimBacterium.BILINEAR_ELONGATION;
									
					//determination of L0
					while(!trueL0Value){
						L0Value=(SD_L0*rL0.nextGaussian())+MEAN_L0;
						if( (L0Value>CONFIDENCE_L0_MIN) && (L0Value<CONFIDENCE_L0_MAX) ){
							trueL0Value=true;
						}
					}
					//determination of LTC
					while(!trueLTCValue){
						LTCValue=(SD_LTC*rLTC.nextGaussian())+MEAN_LTC;
						if( (LTCValue>CONFIDENCE_LTC_MIN) && (LTCValue<CONFIDENCE_LTC_MAX) ){
							trueLTCValue=true;
						}
					}
					//determination of LTG
					while(!trueLTGValue){
						LTGValue=(SD_LTG*rLTG.nextGaussian())+MEAN_LTG;
						if( (LTGValue>CONFIDENCE_LTG_MIN) && (LTGValue<CONFIDENCE_LTG_MAX) ){
							trueLTGValue=true;
						}
					}
					
					//determination of AC
					while(!trueACValue){
						ACValue=(SD_AC*rAC.nextGaussian())+MEAN_AC;
						if( (ACValue>CONFIDENCE_AC_MIN) && (ACValue<CONFIDENCE_AC_MAX) ){
							trueACValue=true;
						}
					}
					
					//determination of LTG
					while(!trueTGValue){
						TGValue=(SD_TG*rTG.nextGaussian())+MEAN_TG;
						if( (TGValue>CONFIDENCE_TG_MIN) && (TGValue<CONFIDENCE_TG_MAX) ){
							trueTGValue=true;
						}
					}
					
					TCValue=ACValue*TGValue;
				}
				else{
					//Trilinear elongation
					elongationType=BSimBacterium.TRILINEAR_ELONGATION;
					
					//determination of L0
					while(!trueL0Value){
						L0Value=(SD_L0*rL0.nextGaussian())+MEAN_L0;
						if( (L0Value>CONFIDENCE_L0_MIN) && (L0Value<CONFIDENCE_L0_MAX) ){
							trueL0Value=true;
						}
					}
							
					//determination of AC
					while(!trueACValue){
						ACValue=(SD_AC*rAC.nextGaussian())+MEAN_AC;
						if( (ACValue>CONFIDENCE_AC_MIN) && (ACValue<CONFIDENCE_AC_MAX) ){
							trueACValue=true;
						}
					}
					
					//determination of LTG
					while(!trueTGValue){
						TGValue=(SD_TG*rTG.nextGaussian())+MEAN_TG;
						if( (TGValue>CONFIDENCE_TG_MIN) && (TGValue<CONFIDENCE_TG_MAX) ){
							trueTGValue=true;
						}
					}
					
					//determination of a1
					while(!truea1Value){
						a1Value=(SD_a1*ra1.nextGaussian())+MEAN_a1;
						if( (a1Value>CONFIDENCE_a1_MIN) && (a1Value<CONFIDENCE_a1_MAX) ){
							truea1Value=true;
						}
					}
					
					//determination of a2
					while(!truea2Value){
						a2Value=(SD_a2*ra2.nextGaussian())+MEAN_a2;
						if( (a2Value>CONFIDENCE_a2_MIN) && (a2Value<CONFIDENCE_a2_MAX) ){
							truea2Value=true;
						}
					}
					
					//determination of a3
					while(!truea3Value){
						a3Value=(SD_a3*ra3.nextGaussian())+MEAN_a3;
						if( (a3Value>CONFIDENCE_a3_MIN) && (a3Value<CONFIDENCE_a3_MAX) ){
							truea3Value=true;
						}
					}
					
					T2Value=A2Value*TGValue;
					TCValue=ACValue*TGValue;
				}
			}
			else{
				// Parameter that comes for the user parameter file
				elongationType=(int)args[8];
			}
			
			// Randomly select a new position for the bacterium
			newPosition[0] = args[0] + (args[3] * Math.random());
			newPosition[1] = args[1] + (args[4] * Math.random());
			newPosition[2] = args[2] + (args[5] * Math.random());
			
			dx = (2.0 * Math.random()) - 1.0;
			dy = (2.0 * Math.random()) - 1.0;
			dz = (2.0 * Math.random()) - 1.0;

			newDirection[0] = dx / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)+ Math.pow(dz,2.0)));
			newDirection[1] = dy / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)+ Math.pow(dz,2.0)));
			newDirection[2] = dz / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)+ Math.pow(dz,2.0)));

			newForceUp = params.getBactForceUp();
			newForceDown = params.getBactForceDown();

			newTumbleSpeed = 0.0;
			
			newRemDt = BSimUtils.roundToDtSteps(BSimUtils.expRandVar(0.83), params.getDtSecs());
			
			newCentrePos[0]= newPosition[0] + (L0Value/2);
			newCentrePos[1]= newPosition[1] + (L0Value/2);
			newCentrePos[2]= newPosition[2] + (L0Value/2);
			
			for(int j=0; j<beadVec.size(); j++) {
				if(BSimUtils.particlesIntersecting((BSimParticle)beadVec.elementAt(j), newCentrePos, (L0Value/2) )) {
					i = i-1;
					invalidPlacement = true;
				}
			}
			
			if(!invalidPlacement) {
				//Type of elongation
				if(standardValues){
					//the simulator choose the values of the parameters
					if(elongationType==BSimBacterium.TRILINEAR_ELONGATION){
						// Create the type of bacteria required
						switch((int)args[7]){
							// Standard bacteria
							case 1: bactVec.add(new BSimBacterium(newSpeed, newMass, L0Value, RValue, TCValue, T2Value, TGValue, a1Value, a2Value, a3Value, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params)); break;
							// Bead sensing bacteria
							case 2: bactVec.add(new BSimSensingBacterium(newSpeed, newMass, L0Value, RValue, TCValue, T2Value, TGValue, a1Value, a2Value, a3Value, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[9])); break;
							// Bead sensing and co-ordinating bacteria
							case 3: bactVec.add(new BSimCoordBacterium(newSpeed, newMass, L0Value, RValue, TCValue, T2Value, TGValue, a1Value, a2Value, a3Value, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[9], args[10])); break;
							// Bead sensing, co-ordinating and recruiting bacteria
							case 4: bactVec.add(new BSimRecruitBacterium(newSpeed, newMass, L0Value, RValue, TCValue, T2Value, TGValue, a1Value, a2Value, a3Value, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[9], args[10])); break;
							// Dead bacteria
							case 5: bactVec.add(new BSimDeadBacterium(newSpeed, newMass, L0Value, RValue, TCValue, T2Value, TGValue, a1Value, a2Value, a3Value, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params)); break;
						}
					}
					else{
						// Create the type of bacteria required
						switch((int)args[7]){
							// Standard bacteria
							case 1: bactVec.add(new BSimBacterium(newSpeed, newMass, L0Value, LTCValue, LTGValue, RValue, TCValue, TGValue, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params)); break;
							// Bead sensing bacteria
							case 2: bactVec.add(new BSimSensingBacterium(newSpeed, newMass, L0Value, LTCValue, LTGValue, RValue, TCValue, TGValue, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[9])); break;
							// Bead sensing and co-ordinating bacteria
							case 3: bactVec.add(new BSimCoordBacterium(newSpeed, newMass, L0Value, LTCValue, LTGValue, RValue, TCValue, TGValue, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[9], args[10])); break;
							// Bead sensing, co-ordinating and recruiting bacteria
							case 4: bactVec.add(new BSimRecruitBacterium(newSpeed, newMass, L0Value, LTCValue, LTGValue, RValue, TCValue, TGValue, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[9], args[10])); break;
							// Dead bacteria
							case 5: bactVec.add(new BSimDeadBacterium(newSpeed, newMass, L0Value, LTCValue, LTGValue, RValue, TCValue, TGValue, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params)); break;
						}
					}
				}
				else{
					//the user choose the values of the parameters
					if(elongationType==BSimBacterium.TRILINEAR_ELONGATION){
						// Create the type of bacteria required
						switch((int)args[7]){
							// Standard bacteria
							case 1: bactVec.add(new BSimBacterium(newSpeed, newMass, args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params)); break;
							// Bead sensing bacteria
							case 2: bactVec.add(new BSimSensingBacterium(newSpeed, newMass, args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[17])); break;
							// Bead sensing and co-ordinating bacteria
							case 3: bactVec.add(new BSimCoordBacterium(newSpeed, newMass, args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[17], args[18])); break;
							// Bead sensing, co-ordinating and recruiting bacteria
							case 4: bactVec.add(new BSimRecruitBacterium(newSpeed, newMass, args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[17], args[18])); break;
							// Dead bacteria
							case 5: bactVec.add(new BSimDeadBacterium(newSpeed, newMass, args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params)); break;
						}
					}
					else{
						// Create the type of bacteria required
						switch((int)args[7]){
							// Standard bacteria
							case 1: bactVec.add(new BSimBacterium(newSpeed, newMass, args[9], args[10], args[11], args[12], args[13], args[14], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params)); break;
							// Bead sensing bacteria
							case 2: bactVec.add(new BSimSensingBacterium(newSpeed, newMass, args[9], args[10], args[11], args[12], args[13], args[14], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[15])); break;
							// Bead sensing and co-ordinating bacteria
							case 3: bactVec.add(new BSimCoordBacterium(newSpeed, newMass, args[9], args[10], args[11], args[12], args[13], args[14], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[15], args[16])); break;
							// Bead sensing, co-ordinating and recruiting bacteria
							case 4: bactVec.add(new BSimRecruitBacterium(newSpeed, newMass, args[9], args[10], args[11], args[12], args[13], args[14], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[15], args[16])); break;
							// Dead bacteria
							case 5: bactVec.add(new BSimDeadBacterium(newSpeed, newMass, args[9], args[10], args[11], args[12], args[13], args[14], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params)); break;
						}
					}
				}
			}
		}

		// Return the new bacteria set
		return bactVec;
	}
	
	
	/**
	 * Create a single bacterium with given parameters.
	 */
	public static BSimBacterium createBacterium (double[] args, BSimScene scene, BSimParameters params) {
		
		// Variables for each bacterium property
		double newSpeed = params.getBactSpeed();
		double newMass = 0; // Not used
		double[] newPosition = new double[3];
		newPosition[0] = args[0];
		newPosition[1] = args[1];
		newPosition[2] = args[2];		
		double[] newDirection = {0,0,0};
		double newForceUp, newForceDown;		
		double dx,dy,dz;
		double newTumbleSpeed;
		int newRemDt;
		BSimBacterium newBact = null;
		int elongationType = 0;
		boolean standardValues = true;
		boolean trueRValue = false;
		boolean trueA2Value = false;
		boolean trueL0Value = false;
		boolean trueLTCValue = false;
		boolean trueLTGValue = false;
		boolean trueACValue = false;
		boolean trueTGValue = false;
		boolean truea1Value = false;
		boolean truea2Value = false;
		boolean truea3Value = false;
		Random rR = new Random();
		Random rA2 = new Random();
		Random rL0 = new Random();
		Random rLTC = new Random();
		Random rLTG = new Random();
		Random rAC = new Random();
		Random rTG = new Random();
		Random ra1 = new Random();
		Random ra2 = new Random();
		Random ra3 = new Random();
		double RValue = 0;
		double A2Value = 0;
		double L0Value = 0;
		double LTCValue = 0;
		double LTGValue = 0;
		double ACValue = 0;
		double TCValue = 0;
		double TGValue = 0;
		double a1Value = 0;
		double a2Value = 0;
		double a3Value = 0;
		double T2Value = 0;
		
		if(args[4] == 0) {
			standardValues = true;
		}
		else{
			standardValues = false;
		}
		
			if(standardValues){
				//determination of R
				while(!trueRValue){
					RValue=(SD_R*rR.nextGaussian())+MEAN_R;
					if( (RValue>CONFIDENCE_R_MIN) && (RValue<CONFIDENCE_R_MAX) ){
						trueRValue=true;
					}
				}
				
				//determination of A2
				while(!trueA2Value){
					A2Value=(SD_A2*rA2.nextGaussian())+MEAN_A2;
					if( (A2Value>CONFIDENCE_A2_MIN) && (A2Value<CONFIDENCE_A2_MAX) ){
						trueA2Value=true;
					}
				}
				if(A2Value>1){
					//Bilinear elongation
					elongationType=BSimBacterium.BILINEAR_ELONGATION;
									
					//determination of L0
					while(!trueL0Value){
						L0Value=(SD_L0*rL0.nextGaussian())+MEAN_L0;
						if( (L0Value>CONFIDENCE_L0_MIN) && (L0Value<CONFIDENCE_L0_MAX) ){
							trueL0Value=true;
						}
					}
					//determination of LTC
					while(!trueLTCValue){
						LTCValue=(SD_LTC*rLTC.nextGaussian())+MEAN_LTC;
						if( (LTCValue>CONFIDENCE_LTC_MIN) && (LTCValue<CONFIDENCE_LTC_MAX) ){
							trueLTCValue=true;
						}
					}
					//determination of LTG
					while(!trueLTGValue){
						LTGValue=(SD_LTG*rLTG.nextGaussian())+MEAN_LTG;
						if( (LTGValue>CONFIDENCE_LTG_MIN) && (LTGValue<CONFIDENCE_LTG_MAX) ){
							trueLTGValue=true;
						}
					}
					
					//determination of AC
					while(!trueACValue){
						ACValue=(SD_AC*rAC.nextGaussian())+MEAN_AC;
						if( (ACValue>CONFIDENCE_AC_MIN) && (ACValue<CONFIDENCE_AC_MAX) ){
							trueACValue=true;
						}
					}
					
					//determination of LTG
					while(!trueTGValue){
						TGValue=(SD_TG*rTG.nextGaussian())+MEAN_TG;
						if( (TGValue>CONFIDENCE_TG_MIN) && (TGValue<CONFIDENCE_TG_MAX) ){
							trueTGValue=true;
						}
					}
					
					TCValue=ACValue*TGValue;
				}
				else{
					//Trilinear elongation
					elongationType=BSimBacterium.TRILINEAR_ELONGATION;
					
					//determination of L0
					while(!trueL0Value){
						L0Value=(SD_L0*rL0.nextGaussian())+MEAN_L0;
						if( (L0Value>CONFIDENCE_L0_MIN) && (L0Value<CONFIDENCE_L0_MAX) ){
							trueL0Value=true;
						}
					}
							
					//determination of AC
					while(!trueACValue){
						ACValue=(SD_AC*rAC.nextGaussian())+MEAN_AC;
						if( (ACValue>CONFIDENCE_AC_MIN) && (ACValue<CONFIDENCE_AC_MAX) ){
							trueACValue=true;
						}
					}
					
					//determination of LTG
					while(!trueTGValue){
						TGValue=(SD_TG*rTG.nextGaussian())+MEAN_TG;
						if( (TGValue>CONFIDENCE_TG_MIN) && (TGValue<CONFIDENCE_TG_MAX) ){
							trueTGValue=true;
						}
					}
					
					//determination of a1
					while(!truea1Value){
						a1Value=(SD_a1*ra1.nextGaussian())+MEAN_a1;
						if( (a1Value>CONFIDENCE_a1_MIN) && (a1Value<CONFIDENCE_a1_MAX) ){
							truea1Value=true;
						}
					}
					
					//determination of a2
					while(!truea2Value){
						a2Value=(SD_a2*ra2.nextGaussian())+MEAN_a2;
						if( (a2Value>CONFIDENCE_a2_MIN) && (a2Value<CONFIDENCE_a2_MAX) ){
							truea2Value=true;
						}
					}
					
					//determination of a3
					while(!truea3Value){
						a3Value=(SD_a3*ra3.nextGaussian())+MEAN_a3;
						if( (a3Value>CONFIDENCE_a3_MIN) && (a3Value<CONFIDENCE_a3_MAX) ){
							truea3Value=true;
						}
					}
					
					T2Value=A2Value*TGValue;
					TCValue=ACValue*TGValue;
				}
			}
			else{
				// Parameter that comes for the user parameter file
				elongationType=(int)args[4];
			}
			
			dx = (2.0 * Math.random()) - 1.0;
			dy = (2.0 * Math.random()) - 1.0;
			dz = (2.0 * Math.random()) - 1.0;

			newDirection[0] = dx / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)+ Math.pow(dz,2.0)));
			newDirection[1] = dy / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)+ Math.pow(dz,2.0)));
			newDirection[2] = dz / (Math.sqrt(Math.pow(dx,2.0) + Math.pow(dy,2.0)+ Math.pow(dz,2.0)));

			newForceUp = params.getBactForceUp();
			newForceDown = params.getBactForceDown();

			newTumbleSpeed = 0.0;
			
			newRemDt = BSimUtils.roundToDtSteps(BSimUtils.expRandVar(0.83), params.getDtSecs());
			
				//Type of elongation
				if(standardValues){
					//the simulator choose the values of the parameters
					if(elongationType==BSimBacterium.TRILINEAR_ELONGATION){
						// Create the type of bacteria required
						switch((int)args[3]){
							// Standard bacteria
							case 1: newBact = new BSimBacterium(newSpeed, newMass, L0Value, RValue, TCValue, T2Value, TGValue, a1Value, a2Value, a3Value, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params); break;
							// Bead sensing bacteria
							case 2: newBact = new BSimSensingBacterium(newSpeed, newMass, L0Value, RValue, TCValue, T2Value, TGValue, a1Value, a2Value, a3Value, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[5]); break;
							// Bead sensing and co-ordinating bacteria
							case 3: newBact = new BSimCoordBacterium(newSpeed, newMass, L0Value, RValue, TCValue, T2Value, TGValue, a1Value, a2Value, a3Value, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[5], args[6]); break;
							// Bead sensing, co-ordinating and recruiting bacteria
							case 4: newBact = new BSimRecruitBacterium(newSpeed, newMass, L0Value, RValue, TCValue, T2Value, TGValue, a1Value, a2Value, a3Value, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[5], args[6]); break;
							// Dead bacteria
							case 5: newBact = new BSimDeadBacterium(newSpeed, newMass, L0Value, RValue, TCValue, T2Value, TGValue, a1Value, a2Value, a3Value, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params); break;
						}
					}
					else{
						// Create the type of bacteria required
						switch((int)args[3]){
							// Standard bacteria
							case 1: newBact = new BSimBacterium(newSpeed, newMass, L0Value, LTCValue, LTGValue, RValue, TCValue, TGValue, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params); break;
							// Bead sensing bacteria
							case 2: newBact = new BSimSensingBacterium(newSpeed, newMass, L0Value, LTCValue, LTGValue, RValue, TCValue, TGValue, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[5]); break;
							// Bead sensing and co-ordinating bacteria
							case 3: newBact = new BSimCoordBacterium(newSpeed, newMass, L0Value, LTCValue, LTGValue, RValue, TCValue, TGValue, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[5], args[6]); break;
							// Bead sensing, co-ordinating and recruiting bacteria
							case 4: newBact = new BSimRecruitBacterium(newSpeed, newMass, L0Value, LTCValue, LTGValue, RValue, TCValue, TGValue, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[5], args[6]); break;
							// Dead bacteria
							case 5: newBact = new BSimDeadBacterium(newSpeed, newMass, L0Value, LTCValue, LTGValue, RValue, TCValue, TGValue, elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params); break;
						}
					}
				}
				else{
					//the user choose the values of the parameters
					if(elongationType==BSimBacterium.TRILINEAR_ELONGATION){
						// Create the type of bacteria required
						switch((int)args[3]){
							// Standard bacteria
							case 1: newBact = new BSimBacterium(newSpeed, newMass, args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params); break;
							// Bead sensing bacteria
							case 2: newBact = new BSimSensingBacterium(newSpeed, newMass, args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[13]); break;
							// Bead sensing and co-ordinating bacteria
							case 3: newBact = new BSimCoordBacterium(newSpeed, newMass, args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[13], args[14]); break;
							// Bead sensing, co-ordinating and recruiting bacteria
							case 4: newBact = new BSimRecruitBacterium(newSpeed, newMass, args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[13], args[14]); break;
							// Dead bacteria
							case 5: newBact = new BSimDeadBacterium(newSpeed, newMass, args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params); break;
						}
					}
					else{
						// Create the type of bacteria required
						switch((int)args[3]){
							// Standard bacteria
							case 1: newBact = new BSimBacterium(newSpeed, newMass, args[5], args[6], args[7], args[8], args[9], args[10], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params); break;
							// Bead sensing bacteria
							case 2: newBact = new BSimSensingBacterium(newSpeed, newMass, args[5], args[6], args[7], args[8], args[9], args[10], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[11]); break;
							// Bead sensing and co-ordinating bacteria
							case 3: newBact = new BSimCoordBacterium(newSpeed, newMass, args[5], args[6], args[7], args[8], args[9], args[10], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[11], args[12]); break;
							// Bead sensing, co-ordinating and recruiting bacteria
							case 4: newBact = new BSimRecruitBacterium(newSpeed, newMass, args[5], args[6], args[7], args[8], args[9], args[10], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params, args[11], args[12]); break;
							// Dead bacteria
							case 5: newBact = new BSimDeadBacterium(newSpeed, newMass, args[5], args[6], args[7], args[8], args[9], args[10], elongationType, newDirection, newPosition, newForceDown, newForceUp, BSimBacterium.BAC_STATE_RUNNING, newTumbleSpeed, newRemDt, scene, params); break;
						}
					}
				}
		return newBact;
	}
}
