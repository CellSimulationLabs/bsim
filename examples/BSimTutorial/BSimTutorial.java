package BSimTutorial;

import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.BSimUtils;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimLogger;
import bsim.export.BSimMovExporter;
import bsim.export.BSimPngExporter;
import bsim.particle.BSimBacterium;

/**
 * An example simulation definition to illustrate the key features of a BSim model.</br>
 */
public class BSimTutorial {

	public static void main(String[] args) {

		/*********************************************************
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
		BSim sim = new BSim();			// New simulation object
		sim.setDt(0.01);				// Global dt (time step)
		sim.setSimulationTime(10);		// Total simulation time [sec]
		sim.setTimeFormat("0.00");		// Time format (for display etc.)
		sim.setBound(100,100,100);		// Simulation boundaries [um]

		/*********************************************************
		 * Step 2: Extend BSimParticle as required and create vectors marked final
		 * As an example let's make a bacteria that turns red upon colliding
		 */				
		class BSimTutorialBacterium extends BSimBacterium {
			// local field for setting whether a collision is occurring
			private boolean collision = false;			

			// Constructor for the BSimTutorialBacterium
			public BSimTutorialBacterium(BSim sim, Vector3d position) {
				super(sim, position); // default radius is 1 micron			
			}

			// What happens in an interaction with another bacterium?
			public void interaction(BSimTutorialBacterium p) {
				// If the bacteria intersect, then set the collision state to 'true'
				if(outerDistance(p) < 0) {
					collision = true;
					p.collision = true;
				}
			}
		}		
		// Set up a list of bacteria that will be present in the simulation
		final Vector<BSimTutorialBacterium> bacteria = new Vector<BSimTutorialBacterium>();
		// Add 100 bacteria to the simulation
		while(bacteria.size() < 100) {		
			// Creates a new bacterium with random position within the boundaries
			BSimTutorialBacterium b = new BSimTutorialBacterium(sim, 
					new Vector3d(Math.random()*sim.getBound().x, 
								Math.random()*sim.getBound().y, 
								Math.random()*sim.getBound().z));
			// If the bacterium doesn't intersect any others then add it to the overall list
			if(!b.intersection(bacteria)) bacteria.add(b);		
		}

		/*********************************************************
		 * Step 3: Implement tick() on a BSimTicker and add the ticker to the simulation	  
		 */
		sim.setTicker(new BSimTicker() {
			
			// This will be called once at each global time step
			@Override
			public void tick() {
				// Check all bacteria in the simulation for intersection with each other
				for(int i = 1; i < bacteria.size(); i++) {
					for(int j = i+1; j < bacteria.size(); j++) {
						bacteria.get(i).interaction(bacteria.get(j));
					}
				}
				
				// Each bacterium performs its action and updates its position
				for(BSimTutorialBacterium b : bacteria) {
					b.action();		
					b.updatePosition();
				}
			}		
		});

		/*********************************************************
		 * Step 4: Implement draw(Graphics) on a BSimDrawer and add the drawer to the simulation 
		 * 
		 * Here we use the BSimP3DDrawer which has already implemented draw(Graphics) to draw boundaries
		 * and a clock but still requires the implementation of scene(PGraphics3D) to draw particles
		 * You can use the draw(BSimParticle, Color) method to draw particles 
		 */
		BSimP3DDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			
			@Override
			public void scene(PGraphics3D p3d) {	
				// loop through all the bacteria and draw them (red if colliding, green if not)
				for(BSimTutorialBacterium b : bacteria) {
					draw(b, b.collision ? Color.RED : Color.GREEN);
				}			
			}
		};
		sim.setDrawer(drawer);			// add the drawer to the simulation object.		

		/*********************************************************
		 * Step 5: Implement before(), during() and after() on BSimExporters and add them to the simulation
		 * Available setters:
		 * 	BSimExporter#setDt()
		 */
		
		// Create a new directory for the simulation results
		String resultsDir = BSimUtils.generateDirectoryPath("./results/");			

		/* 
		 * BSimMovExporter is a concrete BSimExporter for creating Quicktime movies
		 * Available setters:
		 * 	BSimMovExporter#setSpeed()
		 */			
		BSimMovExporter movExporter = new BSimMovExporter(sim, drawer, resultsDir + "BSim.mov");
		movExporter.setDt(0.03);
		sim.addExporter(movExporter);			

		/* 
		 * BSimPngExporter is another concrete BSimExporter for creating png images. 
		 */
		BSimPngExporter pngExporter = new BSimPngExporter(sim, drawer, resultsDir);
		pngExporter.setDt(0.5);
		sim.addExporter(pngExporter);			

		/* 
		 * BSimLogger is an abstract BSimExporter that requires an implementation of during().
		 * It provides the convenience method write() 
		 */
		BSimLogger logger = new BSimLogger(sim, resultsDir + "tutorialExample.csv") {
			
			@Override
			public void before() {
				super.before();
				// Write a header containing the names of variables we will be exporting
				write("time,collisions"); 
			}
			
			@Override
			public void during() {
				// Counter for the number of current collisions
				int collisions = 0;
				
				// Loop through the bacteria and count collisions
				for (BSimTutorialBacterium p : bacteria){
					if(p.collision) collisions++;
				}
				
				// Write the time and number of collisions to file
				write(sim.getFormattedTime()+","+collisions);
			}
		};
		sim.addExporter(logger);

		/*
		 * Add your own exporters by extending BSimExporter like
		 * 
		 * BSimExporter e = new BSimExporter(){}; 
		 *
		 */		

		/*********************************************************
		 * Step 6: Call sim.preview() to preview the scene or sim.export() to set exporters working 
		 */
		sim.preview();

	}
}
