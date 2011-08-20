package BSimParser;

/* This file contains a representation of a simulation and is
 * updated by the BSimParser.
 */

import java.util.Vector;
import java.util.HashMap;
import java.util.Map;

import bsim.*;
import bsim.particle.*;


class BSimFromFile {
	
	// Simulation details
	private BSim sim;
	private HashMap<String, Vector<BSimBacterium>> bacteria;
	private HashMap<String, Vector<BSimParticle>> particles;
	
	private String outputPath;
	private String dataFileName;
	private String movieFileName;
	
	public BSimFromFile () {
		sim = new BSim();
		bacteria = new HashMap<String, Vector<BSimBacterium>>();
		particles = new HashMap<String, Vector<BSimParticle>>();
		outputPath = "";
		dataFileName = "bsim_data.txt";
		movieFileName = "bsim_movie.mov";
	}
	
	
	
	public void setOutputPath (String newPath) { outputPath = newPath; }
	
	public void setDataFileName (String newFileName) { dataFileName = newFileName; }
	
	public void setMovieFileName (String newFileName) { movieFileName = newFileName; }
	
	
	
	public void addBacteria (String name, Vector<BSimBacterium> newBacteria) { bacteria.put(name, newBacteria); }
	
	public void addParicles (String name, Vector<BSimParticle> newParticles) { particles.put(name, newParticles); }
	
	
	/**
	 * Run the simulation
	 */
	public void run() { sim.export(); }
	
	
	
// We have a set of inner classes that implement the various exporters and tickers for
// our simulation.
	
	

	class BSimFromFileTicker extends BSimTicker {
		@Override
		public void tick() {
			
			for (Map.Entry<String,Vector<BSimBacterium>> bacPop : bacteria.entrySet()) {
				for(BSimBacterium bacterium : bacPop.getValue()) {
					bacterium.action();
					bacterium.updatePosition();
				}
			}
			
			for (Map.Entry<String,Vector<BSimParticle>> partPop : particles.entrySet()) {
				for(BSimParticle particle : partPop.getValue()) {
					particle.action();
					particle.updatePosition();
				}
			}
			
		}
	}
	
	
	
	
	
}


