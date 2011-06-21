package BSimRunTumble;

import java.awt.Color;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.BSimUtils;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimLogger;
import bsim.particle.BSimBacterium;

/**
 * Example to test whether the distributions of the run durations and tumble angle are correct.
 */
public class BSimRunTumble {

	public static void main(String[] args) {
		
		/*********************************************************
		 * Set the simulation properties
		 */
		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setSimulationTime(5000);
		sim.setTimeFormat("0.00");
		sim.setBound(100,100,100);
				
		/*********************************************************
		 * Set up and create the bacteria
		 * 
		 * In this case the default, built in, BSimBacterium
		 */
		final BSimBacterium bacterium = new BSimBacterium(sim, new Vector3d(50,50,50));	
		
		/*********************************************************
		 * Set up the ticker
		 */
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				bacterium.action();		
				bacterium.updatePosition();
			}
		});
		
		/*********************************************************
		 * Set up the drawer
		 */
		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {							
				draw(bacterium,Color.GREEN);
			}
		});	
		
		/*********************************************************
		 * Set up and create loggers for the run and tumble state data
		 */
		// Create a new directory for the simulation results	 
		String resultsDir = BSimUtils.generateDirectoryPath("./results/");			
		
		class BSimRunTumbleLogger extends BSimLogger {
			protected BSimBacterium.MotionState lastState;
			protected double startTimeOfLastRun;
			protected double startTimeOfLastTumble;
			protected Vector3d directionAtStartOfLastRun;
			protected Vector3d directionAtEndOfLastRun;
			
			public BSimRunTumbleLogger(BSim sim, String filename) {
				super(sim, filename);
			}
			
			@Override
			public void before() {
				super.before();
				assert(bacterium.getMotionState() == BSimBacterium.MotionState.RUNNING);
				lastState = BSimBacterium.MotionState.RUNNING; 
				directionAtStartOfLastRun = new Vector3d(bacterium.getDirection());
				startTimeOfLastRun = 0;
				startTimeOfLastTumble = 0;
			}
			@Override
			public void during() {	
				lastState = bacterium.getMotionState();			
			}
			
			public void set() {
				if (lastState == BSimBacterium.MotionState.TUMBLING && bacterium.getMotionState() == BSimBacterium.MotionState.RUNNING) {
					startTimeOfLastRun = sim.getTime();
					directionAtStartOfLastRun = new Vector3d(bacterium.getDirection());
				} else if (lastState == BSimBacterium.MotionState.RUNNING && bacterium.getMotionState() == BSimBacterium.MotionState.TUMBLING) {
					startTimeOfLastTumble = sim.getTime();
					directionAtEndOfLastRun = new Vector3d(bacterium.getDirection());
				}				
			}
		};
		
		
		sim.addExporter(new BSimRunTumbleLogger(sim, resultsDir + "tumbleAngle.csv") {					
			@Override
			public void during() {								
				set();
				if (lastState == BSimBacterium.MotionState.TUMBLING && bacterium.getMotionState() == BSimBacterium.MotionState.RUNNING) {
					write(Math.toDegrees(bacterium.getDirection().angle(directionAtEndOfLastRun))+"");
				}	
				super.during();					
			}
		});
		
		sim.addExporter(new BSimRunTumbleLogger(sim, resultsDir + "runAngle.csv") {					
			@Override
			public void during() {								
				set();
				if (lastState == BSimBacterium.MotionState.RUNNING && bacterium.getMotionState() == BSimBacterium.MotionState.TUMBLING) {
					write(Math.toDegrees(bacterium.getDirection().angle(directionAtStartOfLastRun))+"");
				}	
				super.during();					
			}
		});
		
		sim.addExporter(new BSimRunTumbleLogger(sim, resultsDir + "runDuration.csv") {		
			@Override
			public void during() {	
				set();
				if (lastState == BSimBacterium.MotionState.RUNNING && bacterium.getMotionState() == BSimBacterium.MotionState.TUMBLING) {
					// end of run
					write(sim.getTime()-startTimeOfLastRun+"");
				}			
				super.during();					
			}
		});
		
		sim.addExporter(new BSimRunTumbleLogger(sim, resultsDir + "tumbleDuration.csv") {		
			@Override
			public void during() {	
				set();
				if (lastState == BSimBacterium.MotionState.TUMBLING && bacterium.getMotionState() == BSimBacterium.MotionState.RUNNING) {
					// end of tumble
					write(sim.getTime()-startTimeOfLastTumble+"");
				}			
				super.during();					
			}
		});
		
		// Run the simulation
		sim.export();
	}


}
