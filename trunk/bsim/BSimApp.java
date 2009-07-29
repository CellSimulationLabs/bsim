/**
 * BSimApp.java
 *
 * Class to generate the main application frame and GUI components. It also contains 
 * methods to handle user input and generate animation files.
 *
 * Authors: Thomas Gorochowski
 * Created: 12/07/2008
 * Updated: 24/08/2008
 */


// Define the location of the class in the bsim package
package bsim;

// Import the bsim packages used
import bsim.*;
import bsim.export.*;

// Standard packages required by the application
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;


public class BSimApp extends JFrame{
	
	
	// Toolbar that provides acccess to all features of the application
	private BSimToolbar toolBar;
	
	// Simulation scene will render the animation
	private BSimScene scene;
	
	// Semaphore used for controlling the animation (notifiable object)
	private BSimSemaphore simSem;
	
	// Variables required for the progress bar when saving a movie
	private int pMin, pMax, pCur;
	private ProgressMonitor pBar; 
	
	public static BSimParameters params;
	
	
	/**
	 * General constructor.
	 */
	public BSimApp (){
		super();
		
		// Semaphore used for animation loop control (notifiable object)
		simSem = new BSimSemaphore();
		
		// Initialise class holding parameter values
		params = new BSimParameters();
		
		// Setup the frame and its contents
		this.setTitle("BSim");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		
		// Create the BSim scene and fill centre of frame
		scene = new BSimScene(simSem, this, params);
		scene.setSize(new Dimension(params.getScreenWidth(), params.getScreenHeight()));
		this.getContentPane().add(scene, BorderLayout.CENTER);
		// Create the toolbar and align to bottom of window
		toolBar = new BSimToolbar(this, scene, params);
		this.getContentPane().add(toolBar, BorderLayout.SOUTH);
		// Set the initial window size and display
		this.setSize(params.getScreenWidth(),params.getScreenHeight());
		this.setVisible(true);
	}
	
	
	/**
	 * Updates the time of the simulation.
	 */
	public void updateTime(String newTime){
		// Pass on the update to the toolbar
		toolBar.updateTime(newTime);
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
	public void reset() {
		
		// Get the current play state
		int playState = scene.getPlayState();
		
		// Pass on the reset signal to the scene
		scene.reset();
		
		if(playState != scene.PAUSED){
			// Notify the animation of the update
			simSem.signal();
		}
	}
	
	
	public void createImage(String filename) {
		
		// Ensure that the filename has the correct extension
		filename = filename + ".png";
		
		// Create the image required to hold the output
        BufferedImage img = new BufferedImage(scene.getWidth(), 
			scene.getHeight(), 
			BufferedImage.TYPE_INT_RGB);
			
		Graphics2D g = img.createGraphics();
    
		// Draw the frame to the graphics context
		scene.drawFrame((Graphics)g);
		
		byte[] pngbytes;
		// PngEncoder.ENCODE_ALPHA, PngEncoder.NO_ALPHA
		PngEncoder png =  new PngEncoder( img,
			PngEncoder.NO_ALPHA, 0, 1);

        try
        {
            FileOutputStream outfile = new FileOutputStream( filename );
            pngbytes = png.pngEncode();
            if (pngbytes == null)
            {
                System.out.println("Null image");
            }
            else
            {
                outfile.write( pngbytes );
            }
            outfile.flush();
            outfile.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}
	
	
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
		            BufferedImage img = new BufferedImage(scene.getWidth(), 
						scene.getHeight(), 
						BufferedImage.TYPE_INT_RGB);
		            
					// Loop through each frame in animation and draw scene
					for(int a = 0; a<numOfFrames; a++) {
						// Update progress
						pCur = a;
						g = img.createGraphics();
		            
						// Draw the frame to the graphics context
						scene.drawFrame((Graphics)g);
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
			scene.repaint();
			// Enable user input again
			toolBar.setEnabled(true);
		}
	}
		
	
	/**
	 * Runs the GUI application.
	 */
	public static void main(String[] args){
		BSimApp gui = new BSimApp();
	}
}
