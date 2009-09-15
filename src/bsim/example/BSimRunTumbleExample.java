package bsim.example;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimExerter;
import bsim.BSimParticle;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.exert.BSimFlagella;
import bsim.exert.BSimFlagella.State;
import bsim.export.BSimLogger;

/**
 * Tests BSimRunTumbleMixin to see whether the distributions of the run durations
 * and tumble angle are correct
 */
public class BSimRunTumbleExample {

	public static void main(String[] args) {

		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setSimulationTime(5000);
		sim.setTimeFormat("0.00");
		sim.setBound(100,100,100);
				
		class BSimRunningTumblingBacterium extends BSimParticle  {			
			BSimFlagella flagella = new BSimFlagella(sim, this);
											
			public BSimRunningTumblingBacterium(BSim sim, Vector3d position) {
				super(sim, position, 1);
			}

			public void action() {
				flagella.exert();				
			}
			
			public void setLambdaBundledApart(double d) { flagella.setLambdaBundledApart(d); }
			public void setState(BSimFlagella.State s) { flagella.setState(s); }
			public BSimFlagella.State getState() { return flagella.getState(); }			
			public Vector3d getBundleDirection() { return flagella.getBundleDirection(); }			
			public double tumbleAngle() { return flagella.tumbleAngle(); }
		}
		final BSimRunningTumblingBacterium runningTumblingBacterium = new BSimRunningTumblingBacterium(sim, new Vector3d(50,50,50));

		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				runningTumblingBacterium.action();		
				runningTumblingBacterium.updatePosition();
			}
		});
		
		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void draw(PGraphics3D p3d) {							
					p3d.pushMatrix();					
					Vector3d position = runningTumblingBacterium.getPosition();
					p3d.translate((float)position.x, (float)position.y, (float)position.z);		
					p3d.fill(0,255,0);
					p3d.sphere((float)runningTumblingBacterium.getRadius());
					p3d.popMatrix();						
			}
		});	
		
		class BSimRunTumbleLogger extends BSimLogger {
			protected BSimFlagella.State lastState;
			
			public BSimRunTumbleLogger(BSim sim, String filename) {
				super(sim, filename);
			}
			
			@Override
			public void before() {
				super.before();
				runningTumblingBacterium.setState(BSimFlagella.State.BUNDLED);				
				lastState = BSimFlagella.State.BUNDLED; 
			}
			@Override
			public void during() {								
				lastState = runningTumblingBacterium.getState();					
			}
		};
		
		
		
		sim.addExporter(new BSimRunTumbleLogger(sim, "results/tumbleAngle.csv") {
			private Vector3d bundleDirectionAtStartOfLastRun;
			
			@Override
			public void before() {
				super.before();
				bundleDirectionAtStartOfLastRun = new Vector3d(runningTumblingBacterium.getBundleDirection());
				/* Distributions should be the same */
				write("tumbleAngle,sampledTumbleAngle");
			}
			@Override
			public void during() {								
				if (lastState == BSimFlagella.State.APART && runningTumblingBacterium.getState() == BSimFlagella.State.BUNDLED) {
					write(Math.toDegrees(runningTumblingBacterium.getBundleDirection().angle(bundleDirectionAtStartOfLastRun))+","+Math.toDegrees(runningTumblingBacterium.tumbleAngle()));
					bundleDirectionAtStartOfLastRun = new Vector3d(runningTumblingBacterium.getBundleDirection());
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
				runningTumblingBacterium.setLambdaBundledApart(1);
				/* Should be exponentially distributed with mean 1 */
				write("runDuration");
			}
			@Override
			public void during() {								
				if (lastState == BSimFlagella.State.BUNDLED && runningTumblingBacterium.getState() == BSimFlagella.State.APART) {
					// end of run
					write(sim.getTime()-startTimeOfLastRun+"");
				} else if (lastState == BSimFlagella.State.APART && runningTumblingBacterium.getState() == BSimFlagella.State.BUNDLED) {
					// start of run
					startTimeOfLastRun = sim.getTime();
				}				
				super.during();					
			}
		});
		

		
		sim.export();

	}


}
