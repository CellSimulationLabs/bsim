package BSimDdeDynamics;

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
import bsim.dde.BSimDdeSolver;
import bsim.dde.BSimDdeSystem;
import bsim.particle.BSimBacterium;

public class BSimDdeDynamics {

	
	/*********************************************************
	 * Simulation Definition
	 */
	 public static void main(String[] args) {
		 
		
		/*********************************************************
		 * Create a new simulation object
		 */
		BSim sim = new BSim();			// New Sim object
		sim.setDt(0.01);				// Simulation Timestep
		sim.setSimulationTime(10); 		// Simulation Length (6000 = 100 minutes)
		sim.setTimeFormat("0.00");		// Time Format for display
		sim.setBound(100,100,100);		// Simulation Boundaries

		
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
			class QuorumRepressilator implements BSimDdeSystem{
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

		
		/**********************************************************/
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

		
		/**********************************************************/
		BSimDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {
				for(BSimRepressilatorBacterium p : bacteria) {		
					draw(p,Color.RED);
				}
			}
		}; 
		sim.setDrawer(drawer);	
		
		
		/**********************************************************/
		sim.preview();
	}
}
