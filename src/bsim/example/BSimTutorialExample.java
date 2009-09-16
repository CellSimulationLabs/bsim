package bsim.example;

import java.awt.Color;
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
		final Vector<BSimTutorialBacterium> tutorialParticles = new Vector<BSimTutorialBacterium>();		
		while(tutorialParticles.size() < 100) {		
			BSimTutorialBacterium p = new BSimTutorialBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
			if(!p.intersection(tutorialParticles)) tutorialParticles.add(p);		
		}

		/* 
		 * Step 3: Implement tick() on a BSimTicker and add the ticker to the simulation	  
		 */
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(int i = 1; i < tutorialParticles.size(); i++)
					for(int j = i+1; j < tutorialParticles.size(); j++)
						tutorialParticles.get(i).interaction(tutorialParticles.get(j));

				for(BSimTutorialBacterium p : tutorialParticles) {
					p.action();		
					p.updatePosition();
				}
			}		
		});

		/* 
		 * Step 4: Implement draw(Graphics) on a BSimDrawer and add the drawer to the simulation 
		 * 
		 * Here we use the BSimP3DDrawer which has already implemented draw(Graphics) to draw boundaries
		 * and a clock but still requires the implementation of draw(PGraphics3D) to draw particles
		 * You can use the draw(BSimParticle, Color) method to draw particles 
		 */
		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void draw(PGraphics3D p3d) {						
				for(BSimTutorialBacterium p : tutorialParticles) {
					draw(p, p.collision ? Color.RED : Color.GREEN);
				}			
			}
		});				

		/* 
		 * Step 5: Implement before(), during() and after() on BSimExporters and add them to the simulation
		 * Available setters:
		 * 	BSimExporter#setDt()
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
		BSimLogger logger = new BSimLogger(sim, "results/tutorialExample.csv") {
			@Override
			public void before() {
				super.before();
				write("time,collisions"); 
			}
			@Override
			public void during() {
				int collisions = 0;
				for (BSimTutorialBacterium p : tutorialParticles)
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
