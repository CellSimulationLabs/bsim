package BSimReplication;

import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.particle.BSimBacterium;

/**
 * Growth and replication test.</br>
 * 
 * Simple example of growing and replicating population of bacteria swimming in an environment.
 * Visualisation and testing of built in {@link BSimBacterium} growth/replication methods.
 */
public class BSimReplication {

	public static void main(String[] args) {

		/*********************************************************
		 * Set simulation properties
		 */
		BSim sim = new BSim();
		sim.setDt(0.5);
						
		/*********************************************************
		 * Set up the bacteria
		 */
		final Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>();
		final Vector<BSimBacterium> children = new Vector<BSimBacterium>();
		while(bacteria.size() < 1) {		
			BSimBacterium b = new BSimBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
			b.setRadius();
			b.setSurfaceAreaGrowthRate(0.05);
			b.setChildList(children);
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
			}		
		});

		/*********************************************************
		 * Set up the drawer
		 */
		BSimP3DDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {						
				for(BSimBacterium b : bacteria) {
					draw(b, Color.GREEN);
				}			
			}
		};
		sim.setDrawer(drawer);				

		// Run the simulation
		sim.preview();

	}
}
