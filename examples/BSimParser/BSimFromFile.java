package BSimParser;

/* This file contains a representation of a simulation and is updated by the BSimParser. */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimExporter;
import bsim.export.BSimMovExporter;
import bsim.geometry.BSimCollision;
import bsim.geometry.BSimOBJMesh;
import bsim.particle.BSimBacterium;

class BSimFromFile {
	
	// Simulation details
	private BSim sim;
	
	// Objects in the simulation (most are referenced by 'name')
	private HashMap<String, Vector<BSimFromFileBacterium>> bacteria;
	private HashMap<String, Vector<BSimFromFileParticle>>  particles;
	private HashMap<String, BSimFromFileChemicalField>     fields;
	private BSimOBJMesh                                    mesh;
	
	// Output related parameters
	private boolean outputData;
	private String  outputPath;
	private String  dataFileName;
	private boolean	previewMode;
	
	// Movie related parameters
	private boolean outputMovie;
	private String  movieFileName;
	private int     movieSpeed;
	private double  movieDt;
	private int     movieWidth;
	private int     movieHeight;
	
	// Rendering parameters
	private Vector3f cameraPos;
	
	public BSimFromFile () {
		
		// Create the simulation
		sim = new BSim();
		
		// Create empty containers for simulation objects
		bacteria = new HashMap<String, Vector<BSimFromFileBacterium>>();
		particles = new HashMap<String, Vector<BSimFromFileParticle>>();
		fields = new HashMap<String, BSimFromFileChemicalField>();
		mesh = new BSimOBJMesh();
		
		// Defaults for output files
		outputData = true;
		outputMovie = false;
		outputPath = "";
		dataFileName = "bsim_data.txt";
		previewMode = false;
		
		// Defaults for movie related parameters
		movieFileName = "bsim_movie.mov";
		movieSpeed = 1;
		movieDt = 1.0;
		movieWidth = 800;
		movieHeight = 600;
		
		// Defaults for rendering based parameters
		cameraPos = new Vector3f(0.0f, 0.0f, 0.0f);
		
		// Our own ticker that knows how to use our object collections
		sim.setTicker(new BSimFromFileTicker());
	}
	
	/** Get the actual BSim simulation */
	public BSim getSim () { return sim; }
	
	/** Add bacterial population to simulation */
	public void addBacteria (String name, Vector<BSimFromFileBacterium> newBacteria) { bacteria.put(name, newBacteria); }
	/** Add particle population to simulation */
	public void addParticles (String name, Vector<BSimFromFileParticle> newParticles) { particles.put(name, newParticles); }
	/** Add chemical field to simulation */
	public void addChemicalField (String name, BSimFromFileChemicalField newField) { fields.put(name, newField); }
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
	/** Set state of preview mode */
	public void setPreviewMode (boolean previewModeState) { previewMode = previewModeState; }
	
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
	/** Set output movie translation */
	public void setMovieCameraPosition (Vector3f pos) { cameraPos = pos; }
	
	/**
	 * Run the simulation. Generates necessary exporters and runs export() for the simulation.
	 */
	public void run () { 
		
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
		if(previewMode){
			sim.preview();
		} else {
			sim.export();
		}
	}
	
	
	/**
	 * Assign to the bacteria any necessary chemical fields
	 */
	public void assignBacteriaChemicalFieldsFromNames() {
		for (Map.Entry<String,Vector<BSimFromFileBacterium>> bacPop : bacteria.entrySet()) {
			for(BSimFromFileBacterium bacterium : bacPop.getValue()) {
				if(fields.get((bacterium.getChemotaxisGoalFieldName())) != null){
					bacterium.setGoal(fields.get((bacterium.getChemotaxisGoalFieldName())));
				}
				if(fields.get((bacterium.getChemicalInputName())) != null){
					bacterium.setInput(fields.get((bacterium.getChemicalInputName())));
				}
				if(fields.get((bacterium.getChemicalOutputName())) != null){
					bacterium.setOutput(fields.get((bacterium.getChemicalOutputName())));
				}
			}
		}
	}
	
	
// ----------------------------------------------------------------------------------------	
// We have a set of inner classes that implement the various drawers, exporters and tickers
// for our simulation.
// ----------------------------------------------------------------------------------------		
	
	class BSimFromFileTicker extends BSimTicker {
		@Override
		public void tick () {
			// Update all bacteria
			for (Map.Entry<String, Vector<BSimFromFileBacterium>> bacPop : bacteria.entrySet()) {
				for(BSimBacterium bacterium : bacPop.getValue()) {
					bacterium.action();
					// Collisions with particles
					for (Map.Entry<String, Vector<BSimFromFileParticle>> partPop : particles.entrySet()) {
						for(BSimFromFileParticle particle : partPop.getValue()) {
							if(bacterium.outerDistance(particle) < 0) {
								// Forces in both directions
								bacterium.logReaction(particle, 1);
								particle.logReaction(bacterium, 1);
							}
						}
					}
					if (mesh != null) {
						BSimCollision.collideAndRepel(bacterium, mesh);
					}
					bacterium.updatePosition();
				}
			}
			
			// Update all particles
			for (Map.Entry<String, Vector<BSimFromFileParticle>> partPop : particles.entrySet()) {
				for(BSimFromFileParticle particle : partPop.getValue()) {
					particle.action();
					if (mesh != null) {
						BSimCollision.collideAndRepel(particle, mesh);
					}
					particle.updatePosition();
				}
			}
			
			// Update all chemical fields
			for (Map.Entry<String, BSimFromFileChemicalField> chemField : fields.entrySet()) {
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
		public void before () {}
		@Override
		public void during () {}
		@Override
		public void after () {
			try{
				// Open the file for writing
				FileWriter writer = new FileWriter(new File(fileName));

				// Write the bacteria section
				writer.write("Bacteria\n");
				
				// Export the last positions of each bacterial population
				// Format:
				// PopName1,x1,y1,z1,x2,y2,y2,...
				// PopName2,x1,y1,z1,x2,y2,z2,...
				// ...
				for (Map.Entry<String,Vector<BSimFromFileBacterium>> bacPop : bacteria.entrySet()) {

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
				
				// Write the particles section
				writer.write("Particles\n");
				
				// Export the last positions of each particle population
				// Format:
				// PopName1,x1,y1,z1,x2,y2,y2,...
				// PopName2,x1,y1,z1,x2,y2,z2,...
				// ...
				for (Map.Entry<String,Vector<BSimFromFileParticle>> partPop : particles.entrySet()) {

					// Write the population name to file
					writer.write(partPop.getKey());

					// Cycle through each particle in the population and output 3D position
					for(BSimFromFileParticle particle : partPop.getValue()) {
						Vector3d pos = particle.getPosition();
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
		public void scene (PGraphics3D p3d) {
			
			// Allow users to specify the eye position, always centred on middle of bounds
			p3d.camera(cameraPos.x, cameraPos.y, cameraPos.z, 
					(float)boundCentre.x,(float)boundCentre.y,(float)boundCentre.z, 
					0, 1, 0);
			
			// Draw chemical fields
			for (Map.Entry<String,BSimFromFileChemicalField> fieldKV : fields.entrySet()) {
				BSimFromFileChemicalField field = fieldKV.getValue();
				draw(field, field.getColor(), field.getAlphaPerUnit(), field.getAlphaMax());
			}
						
			// Draw the bacteria
			for (Map.Entry<String,Vector<BSimFromFileBacterium>> bacPop : bacteria.entrySet()) {
				for(BSimFromFileBacterium bacterium : bacPop.getValue()) {
					draw(bacterium, bacterium.getColor());
				}
			}
			
			// Draw the particles
			for (Map.Entry<String,Vector<BSimFromFileParticle>> partPop : particles.entrySet()) {
				for(BSimFromFileParticle particle : partPop.getValue()) {
					draw(particle, particle.getColor());
				}
			}
			
			// Draw the mesh
			if (mesh != null) {
				draw(mesh, 0);
			}
		}
	}
}
