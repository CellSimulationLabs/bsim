package bsim.export.visual;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ProgressMonitor;

import bsim.export.QuickTimeOutputStream;

public class BSimVideoQuicktime {
	// Variables required for the progress bar when saving a movie
	private int pMin, pMax, pCur;
	private ProgressMonitor pBar; 	
	
	/**
	 * Creates a movie of the simultion from the current time for a given
	 * length (in frames) with a skip rate (in frames) and filename.
	 */
	public void createMovie(String filename, int numOfFrames, int skipFrames) {
		
		// Ensure that the filename has the correct extension
		filename = filename + ".mov";
		
		// Update the initial values for the progress monitor
		pCur = 0;
		pMin = 0;
		pMax = numOfFrames-1;
		
		// Disble the toolbar to stop interaction during the recording
		toolBar.setEnabled(false);
		
		// Thread to display and update the progress bar
		Thread recProgress = new BSimRecordProgress();
		recProgress.start();
		
		// Thread to record the movie
		// This ensures the main thread can redraw the screen as required
		Thread recThread = new BSimRecordThread (filename, numOfFrames, skipFrames);
		recThread.start();
	}
	
	/**
	 * Internal class to handle the progress monitor for movies.
	 */
	class BSimRecordProgress extends Thread {
		
		/**
		 * General constructor.
		 */
		public BSimRecordProgress(){
			// Create the progress monitor object with initial conditions
			pBar = new ProgressMonitor(null, "Recording Movie", "Writing Movie...", pMin, pMax);
			// Update the initial progress
			pBar.setProgress(pCur);
		}

		
		/**
		 * Function run when thread starts. Updates the progress monitor window.
		 */
		public void run(){
			// Variables to hold user friendly status
			int i, j;
			
			// Because sleep function is called must handle exceptions
			try {
				// Delay to redraw the progress monitor
				Thread.sleep(10);
				
				// Loop while frames are being processed
				while(pCur < pMax){
					// Delay the update (ensures no continually cycling)
					Thread.sleep(100);
					// Update the progress
					pBar.setProgress(pCur);
					// Update the user friendly progress values
					i = pCur + 1;
					j = pMax + 1;
					// Update status note to display current frame
					pBar.setNote("Frame " + i + " of " + j);
				}
			} catch (InterruptedException ignore) {}
		}
	}
	
	
	
	/**
	 * Internal class to record a movie of the simulation.
	 */
	class BSimRecordThread extends Thread {
		
		
		// Variables to hold movie properties
		String filename;
		int numOfFrames, skipFrames;
		
		
		/**
		 * General constructor.
		 */
		public BSimRecordThread (String newFilename, int newNumOfFrames, int newSkipFrames){
			// Update movie parameters
			filename = newFilename;
			numOfFrames = newNumOfFrames;
			skipFrames = newSkipFrames;
		}
		
		
		/**
		 * Main thread method. Creaes a movie output using the given parameters.
		 */
			public void run() {
				// Create the quicktime output stream and the image to hold each frame
				QuickTimeOutputStream out = null;
		        Graphics2D g = null;
		        try {
		            // The quicktime output (format is JPG by default, PNG also supported)
					out = new QuickTimeOutputStream(new File(filename), 
						QuickTimeOutputStream.VideoFormat.JPG);
		            // Set the video quality
					out.setVideoCompressionQuality(1f);
		            // Set the number of frames per second
					out.setTimeScale(30); // 30 fps
					
					// Create the image required to hold the output
		            //BufferedImage img = new BufferedImage(scene.getWidth(), 
					//	scene.getHeight(), 
					//	BufferedImage.TYPE_INT_RGB);
		            // TODO: Broke this to compile on Mac
					BufferedImage img = new BufferedImage(100, 
						100, 
						BufferedImage.TYPE_INT_RGB);
					
					// Loop through each frame in animation and draw scene
					for(int a = 0; a<numOfFrames; a++) {
						// Update progress
						pCur = a;
						g = img.createGraphics();
		            
						// Draw the frame to the graphics context
						// TODO: Broken to compile on Mac
						//scene.drawFrame((Graphics)g);
						
						// Skip the required number of frames
						// (This will still calculate all intermediate timesteps)
						scene.skipFrames(skipFrames);
						
						// Write the frame to the file
			            out.writeFrame(img, 1);
					}
				} catch (IOException ex) { ex.printStackTrace();  
		        // Handle any existing problems that arise
				} finally {
		            if (g != null) {
		                g.dispose();
		            }
		            if (out != null) {
						try{
							out.close();
						} catch (IOException ex) { ex.printStackTrace(); }
		            }
		        }
			
			// Update final progress to ensure progress monitor exits
			pCur = pMax;
			// Update the display with current frame
			// TODO: Broken to compile on Mac
			//scene.repaint();
			// Enable user input again
			toolBar.setEnabled(true);
		}
	}
}
