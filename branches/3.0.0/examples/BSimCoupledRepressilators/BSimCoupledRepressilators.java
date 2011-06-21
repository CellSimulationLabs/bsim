package BSimCoupledRepressilators;

import java.awt.Color;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.BSimTicker;
import bsim.BSimUtils;
import bsim.draw.BSimDrawer;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimLogger;
import bsim.ode.BSimOdeSolver;
import bsim.ode.BSimOdeSystem;
import bsim.particle.BSimBacterium;

/**
 * Simulation of bacteria with repressilator GRNs coupled by a chemical field.</br>
 * 
 * ODE system for the repressilator taken from:
 * "Modeling a synthetic multicellular clock: Repressilators coupled by quorum sensing"
 * PNAS 2004 101 (30) 10955-10960; doi:10.1073/pnas.0307095101
 */
public class BSimCoupledRepressilators {

	// Initial conditions of the GRNs - Used for convenience
	public static int ICS_RANDOM = 1;
	public static int ICS_UNIFORM = 2;

	/*********************************************************
	 * Simulation Definition
	 */
	 public static void main(String[] args) {


		/*********************************************************
		 * Create a new directory for the simulation results
		 */
		String filePath = BSimUtils.generateDirectoryPath("results/" + BSimUtils.timeStamp() +"/");			
		
		/*********************************************************
		 * Create a new simulation object
		 */
		BSim sim = new BSim();			// New Sim object
		sim.setDt(0.01);				// Simulation Timestep
		sim.setSimulationTime(10); 		// Simulation Length (6000 = 100 minutes)
		sim.setTimeFormat("0.00");		// Time Format for display
		sim.setBound(100,100,100);		// Simulation Boundaries
		
		
		/*********************************************************
		 * Set up some constants
		 */
		// diffusivity of AI in (microns^2/sec)? (BSim 1.0: diffusion coeff. of AHL = 0.23 per second)
		final double diffusivity = 100;  	// Diffusivity of AHL
		final double decayRate = 0.01/60; 	// Decay Rate (0.1666 = 10 per minute, 0.01/60 = 1e-2 per min)
		
		final double cellWallDiffusivity = 2.0; 		// Cell wall diffusivity (Should be the same as eta in the GRN?)
		final int theInitialConditions = ICS_RANDOM;	// What initial conditions do we want to use?
		
		// Set up the chemical field for AHL:
		final BSimChemicalField field = new BSimChemicalField(sim, new int[]{25,25,25}, diffusivity, decayRate);

		
		/*********************************************************
		 * BSimBacterium with a repressilator inside
		 */
		class BSimRepressilatorBacterium extends BSimBacterium {
			protected QuorumRepressilator repGRN;	// Instance of internal class
			protected double[] y, yNew;				// Local values of ODE variables

			/*
			 * Constructor for a repressilator GRN bacterium.
			 */
			public BSimRepressilatorBacterium(BSim sim, Vector3d position){
				super(sim, position);
				
				// Create the parameters and initial conditions for the ODE system
				repGRN = new QuorumRepressilator();
				repGRN.generateBeta();
				y = repGRN.getICs();
			}
			
			/*
			 * Action each time step: iterate ODE system for one time-step.
			 */
			@Override
			public void action() {
				
				// Movement etc:
				super.action();
				
				// Variables for chemical field response:
				double externalChem;	// External chem. field
				double deltaChem;		// Change in chemical quantity
				
				// external chemical level at position of the bacterium:
				externalChem = field.getConc(position);
				
				// Get the external chemical field level for the GRN ode system later on:
				repGRN.setExternalQuorumLevel(externalChem);
				
				// Solve the ode system
				// IMPORTANT: re-scale the time units correctly (GRN equations are in minutes, BSim works in seconds)
				yNew = BSimOdeSolver.rungeKutta45(repGRN, sim.getTime()/60, y, sim.getDt()/60);
				y = yNew;
				
				// Adjust the external chemical field
				deltaChem = externalChem - y[6];
								
				//( note - 11/2010 -  not sure quite why this is still an if statement as both the same...)
				// Well, it works fine but there is obviously some redundant code :)
				if( deltaChem < 0){
					field.addQuantity(position, cellWallDiffusivity*(-deltaChem));
				}else if(deltaChem > 0){
					field.addQuantity(position, cellWallDiffusivity*(-deltaChem));
				}				
			}
			
			/*
			 * Representation of the repressilator ODE system with quorum coupling
			 */
			class QuorumRepressilator implements BSimOdeSystem{
				private int numEq = 7;				// System of 7 equations
				private double beta;				// beta parameter
				private Random r = new Random();	// Random number generator
				private double Se = 0;				// External chemical level
				
				// The equations
				public double[] derivativeSystem(double x, double[] y) {
					double[] dy = new double[numEq];
					// Various parameters from the paper:
					double alpha = 216, 
						nExp = 2.0, 
						eta = cellWallDiffusivity, 
						ks0 = 1, 
						ks1 = 0.01, 
						kappa = 20;
					
					// rate of change of a, b, c: mRNA transcribed from tetR, cI, lacI respectively
					dy[0] = -y[0] + alpha/(1 + Math.pow(y[5], nExp));
					dy[1] = -y[1] + alpha/(1 + Math.pow(y[3], nExp));
					dy[2] = -y[2] + alpha/(1 + Math.pow(y[4], nExp)) + (kappa*y[6])/(1 + y[6]);
					
					// rate of change of A, B, C: proteins corresponding to a,b,c above
					dy[3] = beta*(y[0]-y[3]);
					dy[4] = beta*(y[1]-y[4]);
					dy[5] = beta*(y[2]-y[5]);
					
					// rate of change of AI inside the cell
					dy[6] = -ks0*y[6] + ks1*y[3] - eta*(y[6] - Se);
					return dy;
				}
				
				// Set up what the external chemical level is:
				public void setExternalQuorumLevel(double externalQuorumField){
					Se = externalQuorumField;
				}
				
				// Initial conditions for the ODE system
				public double[] getICs() {
					double[] ics = new double[numEq];
					
					if(theInitialConditions == ICS_UNIFORM){
						// Start synchronised
						ics[0] = 1.728;
						ics[1] = 0.4414;
						ics[2] = 184;
						ics[3] = 9.363;
						ics[4] = 0.4414;
						ics[5] = 155.1;
						ics[6] = 1.5;
					}else{
						// Start random
						for(int i =0;i<numEq-1;i++){
							ics[i] = 100*r.nextDouble();
						}
						ics[6] = 0.5*r.nextDouble();
					}
					return ics;
				}
				
				
				// Parameter Beta - ratio between mRNA and protein lifetimes
				public void generateBeta(){
					// Garcia-Ojalvo paper part 1:
					beta = 1.0 + 0.05*r.nextGaussian();
				}
		
				public int getNumEq() {
					return numEq;
				}
			}
		}
		
		
		/*********************************************************
		 * Create the vector of all bacteria used in the simulation 
		 */
		final Vector<BSimRepressilatorBacterium> bacteria = new Vector<BSimRepressilatorBacterium>();
		
		// Add randomly positioned bacteria to the vector
		while(bacteria.size() < 200) {		
			BSimRepressilatorBacterium p = new BSimRepressilatorBacterium(sim, 
										  new Vector3d(Math.random()*sim.getBound().x,
													   Math.random()*sim.getBound().y,
													   Math.random()*sim.getBound().z));
			if(!p.intersection(bacteria)) bacteria.add(p);
		}

		
		/********************************************************* 
		 * Implement tick() on a BSimTicker and add the ticker to the simulation	  
		 */
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				
				// Update the bacteria at each time step
				for(BSimRepressilatorBacterium p : bacteria) {
					p.action();		
					p.updatePosition();
				}
				// Update the chemical field
				field.update();
			}		
		});

		
		/*********************************************************
		 * Implement draw(Graphics) on a BSimDrawer and add the 
		 * drawer to the simulation.
		 * 
		 * Draw the particles such that they are yellow for low 
		 * levels of lacI mRNA and get more red as the level 
		 * increases.
		 */
		BSimDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {
				// Draw the chemical field a pretty pale blue colour
				draw(field, new Color(Integer.parseInt("6899d3", 16)), 255/2);
				
				for(BSimRepressilatorBacterium p : bacteria) {		
					// Oscillate from pale yellow to red.
					int R = 255, 
						G = 255 - (int)(255*p.y[2]*0.0111), 
						B = 128 - (int)(128*p.y[2]*0.0111);
					// Clamp these bad boys to [0, 255] to avoid errors
					if(G < 0) G = 0;
					if(B < 0) B = 0;
					draw(p,new Color(R, G, B));
				}
			}
		}; 
		sim.setDrawer(drawer);	

		
		/********************************************************* 
		 * Implement before(), during() and after() on BSimExporters
		 * and add them to the simulation
		 */
		// MOVIES
//		BSimMovExporter movieExporter = new BSimMovExporter(sim, drawer, filePath + "Repressilator.mov");
//		movieExporter.setSpeed(10);
//		movieExporter.setDt(0.25);
//		sim.addExporter(movieExporter);	

		// IMAGES
//		BSimPngExporter imageExporter = new BSimPngExporter(sim, drawer, filePath);
//		imageExporter.setDt(10);
//		sim.addExporter(imageExporter);			


		/*********************************************************
		 *  Create Loggers:
		 *  - Simulation statistics
		 *  - Time series of lacI mRNA concentration for every bacterium.
		 *  
		 *  setDt() to reduce the amount of data.
		 */
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
				write("Diffusivity," + diffusivity);
				write("Decay rate," + decayRate);
				write("Cell wall diffusion," + cellWallDiffusivity);
				if(theInitialConditions == ICS_RANDOM) write("Initial Conditions, Random");
				else write("Initial Conditions, Uniform");
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
		
		
		// Print the level of lacI mRNA in all bacteria
		// WARNING: lots of data :)
		BSimLogger lacI_logger_ALL = new BSimLogger(sim, filePath + "lacI_ALL.csv") {
			@Override
			public void before() {
				super.before();
				write("time(seconds),lacI_mRNA"); 
			}
			
			@Override
			public void during() {
				String o = sim.getFormattedTime();
				String buffer = new String();
				for(int i = 0, n = bacteria.size();i<n;i++){
					buffer = buffer +"," + bacteria.elementAt(i).y[2];
				}
				write(o + buffer);
			}
		};
		lacI_logger_ALL.setDt(1);			// Set export time step
		sim.addExporter(lacI_logger_ALL);
		
		
		// Print the level of internal AI in all bacteria
		// WARNING: lots of data :)
		BSimLogger AI_internal_logger_ALL = new BSimLogger(sim, filePath + "AI_internal_ALL.csv") {
			@Override
			public void before() {
				super.before();
				write("time(seconds),internal_AI"); 
			}
			
			@Override
			public void during() {
				String o = sim.getFormattedTime();
				String buffer = new String();
				for(int i = 0, n = bacteria.size();i<n;i++){
					buffer = buffer + "," + bacteria.elementAt(i).y[6];
				}
				write(o + buffer);
			}
		};
		AI_internal_logger_ALL.setDt(1);	// Set export time step
		sim.addExporter(AI_internal_logger_ALL);
		
		
		/*********************************************************
		 * Call sim.preview() to preview the scene 
		 * or sim.export() to set exporters working 
		 */
		sim.preview();
	}
}
