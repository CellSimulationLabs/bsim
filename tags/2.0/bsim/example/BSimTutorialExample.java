package bsim.example;

import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimLogger;
import bsim.export.BSimMovExporter;
import bsim.export.BSimPngExporter;
import bsim.particle.BSimBacterium;

public class BSimTutorialExample {

	public static void main(String[] args) {

		/*
		 * Step 1: Create a new simulation object and set environmental properties
		 * Available setters:
		 * 	BSim#setDt() defaults to 0.01
		 * 	BSim#setSimulatonTime() 
		 * 	BSim#setTimeFormat() defaults to "0.00"
		 * 	BSim#setBound() defaults to (100,100,100)
		 * 	BSim#setSolid() defaults to {false,false,false} and the particles wrap
		 * 	BSim#setVisc() defaults to 2.7e-3 Pa s
		 * 	BSim#setTemperature() defaults to 305 K
		 */
		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setSimulationTime(10);
		sim.setTimeFormat("0.00");
		sim.setBound(100,100,100);

		/*
		 * Step 2: Extend BSimParticle as required and create vectors marked final
		 * As an example let's make a bacteria that turns red upon colliding
		 */				
		class BSimTutorialBacterium extends BSimBacterium {
			private boolean collision = false;			

			public BSimTutorialBacterium(BSim sim, Vector3d position) {
				super(sim, position); // default radius is 1 micron			
			}

			public void interaction(BSimTutorialBacterium p) {
				if(outerDistance(p) < 0) {
					collision = true;
					p.collision = true;
				}
			}
		}		
		final Vector<BSimTutorialBacterium> bacteria = new Vector<BSimTutorialBacterium>();		
		while(bacteria.size() < 100) {		
			BSimTutorialBacterium b = new BSimTutorialBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
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

				for(BSimTutorialBacterium b : bacteria) {
					b.action();		
					b.updatePosition();
				}
			}		
		});

		/* 
		 * Step 4: Implement draw(Graphics) on a BSimDrawer and add the drawer to the simulation 
		 * 
		 * Here we use the BSimP3DDrawer which has already implemented draw(Graphics) to draw boundaries
		 * and a clock but still requires the implementation of scene(PGraphics3D) to draw particles
		 * You can use the draw(BSimParticle, Color) method to draw particles 
		 */
		BSimP3DDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {						
				for(BSimTutorialBacterium b : bacteria) {
					draw(b, b.collision ? Color.RED : Color.GREEN);
				}			
			}
		};
		sim.setDrawer(drawer);				

		/* 
		 * Step 5: Implement before(), during() and after() on BSimExporters and add them to the simulation
		 * Available setters:
		 * 	BSimExporter#setDt()
		 * 
		 * BSimMovExporter is a concrete BSimExporter for creating Quicktime movies
		 * Available setters:
		 * 	BSimMovExporter#setSpeed()
		 */			
		BSimMovExporter movExporter = new BSimMovExporter(sim, drawer, "results/BSim.mov");
		movExporter.setDt(0.03);
		sim.addExporter(movExporter);			

		/* BSimPngExporter is another concrete BSimExporter for creating png images */
		BSimPngExporter pngExporter = new BSimPngExporter(sim, drawer, "results");
		pngExporter.setDt(0.5);
		sim.addExporter(pngExporter);			

		/* BSimLogger is an abstract BSimExporter that requires an implementation of during() 
		 * It provides the convinience method write() */
		BSimLogger logger = new BSimLogger(sim, "results/tutorialExample.csv") {
			@Override
			public void before() {
				super.before();
				write("time,collisions"); 
			}
			@Override
			public void during() {
				int collisions = 0;
				for (BSimTutorialBacterium p : bacteria)
					if(p.collision) collisions++;
				write(sim.getFormattedTime()+","+collisions);
			}
		};
		sim.addExporter(logger);

		/* Add your own exporters by extending BSimExporter like
		 * BSimExporter e = new BSimExporter(){}; */		

		/* Step 6: Call sim.preview() to preview the scene or sim.export() to set exporters working */
		sim.preview();

	}




}
