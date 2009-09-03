/**
 * BSimScene.java
 *
 * Class that represents the scene of a simulation. It extends a JPanel which allows
 * for it to be easily embedded into any swing based GUI. Additionally, this class 
 * maintains the vectors of all bacteria and beads, as well as the physcis engine 
 * used to update the position and properties of all objects in the simulation. The class
 * is threaded to ensure that the main GUI event thread continues to function and
 * control of animaton is via a semaphre implemented as a notifiable object to ensure that
 * minimal processing is carried out when paused.
 *
 * Authors: Thomas Gorochowski
 *          Ian Miles
 *          Charlie Harrison
 *          Mattia Fazzini
 * Created: 12/07/2008
 * Updated: 26/08/2009
 */
package bsim.scene;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Vector;

import javax.swing.JPanel;
import javax.vecmath.Vector3d;

import bsim.BSimParameters;
import bsim.app.BSimApp;
import bsim.app.BSimSemaphore;
import bsim.app.BSimToolbar;
import bsim.field.BSimChemicalField;
import bsim.particle.BSimParticle;
import bsim.particle.bacterium.BSimBacterium;
import bsim.particle.bead.BSimBead;
import bsim.particle.vesicle.BSimVesicle;
import bsim.render.BSimProcessingRenderer;


public class BSimScene extends JPanel implements Runnable, ComponentListener{
	
	
	// Variables and constants for the animation state
	public static final int PLAYING = 1;
	public static final int PAUSED = 2;
	private int playState = PAUSED;
	
	// Background colour of the simulation
	private Color bgColour = new Color(0,0,0);
	
	// Variables used for zoom and pan controls
	private int transX = 0;
	private int transY = 0;
	private static int START_SCALE = 1000;
	private int scale = START_SCALE;
	public static final int VIEW_PAN = 1;
	public static final int VIEW_ZOOM = 2;
	
	private int simWidth = 0;
	private int simHeight = 0;
	
	private int orgSimWidth = 0;
	private int orgSimHeight = 0;
	
	// Vectors holding all bacteria and beads in the simulation
	private Vector<BSimBacterium> bacteria;
	private Vector<BSimBead> beads;
	private Vector<BSimVesicle> vesicles;	
	
	// Chemical fields required for the simulation
	private BSimChemicalField fGoal;
	private BSimChemicalField fQuorum;
	
	// Number of time steps that have occured in current simulation
	private int timeStep = 0;
	
	// Thread to run the simulation in
	private Thread simThread;
	// Semaphore to control the play/pause commands
	private BSimSemaphore simSem;
	
	// The applications that the simulation is embedded
	// (required for changes to time to be sent back to the GUI)
	private BSimApp app = null;
		
	// Parameters for the scene
	
	public boolean guiExists = false;	
	public BSimProcessingRenderer p = null;
	
	public boolean startVideo = false;
	public boolean endVideo = false;
	
	public String imageFileName = null;	
	
	public boolean resizeBug = true;
	private int bSimWidth = BSimToolbar.BSimToolbarWidth;
	
	//BSimBatch parameter for waiting the rigth closing of the file
	public boolean waitingForVideoClosing = true;
	public boolean waitingForVideoOpening = true;
	
	
	/**
	 * General constructor for use when GUI is present
	 */
	public BSimScene(BSimSemaphore newSimSem, BSimApp newApp)
	{
		super();
		
		this.addComponentListener(this);
		
		guiExists = true;
		
		simWidth = BSimParameters.screenWidth;
		simHeight = BSimParameters.screenHeight;
		
		orgSimWidth = BSimParameters.screenWidth;
		orgSimHeight = BSimParameters.screenHeight;
		
		// Update the internal variables
		simSem = newSimSem;
		app = newApp;
				
	    
		// Create initial bacteria and beads
		resetScene(1);
		
		// Create new thread to run the simulation in an associate with this object
		simThread = new Thread(this);
		// Start the simulation thread
		simThread.start();
	}
	
	
	/**
	 * General constructor for use with no GUI
	 */
	public BSimScene()
	{
		super();
		
		simWidth = BSimParameters.screenWidth;
		simHeight = BSimParameters.screenHeight;
		
		orgSimWidth = BSimParameters.screenWidth;
		orgSimHeight = BSimParameters.screenHeight;
		
		// Update the internal variables
		simSem = null;
		app = null;
		
		
		// Create initial bacteria and beads
		resetScene(1);
	}
	
	
	/**
	 * Reset the scene creating bactera and beads.
	 */
	private void resetScene(int firstTime) {
				
		// Update the simulation widths and the window size
		simWidth = orgSimWidth;
		simHeight = orgSimHeight;	
		
		if (guiExists){
			app.setSize(new Dimension(simWidth, simHeight));
		}	
		this.setSize(new Dimension(simWidth, simHeight));
		
		// Update the translation
		double[] newTrans = BSimParameters.screenMove;
		transX = (int)newTrans[0];
		transY = (int)newTrans[1];
		
		// Update the scale
		scale = (int)(START_SCALE * BSimParameters.screenZoom);
	
		// Move back to first time-step 
		timeStep = 0;
		
		// Update GUI time if already created
		if (firstTime == 0 && guiExists){
			app.updateTime(getFormatedTime());
		}
		
		// Create the bacteria and beads
		for(double[] args : BSimParameters.bacteria){			
			bacteria.add(new BSimBacterium(new Vector3d(args[0], args[1], args[2]), args[3], new Vector3d(args[4], args[5], args[6]), this));
		}	
		for(double[] args : BSimParameters.beads){
			beads.add(new BSimBead(new Vector3d(args[0], args[1], args[2]), args[3]));
		}	

		// Create the fields 
		// This looks a bit insane but we should clean up the constructor first
		fGoal = new BSimChemicalField(
				(int)BSimParameters.fGoalFieldType,
				(int)BSimParameters.fGoalBoundaryType,
				BSimParameters.fGoalRate,
				new Vector3d(BSimParameters.fGoalStartPos[0], BSimParameters.fGoalStartPos[1], BSimParameters.fGoalStartPos[2]),
				BSimParameters.fGoalWidth,
				BSimParameters.fGoalHeight,
				BSimParameters.fGoalDepth,
				(int)BSimParameters.fGoalXBoxes,
				(int)BSimParameters.fGoalYBoxes,
				(int)BSimParameters.fGoalZBoxes,
				BSimParameters.dt,
				BSimParameters.fGoalThreshold,
				new Color(0.1f, 0.8f, 0.1f)
              );	
        if(BSimParameters.fGoalSetAsLinearDir != 0) {
        	fGoal.setAsLinear((int)BSimParameters.fGoalSetAsLinearDir, BSimParameters.fGoalSetAsLinearStartCon, BSimParameters.fGoalSetAsLinearEndCon);
        }
		
		fQuorum = new BSimChemicalField(
				(int)BSimParameters.fQuorumFieldType,
				(int)BSimParameters.fQuorumBoundaryType,
				BSimParameters.fQuorumRate,
				new Vector3d(BSimParameters.fQuorumStartPos[0], BSimParameters.fQuorumStartPos[1], BSimParameters.fQuorumStartPos[2]),
				BSimParameters.fQuorumWidth,
				BSimParameters.fQuorumHeight,
				BSimParameters.fQuorumDepth,
				(int)BSimParameters.fQuorumXBoxes,
				(int)BSimParameters.fQuorumYBoxes,
				(int)BSimParameters.fQuorumZBoxes,
				BSimParameters.dt,
				BSimParameters.fQuorumThreshold,
				new Color(0.1f, 0.8f, 0.1f)
              );	
        if(BSimParameters.fQuorumSetAsLinearDir != 0) {
        	fQuorum.setAsLinear((int)BSimParameters.fQuorumSetAsLinearDir, BSimParameters.fQuorumSetAsLinearStartCon, BSimParameters.fQuorumSetAsLinearEndCon);
        }
				
		vesicles = new Vector();
		
		// Processing related activity
		if(guiExists){
			if(firstTime == 0){
				// Remove the PApplet from the display
				remove(p);
				// Kill the (PApplet's) animation thread and free resources
				p.destroy();
			}
		    p = new BSimProcessingRenderer(this);
		    // Initialise new animation thread and PApplet
		    p.init();
		    add(p,BorderLayout.CENTER);
		    
			if(resizeBug){
				if(bSimWidth<BSimToolbar.BSimToolbarWidth){	
					app.resize(simWidth, simHeight+95);
				}
				else{
					app.resize(simWidth+39, simHeight+95);
				}
				resizeBug = false;
			}
			else{
				if(bSimWidth<BSimToolbar.BSimToolbarWidth){	
					app.resize(simWidth, simHeight+96);
				}
				else{
					app.resize(simWidth+40, simHeight+96);
				}
				resizeBug = true;
			}
		}
		else{
			// Equivalent process for BSimBatch
			// This may not be necessary...
			if(firstTime == 0){
				remove(p);
				p.destroy();
			}
			// BSimBatch part
			p = new BSimProcessingRenderer(this);    
		    p.init();
		}

		// Repaint the graphics display
		repaint();
	}
	
	
	/**
	 * Update the parameters that are used
	 */
	public void updateParams () {
		
		// Update the parameter object		
		orgSimWidth = BSimParameters.screenWidth;
		bSimWidth = orgSimWidth;
		if(orgSimWidth<BSimToolbar.BSimToolbarWidth){
			orgSimWidth = BSimToolbar.BSimToolbarWidth;
		}
		orgSimHeight = BSimParameters.screenHeight;
		
		// Reset the scene to recreate all objects and ensure local variables are consistent
		this.reset();
	}
	
	
	/**
	 * The main thread loop. This handles the animation of the simulation using 
	 * notifiable objects to ensure that when paused no additional processing
	 * resources are used.
	 */
	public void run(){
		// Loop forever (until application closes)
		do{
			// Need to catch exceptions because sleep method is called
			try{
				
				// Check to see if playback state has changed
				if(playState == PAUSED) {
					// If paused wait on notifiable object (semaphore)
					simSem.waitOn();
				}
				
				// Wait the for the time-step
				Thread.sleep((int)(1000*BSimParameters.dt));
				
				// Update all the elements in the scene
				runAllUpdates();
				
				// Redraw the backgroundDisplay
				repaint();
				
				// Update the time-step
				timeStep++;
				
				if(guiExists){
					// Update the time in the GUI
					app.updateTime(getFormatedTime());
				}
			}
			catch(InterruptedException error){};
		}while(true);
	}
	
	
	private void runAllUpdates(){
						
		for(BSimBacterium bacterium : bacteria) {
			for(BSimBead bead : beads) {
					BSimParticle.interaction(bacterium, bead);
			}		
		}
		
		for(BSimBacterium p : bacteria) {
			p.action();
			p.updatePosition();
		}		
		for(BSimBead p : beads) {
			p.action();
			p.updatePosition();
		}		
		for(BSimVesicle p : vesicles) {
			p.action();
			p.updatePosition();
		}
								
		// Update the fields
		fGoal.updateField();
		fQuorum.updateField();
		
	}
	
	
	/**
	 * Calculates a formated version of the current simulation time (HH:MM:SS).
	 */
	public String getFormatedTime(){
		String a1, a2, a3;
		a1 = "";
		a2 = "";
		a3 = "";
		
		// Calculate the parts of the time
		int secs = (int)(timeStep * BSimParameters.dt);
		int mins = (int)(secs/60);
		int hrs  = (int)(mins/60);
		secs = secs - (mins * 60);
		mins = mins - (hrs * 60);
		
		// Check if zero padding required 
		// (could use format string but quicker to do manually)
		if(secs < 10){ a3 = "0"; }
		if(mins < 10){ a2 = "0"; }
		if(hrs  < 10){ a1 = "0"; }
		
		// Return the formatted time
		return "Time: " + a1 + hrs + ":" + a2 + mins + ":" + a3 + secs;
	}
	
	
	/**
	 * Calculates a formated version of the current simulation time (HH:MM:SS).
	 */
	public String getFormatedTimeSecs(){
		String timeStr, secStr;
		int secLen = 0, decLen = 0, timeLen = 0, t;
		
		// Check to see at the first time step (no need to carry out calculation)
		if(timeStep == 0){
			return "Time: 0.00 secs";
		}
		
		timeStr = "" + (BSimParameters.dt * timeStep);
		timeLen = timeStr.length();
		t = (int)(BSimParameters.dt * timeStep);
		secStr = "" + t;
		secLen = secStr.length();
		decLen = timeLen - secLen;
		
		// Alter the time so that it is in the format SS.MM
		if(timeLen == 1){ timeStr = timeStr + ".00"; }
		else if(decLen == 2){ timeStr = timeStr + "1"; }
		else if(decLen  > 3){ timeStr = timeStr.substring(0, secLen+3); }
		
		// Return the formatted time
		return "Time: " + timeStr + " secs";
	}
	
	
	/**
	 * Plays the current simulation.
	 */
	public void play() {
		// Update the playback state (if required)
		if(playState == PAUSED) {
			playState = PLAYING;
		}
	}
	
	
	/**
	 * Pauses the current simulation.
	 */
	public void pause() {
		// Update the playback state (if required)
		if(playState == PLAYING) {
			playState = PAUSED;
		}
	}
	
	
	/**
	 * Resets the simulation.
	 */
	public void reset() {
		// Update state variables
		playState = PAUSED;
		scale = START_SCALE;
		transX = 0;
		transY = 0;
		
		// Recreate all simulation objects
		resetScene(0);
	}
	
	
	/**
	 * Redraws the screen (inherited). Called when repaint() is invoked.
	 */
	public void paint(Graphics g) {
		redraw(g);
	}
	
	
	/**
	 * Redraws the screen and should remove need for double buffering.
	*/ 
	public void update(Graphics g) {
		redraw(g);
	}
	
	/**
	 * Redraws the screen.
	 */
	public void redraw(Graphics g) {
		// If flicker returns may need to implement double buffered display
		// (should not be required though as update method has been implemented)
		drawFrame(g);
	}
	
	
	/**
	 * Draws the current simulation to a given graphics context.
	 */
	public void drawFrame(Graphics g) {
		//Fill the background and clear output
		g.setColor(bgColour);
		g.fillRect(0,0,simWidth,simHeight);
	}
	

	/**
	 * Skips the simulation forward a given number of frames. Intermediate frames still
	 * have to be computed.
	 */
	public void skipFrames(int numOfFrames) {
		// Loop through the necessary frames and update positions of objects
		for(int i=0; i<numOfFrames; i++){
			// Update all the elements in the scene
			runAllUpdates();
			
			// Update the time-step
			timeStep++;
			
			if(guiExists){
				// Update the time in the GUI
				app.updateTime(getFormatedTime());
			}
		}
	}
	
	
	/**
	 * Check for resize events
	 */
	public void componentResized(ComponentEvent e) {
		
	    // Check to see if it is the panel and if so update the screen size
		if(e.getComponent() == this){
			
			// Update the parameters
			BSimParameters.screenWidth = getWidth();
			BSimParameters.screenHeight = getHeight();
			
			// Update the local variables
			simWidth = getWidth();
			simHeight = getHeight();
		}         
	}
	
	/**
	 * Not overwritten by this class.
	 */
	public void componentHidden(ComponentEvent e) {}
	/**
	 * Not overwritten by this class.
	 */
	public void componentMoved(ComponentEvent e) {}
	/**
	 * Not overwritten by this class.
	 */
	public void componentShown(ComponentEvent e) {}
	
	
	/**
	 * Standard get methods for the class.
	 */
	public Vector getBacteria (){ return bacteria; }
	public void addBacterium (BSimBacterium b){ bacteria.add(b); }
	public Vector getBeads (){ return beads; }
	public void addBead(BSimBead b){ beads.add(b); }
	public Vector getVesicles (){ return vesicles; }
	public void addVesicle(BSimVesicle b){ vesicles.add(b); }
		
	public int getTimeStep (){ return timeStep; }
	public BSimChemicalField getGoalField (){ return fGoal; }
	public BSimChemicalField getQuorumField() { return fQuorum; }	
	
	public boolean getStartVideo (){ return startVideo; }
	public boolean getEndVideo (){ return endVideo; }
	public void setStartVideo (boolean b){ startVideo=b; }
	public void setEndVideo (boolean b){ endVideo=b; }
	public String getImageFileName (){ return imageFileName; }
	public void setImageFileName (String s){ imageFileName=s; }
	public int getPlayState () { return playState; }
	public int getTransX (){ return transX; }
	public int getTransY (){ return transY; }
	public double getScale () { return (1.0/START_SCALE)*scale; }
	
	public BSimProcessingRenderer getProcessing(){return p;}
	public boolean getWaitingForVideoClosing(){return waitingForVideoClosing;}	
	public boolean getWaitingForVideoOpening(){return waitingForVideoOpening;}
	public void setWaitingForVideoOpening(boolean b){waitingForVideoOpening=b;}	

}
