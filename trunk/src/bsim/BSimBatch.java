/**
 * BSimBatch.java
 *
 * Class to run a batch of simulations.
 *
 * Authors: Thomas Gorochowski
 * Created: 18/08/2008
 * Updated: 18/08/2008
 */
package bsim;

import java.awt.Graphics2D;
import java.io.File;
import java.util.Calendar;
import java.util.Vector;

import bsim.export.BSimBacteriaFileExport;
import bsim.export.BSimBeadFileExport;
import bsim.export.BSimExport;


public class BSimBatch{
	
	
	// The simulation scene
	private BSimScene scene;
	
	// Parameters for the batch
	private BSimParameters params;
	
	// Vector of all the export plug-ins that are working in the current scene
	private Vector exportPlugins;
	
	// General proerties that are esier to store locally
	private int numOfRuns    = 1;
	private int lenOfSim     = 1;
	private int movFrameSkip = 1;
	private int txtFrameSkip = 1;
	private String pathToExport;
	private boolean movieOutput = true;
	
	
	/**
	 * General constructor.
	 */
	public BSimBatch(File f){
		
		// Create a parameter file loader and read into our local variable
		BSimParametersLoader paramLoader = new BSimParametersLoader(f);
		params = paramLoader.parseFile();
		
		// Update the batch variables
		numOfRuns = params.getSimRuns();
		lenOfSim = params.getSimLength();
		movFrameSkip = params.getVideoFramesSkip();
		txtFrameSkip = params.getDataFramesSkip();
		movieOutput = params.getRecordVideo();
		pathToExport = params.getExportDir();
		
		// Check that path is valid and that it exists, if not create
		File testPath = new File(pathToExport);
		if(!testPath.exists()){
			// Create directories as required
			testPath.mkdirs();
		}
		
		// Create the simulation scene
		scene = new BSimScene(params);
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
			exportPlugins = new Vector(3);

			// Create the files for each of the outputs
			File fileBac = new File(pathToExport + "/" + timestampStr + 
				"_Bacteria-Export_"  + BSimUtils.padInt4(i+1) + ".csv");
			
			File filePart = new File(pathToExport + "/" + timestampStr + 
				"_Bead-Export_"  + BSimUtils.padInt4(i+1) + ".csv");
			
			String filenameMovie = pathToExport + "/" + timestampStr + 
				"_Video-Export_"  + BSimUtils.padInt4(i+1) + ".mov";
				
			exportPlugins.add(new BSimBacteriaFileExport(fileBac, txtFrameSkip));
			exportPlugins.add(new BSimBeadFileExport(filePart, txtFrameSkip));
			
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
					scene.getProcessing().createMovie(filenameMovie);
					while(scene.getWaitingForVideoOpening()){}
					//frame for sec in the video
					int frameForSec = params.getFrameRecordForSec();
					//time step in un sec
					int timeStepSec=(int) (1/params.getDtSecs()); 
					//one frame Rate in confront of timeStep
					frameRate  = timeStepSec/frameForSec;
					
				}

				// Run the scene for the length specified
				for(int t=0; t<lenOfSim; t++){
					
					if(movieOutput){
						if(t % frameRate == 0){
							scene.getProcessing().addMovieFrame();
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
					scene.getProcessing().closeMovie();
					while(scene.getWaitingForVideoClosing()){}
				}
				
			} catch (Exception ex) { ex.printStackTrace();}			
			// Finalise the current run (close open files, etc)
			finaliseCurrent();
		}
	}
	
	
	/** 
	 * Finalise the current run to allow for any files to be closed safely.
	 */
	private void finaliseCurrent(){
		
		// Cycle through each of the export plugins
		for(int i=0; i<exportPlugins.size(); i++){
			// Finish the export
			((BSimExport)exportPlugins.elementAt(i)).finishExport(scene, params);
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
				((BSimExport)exportPlugins.elementAt(i)).exportFrame(scene, params);
			}
		}
		
		// Move the simulation to the next frame
		scene.skipFrames(1);
		
		// Export the next frames data
		for(int i=0; i<exportPlugins.size(); i++){
			// Export the current frame
			((BSimExport)exportPlugins.elementAt(i)).exportFrame(scene, params);
		}	
	}
	
	
	/**
	 * Runs the batch command line application.
	 */
	public static void main(String[] args){
		int numOfRuns = 1;
		
		// Load the file and create the batch object
		try{
			// The parameter file for the simulation
			File fParams = new File(args[0]);
			
			// Create the batch object
			BSimBatch batch = new BSimBatch(fParams);
			
			batch.runBatch();
		}
		catch(Exception e){ 
			System.err.println("Error writing to file (BSimBatch.main)");
			e.printStackTrace();
		}
		
		System.out.println("Finish");
	}
}