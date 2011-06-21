package BSimVesiculation;

import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimDrawer;
import bsim.draw.BSimP3DDrawer;
import bsim.particle.BSimBacterium;
import bsim.particle.BSimVesicle;

/**
 * A simple example to test built-in vesiculation.
 */
public class BSimVesiculation {
	
	public static void main(String[] args) {

		/*********************************************************
		 * Set up the simulation
		 */
		BSim sim = new BSim();	
			
		/*********************************************************
		 * Set up the lists for storing vesicles and add bacteria to the simulation.
		 */
		final Vector<BSimVesicle> vesicles = new Vector<BSimVesicle>();
		final Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>();		
		final Vector<BSimBacterium> children = new Vector<BSimBacterium>();
		while(bacteria.size() < 10) {		
			BSimBacterium b = new BSimBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
			b.setRadius();
			b.setSurfaceAreaGrowthRate();
			b.setChildList(children);
			b.pVesicle(0.2);
			b.setVesicleList(vesicles);
			bacteria.add(b);		
		}
		
		/*********************************************************
		 * Set up the ticker
		 */
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(BSimBacterium b : bacteria) {
					b.action();		
					b.updatePosition();
				}
				bacteria.addAll(children);
				children.clear();
				for(BSimVesicle vesicle : vesicles) {
					vesicle.action();	
					vesicle.updatePosition();		
				}
			}
		});
		
		/*********************************************************
		 * Set up the drawer
		 */
		BSimDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {							
				for(BSimBacterium b : bacteria) {
					draw(b,Color.GREEN);
				}
				for(BSimVesicle vesicle : vesicles)
					draw(vesicle,Color.RED);
			}
		};	
		sim.setDrawer(drawer);	
		
		// reun the simulation
		sim.preview();
	}

}
