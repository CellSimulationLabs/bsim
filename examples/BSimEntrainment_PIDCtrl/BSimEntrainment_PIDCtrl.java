package BSimEntrainment_PIDCtrl;

import java.io.File;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.BSimTicker;
import bsim.BSimUtils;
import bsim.export.BSimLogger;
import bsim.ode.BSimOdeSolver;
import bsim.ode.BSimOdeSystem;
import bsim.particle.BSimBacterium;
import bsim.chemsignal.BSimChemFieldExt;

public class BSimEntrainment_PIDCtrl {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
			
		/*********************************************************
		 * Basic setup if we have command line arguments
		 */
		
		final double periodOfSignal;
		final double alphaConstant;
		final double amplitudeConstant;
		final double simtime;
		final double dttime;
		final double kgain;
		final double integraltime;
		final double signaldelayonset;
		final double signalstop;
		final double derivativetime;
		final double leakrate;
		 
		final String rndparamstest="true";
		final String P_action="P";
		final String PI_action="PI";
		final String PID_action="PID";
		final boolean rndParameters;
		final boolean p_control;
		final boolean i_control;
		final boolean d_control;
		
			if(args.length != 0){
			/*
			 * What do we want to set from args?
			 * Set the entrainment values, (Tf,a,A) of the chemical field Aext(t)=a + Asin(2*pi*t/Tf)
			 * Then also use these to produce a hierarchical folder structure HastysOscillatorEnzymatic/Tf/a/A/
			 * 
			 */
				//alphaValuesinTxt=(int) Double.parseDouble(args[0]);/*the first value of the text file should declare the number of alphas that precede the amplitudes */
				//amplitudeStart = alphaValuesinTxt + 2;
				periodOfSignal= Double.parseDouble(args[0]); //will be supplied from bash script.
				alphaConstant= Double.parseDouble(args[1]); //will be supplied from bash script.
				amplitudeConstant= Double.parseDouble(args[2]); //will be supplied from bash script.
				simtime=Double.parseDouble(args[3]);
				dttime=Double.parseDouble(args[4]);
				kgain=Double.parseDouble(args[5]);
				integraltime=Double.parseDouble(args[6]);
				derivativetime=Double.parseDouble(args[7]);
				signaldelayonset=Double.parseDouble(args[8]);
				signalstop=Double.parseDouble(args[9]);
				
				if(args[10].equals(rndparamstest)){
					rndParameters=true;
				}
				else{
					rndParameters=false;
				}
				
				if(args[11].equals(P_action)){
					p_control=true;
					i_control=false;
					d_control=false;
				}
				else if(args[11].equals(PI_action)){
					p_control=true;
					i_control=true;
					d_control=false;
				
				}
				else{
					p_control=true;
					i_control=true;
					d_control=true;
				
				}
				
				leakrate=Double.parseDouble(args[12]);
			} 
			else {
				
				periodOfSignal= 15000; //this cannot be zero because its a denominator and if zero NaN is created in data. A zero input is guaranteed by the amplitudeconstant and alphaconstant being set to zero.
				alphaConstant= 3.0;
				amplitudeConstant= 1;
				simtime=1.5e6;
				dttime=5e-2;
				kgain=500;
				derivativetime=0.1;
				integraltime=9e4;
				signaldelayonset=5e4;
				signalstop=1.4e6;				
				rndParameters=true;
				p_control=true;
				i_control=false;
				d_control=false;
				leakrate=1.0;
			}
			
		
			
			
		
			
			
			/*********************************************************
			 * Create a new directory for the simulation results
			 *********************************************************/
			String filePath = new String();
			
			filePath = "../HastysOscillatorEnzymatic/control/closedloop/Pctrl/60cells/6pct/" + Double.toString(kgain)  +"/"+ Double.toString(integraltime) +"/"+ Double.toString(derivativetime) +"/"+ Double.toString(simtime) + "/" + Double.toString(dttime) + "/" ;
			//filePath = "../HastysOscillatorEnzymatic/control/closedloop/PIctrl/60cells/"  + Double.toString(periodOfSignal) +"/" + Double.toString(alphaConstant) +"/"+ Double.toString(amplitudeConstant) +"/"+ Double.toString(simtime) + "/" + Double.toString(dttime) + "/" ;
			
			// Check that path is valid and that it exists, if it doesn't then create it
			File testPath = new File(filePath);
			if(!testPath.exists()){
				// Create directories as required
				testPath.mkdirs();
			}
			
			
			/**
			 * Setup the simulation world. The world will have solid elastic boundaries
			 *  and all other default properties as defined in BSim class. Timesteps
			 *  and simulation durations should be given in seconds.Spatial dimensions
			 *  should be given in microns.
			 *  */
			 final BSim sim = new BSim();
			 double BsimXdimension = 200.0; //Micrometer dimensions of Hasty's microfluidic chambers as given in Danino et al. (2010).
			 double BsimYdimension = 50.0;
			 double BSimZdimension = 1.0;
			 sim.setBound(BsimXdimension, BsimYdimension, BSimZdimension); //setting world dimensions in um.
			 sim.setSolid(false,false,false);
			 sim.setLeaky(true, false, false, false, false, false);
			 sim.setLeakyRate(leakrate, 0.0, 0.0 , 0.0, 0.0, 0.0);
			 sim.setDt(dttime); //setting simulation timestep in seconds.
			 sim.setSimulationTime(simtime); //setting total simulation time in seconds.
			 sim.setTimeFormat("0.00"); //outputs time in this format.
			 
			 
		
			 final String ICS_CONDITIONS = "ICS_UNIFORM"; //type "ICS_RANDOM" or "ICS_UNIFORM" for RANDOM or UNIFORM initial conditions.
			 //final boolean rndParameters = true; //type true for random parameters. type false for fixed parameters//final boolean rndParameters = false; //type true for random parameters. type false for fixed parameters
			 
			 /**
			  * Setup an AHL chemical field behaviour once AHL is in environment 
			  * by diffusing from each cell. Note that chemical field decay and diffuse
			  * methods should be supplied in quantities and not concentrations.
			  * In this example since the AHL is supplied by the GRN of cells then quantity is umol. 
			  * */
			 
			 final double AHLdecayRate = 2.76e-3/60.0; //AHL decay rate in environment, 2.76e-3 min^(-1). Hence  2.76e-3/60 sec^(-1). This uses the relationship ln(2)/half-life where half-life is 45523 sec.
			 final double AHLdiffusivity = 159; //AHL diffusivity in environment taken from manuscript on proposed model given in um^2/sec. (z-axis dimension is only 1um).
			 final int[] boxes = new int[]{40,10,1}; //discretise the spatial dimension of the chemical field.
	 		 final BSimChemFieldExt AHLfield = new BSimChemFieldExt(sim, boxes, AHLdiffusivity, AHLdecayRate); //Create an instance of the chemical field with the above properties.
			 
	 		 //AHLfield.setConc(0,5,0,1e16);
			 //AHLfield.setConc(39,5,0,1e16);
			 
	 		 
	 		 final double boxSizeX=sim.getBound().x/boxes[0];
	 		 final double boxSizeY=sim.getBound().y/boxes[1];
	 		 final double boxSizeZ=sim.getBound().z/boxes[2];
	 		 final double boxVolume=boxSizeX*boxSizeY*boxSizeZ;
	 		 
	 		
	 		 
	 			 
	 		 
	 		 /** 
	 		  * Setup a particle class that extends the standard BSimBacterium to carry the GRN and other functions we want
	 		  * 
	 		  * */
			 class BSimOscillatorBacterium extends BSimBacterium{ 
				 
				//Instance fields of the class BSimBacterium. If declared here they are guaranteed to be present for the duration of the class.
				 protected GRNode GRN; //instance of internal class GRN.
				 protected double[] y, yNew; //Instances of internal class GRN parameters.
				 protected double Difftest;
				 protected double CellWallDiff;
				 protected double amountToshift;
				 //inherit constructor from BSimParticle class. Note that this does not include a construction of GRN related objects. 
				 //GRN related construction are defined separetely in the constructor.
				 
				 public BSimOscillatorBacterium(BSim sim, Vector3d position, double radius){
					 super(sim, position); 
					 
					 
					 GRN = new GRNode(rndParameters);
					 y=GRN.getICs();
					 CellWallDiff= 3.0/60.0; // Kaplan and Greenberg (1985) mention that conc. of AHL inside a cell and outside a cell occurred by 20sec. 1/20 sec^(-1).
				    
				 }
				 
				 				
				 /**
					 * The getConc() method retrieves the extracellular concentration in box_(i,j,k) as quantity.um^(-3).
					 * We pass coordinates i,j,k of bacterium location to retrieve extracellular conc at those coordinates. 
					 * The multiplication of 10^15 converts the retrieved concentration to microMolar if external quantity is defined in micromoles as in this model.
					 *  This allows a direct comparison between the two concentrations, intra/extra cellular.
					 *  Perform concentration difference between intracellular variable and extracellular variable.
					 *  Return the value of the difference.
					 * */
				 public double  intraExtraInteraction(double intracellvariable, BSimChemicalField chemfield){
						double extracellconcInUmol = chemfield.getConc(position);
						double intracellconcInUmol = intracellvariable*1e15; 
						double concdiff = intracellconcInUmol - extracellconcInUmol;
						return concdiff;
					}

				 
				 /** 
				  * Here we define a GRN for each bacterium.
				  * We define the GRN as an ODE System by implementing the BSimOdeSystem interface.
				  * This means that we define the three methods of the BSimOdeSystem interface:
				  * (i) derivativeSystem where we define the system of ODEs as an array (similar to Matlab),
				  * (ii) getIcs where we define the vector of initial conditions as an array and
				  * (iii) A method that simply returns the size of the array of the system of ODEs. 
				  * */
				 class GRNode implements BSimOdeSystem{

					 /* GRN size*/
					 final int GRNsize = 4;
					 
					 final double timeadj = 60.0;
					 
				    	//--------------------------------------
						//enzymatic parameters
						//--------------------------------------
						

					 
					 protected double delta1; 
					 protected double delta2;
					 protected double g;

						//--------------------------------------
						//parameters with units of minutes^(-1)
						//--------------------------------------
						
					 
					 protected double Kp2;
					 protected double Kr1OFF;
					 protected double KcatAiiA;
					 protected double t_A;
					 protected double t_LA;

						//--------------------------------------
						//parameters with units of microMolar minutes^(-1)
						//--------------------------------------
					 protected double a0LI;
					 protected double a0AA;
					 protected double Kpli;
					 protected double KpaA;
						
						//--------------------------------------
						//parameters with units of microMolar^(-1) minutes^(-1)
						//-------------------------------------- 
					 protected double Kr1ON;

						//--------------------------------------
						//parameters with units of microMolar
						//-------------------------------------- 
					 protected double Kmla;
					 protected double KmaA;
					 protected double Ltot;


						//--------------------------------------
						//Cooperativity
						//-------------------------------------- 

					 protected double n;
					 
					 public GRNode (boolean rndParams) {
						 if(rndParams) {
							 
							 double stdev = 0.06;
							 
							 delta1 = BSimUtils.sampleNormal(0.8487, stdev*0.8487)/timeadj ; 
							 delta2 = BSimUtils.sampleNormal(0.0234,stdev*0.0234)/timeadj;
							 g =	0.0412;
							 Kp2= 9/timeadj; //15/timeadj; 
							 Kr1OFF= 6e-6/timeadj; //6e-6/timeadj;
							 KcatAiiA= 2631.4/timeadj; //1055/timeadj; //1.3608e3/timeadj;
							 t_A= BSimUtils.sampleNormal(0.00276,stdev*0.00276)/timeadj; //0.00276/timeadj;
							 t_LA= BSimUtils.sampleNormal(0.024,stdev*0.024)/timeadj;//0.02/timeadj;
							 a0LI= BSimUtils.sampleNormal(7.785e-6,stdev*7.785e-6)/timeadj;//7.785070321e-6/timeadj;
							 a0AA= BSimUtils.sampleNormal(6.183e-6,stdev*6.183e-6)/timeadj;//6.183526866e-6/timeadj;
						     Kpli= BSimUtils.sampleNormal(0.9,stdev*0.9)/timeadj; //0.00996/timeadj;//0.00996e-1/timeadj;CHANGED PARAMETER
						     KpaA= BSimUtils.sampleNormal(0.9,stdev*0.9)/timeadj;//0.06469/timeadj;//0.005892e-1/timeadj; CHANGED PARAMETER
						     Kr1ON=5.99e-5/timeadj;//5.99e-5/timeadj;	
						     Kmla=1e-2;//1e-2;
						     KmaA=1200;
							 Ltot= 15;
							 n=2.0;
							 }
						 else{
							 
							 
										 
							 delta1 = 0.8487/timeadj; 
							 delta2 = 0.0234/timeadj;
							 g =	0.0412;
							 Kp2= 9/timeadj; //15/timeadj; 
							 Kr1OFF= 6e-6/timeadj; //6e-6/timeadj;
							 KcatAiiA= 2631.4/timeadj; //1055/timeadj; //1.3608e3/timeadj;
							 t_A= 0.00276/timeadj; //0.00276/timeadj;
							 t_LA= 0.024/timeadj;//0.02/timeadj;
							 a0LI= 7.785e-6/timeadj;;//7.785070321e-6/timeadj;
							 a0AA= 6.183e-6/timeadj;;//6.183526866e-6/timeadj;
						     Kpli= 0.9/timeadj; //0.00996/timeadj;//0.00996e-1/timeadj;CHANGED PARAMETER
						     KpaA= 0.9/timeadj;//0.06469/timeadj;//0.005892e-1/timeadj; CHANGED PARAMETER
						     Kr1ON=5.99e-5/timeadj;//5.99e-5/timeadj;	
						     Kmla=1e-2;//1e-2;
							 KmaA=1200;//2.95e3;
							 Ltot=15;//15;
							 n=2.0;
	

						 }
							 
							 
						 
						 
						 
					 }
					 
					 public double[] derivativeSystem(double x, double[] y) {
					
					
						
				
						double extraintradiff=Difftest*1e-15;

						 

						// TODO Auto-generated method stub
						/** Create a new double array of size equal to the system of ODEs that you need to solve. 
						 * Then define each element in the array; One element must correspond to one equation of the ODE system. */
						double[] dy = new double[GRNsize]; 
						
						dy[0] = a0LI + Kpli*(Math.pow(y[3], n)/(Math.pow(Kmla, n) + Math.pow(y[3], n))) - (delta1*y[0])/(g*(y[0]+y[2])+1);
						dy[1] = Kp2*y[0] - Kr1ON*(Ltot - y[3])*y[1] + Kr1OFF*y[3] - (KcatAiiA*y[2]*y[1])/(KmaA+y[1]) - t_A*y[1] -CellWallDiff*extraintradiff; //NEED TO DEFINE WAY OF OBTAINING EXT.CONC and implement CONC DIFFERENCE.
					    dy[2] = a0AA + KpaA*(Math.pow(y[3], n)/(Math.pow(Kmla, n) + Math.pow(y[3], n))) - (delta2*y[2])/(g*(y[0]+y[2])+1); 
						dy[3] = Kr1ON*(Ltot - y[3])*y[1] - Kr1OFF*y[3] - t_LA*y[3];
				
											                                                        
						return dy;
					 }

					/**
					 *This method should return the size of the array of the ODE system. 
					 */
					 
					@Override
					public int getNumEq() { //This might mean i need to rename GRNSize to NumEq or have NumEq return GRNsize
						// TODO Auto-generated method stub
						return GRNsize;
					}

					/**
					 * Create a vector of initial conditions for each of the ODEs given in the system above.
					 * There must be a one to one correspondence between the arrays. In the example below ics[0]
					 * is the initial condition of y[0] (dy[0]). At the end the method must return the vector.
					 * */
					@Override
					public double[] getICs() {
						
						//Initial conditions of the system. Should be defined as a vector. Given in uM.
						//-------------------------------------- 

						String conditions = "ICS_UNIFORM";
						

						// TODO Auto-generated method stub
						
						double[] ics = new double[GRNsize];
						
						if(ICS_CONDITIONS.equals(conditions)){
						ics[0] = 0.05;
						ics[1] = 0.05;
						ics[2] = 0.05;
						ics[3] = 0.05;
						
						}
						else{
							Random randomgenerator = new Random();
							ics[0] = randomgenerator.nextDouble();
							ics[1] = randomgenerator.nextDouble();
							ics[2] = randomgenerator.nextDouble();
							ics[3] = randomgenerator.nextDouble();
							
						}
						
						return ics;
					}
					 
				 
			 			 
			 }

				 @Override
				 public void action(){
					 super.action(); //Inherit all actions from BSimBacterium
					 Difftest =intraExtraInteraction(y[1], AHLfield);	 // perform a concentration difference test and return the value to the instance field Difftest.
					 amountToshift = Difftest*CellWallDiff*sim.getDt()*boxVolume; //this should now be correct as difftest gives quantity in uMol.um^(-3).
					 yNew = BSimOdeSolver.rungeKutta45(GRN, sim.getTime(), y, sim.getDt()); //Call to ODE solver from BSimOdeSolver class. 
					 y = yNew; // Computed yNew that is answer of numerical solution of the set of ODEs replaces y for next time step.
					 
					 AHLfield.addQuantity(position, amountToshift);

				    
					
					  
				 }
				
			 }
				 
				 /**
				  * Create a vector of bacteria establishing confluent conditions in environment.
				  * A vector of positions around the plane is established first, depending on environment dimensions.
				  * Then use this vector to create new bacteria at given positions.
				  * */
				 
			 final Vector<Vector3d> confluentPositions = new Vector<Vector3d>();	 
			 for(int i=1;i<sim.getBound().x;i+=14)
				 for(int j=1;j<sim.getBound().y;j+=14){
					 confluentPositions.add(new Vector3d(i-0.5,j-0.5,0.5));
					 
				 }
		
			 final Vector<Vector3d> singleposition = new Vector<Vector3d>();
			 singleposition.add(new Vector3d(100,25,0.5));
			
			 final Vector<Vector3d> doubleposition = new Vector<Vector3d>();
			 doubleposition.add(new Vector3d(1,25,0.5));
			 doubleposition.add(new Vector3d(185, 25, 0.5));
			
			 final Vector<Vector3d> tripleposition = new Vector<Vector3d>();
			 tripleposition.add(new Vector3d(1,25,0.5));
			 tripleposition.add(new Vector3d(100, 25, 0.5));
			 tripleposition.add(new Vector3d(200,25,0.5));
			 
			 final Vector<Vector3d> fourcells = new Vector<Vector3d>();
			 fourcells.add(new Vector3d(1,25,0.5));
			 fourcells.add(new Vector3d(70, 25, 0.5));
			 fourcells.add(new Vector3d(140,25,0.5));
			 fourcells.add(new Vector3d(200,25,0.5));
			 
			 final Vector<Vector3d> fivecells = new Vector<Vector3d>();
			 fivecells.add(new Vector3d(1,25,0.5));
			 fivecells.add(new Vector3d(60, 25, 0.5));
			 fivecells.add(new Vector3d(120,25,0.5));
			 fivecells.add(new Vector3d(180,25,0.5));
			 fivecells.add(new Vector3d(200,25,0.5));
			 
			 final Vector<Vector3d> sixcells = new Vector<Vector3d>();
			 sixcells.add(new Vector3d(10, 5, 0.5));
			 sixcells.add(new Vector3d(10,10,0.5));
			 sixcells.add(new Vector3d(10,15,0.5));
			 sixcells.add(new Vector3d(10, 20, 0.5));
			 sixcells.add(new Vector3d(10,25,0.5));
			 sixcells.add(new Vector3d(10,30,0.5));
			
			 
			 final Vector<Vector3d> sevencells = new Vector<Vector3d>();
			 sevencells.add(new Vector3d(1,10,0.5));
			 sevencells.add(new Vector3d(70, 10, 0.5));
			 sevencells.add(new Vector3d(140,10,0.5));
			 sevencells.add(new Vector3d(200,10,0.5));
			 sevencells.add(new Vector3d(70, 40, 0.5));
			 sevencells.add(new Vector3d(140,40,0.5));
			 sevencells.add(new Vector3d(200,40,0.5));
			 
			 final Vector<Vector3d> eightcells = new Vector<Vector3d>();
			 eightcells.add(new Vector3d(1,10,0.5));
			 eightcells.add(new Vector3d(70, 10, 0.5));
			 eightcells.add(new Vector3d(140,10,0.5));
			 eightcells.add(new Vector3d(200,10,0.5));
			 eightcells.add(new Vector3d(1,40,0.5));
			 eightcells.add(new Vector3d(70, 40, 0.5));
			 eightcells.add(new Vector3d(140,40,0.5));
			 eightcells.add(new Vector3d(200,40,0.5));
			 
			 final Vector<Vector3d> tencells = new Vector<Vector3d>();
			 tencells.add(new Vector3d(1,25,0.5));
			 tencells.add(new Vector3d(60, 25, 0.5));
			 tencells.add(new Vector3d(120,25,0.5));
			 tencells.add(new Vector3d(180,25,0.5));
			 tencells.add(new Vector3d(200,25,0.5));
			 tencells.add(new Vector3d(1,25,0.5));
			 tencells.add(new Vector3d(60, 25, 0.5));
			 tencells.add(new Vector3d(120,25,0.5));
			 tencells.add(new Vector3d(180,25,0.5));
			 tencells.add(new Vector3d(200,25,0.5));
			 
			 final Vector<BSimOscillatorBacterium> bacteria = new Vector<BSimOscillatorBacterium>(); //an empty vector/list to hold our bacteria particles.
				 			 
				 for(Vector3d position : confluentPositions){
					 BSimOscillatorBacterium bacterium = new BSimOscillatorBacterium(sim,position,1);
					 bacteria.add(bacterium);
				 }
			
			 
				 
				 final bsim.chemsignal.RefSignals refsignal= new bsim.chemsignal.RefSignals();
				 final bsim.chemsignal.ControlMethods piCTRL = new bsim.chemsignal.ControlMethods(kgain, 0, integraltime , derivativetime, AHLfield, signaldelayonset, signalstop);
				 
				 
				 /** Setup ticker to specify and update actions of bacteria and other actions
				  * 
				  * */
				 
				 
				 class myTicker extends BSimTicker{
				    	
				    	double averageLIdynamics;
				    	double averageAAdynamics;
				    	double averageLAdynamics;
				    	double averageAHLdynamics;
		                 
		                
				    	public double averageLI(Vector<BSimOscillatorBacterium> bacteria){
							double totalLI = 0;
				    		int vectorsize=bacteria.size();
							
							for(int i=0;i<vectorsize;i++){
								 totalLI+=bacteria.elementAt(i).y[0];
							}
							
							double avgLI=totalLI/vectorsize;
							
							return avgLI;
							
						}
				    	
				    	public double averageAHL(Vector<BSimOscillatorBacterium> bacteria){
							double totalAHL = 0;
				    		int vectorsize=bacteria.size();
							
							for(int i=0;i<vectorsize;i++){
								 totalAHL+=bacteria.elementAt(i).y[1];
							}
							
							double avgAHL=totalAHL/vectorsize;
							
							return avgAHL;
							
						}
				    	
				    	public double averageAA(Vector<BSimOscillatorBacterium> bacteria){
							double totalAA = 0;
				    		int vectorsize=bacteria.size();
							
							for(int i=0;i<vectorsize;i++){
								 totalAA+=bacteria.elementAt(i).y[2];
							}
							
							double avgAA=totalAA/vectorsize;
							
							return avgAA;
							
						}
				    	
				    	public double averageLA(Vector<BSimOscillatorBacterium> bacteria){
							double totalLA = 0;
				    		int vectorsize=bacteria.size();
							
							for(int i=0;i<vectorsize;i++){
								 totalLA+=bacteria.elementAt(i).y[3];
							}
							
							double avgLA=totalLA/vectorsize;
							
							return avgLA;
							
						}
					 
					 @Override
					 public void tick(){
						 
						 averageLIdynamics = averageLI(bacteria);
						 averageAAdynamics = averageAA(bacteria);
						 averageLAdynamics = averageLA(bacteria);
						 averageAHLdynamics= averageAHL(bacteria);
						 for(BSimOscillatorBacterium b : bacteria){ //loop iterates through bacterium vector and adds random brownian motion to each item in vector and then updates its position
							 b.action();
						     //b.updatePosition(); //updates position by using the force f defined in action. Need Tom to send over this bug fix.
						 }
						
						if(p_control && !i_control && !d_control){
							piCTRL.PCtrl(sim, AHLfield, refsignal.refSignalSin(sim, periodOfSignal, amplitudeConstant, alphaConstant) , averageLIdynamics);
						}
						else if(p_control && i_control && !d_control){
							piCTRL.PICtrl(sim, AHLfield, refsignal.refSignalSin(sim, periodOfSignal, amplitudeConstant, alphaConstant), averageLIdynamics);
						}
						else{
							piCTRL.PIDCtrl(sim, AHLfield, refsignal.refSignalSin(sim, periodOfSignal, amplitudeConstant, alphaConstant), averageLIdynamics);
						}
						 
						 
						 
						 //AHLfield.externalConstantAdd(2.3);
						 AHLfield.update();
						 			 }

					
				    }
				
				
					final myTicker simulationTicker = new myTicker();
					sim.setTicker(simulationTicker);
				 

//				 
//				/**
//				 * 
//				 * Setup drawer to visualise actions in environmnent
//				 * Need to set it up so bacteria change color as [GFP] changes (variable y[3] in GRN).
//				 * */ 
//				 
//				 BSimP3DDrawer drawer = new BSimP3DDrawer(sim, 800,600){
//					 public float rot = 1.0f;
//					 @Override
//					 public void scene(PGraphics3D p3d){
//						 //-----------------
//						 //Scene rotation
//						 //-----------------
//						    p3d.translate(0, 0, 0);
//							p3d.scale(0.75f);
//							p3d.rotateZ(9.0f * (float)(Math.PI/0.93));
//							p3d.rotateY(9.0f * (float)(Math.PI/2.55));
//							p3d.rotateX(9.0f * (float)(Math.PI/1.4));
//							p3d.translate(-100, -25, -1);
//						 
//						 //-----------------
//						 // Drawing chemical field 
//						 //-----------------
//						 draw(AHLfield, new Color(Integer.parseInt("6899d3", 16)), (float)((255/2)*4.4e-16));
//						 
//						 //-----------------
//						 // Drawing bacteria 
//						 //-----------------
//						 
//						 for(BSimOscillatorBacterium b : bacteria){ //go through the vector bacteria and draw all the bacteria as green
//							 int  R = (int) (255);  //Setting up a color scheme that will allow bacteria to oscillate depending on expression of GFP between orange(inactive) to red (active).
//	 					 int  G = 165 - (int)  (252*b.y[0]); //http://www.tayloredmktg.com/rgb/ RGB values taken from 'oranges' section on this webpage.
//							 int  B = (int) 0;
//							 if(G < 0) G = 0; //to avoid any chance of G having a negative value this if statement is included. RGB values range from 0 - 255. 
//							 Color bacteriumColor = new Color(R, G, B); //Create a new instance field of the class Color to store color preferences.
//							 draw(b, bacteriumColor);
//						 }
//						 
////						 for(BSimBacteriumOFF b : bacteriaOFF){ //go through the vector bacteria and draw all the bacteria as green
////							 int  R = (int) (155);  //Setting up a color scheme that will allow bacteria to oscillate depending on expression of GFP between orange(inactive) to red (active).
////							 int  G = 165 - (int)  (252*b.y[0]); //http://www.tayloredmktg.com/rgb/ RGB values taken from 'oranges' section on this webpage.
////							 int  B = (int) 0;
////							 if(G < 0) G = 0; //to avoid any chance of G having a negative value this if statement is included. RGB values range from 0 - 255. 
////							 Color bacteriumColor = new Color(R, G, B); //Create a new instance field of the class Color to store color preferences.
////							 draw(b, bacteriumColor);
////						 }
//					 }
//				 };
//				 
//				 
//			 sim.setDrawer(drawer); //set the BSim drawer to obey defined rules as above.
//			 
			/** 
			 * Setup exporters for GRN components.
			 * 
			 * */
			 
			 BSimLogger LI_dynamics = new BSimLogger(sim, filePath + "LI_dynamics.csv"){
				 @Override
				 public void before(){
					 super.before();
					 write("time (sec),[LI]");
				 }
				 @Override
				 public void during(){
					 String timer = sim.getFormattedTime()+",";
					 String buffer = new String();
					 for(int i=0;i<bacteria.size();i++){
						 buffer += bacteria.elementAt(i).y[0]  + "," ;
					 }
					 write(timer + buffer); 
				 }
				
			 };
			 
			
			 
			 
			 
			 BSimLogger AHL_dynamics = new BSimLogger(sim, filePath + "AHL_dynamics.csv"){
				 @Override
				 public void before(){
					 super.before();
					 write("time (sec),[AHL]");
				 }
				 @Override
				 public void during(){
					 String timer = sim.getFormattedTime()+",";
					 String buffer = new String();
					 for(int i=0;i<bacteria.size();i++){
						 buffer += bacteria.elementAt(i).y[1]  + "," ;
					 }
					 write(timer + buffer); 
				 }
				 
			 };
			 
			

			 BSimLogger AA_dynamics = new BSimLogger(sim, filePath + "AA_dynamics.csv"){
				 @Override
				 public void before(){
					 super.before();
					 write("time (sec),[AA]");
				 }
				 @Override
				 public void during(){
					 String timer = sim.getFormattedTime()+","; 
					 String buffer = new String();
					 for(int i=0;i<bacteria.size();i++){
						 buffer += bacteria.elementAt(i).y[2]  + "," ;
					 }
					 write (timer + buffer);
				 }
				 
			 };
	//
//			 BSimLogger AA_dynamics_OFF = new BSimLogger(sim, filePath + "AA_dynamics_OFF.csv"){
//				 @Override
//				 public void before(){
//					 super.before();
//					 write("time (sec),[AA]");
//				 }
//				 @Override
//				 public void during(){
//					 String timer = sim.getFormattedTime()+","; 
//					 String buffer = new String();
//					 for(int i=0;i<bacteriaOFF.size();i++){
//						 buffer += bacteriaOFF.elementAt(i).y[2]  + "," ;
//					 }
//					 write (timer + buffer);
//				 }
//				 
//			 };
	//
	//
//			 BSimLogger GFP_dynamics_OFF = new BSimLogger(sim, filePath + "GFP_dynamics_OFF.csv"){
//				 @Override
//				 public void before(){
//					 super.before();
//					 write("time (sec),[GFP]");
//				 }
//				 @Override
//				 public void during(){
//					 String timer = sim.getFormattedTime()+","; 
//					 String buffer = new String();
//					 for(int i=0;i<bacteriaOFF.size();i++){
//						 buffer += bacteriaOFF.elementAt(i).y[2]  + "," ;
//					 }
//					 write (timer + buffer);
//				 }
//				 
//			 };
			 
			 BSimLogger LI_average = new BSimLogger(sim, filePath + "LI_average.csv"){
					
				 @Override
				 public void before(){
					 super.before();
					 write("time (sec),[LI-Avg]");
					 
				 }
				 @Override
				 public void during(){
					 String timer = sim.getFormattedTime()+",";
					 String buffer = new String();
					 buffer += 	simulationTicker.averageLIdynamics + ",";
					 write(timer + buffer); 
				 }
				
			 };	 
			 
			
			 
			 BSimLogger aA_average = new BSimLogger(sim, filePath + "aA_average.csv"){
				 
				@Override
				 public void before(){
					 super.before();
					 write("time (sec),[aA-Avg]");
					 
				 }
				 @Override
				 public void during(){
					 String timer = sim.getFormattedTime()+",";
					 String buffer = new String();
					 buffer += 	simulationTicker.averageAAdynamics + ",";
					 write(timer + buffer); 
				 }
				
			 };	 
			 
			 BSimLogger referenceval = new BSimLogger(sim, filePath + "referenceval.csv"){
			 @Override
			 public void before(){
				 super.before();
				 write("time (sec),referenceval");
			 }
			 @Override
			 public void during(){
				 String timer = sim.getFormattedTime()+",";
				 String buffer = new String();
				buffer += refsignal.getrefsignal(); ///(sim.getBound().x*sim.getBound().y*sim.getBound().z);
				
			     write(timer + buffer); 
		 }
		 }; 
		 
		 
		
			 BSimLogger LA_dynamics = new BSimLogger(sim, filePath + "LA_dynamics.csv"){
				 @Override
				 public void before(){
					 super.before();
					 write("time (sec),[LA]");
				 }
				 @Override
				 public void during(){
					 String timer = sim.getFormattedTime()+",";
					 String buffer = new String();
					 for(int i=0;i<bacteria.size();i++){
						 buffer += bacteria.elementAt(i).y[3]  + "," ;
					 }
					 write(timer + buffer); 
				 }
				 
			 };
			 
			 
			 BSimLogger AHLext_dynamics = new BSimLogger(sim, filePath + "AHLext_dynamics.csv"){
				 @Override
				 public void before(){
					 super.before();
					 write("time (sec),[AHL_ext]");
				 }
				 @Override
				 public void during(){
					 String timer = sim.getFormattedTime()+",";
					 String buffer = new String();
					buffer += AHLfield.totalQuantity();///(sim.getBound().x*sim.getBound().y*sim.getBound().z);
					 // for(int i=0;i<bacteria.size();i++){
					//	 buffer += bacteria.elementAt(i).amountToshift  + "," ;
					// }
				     write(timer + buffer); 
			 }
			 }; 
			 
			 
			 BSimLogger integralctrlval = new BSimLogger(sim, filePath + "integralctrlval.csv"){
				 @Override
				 public void before(){
					 super.before();
					 write("time (sec),intctrlval");
				 }
				 @Override
				 public void during(){
					 String timer = sim.getFormattedTime()+",";
					 String buffer = new String();
					buffer += piCTRL.getIctrlval();
					
				     write(timer + buffer); 
			 }
			 }; 
			 
			 BSimLogger propctrlval = new BSimLogger(sim, filePath + "propctrlval.csv"){
				 @Override
				 public void before(){
					 super.before();
					 write("time (sec),propctrlval");
				 }
				 @Override
				 public void during(){
					 String timer = sim.getFormattedTime()+",";
					 String buffer = new String();
					buffer += piCTRL.getPctrlval();
					
				     write(timer + buffer); 
			 }
			 }; 
			 
			 BSimLogger dctrlval = new BSimLogger(sim, filePath + "dctrlval.csv"){
				 @Override
				 public void before(){
					 super.before();
					 write("time (sec),dctrlval");
				 }
				 @Override
				 public void during(){
					 String timer = sim.getFormattedTime()+",";
					 String buffer = new String();
					buffer += piCTRL.getDctrlval();
					
				     write(timer + buffer); 
			 }
			 };
			 
			 BSimLogger chemfluxinputval = new BSimLogger(sim, filePath + "chemfluxinputval.csv"){
				 @Override
				 public void before(){
					 super.before();
					 write("time (sec),chemfluxinputval");
				 }
				 @Override
				 public void during(){
					 String timer = sim.getFormattedTime()+",";
					 String buffer = new String();
					buffer += piCTRL.getchemfluxinput();
					
				     write(timer + buffer); 
			 }
			 }; 
			 
			 
			 BSimLogger BacteriaPositions = new BSimLogger(sim, filePath + "BacteriaPositions.csv"){
				 @Override
				 public void before(){
					 super.before();
					 write("");
				 }
				 @Override
				 public void during(){
					
					 String buffer = new String();
					 for(int i=0;i<bacteria.size();i++){
						 buffer += (int) bacteria.elementAt(i).getPosition().x + "," + (int) bacteria.elementAt(i).getPosition().y + "," + (int)  bacteria.elementAt(i).getPosition().z + "," ;
					 }
					 write(buffer); 
				 }
				 
			 };
			 
			 BSimLogger BacteriaParams = new BSimLogger(sim, filePath + "BacteriaParams.csv"){
				 @Override
				 public void before(){
					 super.before();
					 String buffer = new String();
					 for(int i=0;i<bacteria.size();i++){
						 buffer += (double) bacteria.elementAt(i).GRN.delta1 + "," 
						 		+ (double) bacteria.elementAt(i).GRN.delta2 +  "," 
						 		+ (double) bacteria.elementAt(i).GRN.g + "," 
						 		+ (double) bacteria.elementAt(i).GRN.Kp2 + "," 
						 		+ (double) bacteria.elementAt(i).GRN.Kr1OFF + "," 
						 		+ (double) bacteria.elementAt(i).GRN.KcatAiiA + "," 
						 		+ (double) bacteria.elementAt(i).GRN.t_A + "," 
						 		+ (double) bacteria.elementAt(i).GRN.t_LA + "," 
						 		+ (double) bacteria.elementAt(i).GRN.a0LI + "," 
						 		+ (double) bacteria.elementAt(i).GRN.a0AA + "," 
						 		+ (double) bacteria.elementAt(i).GRN.Kpli + "," 
						 		+ (double) bacteria.elementAt(i).GRN.KpaA + "," 
						 		+ (double) bacteria.elementAt(i).GRN.Kr1ON + "," 
						 		+ (double) bacteria.elementAt(i).GRN.Kmla + "," 
						 		+ (double) bacteria.elementAt(i).GRN.KmaA + "," 
						 		+ (double) bacteria.elementAt(i).GRN.Ltot + "," 
						 		+ (double) bacteria.elementAt(i).GRN.n + "," + "\n" ;
						 // Gives a single row of parameters per bacterium. New line is new bacterium.
					 }
					 write(buffer); 
				 }
				 @Override
				 public void during(){
					
					
				 }
				 
			 };
			 
			 
//			 BSimLogger ChemfieldBoxes = new BSimLogger(sim, filePath + "BSimBoxes.csv"){
//				 @Override
//				 public void before(){
//					 super.before();
//					 String buffer = new String();
//					 buffer += (int) AHLfield.getBoxes()[0] + "," + (int) AHLfield.getBoxes()[1] + "," + (int) AHLfield.getBoxes()[2];
//					 write(buffer); 
//				 }
//				 @Override
//				 public void during(){
//					
//					
//				 }
//				 
//			 
//			 };
//			 sim.addExporter(ChemfieldBoxes);
			
			
			sim.addExporter(LI_average);
			LI_average.setDt(100);
			
			sim.addExporter(LI_dynamics);
			LI_dynamics.setDt(100);
			
			sim.addExporter(AA_dynamics);
			AA_dynamics.setDt(100);
				
			sim.addExporter(aA_average);
			aA_average.setDt(100);
			
			sim.addExporter(AHLext_dynamics);
			AHLext_dynamics.setDt(100);
			
			sim.addExporter(referenceval);
			referenceval.setDt(100 );
			
			sim.addExporter(propctrlval);
			propctrlval.setDt(100);
							
			sim.addExporter(integralctrlval);
			integralctrlval.setDt(100);
			
			sim.addExporter(chemfluxinputval);
			chemfluxinputval.setDt(100);
			
			sim.addExporter(dctrlval);
			dctrlval.setDt(100);
			
			sim.addExporter(BacteriaParams); 
			
			//LI_dynamics.setDt(100);
			//sim.addExporter(LI_dynamics);
			
			//AHL_dynamics.setDt(500);
			//sim.addExporter(AHL_dynamics);
			
			//AA_dynamics.setDt(100);
			//sim.addExporter(AA_dynamics);
			
			//LA_dynamics.setDt(500);
			//sim.addExporter(LA_dynamics);
			
			
			//BacteriaPositions.setDt(500);
			// sim.addExporter(BacteriaPositions);
			 
			//GFP_dynamics_OFF.setDt(500);
			//sim.addExporter(GFP_dynamics_OFF);
			
			// MOVIES
				//BSimMovExporter movieExporter = new BSimMovExporter(sim, drawer, filePath + "HastyOscillator.mov");
				//movieExporter.setSpeed(4800);
				//movieExporter.setDt(500);
				//sim.addExporter(movieExporter);	
				
				//Chemical Field Exporter
				//BSimChemicalFieldExporterBinary AHLfieldelements = new BSimChemicalFieldExporterBinary(sim, AHLfield, filePath + "AHLchemfieldelements.txt", filePath + "AHLSTATS.txt");
				//AHLfieldelements.setDt(500);
				//sim.addExporter(AHLfieldelements);
			
				BSimLogger stats_Logger = new BSimLogger(sim, filePath + "Settings.csv") {
					long tStart = 0;
					long tEnd = 0;
					@Override
					public void before() {
						super.before();
						tStart = Calendar.getInstance().getTimeInMillis();
						// Write parameters of the simulation
						write("Dt," + sim.getDt()); 
						write("Time (sec)," + sim.getSimulationTime());
						write("Diffusivity," + AHLdiffusivity);
						write("Decay rate," + AHLdecayRate);
						write("Population size," + bacteria.size());
						write("Amplitude," + amplitudeConstant);
						write("Period," + periodOfSignal);
						write("alphaConstant," + alphaConstant);
						write("k gain," + kgain);
						write("integral time," + integraltime);
						write("signal onset," + signaldelayonset);
						write("signal off," +signalstop);
						write("derivative gain,"+derivativetime);
						//write("Type of bounds"+ sim.getSolid() );
						//write("Cell wall diffusion," + iff);
						//if(theInitialConditions == ICS_RANDOM) write("Initial Conditions, Random");
						//else write("Initial Conditions, Uniform");
					}
					
					@Override
					public final void during() {
						// Ignore this...
					}
					
					public void after(){
						// Elapsed time (real time)
						tEnd = Calendar.getInstance().getTimeInMillis();
						write("Elapsed time (sec)," + ((tEnd - tStart)/1000.0));
						super.after();
					}
				};
				sim.addExporter(stats_Logger);
				
				
			
			sim.export(); //Preview the environment and bacteria
				
			
		}

}
