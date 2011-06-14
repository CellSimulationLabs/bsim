package BSimTracking;

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
 * Simple example of an exporter that will track a individual bacterium and log its (x,y) position.
 */
public class BSimTracking {

	public static void main(String[] args) {

		/*********************************************************
		 * Set the simulation properties
		 */
		BSim sim = new BSim();
		sim.setBound(1000,1000,1000);
		sim.setSimulationTime(30);

		/*********************************************************
		 * Set up the bacterium
		 */
		final BSimBacterium bacterium = new BSimBacterium(sim, new Vector3d(500,500,500));
		
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
		 * Set up the tracker (logger)
		 */
		// Create a new directory for the simulation results
		String resultsDir = BSimUtils.generateDirectoryPath("./results/");			
		
		BSimLogger trackerXY = new BSimLogger(sim, resultsDir + "trackerXY.csv") {
			@Override
			public void during() {
				write(bacterium.getPosition().x+","+bacterium.getPosition().y);
			}
		};
		trackerXY.setDt(0.1);
		sim.addExporter(trackerXY);

		// run the simulation
		sim.export();
	}

}
