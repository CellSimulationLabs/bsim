/**
 * BSimProcessingRenderer.java
 * 
 * BSim's standard 3D renderer class.
 * 
 * This class uses Processing's library functions to draw the scene.
 * Currently the P3D renderer has been implemented in this class. Although it
 * is not the fastest renderer it is reasonably robust and consistent in its
 * output. P3D renders in software and therefore should be perfectly suitable 
 * for rendering on headless nodes or those without graphics cards. It also 
 * provides a number of output methods (screenshots, movies etc) conveniently
 * via the Processing libraries.
 *
 * Current implementation with processing core v1.0.6
 * http://www.processing.org
 * 
 * 3D camera uses PeasyCam library
 * http://mrfeinberg.com/peasycam/
 * 
 */

package bsim.render;

import java.util.Vector;

import javax.vecmath.Vector3f;

import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PFont;
import bsim.BSimUtils;
import bsim.app.BSimApp;
import bsim.field.BSimChemicalField;
import bsim.particle.BSimBacterium;
import bsim.particle.BSimBead;
import bsim.particle.BSimParticle;
import bsim.particle.BSimRepBacterium;
import bsim.particle.BSimVesicle;
import bsim.particle.BSimVesicleAcceptor;
import bsim.scene.BSimScene;


public class BSimProcessingRenderer extends PApplet implements BSimRenderer {
	
	// Simulation core
	private BSimScene scene = null;
	private BSimApp app = null;
	
	// The simulation objects
	private Vector<BSimBacterium> bacteria = null;
	private Vector<BSimVesicle> vesicles = null;
	private Vector<BSimBead> beads = null;
	// Centre position of the scene boundary
	private double[] boundCentre = new double[3];
	
	// Processing and PApplet
	private int widthInitial;
	private int heightInitial;
	private int maximumFPS;
	private PFont myFont;

	// Camera parameters
	private double minDistance;
	private double maxDistance;
	private double defaultDistance;
	private PeasyCam cam = null;
		
	// Temporary storage for the play state of the scene
	private int oldPlayState = -1;
	
	/**
	 * Standard constructor for the renderer
	 */
	public BSimProcessingRenderer(BSimApp newApp) {
		// BSimScene used in the core simulation
		app = newApp;
		scene = app.getScene();
		
		// PApplet parameters
		widthInitial = BSimScene.screenWidth;
		heightInitial = BSimScene.screenHeight;
		maximumFPS = 100;
		
		// Camera specific parameters
		// TODO: get them from the parameters file?
		minDistance = 10;
		maxDistance = 1000;
		defaultDistance = 200;
		
		// Get simulation objects from the scene
		bacteria = scene.getBacteria();
		vesicles = scene.getVesicles();
		beads = scene.getBeads();
		
		// Set scene bounds
		boundCentre[0] = BSimScene.xBound/2;
		boundCentre[1] = BSimScene.yBound/2;
		boundCentre[2] = BSimScene.zBound/2;

	}
	
	
	/************************ Processing's initialisation and drawing ************************/
	/**
	 * Setup the PApplet; initialise the Processing renderer and its settings
	 * TODO: 	Passing/selecting colours so that e.g. draw(sensingBacterium) can call 
	 * 			draw(bacterium) but be a different colour
	 */
	public void setup(){
		// Initialise the Processing renderer:
		size(widthInitial, heightInitial, P3D);

		// Set our target frame rate:
		frameRate(maximumFPS);
		
		// Switch off draw() looping, must call redraw() each time we want a new frame drawn.
		noLoop();

		// Set up and initialise PeasyCam:
		cam = new PeasyCam(this, boundCentre[0], boundCentre[1], boundCentre[2], (float) defaultDistance);
		cam.setMinimumDistance((float) minDistance);
		cam.setMaximumDistance((float) maxDistance);

		// Speed up sphere rendering somewhat. Processing's default value is 30 (much slower to render).
		sphereDetail(10);

		// Other Processing drawing options:
		noStroke();

		// Font for drawing overlay text:
		myFont = createFont("FFScala", 20);
		textFont(myFont);
		textMode(SCREEN);
	}
	
	
	/**
	 * Reset the camera to its original state
	 */
	public void resetCamera(){
		cam.reset();
	}
	
	
	/**
	 *  Draw all objects in the scene 
	 *  By default Processing loops this automatically based on the FPS set above with maximumFPS,
	 *  however we can control whether looping is on or off with loop() and noLoop() respectively.
	 */
	public void draw(){
		// Clean slate, otherwise we end up drawing on top of the previous frame.
		background(0);

		// Don't want 3D lighting to slow down our chemical field rendering...
		noLights();

		// Chemical fields
		//	TODO: 	time to vectorise our chemical fields or something like that
		//	TODO:	better way of looping through, to allow multiple coloured fields
		if(scene.getGoalField() != null && scene.getGoalField().getFieldType() != BSimChemicalField.TYPE_UNDEFINED){
			draw(scene.getGoalField());
		}
//		if(scene.getQuorumField() != null && scene.getQuorumField().getFieldType() != BSimChemicalField.TYPE_UNDEFINED){
//			draw(scene.getQuorumField());
//		}
		
		// Add Processing's 3D lighting.
		lights();
		
		// Doesn't draw an object if it doesn't exist...
		// Bacteria
		for(int i = 0, n = bacteria.size(); i < n; i++) {
			draw(bacteria.elementAt(i));
		}
		// Vesicles
		for(int i = 0, n = vesicles.size(); i < n; i++) {
			draw(vesicles.elementAt(i));
		}
		// Particles
		for(int i = 0, n = beads.size(); i < n; i++) {
			draw(beads.elementAt(i));
		}
		
		noLights();
		// Draw the scene boundaries.
		drawBoundary();
		
		// Text overlays; drawn last so they're on top of everything else.
		drawTime();
		drawFPS();
		
		// Signal the scene that redraw has been completed
		if(scene != null){
			app.getRenderSem().signal();
		}
	}
	
	/**
	 * Loop the animation if the mouse button is clicked (i.e. on camera movement)
	 */
	// Need to tolerate multiple mouse buttons.
	// flag the current playstate/renderstate and if() these mouse pressed functions
	public void mousePressed() {
		// Pause the scene calculations while moving the camera
		oldPlayState = scene.getPlayState();
		app.pause();
		loop();
	}

	public void mouseReleased() {
		// Start to play again if the scene was previously playing
		noLoop();
		// null check may not be necessary as BSimBatch will not have a window to click on
		if(BSimScene.getApp() != null){
			if(oldPlayState == BSimScene.PLAYING){
				app.play();
			}
		}
	}
	
	
	/****************************** BSimParticle Draw Methods ******************************/
	/**
	 * Draw a particle
	 */
	public void draw(BSimParticle part){
		fill(255, 255, 255);
		pushMatrix();
		translate((float)part.getPosition().x, (float)part.getPosition().y,(float)part.getPosition().z);
		sphere((float)(part.getRadius()));			
		popMatrix();
	}

	/**
	 * Draw a bead
	 */	
	public void draw(BSimBead bead){
		fill(255, 0, 0);
		pushMatrix();
		translate((float)bead.getPosition().x, (float)bead.getPosition().y,(float)bead.getPosition().z);
		sphere((float)(bead.getRadius()));			
		popMatrix();
	}

	/**
	 * Draw a bacterial membrane vesicle
	 */
	public void draw(BSimVesicle vesicle){
		fill(255, 131, 223, 50);
		
		pushMatrix();
		translate((float)vesicle.getPosition().x, (float)vesicle.getPosition().y,(float)vesicle.getPosition().z);
		sphere((float)(100*vesicle.getRadius()));
		popMatrix();
		// As vesicles are so small compared to everything else we can just draw them as a point
		stroke(255,0,0);
		point((float)vesicle.getPosition().x, (float)vesicle.getPosition().y,(float)vesicle.getPosition().z);
		noStroke();
	}
	// TODO: pulsating vesicle acceptor when a vesicle combines.
	/**
	 * Draw a vesicle acceptor
	 */
	public void draw(BSimVesicleAcceptor acceptor){
		fill(255, 0, 0);
		pushMatrix();
		translate((float)acceptor.getPosition().x, (float)acceptor.getPosition().y,(float)acceptor.getPosition().z);
		sphere((float)(acceptor.getRadius()));			
		popMatrix();
	}


	/****************************** BSimBacterium Draw Methods *****************************/
	/**
	 * Draw a standard BSimBacterium
	 */
	public void draw(BSimBacterium bact){
		fill(0, 255, 0);
		
		// Draw the bacterium as a rod shape.
		Vector3f worldY = new Vector3f(0,1,0);
		Vector3f bacDirVector = new Vector3f(bact.getDirection());
		Vector3f bacRotVector = new Vector3f();
		
		bacRotVector.cross(worldY,bacDirVector);
		
		bacRotVector.normalize();
			
		pushMatrix();
			translate((float)bact.getPosition().x, (float)bact.getPosition().y,(float)bact.getPosition().z);
			rotate(worldY.angle(bacDirVector), bacRotVector.x, bacRotVector.y, bacRotVector.z);
			drawRodShape(0.4f, (float)(bact.getRadius()*2),90);
		popMatrix();
		
		// Draw the bacterium as a sphere (faster).
		//pushMatrix();
		//translate((float)bact.getPosition().x, (float)bact.getPosition().y,(float)bact.getPosition().z);
		//sphere((float)bact.getRadius());
		//popMatrix();
	}
		
	/**
	 * Draw a repressilator GRN bacterium
	 */
	public void draw(BSimRepBacterium bact){
		// TODO: 	Restore colour changing behaviour without having to write more
		//			rod transform code in here
		draw((BSimBacterium)bact);
	}
		
	/**
	 * Draw a rod shape, aligned with Processing's global y axis.
	 */
	// TODO: 	why is there both a radius and diameter? Is diameter actually 'length'?
	public void drawRodShape(float radius, float diameter, int sides) {
		  float angle = 0;
		  float angleIncrement = TWO_PI / sides;
		  float diameterRatio = -(diameter/2)+radius;
		  
		  beginShape(QUAD_STRIP);
		  for (int i = 0; i < sides + 1; ++i) {
		    vertex(radius*cos(angle), 0 + diameterRatio, radius*sin(angle));
		    vertex(radius*cos(angle), 0 - diameterRatio, radius*sin(angle));
		    angle += angleIncrement;
		  }
		  endShape();
		  
		  //bottom capping sphere
		  pushMatrix();
		  translate(0,0 + diameterRatio,0);
		  sphere(radius);
		  popMatrix();
		  
		  //top capping sphere
		  pushMatrix();
		  translate(0,0 - diameterRatio,0);
		  sphere(radius);
		  popMatrix();
	}
	
	
	/**************************** BSimChemicalField Draw Methods ****************************/
	/**
	 * Draw a BSimChemicalField grid in the scene space
	 */
	public void draw(BSimChemicalField field){
		if(field.getDisplayed()){
			// Initialise variables
			double[][][] theField = field.getField();
	        int nBoxX = 100, nBoxY = 100, nBoxZ = 100;
	        int fieldDimX, fieldDimY, fieldDimZ;
	        double conc;
	        double[] boxPos = new double[3];
	        
	        fieldDimX = theField.length;
	        fieldDimY = theField[0].length;
	        fieldDimZ = theField[0][0].length;
	        
	        if(fieldDimX<nBoxX){
	                nBoxX = fieldDimX;
	        }
	        if(fieldDimY<nBoxY){
	                nBoxY = fieldDimY;
	        }
	        if(fieldDimZ<nBoxZ){
	                nBoxZ = fieldDimZ;
	        }
			
	        // Draw the field
			noStroke();
			for (int i=0; i<nBoxX; i++){
				for(int j=0; j<nBoxY; j++){
					for(int k=0; k<nBoxZ;k++){
						boxPos[0] = (i + 0.5)*BSimScene.xBound/nBoxX;
						boxPos[1] = (j + 0.5)*BSimScene.yBound/nBoxY;
						boxPos[2] = (k + 0.5)*BSimScene.zBound/nBoxZ;

						conc = 0;
						// If the (drawn) boxes are not equivalent to those of the actual field then take local averages
						if(nBoxX != fieldDimX || nBoxY != fieldDimY || nBoxZ != fieldDimZ){
							for(int bi = floor((float)i*(float)fieldDimX/(float)nBoxX); bi<ceil(((float)i+1.0f)*(float)fieldDimX/(float)nBoxX); bi++){
								for(int bj = floor((float)j*(float)fieldDimY/(float)nBoxY); bj<ceil(((float)j+1.0f)*(float)fieldDimY/(float)nBoxY); bj++){
									for(int bk = floor((float)k*(float)fieldDimZ/(float)nBoxZ); bk<ceil(((float)k+1.0f)*(float)fieldDimZ/(float)nBoxZ); bk++){
										conc += theField[bi][bj][bk];
									}
								}
							}
							conc = conc/(((float)fieldDimX/(float)nBoxX)*((float)fieldDimY/(float)nBoxY)*((float)fieldDimZ/(float)nBoxZ));
						// Otherwise the drawn box concentration simply corresponds to the actual field
						}else{
							conc = theField[i][j][k];
						}
						// Cheat (this won't look so good if for example a bacteria releases a small amount 
						// of chemical as it is not an average for the box):
						// conc = fGoal.getConcentration(boxPos);

						fill((255*(float)conc), 0, 0,(25*(float)conc));
						pushMatrix();
						translate((float)boxPos[0], (float)boxPos[1], (float)boxPos[2]);
						box((float)BSimScene.xBound/nBoxX,(float)BSimScene.yBound/nBoxY, (float)BSimScene.zBound/nBoxZ);
						popMatrix();
					}
				}
			}
		}	
	}
	
	
	/***************************** Scene Boundary Draw Methods ******************************/
	/**
	 * Draw the scene boundary
	 */
	public void drawBoundary(){
		fill(128, 128, 255, 25);
		stroke(255);
		pushMatrix();
		translate((float)boundCentre[0],(float)boundCentre[1],(float)boundCentre[2]);
		box((float)BSimScene.xBound, (float)BSimScene.yBound, (float)BSimScene.zBound);
		popMatrix();
		noStroke();
	}
	
	
	/******************************* GUI Overlay Draw Methods *******************************/
	/**		These should be drawn last to prevent other stuff being drawn over them			*/
	/**
	 * Draw the current Simulation time to screen
	 */
	public void drawTime(){
		fill(255);
		text(BSimUtils.formatTime(scene.getTimeStep() * BSimScene.dt), 10, 30);	
	}
	
	/**
	 * Draw the estimate frames per second to screen
	 */
	public void drawFPS(){
		fill(255);
		text(frameRate, 300, 30);
	}
	
}


