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
package bsim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Vector;

import javax.swing.JPanel;

import bsim.field.BSimChemicalField;
import bsim.particle.BSimParticle;
import bsim.particle.bacterium.BSimBacterium;
import bsim.particle.bead.BSimBead;
import bsim.particle.vesicle.BSimVesicle;
import bsim.rendering.Processing;


public class BSimScene extends JPanel implements Runnable, ComponentListener{
	
	
	// Variables and constants for the animation state
	public static final int PLAYING = 1;
	public static final int PAUSED = 2;
	private int playState = PAUSED;
	
	// Background colour of the simulation
	private Color bgColour = new Color(0,0,0);
	
	// Variables used for zoom and pan controls
	private int startX = 0;
	private int startY = 0;
	private int transX = 0;
	private int transY = 0;
	private int transXo = 0;
	private int transYo = 0;
	private static int START_SCALE = 1000;
	private int scale = START_SCALE;
	private int scaleO = START_SCALE;
	private int button = 0;
	public static final int VIEW_PAN = 1;
	public static final int VIEW_ZOOM = 2;
	private int viewState = VIEW_PAN;
	
	private int simWidth = 0;
	private int simHeight = 0;
	
	private int orgSimWidth = 0;
	private int orgSimHeight = 0;
	
	// Vectors holding all bacteria and beads in the simulation
	private Vector bacteria;
	private Vector beads;
	private Vector vesicles;
	private Vector solidBoundaries;
	private Vector wrapBoundaries;
	private Vector solidBoxes;
	private Vector wrapBoxes;
	private Vector visualAids;
	
	// Chemical fields required for the simulation
	private BSimChemicalField fGoal;
	private BSimChemicalField fRecruitment;
	private BSimChemicalField fCoordination;
	private BSimChemicalField fQuorum;
	
	// Time related variables
	private double dt; //= 0.01;
	// Number of time steps that have occured in current simulation
	private int timeStep = 0;
	
	// Thread to run the simulation in
	private Thread simThread;
	// Semaphore to control the play/pause commands
	private BSimSemaphore simSem;
	
	// The applications that the simulation is embedded
	// (required for changes to time to be sent back to the GUI)
	private BSimApp app = null;
	
	// Threshold differences in concentration that result in bacterial response
	private static double GOAL_THRESHOLD = 0.0001;
	private static double RECRUIT_THRESHOLD = 0.0001;
	private static double COORD_THRESHOLD = 0.0001;
	
	// Parameters for the scene
	public BSimParameters params;
	
	public boolean guiExists = false;
	
	public boolean reallocateNewForceMat = true;
	
	public boolean reallocateNewFusionExists = true;
	
	public double[][] vesiclesForcesBeads = null;
	
	public Processing p = null;
	
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
	public BSimScene(BSimSemaphore newSimSem, BSimApp newApp, BSimParameters newParams)
	{
		super();
		
		this.addComponentListener(this);
		
		guiExists = true;
		
		params = newParams;
		dt = params.getDtSecs();
		
		simWidth = params.getScreenWidth();
		simHeight = params.getScreenHeight();
		
		orgSimWidth = params.getScreenWidth();
		orgSimHeight = params.getScreenHeight();
		
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
	public BSimScene(BSimParameters newParams)
	{
		super();
		
		params = newParams;
		dt = params.getDtSecs();
		
		simWidth = params.getScreenWidth();
		simHeight = params.getScreenHeight();
		
		orgSimWidth = params.getScreenWidth();
		orgSimHeight = params.getScreenHeight();
		
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
		
		// Update dt
		dt = params.getDtSecs();
		
		// Update the simulation widths and the window size
		simWidth = orgSimWidth;
		simHeight = orgSimHeight;	
		
		if (guiExists){
			app.setSize(new Dimension(simWidth, simHeight));
		}	
		this.setSize(new Dimension(simWidth, simHeight));
		
		// Update the translation
		double[] newTrans = params.getScreenMove();
		transX = (int)newTrans[0];
		transY = (int)newTrans[1];
		
		// Update the scale
		scale = (int)(START_SCALE * params.getScreenZoom());
	
		// Move back to first time-step 
		timeStep = 0;
		
		// Update GUI time if already created
		if (firstTime == 0 && guiExists){
			app.updateTime(getFormatedTime());
		}
		
		// Create the bacteria and bead sets
		beads = params.createNewBeadVec();
		bacteria = params.createNewBacteriaVec(this);
		vesicles = new Vector();
		reallocateNewForceMat = true;
		
		// Create both wrapping and solid boundaries
		solidBoundaries = params.createNewSolidPlaneBoundariesVec();
		wrapBoundaries = params.createNewWrapPlaneBoundariesVec();
		
		// Create both wrapping and solid boxes
		solidBoxes = params.createNewSolidBoxBoundariesVec();
		wrapBoxes = params.createNewWrapBoxBoundariesVec();
		
		// Create any visual aids
		visualAids = params.createNewVisualAidsVec(this);
		
		// Create each of the three chemical fields
		fGoal = params.createNewGoalChemicalField();
		fCoordination = params.createNewCoordChemicalField();
		fRecruitment = params.createNewRecruitChemicalField();
		fQuorum = params.createNewQuorumChemicalField();
		
		// Processing related activity
		if(guiExists){
			if(firstTime == 0){
				// Remove the PApplet from the display
				remove(p);
				// Kill the (PApplet's) animation thread and free resources
				p.destroy();
			}
		    p = new Processing(this);
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
			p = new Processing(this);    
		    p.init();
		}

		// Repaint the graphics display
		repaint();
	}
	
	
	/**
	 * Update the parameters that are used
	 */
	public void updateParams (BSimParameters newParams) {
		
		// Update the parameter object
		params = newParams;
		orgSimWidth = params.getScreenWidth();
		bSimWidth = orgSimWidth;
		if(orgSimWidth<BSimToolbar.BSimToolbarWidth){
			orgSimWidth = BSimToolbar.BSimToolbarWidth;
		}
		orgSimHeight = params.getScreenHeight();
		
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
				Thread.sleep((int)(1000*dt));
				
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
	
		// Growth and replication
//		BSimBacterium b;
//		for(int k=0; k<bacteria.size();k++){			
//			b = (BSimBacterium)(bacteria.elementAt(k));			
//			b.grow();
//			if(b.getRadius() > b.getReplicationRadius()){
//				b.replicate();	
//			}
//		}
			
		// Update the properties for bacteria and beads		
		Vector<BSimParticle> particles = new Vector();
		particles.addAll(bacteria);
		particles.addAll(beads);
		particles.addAll(vesicles);
		
		Vector obstacles = new Vector();
		obstacles.addAll(bacteria);
		obstacles.addAll(beads);
		obstacles.addAll(vesicles);
		
		for (int i = 0; i < particles.size(); i++) {
			for (int j = i+1; j < obstacles.size(); j++) {
				if (obstacles.get(j) instanceof BSimBacterium){
					particles.get(i).collide((BSimBacterium)obstacles.get(j));
				}
				else if (obstacles.get(j) instanceof BSimBead){
					particles.get(i).collide((BSimBead)obstacles.get(j));
				}
				else if (obstacles.get(j) instanceof BSimVesicle){
					particles.get(i).collide((BSimVesicle)obstacles.get(j));
				}
			}		
		}
		
		for(BSimParticle p : particles) {
			p.step();
			p.updatePosition();
		}
						
//		// Perform necessary boundary operations
//		for (int i = 0; i < wrapBoundaries.size(); i++) {
//			((BSimWrapPlaneBoundary)wrapBoundaries.elementAt(i)).boundaryCollisions(bacteria, beads);
//		}
		
		// Update the fields
		fGoal.updateField();
		fRecruitment.updateField();
		fCoordination.updateField();
		fQuorum.updateField();
		
		// Update the visual aids
//		for(int i=0; i<visualAids.size(); i++) {
//			((BSimVisualAid)visualAids.elementAt(i)).updateState();
//		}	
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
		int secs = (int)(timeStep * dt);
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
		
		timeStr = "" + (dt * timeStep);
		timeLen = timeStr.length();
		t = (int)(dt * timeStep);
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
	 * Update the viewState.
	 */
	public void setViewState(int newState){
		viewState = newState;
	}
	
	
	/**
	 * Check for resize events
	 */
	public void componentResized(ComponentEvent e) {
		
	    // Check to see if it is the panel and if so update the screen size
		if(e.getComponent() == this){
			
			// Update the parameters
			params.setScreenWidth(getWidth());
			params.setScreenHeight(getHeight());
			
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
	public Vector getSolidBoundaries (){ return solidBoundaries; }
	public Vector getWrapBoundaries (){ return wrapBoundaries; }
	public Vector getSolidBoxes (){ return solidBoxes; }
	public Vector getWrapBoxes (){ return wrapBoxes; }
	public Vector getVisualAids (){ return visualAids; }
	public boolean getStartVideo (){ return startVideo; }
	public boolean getEndVideo (){ return endVideo; }
	public void setStartVideo (boolean b){ startVideo=b; }
	public void setEndVideo (boolean b){ endVideo=b; }
	public String getImageFileName (){ return imageFileName; }
	public void setImageFileName (String s){ imageFileName=s; }	
	public double getDtSec (){ return dt; }
	public int getTimeStep (){ return timeStep; }
	public double getDtMilli (){ return dt * 1000; }
	public int getPlayState () { return playState; }
	public BSimChemicalField getGoalField (){ return fGoal; }
	public BSimChemicalField getRecruitmentField (){ return fRecruitment; }
	public BSimChemicalField getCoordinationField (){ return fCoordination; }
	public BSimChemicalField getQuorumField() { return fQuorum; }
	public int getTransX (){ return transX; }
	public int getTransY (){ return transY; }
	public double getScale () { return (1.0/START_SCALE)*scale; }
	public boolean getReallocateNewForceMat () { return reallocateNewForceMat; }
	public void setReallocateNewForceMat (boolean b) { reallocateNewForceMat=b; }
	public boolean getReallocateNewFusionExists () { return reallocateNewFusionExists; }
	public void setReallocateNewFusionExists(boolean b) { reallocateNewFusionExists=b; }
	public void setVesiclesForcesBeads(double[][] newVesiclesForcesBeads){vesiclesForcesBeads=newVesiclesForcesBeads;}
	public double[][] getVesiclesForcesBeads(){return vesiclesForcesBeads;}
	public void setVesicles( Vector newVesicles){vesicles=newVesicles;}
	public BSimParameters getParams(){return params;}
	public Processing getProcessing(){return p;}
	public boolean getWaitingForVideoClosing(){return waitingForVideoClosing;}
	public void setWaitingForVideoClosing(boolean b){waitingForVideoClosing=b;}
	public boolean getWaitingForVideoOpening(){return waitingForVideoOpening;}
	public void setWaitingForVideoOpening(boolean b){waitingForVideoOpening=b;}
	public boolean getGuiExists(){return guiExists;}

}
