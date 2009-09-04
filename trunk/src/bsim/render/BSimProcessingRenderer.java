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
import bsim.BSimParameters;
import bsim.field.BSimChemicalField;
import bsim.particle.BSimParticle;
import bsim.particle.bacterium.BSimBacterium;
import bsim.particle.bacterium.BSimRepBacterium;
import bsim.particle.bead.BSimBead;
import bsim.particle.vesicle.BSimVesicle;
import bsim.scene.BSimScene;


public class BSimProcessingRenderer extends PApplet implements BSimRenderer {
	
	// Simulation core
	public BSimScene scene = null;
	
	// The simulation objects
	public Vector<BSimBacterium> bacteria = null;
	public Vector<BSimVesicle> vesicles = null;
	public Vector<BSimBead> beads = null;
	
	// Processing and PApplet
	public int w;
	public int h;
	public int maximumFPS;
	PFont myFont;

	// Camera parameters
	public double minDistance;
	public double maxDistance;
	public double defaultDistance;
	public PeasyCam cam = null;
		
	
	/**
	 * Standard constructor for the renderer
	 */
	public BSimProcessingRenderer(BSimScene newScene) {
		// BSimScene used in the core simulation
		// TODO: check up on references vs instances in java :s
		scene = newScene;
			
		// PApplet parameters
		w = BSimParameters.screenWidth;
		h = BSimParameters.screenHeight;
		maximumFPS = 25;
		
		// Camera specific parameters
		// TODO: get them from the parameters file?
		minDistance = 20;
		maxDistance = 1000;
		defaultDistance = 200;
//		minDistance = scene.getParams().getMinimumDistance();
//		maxDistance =	scene.getParams().getMaximumDistance();
//		defaultDistance = scene.getParams().getDefaultDistance();
		
		// Get simulation objects from the scene
		bacteria = scene.getBacteria();
		vesicles = scene.getVesicles();
		beads = scene.getBeads();
	}
	
	
	/************************ Processing's initialisation and drawing ************************/
	/**
	 * Setup the PApplet; initialise the Processing renderer and its settings
	 * TODO: 	Passing/selecting colours so that e.g. draw(sensingBacterium) can call 
	 * 			draw(bacterium) but be a different colour
	 */
	public void setup(){
		// Initialise the Processing renderer:
		// TODO: 	correct width and height parameters
		// TODO: 	correlate with window width/height and that blasted resizing bug...!
		size(w, h, P3D);

		// Set our target frame rate:
		frameRate(maximumFPS);

		// Set up and initialise PeasyCam:
		cam = new PeasyCam(this, (float) defaultDistance);
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
	 *  Draw all objects in the scene 
	 *  By default Processing loops this automatically based on the FPS set above with maximumFPS,
	 *  however we can control whether looping is on or off with loop() and noLoop() respectively.
	 */
	public void draw(){
		// Clean slate, otherwise we end up drawing on top of the previous frame.
		background(255,120,200);

		// TODO: 	Draw the scene boundaries.
		//someDrawBoundariesMethod(Boundaries b){}

		// Don't want 3D lighting to slow down our chemical field rendering...
		//noLights();

		// Chemical fields
		//TODO: 	time to vectorise our chemical fields or something like that

		// Add Processing's 3D lighting.
		lights();
		
		// Doesn't draw an object if it doesn't exist...
		// might need to think of a better way of doing this (or go back to old style loops).
		// Bacteria
		try{
			for(BSimBacterium bact : bacteria) {
				draw(bact);
			}
		} catch(NullPointerException ignore){}
		// Vesicles
		try{
			for(BSimVesicle ves : vesicles) {
				draw(ves);
			}
		} catch(NullPointerException ignore){}
		// Particles
		try{

			for(BSimBead bead : beads) {
				draw(bead);
			}
		} catch(NullPointerException ignore){}
		
		// Text overlays; drawn last so they're on top of everything else.
		drawTime();
		drawFPS();
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
		fill(255, 131, 223);
		pushMatrix();
		translate((float)vesicle.getPosition().x, (float)vesicle.getPosition().y,(float)vesicle.getPosition().z);
		sphere((float)(vesicle.getRadius()));			
		popMatrix();
	}


	/****************************** BSimBacterium Draw Methods *****************************/
	/**
	 * Draw a standard BSimBacterium
	 */
	public void draw(BSimBacterium bact){
		fill(0);
		
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
		// OK can get rid of this for now. Still on the repository after all...
		// Draw chemical before drawing bacteria, or they all disappear into the fog!		
	}
	
	
	/***************************** Scene Boundary Draw Methods ******************************/
	/**
	 * Draw the scene boundary
	 */
	// TODO: 	base this on the new boundary implementation when its done:
	/*
	 * The old code:
	 */
//	double[] centrePos= boundingBox.getCentrePos();
//	pushMatrix();
//	translate((float)centrePos[0],(float)centrePos[1],(float)centrePos[2]);
//	stroke(255);
//	noFill();
//	box((float)boundingBox.getLength(), (float)boundingBox.getWidth(), (float)boundingBox.getDepth());
//	noStroke();
//	popMatrix();
	
	
	/******************************* GUI Overlay Draw Methods *******************************/
	/**		These should be drawn last to prevent other stuff being drawn over them			*/
	/**
	 * Draw the current Simulation time to screen
	 */
	public void drawTime(){
		fill(255);
		text(scene.getFormatedTime(), 10, 30);	
	}
	
	/**
	 * Draw the estimate frames per second to screen
	 */
	public void drawFPS(){
		fill(255);
		text(frameRate, 300, 30);
	}
	
}


