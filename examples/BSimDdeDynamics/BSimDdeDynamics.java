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
		 * Create a new simulation object (the environment)
		 */
		BSim sim = new BSim();        // New Sim object
		sim.setDt(0.1);               // Simulation Timestep
		sim.setSimulationTime(3600);  // Simulation Length (3600 seconds = 10 minutes)
		sim.setTimeFormat("0.00");    // Time Format for display
		sim.setBound(100,100,100);    // Simulation Boundaries

		
		/*********************************************************
		 * Define a new type of bacterium that uses a DDE GRN
		 */
		class BSimDdeBacterium extends BSimBacterium {
			protected HastyDdeDyanmics ddeGRN;  // Instance of internal class
			protected Vector<double[]> s;       // Local values of ODE variables

			/* Constructor */
			public BSimDdeBacterium(BSim sim, Vector3d position){
				super(sim, position);
				// Create the internal DDE system and generate initial state for the DDE system
				ddeGRN = new HastyDdeDyanmics();
				s = BSimDdeSolver.getInitialState(ddeGRN, sim.getDt());
			}
			
			/* Action each time step: iterate DDE system and transfer AHL */
			@Override
			public void action() {
				// Movement etc:
				super.action();
				// Solve the ode system
				BSimDdeSolver.rungeKutta45(ddeGRN, sim.getTime(), s, sim.getDt());
			}
			
			/* Representation of the Hasty oscillator DDE model */
			class HastyDdeDyanmics implements BSimDdeSystem{
				
				// The DDE equations
				public double[] derivativeSystem(double x, double[] y, Vector<double[]> ys) {
					double[] dy = new double[3];
					// Get the state for a 600 seconds delay
					double[] tm1 = BSimDdeSolver.getDelayedState(ys, sim.getDt(), 600.0);
					
					// Parameters for the equations (time is in seconds)
					
					
					// A (AiiA)
					dy[0] = 1.1 * tm1[0];
					// I (LuxI)
					dy[1] = 1;
					// H_i (Internal AHL)
					dy[2] = 2;
					
					return dy;
				}
				
				// Initial conditions for the DDE system
				public double[] getICs() {
					double[] ics = new double[2];
					ics[0] = 1.0;
					ics[1] = 1.1;
					return ics;
				}
				
				// Number of equations in the DDE
				public int getNumEq() { return 3; }
				
				// The maximum delay we require (600 seconds in this case)
				public double getMaxDelay() { return 600.0; }
				
				// Constant history the same as the initial conditions
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
		// Add randomly positioned bacteria
		while(bacteria.size() < 3) {		
			BSimDdeBacterium p = new BSimDdeBacterium(sim, 
										  new Vector3d(Math.random()*sim.getBound().x,
													   Math.random()*sim.getBound().y,
													   Math.random()*sim.getBound().z));
			if(!p.intersection(bacteria)) bacteria.add(p);
		}

		
		/*********************************************************
		 * Tasks to perform after each tick of the simulation 
		 */
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				// Update all bacteria at each time step
				for(BSimDdeBacterium p : bacteria) {
					p.action();          // This updates the internal DDE equation
					p.updatePosition();  // Move the bacteria (if necessary)
				}
			}		
		});

		
		/*********************************************************
		 * Drawer to display the simulation to the screen 
		 */
		BSimDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {
				for(BSimDdeBacterium p : bacteria) {		
					draw(p,Color.RED);
				}
			}
		}; 
		sim.setDrawer(drawer);	
		
		
		/*********************************************************
		 * Preview the simulation (directly to screen)
		 */
		sim.preview();
	}
}
