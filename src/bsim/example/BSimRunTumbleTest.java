package bsim.example;

import java.awt.Color;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimLogger;
import bsim.particle.BSimBacterium;

/**
 * Tests whether the distributions of the run durations and tumble angle are correct.
 */
public class BSimRunTumbleTest {

	public static void main(String[] args) {

		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setSimulationTime(5000);
		sim.setTimeFormat("0.00");
		sim.setBound(100,100,100);
				
		final BSimBacterium bacterium = new BSimBacterium(sim, new Vector3d(50,50,50));
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				bacterium.action();		
				bacterium.updatePosition();
			}
		});
		
		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {							
				draw(bacterium,Color.GREEN);
			}
		});	
		
		class BSimRunTumbleLogger extends BSimLogger {
			protected BSimBacterium.MotionState lastState;
			
			public BSimRunTumbleLogger(BSim sim, String filename) {
				super(sim, filename);
			}
			
			@Override
			public void before() {
				super.before();
				assert(bacterium.getMotionState() == BSimBacterium.MotionState.RUNNING);
				lastState = BSimBacterium.MotionState.RUNNING; 
			}
			@Override
			public void during() {								
				lastState = bacterium.getMotionState();					
			}
		};
		
		
		
		sim.addExporter(new BSimRunTumbleLogger(sim, "results/tumbleAngle.csv") {
			private Vector3d directionAtStartOfLastRun;
			
			@Override
			public void before() {
				super.before();
				directionAtStartOfLastRun = new Vector3d(bacterium.getDirection());				
				write("tumbleAngle,sampledTumbleAngle"); // Distributions should be the same
			}
			@Override
			public void during() {								
				if (lastState == BSimBacterium.MotionState.TUMBLING && bacterium.getMotionState() == BSimBacterium.MotionState.RUNNING) {
					write(Math.toDegrees(bacterium.getDirection().angle(directionAtStartOfLastRun))+","+Math.toDegrees(bacterium.tumbleAngle()));
					directionAtStartOfLastRun = new Vector3d(bacterium.getDirection());
				}	
				super.during();					
			}
		});
		
		sim.addExporter(new BSimRunTumbleLogger(sim, "results/runDuration.csv") {
			private double startTimeOfLastRun;
			
			@Override
			public void before() {
				super.before();
				startTimeOfLastRun = 0;
				assert (bacterium.pEndRun() == 1);
				write("runDuration"); // Should be exponentially distributed with mean 1
			}
			@Override
			public void during() {								
				if (lastState == BSimBacterium.MotionState.RUNNING && bacterium.getMotionState() == BSimBacterium.MotionState.TUMBLING) {
					// end of run
					write(sim.getTime()-startTimeOfLastRun+"");
				} else if (lastState == BSimBacterium.MotionState.TUMBLING && bacterium.getMotionState() == BSimBacterium.MotionState.RUNNING) {
					// start of run
					startTimeOfLastRun = sim.getTime();
				}				
				super.during();					
			}
		});
		

		
		sim.export();

	}


}
