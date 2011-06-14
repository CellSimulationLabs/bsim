package BSimRotationalDiffusion;

import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;

import bsim.BSim;
import bsim.BSimTicker;
import bsim.BSimUtils;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimLogger;
import bsim.particle.BSimBacterium;

/**
 * Example to test Rotational diffusion in a fluid environment.
 */
public class BSimRotationalDiffusion {
	
	public static void main(String[] args) {

		/*********************************************************
		 * Set simulation properties
		 */
		BSim sim = new BSim();
		sim.setDt(0.01);
		sim.setSimulationTime(10);
				
		/*********************************************************
		 * Set up and create the bacteria
		 */
		final int n = 100;		
		final Vector3d startDirection = new Vector3d(1,1,1);		
		final Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>();
		for(int i=0;i<n;i++) {
			BSimBacterium bacterium = new BSimBacterium(sim, new Vector3d(50,50,50));
			bacterium.pEndRunElse(0);
			bacterium.setDirection(new Vector3d(startDirection));
			bacteria.add(bacterium);		
		}
		
		/*********************************************************
		 * Set up the ticker
		 */
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(BSimBacterium bacterium : bacteria) {
					bacterium.action();		
					bacterium.updatePosition();
				}
			}
		});
		
		/*********************************************************
		 * Set up the drawer
		 */
		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {	
				for(BSimBacterium bacterium : bacteria)
					draw(bacterium,Color.GREEN);
			}
		});	
		
		/*********************************************************
		 * Set up exporters
		 */
		//Create a new directory for the simulation results
		String resultsDir = BSimUtils.generateDirectoryPath("./results/");			
		
		sim.addExporter(new BSimLogger(sim, resultsDir+ "bacteriumTheta" + System.currentTimeMillis() +  ".csv") {
			@Override
			public void during() {				
				String o = "";
				for(int i=0;i<n;i++) {
					if(i>0) o += ","; 
					o += bacteria.get(i).getDirection().angle(startDirection)+"";
				}
				write(o);		
			}
		});
		
		// Run the simulation
		sim.export();
				
	}

}
