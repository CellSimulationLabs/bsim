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
		
		/* Declare final to allow references from anonymous inner classes */  
		final BSim sim = new BSim();		
		sim.setDt(0.1);
		sim.setSimulationTime(6);
		sim.setTimeFormat("0.00");
		
		final Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>();
		bacteria.add(new BSimBacterium(sim, new Vector3d(50,50,50), 1, new Vector3d(1,1,1)));
		bacteria.add(new BSimBacterium(sim, new Vector3d(55,55,55), 1, new Vector3d(1,1,1)));
		bacteria.add(new BSimBacterium(sim, new Vector3d(60,60,60), 1, new Vector3d(1,1,1)));
				
		/* Add a ticker that implements tick(), run each timestep to update particle properties */
		sim.setTicker(new BSimTicker() {
			public void tick() {
				for(BSimBacterium b : bacteria) {
					b.action();		
					b.updatePosition();
				}
			}		
		});
		
		/* Add a drawer that implements the draw(Graphics) method for drawing the scene to a graphics object */
		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			public void particles(PGraphics3D p3d) {	
				for(BSimBacterium b : bacteria) {
					p3d.translate((float)b.getPosition().x, (float)b.getPosition().y, (float)b.getPosition().z);
					p3d.fill(255, 0, 0);		
					p3d.sphere((float)b.getRadius());
				}
			}
		});
											
		/* Add some exporters */
		BSimMovieExporter movieExporter = new BSimMovieExporter(sim, "results/BSim.mov");
		movieExporter.setSpeed(2);
		sim.addExporter(movieExporter);			
		
		BSimImageExporter imageExporter = new BSimImageExporter(sim, "results");
		imageExporter.setDt(0.5);
		sim.addExporter(imageExporter);			
		
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
				
		/* sim.preview() to preview the scene, sim.export() to set exporters working */
		sim.preview();	
		
	}
}
