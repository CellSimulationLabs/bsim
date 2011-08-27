package BSimParser;

/* This file contains a representation of a simulation and is updated by the BSimParser. */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;

import bsim.*;
import bsim.geometry.*;
import bsim.particle.*;
import bsim.draw.BSimP3DDrawer;
import bsim.export.*;

class BSimFromFile {
	
	// Simulation details
	private BSim sim;
	
	// Objects in the simulation (most are referenced by 'name')
	private HashMap<String, Vector<BSimBacterium>> bacteria;
	private HashMap<String, Vector<BSimParticle>>  particles;
	private HashMap<String, BSimChemicalField>     fields;
	private BSimOBJMesh                            mesh;
	
	// Output related parameters
	private boolean outputData;
	private String  outputPath;
	private String  dataFileName;
	
	// Movie related parameters
	private boolean outputMovie;
	private String  movieFileName;
	private int     movieSpeed;
	private double  movieDt;
	private int     movieWidth;
	private int     movieHeight;
	
	public BSimFromFile () {
		
		// Create the simulation
		sim = new BSim();
		
		// Create empty containers for simulation objects
		bacteria = new HashMap<String, Vector<BSimBacterium>>();
		particles = new HashMap<String, Vector<BSimParticle>>();
		fields = new HashMap<String, BSimChemicalField>();
		mesh = new BSimOBJMesh();
		
		// Defaults for output files
		outputData = true;
		outputMovie = false;
		outputPath = "";
		dataFileName = "bsim_data.txt";
		
		// Defaults for movie related parameters
		movieFileName = "bsim_movie.mov";
		movieSpeed = 1;
		movieDt = 1.0;
		movieWidth = 800;
		movieHeight = 600;
		
		// Our own ticker that knows how to use our object collections
		sim.setTicker(new BSimFromFileTicker());
	}
	
	/** Get the actual BSim simulation */
	public BSim getSim() { return sim; }
	
	/** Add bacterial population to simulation */
	public void addBacteria (String name, Vector<BSimBacterium> newBacteria) { bacteria.put(name, newBacteria); }
	/** Add particle population to simulation */
	public void addParticles (String name, Vector<BSimParticle> newParticles) { particles.put(name, newParticles); }
	/** Add chemical field to simulation */
	public void addChemicalField (String name, BSimChemicalField newField) { fields.put(name, newField); }
	/** Set simulation mesh */
	public void setMesh (String filename) {
		try { mesh.load(filename); }
		catch (Exception e) { System.err.println("Could not load mesh"); }
	}
	
	/** Set if data should be output */
	public void setOutputData (boolean flag) { outputData = flag; }
	/** Set simulation output path */
	public void setOutputPath (String newPath) { outputPath = newPath; }
	/** Set output data file name */
	public void setDataFileName (String newFileName) { dataFileName = newFileName; }
	
	/** Set if movie should be output */
	public void setOutputMovie (boolean flag) { outputMovie = flag; }
	/** Set output movie file name */
	public void setMovieFileName (String newFileName) { movieFileName = newFileName; }
	/** Set output movie speed */
	public void setMovieSpeed (int speed) { movieSpeed = speed; }
	/** Set output movie dt */
	public void setMovieDt (double dt) { movieDt = dt; }
	/** Set output movie width */
	public void setMovieWidth (int width) { movieWidth = width; }
	/** Set output movie height */
	public void setMovieHeight (int height) { movieHeight = height; }
	
	/**
	 * Run the simulation. Generates necessary exporters and runs export() for the simulation.
	 */
	public void run() { 
		
		// Create a data exporter if required
		if (outputData) {
			sim.addExporter(new BSimFromFileExporter(sim, outputPath + dataFileName));
		}
		
		// Create a drawer for the simulation
		BSimFromFileDrawer drawer = new BSimFromFileDrawer(sim, movieWidth, movieHeight);
		sim.setDrawer(drawer);
		
		// Create a movie exporter if required
		if (outputMovie) {
			BSimMovExporter movExporter = new BSimMovExporter(sim, drawer, outputPath + movieFileName);
			movExporter.setSpeed(movieSpeed);
			movExporter.setDt(movieDt);
			sim.addExporter(movExporter);
		}
		
		// Export the results to file and movie (if required)
		sim.export();
	}
	
	
// ----------------------------------------------------------------------------------------	
// We have a set of inner classes that implement the various drawers, exporters and tickers
// for our simulation.
// ----------------------------------------------------------------------------------------		
	
	class BSimFromFileTicker extends BSimTicker {
		@Override
		public void tick() {
			
			// Update all bacteria
			for (Map.Entry<String,Vector<BSimBacterium>> bacPop : bacteria.entrySet()) {
				for(BSimBacterium bacterium : bacPop.getValue()) {
					bacterium.action();
					bacterium.updatePosition();
				}
			}
			
			// Update all particles
			for (Map.Entry<String,Vector<BSimParticle>> partPop : particles.entrySet()) {
				for(BSimParticle particle : partPop.getValue()) {
					particle.action();
					particle.updatePosition();
				}
			}
			
			// Update all chemical fields
			for (Map.Entry<String,BSimChemicalField> chemField : fields.entrySet()) {
				chemField.getValue().update();
			}	
		}
	}
	
	class BSimFromFileExporter extends BSimExporter {
		protected String fileName;
		BSimFromFileExporter(BSim newSim, String newFileName) {
			super(newSim);
			fileName = newFileName;
		}
		@Override
		public void before() {}
		@Override
		public void during() {}
		@Override
		public void after() {
			try{
				// Open the file for writing
				FileWriter writer = new FileWriter(new File(fileName));

				// Export the last positions of each bacterial population
				// Format:
				// PopName1,x1,y1,z1,x2,y2,y2,...
				// PopName2,x1,y1,z1,x2,y2,z2,...
				// ...
				for (Map.Entry<String,Vector<BSimBacterium>> bacPop : bacteria.entrySet()) {

					// Write the population name to file
					writer.write(bacPop.getKey());

					// Cycle through each bacterium in the population and output 3D position
					for(BSimBacterium bacterium : bacPop.getValue()) {
						Vector3d pos = bacterium.getPosition();
						writer.write("," + pos.x + "," + pos.y + "," + pos.z);
					}

					// Move to the next population (on a new line)
					writer.write("\n");
				}

				// Ensure everything has been written and close
				writer.flush();
				writer.close();
			}
			catch (IOException e) {
				System.err.println("I/O error occured writing results to file");
			}
		}
	}
	
	class BSimFromFileDrawer extends BSimP3DDrawer {
		BSimFromFileDrawer (BSim sim, int width, int height) {
			super(sim, width, height);
		}
		@Override
		public void scene(PGraphics3D p3d) {
			// Do drawing here...
		}
	}
}
