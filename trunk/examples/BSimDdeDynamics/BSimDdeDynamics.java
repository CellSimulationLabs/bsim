package BSimDdeDynamics;

import java.awt.Color;
import java.util.Vector;
import javax.vecmath.Vector3d;
import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimDrawer;
import bsim.draw.BSimP3DDrawer;
//import bsim.export.BSimLogger;
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
		sim.setDt(0.1);				    // Simulation Timestep
		sim.setSimulationTime(10); 		// Simulation Length (6000 = 100 minutes)
		sim.setTimeFormat("0.00");		// Time Format for display
		sim.setBound(100,100,100);		// Simulation Boundaries

		
		/*********************************************************
		 * BSimBacterium with a repressilator inside
		 */
		class BSimDdeBacterium extends BSimBacterium {
			protected DdeDyanmics ddeGRN;	// Instance of internal class
			protected Vector<double[]> s;				// Local values of ODE variables

			/*
			 * Constructor for a repressilator GRN bacterium.
			 */
			public BSimDdeBacterium(BSim sim, Vector3d position){
				super(sim, position);
				
				// Create the parameters and initial conditions for the ODE system
				ddeGRN = new DdeDyanmics();
				s = BSimDdeSolver.getInitialState(ddeGRN, sim.getDt());
			}
			
			/*
			 * Action each time step: iterate ODE system for one time-step.
			 */
			@Override
			public void action() {
				
				// Movement etc:
				super.action();
				
				// Solve the ode system
				// IMPORTANT: re-scale the time units correctly (GRN equations are in minutes, BSim works in seconds)
				//BSimDdeSolver.rungeKutta45(repGRN, sim.getTime()/60, ys, sim.getDt()/60);
				BSimDdeSolver.euler(ddeGRN, sim.getTime(), s, sim.getDt());
			}
			
			/*
			 * Representation of the repressilator ODE system with quorum coupling
			 */
			class DdeDyanmics implements BSimDdeSystem{
				
				// The equations
				public double[] derivativeSystem(double x, double[] y, Vector<double[]> ys) {
					double[] dy = new double[2];
					double[] tm1 = BSimDdeSolver.getDelayedState(ys, sim.getDt(), 1.0);
					double[] tm2 = BSimDdeSolver.getDelayedState(ys, sim.getDt(), 2.0);
					
					dy[0] = 1.1 * tm1[0];
					dy[1] = 1.1 * tm2[1];
					
					return dy;
				}
				
				// Initial conditions for the DDE system
				public double[] getICs() {
					double[] ics = new double[2];
					ics[0] = 1.0;
					ics[1] = 1.1;	
					return ics;
				}
		
				public int getNumEq() {
					return 2;
				}
				
				public double getMaxDelay() {
					return 3.0;
					
				}
				
				public void setInitialHistory(Vector<double[]> ys) {
					for (int i=0; i<ys.size(); i++){
						ys.set(i, this.getICs());
					}
				}
			}
		}
		
		
		/*********************************************************
		 * Create the vector of all bacteria used in the simulation 
		 */
		final Vector<BSimDdeBacterium> bacteria = new Vector<BSimDdeBacterium>();
		
		// Add randomly positioned bacteria to the vector
		while(bacteria.size() < 200) {		
			BSimDdeBacterium p = new BSimDdeBacterium(sim, 
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
				for(BSimDdeBacterium p : bacteria) {
					p.action();		
					p.updatePosition();
				}
			}		
		});

		
		/**********************************************************/
		BSimDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {
				for(BSimDdeBacterium p : bacteria) {		
					draw(p,Color.RED);
				}
			}
		}; 
		sim.setDrawer(drawer);	
		
		
		/**********************************************************/
		sim.preview();
	}
}
