package bsim.example;

import java.awt.Graphics;

import javax.vecmath.Vector3d;

import processing.core.PGraphics;
import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimDrawer;
import bsim.BSimTicker;
import bsim.export.BSimExporter;
import bsim.export.BSimImageExporter;
import bsim.export.BSimLogger;
import bsim.export.BSimMovieExporter;
import bsim.particle.BSimBacterium;

public class BSimSwimmingExample {
	
	public static void main(String[] args) {
		
		/* Declare final to allow references from anonymous inner classes */  
		final BSim sim = new BSim();		
		sim.setDt(0.1);
		sim.setSimulationTime(5);
		
		/* Add BSimParticles to the simulation */
		BSimBacterium bacterium = new BSimBacterium(sim, new Vector3d(50,50,50), 1, new Vector3d(1,1,1));
		sim.addBacterium(bacterium);	
				
		/* Add a ticker that implements tick(), run each timestep to update particle properties */
		sim.setTicker(new BSimTicker() {
			public void tick() {
				for(BSimBacterium b : sim.getBacteria()) {
					b.action();		
					b.updatePosition();
				}
			}		
		});
		
		/* Add a drawer that implements the draw(Graphics) method for drawing the scene to a graphics object */
		sim.setDrawer(new BSimDrawer(200,200) {
			public void draw(Graphics g) {
				PGraphics p3d = new PGraphics3D();
				p3d.setPrimary(true); 
				p3d.setSize(width, height);
				p3d.beginDraw();

				p3d.sphereDetail(10);
				p3d.noStroke();		
				p3d.background(0, 0, 0);
					
				for(BSimBacterium b : sim.getBacteria()) {
					p3d.translate((float)b.getPosition().x, (float)b.getPosition().y, (float)b.getPosition().z);
					p3d.fill(255, 0, 0);		
					p3d.sphere((float)b.getRadius());
				}

				p3d.endDraw();		
				g.drawImage(p3d.image, 0,0, null);
			}
		});
											
		/* Add some concrete predefined exporters */ 		 
		sim.addExporter(new BSimMovieExporter(sim, "results/BSim.mov"));		
		sim.addExporter(new BSimImageExporter(sim, "results"));			
		/* BSimLogger is an abstract BSimExporter requires the implementation of before() and during() 
		 * It provides the convinience method write() */
		sim.addExporter(new BSimLogger(sim, "results/BSim.log") {
			public void before() {
				write("Let's go!"); 
			}
			public void during() {
				String o = "";
				for (BSimBacterium b : sim.getBacteria())
					o += sim.getTime() + " " + b.getPosition() + " " + b.getMotionState();
				write(o);
			}
		});			
		/* Add your own exporters like sim.addExporter(new BSimExporter(sim) {}); */
		
				
		/* sim.preview() to preview the scene, sim.export() to set exporters working */
		sim.preview();	
		
	}
}
