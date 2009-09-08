/**
 * BSimBatch.java
 *
 * Class to run a batch of simulations.
 *
 * Authors: Thomas Gorochowski
 * Created: 18/08/2008
 * Updated: 18/08/2008
 */
package bsim.batch;

import java.awt.Graphics2D;
import java.io.File;
import java.util.Calendar;
import java.util.Vector;

import bsim.BSimUtils;
import bsim.export.BSimExport;
import bsim.export.BSimParticleFullFileExport;
import bsim.scene.BSimScene;


public class BSimBatch{
	
	
	// The simulation scene
	private BSimScene scene;
		
	// Vector of all the export plug-ins that are working in the current scene
	private Vector<BSimExport> exportPlugins;
	
	// General proerties that are esier to store locally
	private int numOfRuns    = 1;
	private int lenOfSim     = 100;
	private int movFrameSkip = 1;
	private int txtFrameSkip = 1;
	private String pathToExport = "./results";
	private boolean movieOutput = false;
	private int frameRecordForSec = 1;
	
	
	/**
	 * General constructor.
	 */
	public BSimBatch(){
				
		// Check that path is valid and that it exists, if not create
		File testPath = new File(pathToExport);
		if(!testPath.exists()){
			// Create directories as required
			testPath.mkdirs();
		}
		
		// Create the simulation scene
		scene = new BSimScene();
	}
	
	
	/**
	 * Run the batch of simulations.
	 */
	public void runBatch(){	
		
		// Create the quicktime output stream and the image to hold each frame
		//QuickTimeOutputStream movOut = null;
	    Graphics2D g = null;
		
		// Build the time stamp of the whole batch (this helps to keep batches together)
		Calendar calNow = Calendar.getInstance();
		String yyyyStr, mmStr, ddStr, hhStr, miStr, ssStr, timestampStr;
		String curhhStr, curmiStr, curssStr, curtimestampStr;
		
		
		yyyyStr = "" + calNow.get(Calendar.YEAR);
		mmStr = BSimUtils.padInt2(calNow.get(Calendar.MONTH));
		ddStr = BSimUtils.padInt2(calNow.get(Calendar.DAY_OF_MONTH));
		hhStr = BSimUtils.padInt2(calNow.get(Calendar.HOUR_OF_DAY));
		miStr = BSimUtils.padInt2(calNow.get(Calendar.MINUTE));
		ssStr = BSimUtils.padInt2(calNow.get(Calendar.SECOND));
		
		timestampStr = yyyyStr + "-" + mmStr + "-" + ddStr + "_" +
			hhStr + "-" + miStr + "-" + ssStr;
		
		// Cycle through the number of batch runs required
		for(int i=0; i<numOfRuns; i++){
			
			int iN = i+1;
			calNow = Calendar.getInstance();
			curhhStr = BSimUtils.padInt2(calNow.get(Calendar.HOUR_OF_DAY));
			curmiStr = BSimUtils.padInt2(calNow.get(Calendar.MINUTE));
			curssStr = BSimUtils.padInt2(calNow.get(Calendar.SECOND));
			curtimestampStr = hhStr + ":" + miStr + ":" + ssStr;
			
			System.out.println("[" + curtimestampStr + "] BSimBatch: Starting run " + iN + " of " + numOfRuns 
			   + " (" + timestampStr + ")");
			
			// Setup the export objects for the current run
			
			// Remove any existing export plugins
			exportPlugins = new Vector<BSimExport>(3);

			// Create the files for each of the outputs
			File fileBac = new File(pathToExport + "/" + timestampStr + 
				"_Bacteria-Export_"  + BSimUtils.padInt4(i+1) + ".csv");
			
			File filePart = new File(pathToExport + "/" + timestampStr + 
				"_Bead-Export_"  + BSimUtils.padInt4(i+1) + ".csv");
			
			String filenameMovie = pathToExport + "/" + timestampStr + 
				"_Video-Export_"  + BSimUtils.padInt4(i+1) + ".mov";
				
			exportPlugins.add(new BSimParticleFullFileExport(scene.getBacteria(), fileBac, txtFrameSkip));
			exportPlugins.add(new BSimParticleFullFileExport(scene.getBeads(), filePart, txtFrameSkip));
			exportPlugins.add(new BSimParticleFullFileExport(scene.getVesicles(), filePart, txtFrameSkip));
			
			// If a simulation has been run reset first
			if(i != 0){
				// Reset the scene
				scene.reset();
			}
			
			try {
		        
				int frameRate = 0;
				
				if(movieOutput){				
					//call the method to create the video inside Processing
					scene.setWaitingForVideoOpening(true);
					// TODO: Add back in.
					//scene.getProcessing().createMovie(filenameMovie);
					while(scene.getWaitingForVideoOpening()){}
					//frame for sec in the video
					int frameForSec = frameRecordForSec;
					//time step in un sec
					int timeStepSec=(int) (1/BSimScene.dt); 
					//one frame Rate in confront of timeStep
					frameRate  = timeStepSec/frameForSec;
					
				}

				// Run the scene for the length specified
				for(int t=0; t<lenOfSim; t++){
					
					if(movieOutput){
						if(t % frameRate == 0){
							// TODO: Add back in
							//scene.getProcessing().addMovieFrame();
						}
					}
					
					
		
					// Move to the next frame
					if(t == 0){
						// First frame in the output
						nextFrame(true);
					}
					else{
						// Not the first frame in the output
						nextFrame(false);
					}
				}
				
				if(movieOutput){
					// TODO: Add back in
					//scene.getProcessing().closeMovie();
					while(scene.getWaitingForVideoClosing()){}
				}
				
			} catch (Exception ex) { ex.printStackTrace();}		
			
			// Finalise the current run (close open files, etc)
			finaliseCurrent();
			
			scene.reset();
		}
	}
	
	
	/** 
	 * Finalise the current run to allow for any files to be closed safely.
	 */
	private void finaliseCurrent(){
		
		// Cycle through each of the export plugins
		for(BSimExport i : exportPlugins){
			// Finish the export
			i.finishExport(scene);
		}
	}
		
	
	/**
	 * Move to the next frame in the simulation.
	 */
	private void nextFrame(boolean firstFrame){
		
		// Check if first frame and if so export current simulation before moving to next frame
		if(firstFrame){
			// Using this updated simulation cycle through all export plugins and 
			// export the latest frames data.
			for(int i=0; i<exportPlugins.size(); i++){
				// Export the current frame
				((BSimExport)exportPlugins.elementAt(i)).exportFrame(scene);
			}
		}
		
		// Move the simulation to the next frame
		scene.skipFrames(1);
		
		// Export the next frames data
		for(BSimExport i : exportPlugins){
			// Export the current frame
			i.exportFrame(scene);
		}	
	}
	
	
	/**
	 * Runs the batch command line application.
	 */
	public static void main(String[] args){
		
		System.out.println("Starting Export...");
		
		// Load the file and create the batch object
		try{
			// The parameter file for the simulation
			//System.out.println(" " + args[0]);
			//File fParams = new File(args[0]);
			
			// Create the batch object
			BSimBatch batch = new BSimBatch();
			
			batch.runBatch();
		}
		catch(Exception e){ 
			System.err.println("Error writing to file (bsim.batch.BSimBatch.main)");
			e.printStackTrace();
		}
		
		System.out.println("Finished Export.");
	}
}