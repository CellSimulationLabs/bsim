package bsim.example;

import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimImageExporter;
import bsim.export.BSimLogger;
import bsim.export.BSimMovieExporter;
import bsim.particle.BSimBacterium;

public class BSimSwimmingExample {
	
	public static void main(String[] args) {
		
		/*
		 * Step 1: Create a new simulation object
		 * Available setters:
		 * 	BSim#setDt()
		 * 	BSim#setSimulatonTime()
		 * 	BSim#setTimeFormat()
		 * 	BSim#setBound()
		 * 	BSim#setVisc() defaults to 1e-3 Pa s
		 */
		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setSimulationTime(0.5);
		sim.setTimeFormat("0.00");
		sim.setBound(new Vector3d(100,100,100));
		
		/*
		 * Step 2: Create BSimParticles marked final
		 */		
		final Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>();		
		while(bacteria.size() < 100) {		
			BSimBacterium b = new BSimBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z), new Vector3d(Math.random(),Math.random(),Math.random()));
			if(!b.intersection(bacteria)) bacteria.add(b);		
		}
						
		/* 
		 * Step 3: Implement tick() on a BSimTicker and add the ticker to the simulation	  
		 */
		sim.setTicker(new BSimTicker() {
			public void tick() {
				for(BSimBacterium b : bacteria) {
					b.action();		
					b.updatePosition();
				}
			}		
		});
		
		/* 
		 * Step 4: Implement draw(Graphics) on a BSimDrawer and add the drawer to the simulation 
		 * 
		 * Here we use the BSimP3DDrawer which has already implemented draw(Graphics) to draw boundaries
		 * and a clock but still requires the implementation of draw(PGraphics3D) to draw particles 
		 */
		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			public void draw(PGraphics3D p3d) {							
				for(BSimBacterium b : bacteria) {
					p3d.pushMatrix();					
					Vector3d position = b.getPosition();
					p3d.translate((float)position.x, (float)position.y, (float)position.z);
					p3d.fill(0,255,0);		
					p3d.sphere((float)b.getRadius());
					p3d.popMatrix();
				}			
			}
		});				
											
		/* 
		 * Step 6: Implement before(), during() and after() on BSimExporters and add them to the simulation
		 * Available setters:
		 * 	BSimExporter#setDt(Double)
		 * 
		 * BSimMovieExporter is a concrete BSimExporter for creating Quicktime movies
		 * Uses the drawer defined above
		 * Available setters:
		 * 	BSimMovieExporter#setSpeed()
		 */			
		BSimMovieExporter movieExporter = new BSimMovieExporter(sim, "results/BSim.mov");
		movieExporter.setSpeed(5);
//		sim.addExporter(movieExporter);			
		
		/* BSimImageExporter is another concrete BSimExporter for creating images 
		 * Uses the drawer defined above */
		BSimImageExporter imageExporter = new BSimImageExporter(sim, "results");
		imageExporter.setDt(0.5);
//		sim.addExporter(imageExporter);			
		
		/* BSimLogger is an abstract BSimExporter that requires an implementation of during() 
		 * It provides the convinience method write() */
		BSimLogger logger = new BSimLogger(sim, "results/BSim.log") {
			public void before() {
				super.before();
				write("Let's go!"); 
			}
			public void during() {
				String o = "";
				for (BSimBacterium b : bacteria)
					o += sim.getTime() + " " + b.getPosition() + " " + b.getMotionState();
				write(o);
			}
		};
//		sim.addExporter(logger);
		
		/* Add your own exporters by extending BSimExporter like
		 * BSimExporter e = new BSimExporter(){}; */		
				
		/* Step 7: Call sim.preview() to preview the scene or sim.export() to set exporters working */
		sim.preview();
		
	}
}
