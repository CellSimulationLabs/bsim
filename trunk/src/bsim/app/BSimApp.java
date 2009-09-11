package bsim.app;

import java.io.File;
import java.util.Calendar;
import java.util.Vector;

import bsim.BSimUtils;
import bsim.app.gui.BSimGUI;
import bsim.batch.BSimBatch;
import bsim.export.BSimExport;
import bsim.export.BSimParticleFullFileExport;
import bsim.render.BSimProcessingRenderer;
import bsim.scene.BSimScene;

public class BSimApp {
	
	// The simulation scene
	private BSimScene scene;
	// scene instance for our initial parameters. 
	// (not a good idea for thousands of bacteria really, might have to rethink this one eventually)
	private BSimScene defaultScene;
	private BSimGUI gui;
	// Vector of all the export plug-ins that are working in the current scene
	private Vector<BSimExport> exportPlugins;
	
	// General properties that are easier to store locally
	private int numOfRuns    = 2;
	private int lenOfSim     = 100;
	private int movFrameSkip = 1;
	private int txtFrameSkip = 1;
	private String pathToExport = "./results";
	private boolean movieOutput = false;
	private int frameRecordForSec = 1;
	
	private BSimSemaphore simSem = null;
	// call this drawSem or something better
	private BSimSemaphore renderSem = null;
	
	// Simulation thread
	private Thread simThread;
	
	private int playState = BSimScene.PAUSED;
	
	private BSimProcessingRenderer renderer;
	
	
	/**
	 * General constructor.
	 */
	public BSimApp(){
				
		// Check that path is valid and that it exists, if not create
		File testPath = new File(pathToExport);
		if(!testPath.exists()){
			// Create directories as required
			testPath.mkdirs();
		}
		
		// Create the simulation scene
		simSem = new BSimSemaphore();
		renderSem = new BSimSemaphore();
		
	}
	
	
	public void setScene(BSimScene newScene){
		defaultScene = newScene;
		scene = defaultScene;
		
		renderer = new BSimProcessingRenderer(this);
		
		if(BSimGUI.guiState()){
			// if GUI enabled then create a GUI
			gui = new BSimGUI(this);
		}
	}


	/**
	 * Scene reset
	 */
	private void resetScene(int firstTime) {
		// If we make scene.update() threaded we will need a method to stop all threads
		// and thus make sure the garbage collector gets rid of our old scene before we create a new one
		// Similar to the renderer? (animation thread)
		//scene.destroy();
		
		// Reset the current scene to the default that was originally specified
		// Should reset the time etc.
		// note - Can't use this for batches as starting conditions, positions etc are not randomised
		// outside of the initialisation process in the specification file
		scene = defaultScene;

	}
	
	
	/**
	 * Run the batch of simulations.
	 */
	public void runBatch(){	
		// Create the quicktime output stream and the image to hold each frame
		//QuickTimeOutputStream movOut = null;
		
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
			//exportPlugins.add(new BSimParticleFullFileExport(scene.getBeads(), filePart, txtFrameSkip));
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
					//scene.setWaitingForVideoOpening(true);
					// TODO: Add back in.
					//scene.getProcessing().createMovie(filenameMovie);
					//while(scene.getWaitingForVideoOpening()){}
					//frame for sec in the video
					int frameForSec = frameRecordForSec;
					//time step in un sec
					int timeStepSec=(int) (1/BSimScene.dt); 
					//one frame Rate in confront of timeStep
					frameRate  = timeStepSec/frameForSec;
					
				}
				
				// Set the playState to playing
				play();
				
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
					//while(scene.getWaitingForVideoClosing()){}
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
		scene.advance(1);
		
		// Export the next frames data
		for(BSimExport i : exportPlugins){
			// Export the current frame
			i.exportFrame(scene);
		}	
	}
	
	
	/**
	 * Runs the batch process
	 * TODO: change to run, and implement Runnable?
	 */
	public void runApp(){
		if(!BSimGUI.guiState()){
			System.out.println("Starting Export...");
			
			// Load the file and create the batch object
			try{
				// The parameter file for the simulation
				//System.out.println(" " + args[0]);
				//File fParams = new File(args[0]);
				
				// Create the batch object
	
				runBatch();
			}
			catch(Exception e){ 
				System.err.println("Error writing to file (bsim.batch.BSimBatch.main)");
				e.printStackTrace();
			}
			
			System.out.println("Finished Export.");
		} else{
			scene.initialiseThread();
		}
	}
	
	
	/**
	 * Starts to play the simulation.
	 */
	public void play() {
		// Pass on the play signal to the scene
		scene.play();
		// Notify the animation of the update
		simSem.signal();
	}
	
	
	/**
	 * Pauses the simulation.
	 */
	public void pause() {
		// Pass on the pause signal to the scene
		scene.pause();
	}
	
	
	/**
	 * Resets the simulation.
	 */
	public void reset(int firstTime) {
		
		// Get the current play state
		int playState = scene.getPlayState();
		
		// Pass on the reset signal to the scene
		//scene.reset();
		resetScene(firstTime);
		// Reset the renderer with new scene data
		if(BSimGUI.guiState() && renderer != null){
			gui.resetDisplay(firstTime);
		}
		
		if(playState != scene.PAUSED){
			// Notify the animation of the update
			simSem.signal();
		}
	}
	
	
	/*
	 * Get and set methods
	 */
	public BSimSemaphore getRenderSem() { return renderSem; }
	public BSimSemaphore getSimSem()	{ return simSem; }

	public BSimScene getScene(){ return scene;}
	public BSimGUI getGUI(){ return gui; }
	public BSimProcessingRenderer getRenderer(){ return renderer; }
	
}
