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

public class BSimTutorialExample {

	public static void main(String[] args) {

		/*
		 * Step 1: Create a new simulation object
		 * Available setters:
		 * 	BSim#setDt()
		 * 	BSim#setSimulatonTime()
		 * 	BSim#setTimeFormat()
		 * 	BSim#setBound()
		 * 	BSim#setVisc() defaults to 1e-3 Pa s
		 * 	BSim#setTemperature() defaults to 300 K
		 */
		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setSimulationTime(100);
		sim.setTimeFormat("0.00");
		sim.setBound(new Vector3d(100,100,100));

		/*
		 * Step 2: Extend BSimParticle as required and create vectors marked final
		 * As an example let's make bacteria that turn red upon colliding
		 */				
		class BSimCollidingBacterium extends BSimBacterium {
			boolean collision = false;	

			public BSimCollidingBacterium(BSim sim, Vector3d position, Vector3d direction) {
				super(sim, position, direction);
			}

			public void interaction(BSimCollidingBacterium b) {
				if(outerDistance(b) < 0) {
					collision = true;
					b.collision = true;
				}
			}	
		}		
		final Vector<BSimCollidingBacterium> bacteria = new Vector<BSimCollidingBacterium>();		
		while(bacteria.size() < 20) {		
			BSimCollidingBacterium b = new BSimCollidingBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z), new Vector3d(Math.random(),Math.random(),Math.random()));
			if(!b.intersection(bacteria)) bacteria.add(b);		
		}

		/* 
		 * Step 3: Implement tick() on a BSimTicker and add the ticker to the simulation	  
		 */
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(int i = 1; i < bacteria.size(); i++)
					for(int j = i+1; j < bacteria.size(); j++)
						bacteria.get(i).interaction(bacteria.get(j));

				for(BSimCollidingBacterium b : bacteria) {
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
			@Override
			public void draw(PGraphics3D p3d) {							
				for(BSimCollidingBacterium b : bacteria) {
					p3d.pushMatrix();					
					Vector3d position = b.getPosition();
					p3d.translate((float)position.x, (float)position.y, (float)position.z);
					if(!b.collision)
						p3d.fill(0,255,0); // green
					else
						p3d.fill(255,0,0); // red!
					p3d.sphere((float)b.getRadius());
					p3d.popMatrix();
				}			
			}
		});				

		/* 
		 * Step 5: Implement before(), during() and after() on BSimExporters and add them to the simulation
		 * Available setters:
		 * 	BSimExporter#setDt(Double)
		 * 
		 * BSimMovieExporter is a concrete BSimExporter for creating Quicktime movies
		 * Uses the drawer defined above
		 * Available setters:
		 * 	BSimMovieExporter#setSpeed()
		 */			
		BSimMovieExporter movieExporter = new BSimMovieExporter(sim, "results/BSim.mov");
		movieExporter.setSpeed(2);
		sim.addExporter(movieExporter);			

		/* BSimImageExporter is another concrete BSimExporter for creating images 
		 * Uses the drawer defined above */
		BSimImageExporter imageExporter = new BSimImageExporter(sim, "results");
		imageExporter.setDt(0.5);
		sim.addExporter(imageExporter);			

		/* BSimLogger is an abstract BSimExporter that requires an implementation of during() 
		 * It provides the convinience method write() */
		BSimLogger logger = new BSimLogger(sim, "results/BSim.log") {
			@Override
			public void before() {
				super.before();
				write("time,collisions"); 
			}
			@Override
			public void during() {
				String o = sim.getTime();
				int collisions = 0;
				for (BSimCollidingBacterium b : bacteria)
					if(b.collision) collisions++;
				write(o+","+collisions);
			}
		};
		sim.addExporter(logger);

		/* Add your own exporters by extending BSimExporter like
		 * BSimExporter e = new BSimExporter(){}; */		

		/* Step 6: Call sim.preview() to preview the scene or sim.export() to set exporters working */
		sim.preview();

	}




}
