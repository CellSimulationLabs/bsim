package BSimSimplest;

import java.awt.Color;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.particle.BSimBacterium;

/**
 * Super-simple example of a BSim simulation. 
 * 
 * Creates and draws a single bacterium which swims around in a fluid environment with classic run-and-tumble motion.
 */
public class BSimSimplest {
	
	public static void main(String[] args) {
		
		// create the simulation object
		BSim sim = new BSim();		
				
		/*********************************************************
		 * Create the bacterium
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
				draw(bacterium,Color.YELLOW);
			}
		});	
		
		// Run the simulation
		sim.preview();
	} 

}
