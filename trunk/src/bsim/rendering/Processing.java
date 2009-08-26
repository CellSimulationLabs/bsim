/**
 * Processing.java
 * 
 * This class uses the Processing library functions to draw the scene.
 * 
 * Authors:	Mattia Fazzini
 * 			Antoni Matyjaszkiewicz
 * Created:	17/8/09
 * Updated:	21/8/09
 */


package bsim.rendering;

import java.util.Vector;

import javax.vecmath.Vector3f;

import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PFont;
import processing.video.MovieMaker;
import bsim.BSimBoundingBox;
import bsim.BSimScene;
import bsim.field.BSimChemicalField;
import bsim.particle.bacterium.BSimBacterium;
import bsim.particle.vesicle.BSimVesicle;


public class Processing extends PApplet {
	
	//Papplet parameters
	public int w;
	public int h;
	public int frameForSec;
	public int frameRecordForSec;
	public double minDistance;
	public double maxDistance;
	public double defaultDistance;
	
	//object to handle the simulation camera
	public PeasyCam cam = null;
	
	//object used to start a video recording
	public MovieMaker mm = null;	
	
	//core of the simulation
	public BSimScene scene=null;
	
	//simulation objects
	public Vector bacteria=null;
	public Vector vesicles=null;
	public BSimBoundingBox boundingBox=null;
	
	public BSimChemicalField fGoal = null;
	public double[][][] theField;
	// The number of x,y,z boxes used for display
	public int nBoxX = 20, nBoxY = 20, nBoxZ = 20;
	public int fieldDimX, fieldDimY, fieldDimZ;
	public double conc;
	public double[] boxPos = new double[3];
	public boolean fieldIsDisplayed = false;
	
	PFont myFont;

	public Processing(BSimScene newScene) {
		//core of the simulation
		scene=newScene;
			
		//PApplet parameters
		w = scene.getParams().getScreenWidth();
		h = scene.getParams().getScreenHeight();
		frameForSec = scene.getParams().getFrameForSec();
		frameRecordForSec = scene.getParams().getFrameRecordForSec();
		
		//camera parameters
		minDistance = scene.getParams().getMinimumDistance();
		maxDistance =	scene.getParams().getMaximumDistance();
		defaultDistance = scene.getParams().getDefaultDistance();
		
		//object into the simulation
		bacteria = scene.getBacteria();
		vesicles= scene.getVesicles();
		boundingBox = scene.getBoundingBox();		
		
		//Chemical field bits
		fGoal = scene.getGoalField();
		fieldIsDisplayed = (boolean)fGoal.getDisplayed();
		fieldDimX = fGoal.getField().length;
		fieldDimY = fGoal.getField()[0].length;
		fieldDimZ = fGoal.getField()[0][0].length;
		
		if(fieldDimX<nBoxX){
			nBoxX = fieldDimX;
		}
		if(fieldDimY<nBoxY){
			nBoxY = fieldDimY;
		}
		if(fieldDimZ<nBoxZ){
			nBoxZ = fieldDimZ;
		}
		// This will cause the number of boxes displayed to be exactly equal to those in the chemical
		// field calculations. WARNING this is VERY slow for a large number of boxes so better to
		// just keep display box number constant as above.
		//nBoxX = fGoal.getField().length;
		//nBoxY = fGoal.getField()[0].length;
		//nBoxZ = fGoal.getField()[0][0].length;
	}

	// Yes, this is the P5 setup()
	public void setup() {
		size(w, h, P3D);
		//frame for sec
		frameRate(frameForSec);
		//noStroke() must be here so every object will be draw without stroke
		noStroke();
		//Peasy Cam usage
		cam = new PeasyCam(this, (float) defaultDistance);
		cam.setMinimumDistance((float) minDistance);
		cam.setMaximumDistance((float) maxDistance);
		//simulation time font
		myFont = createFont("FFScala", 20);
		textFont(myFont);
	}

	public void draw() {
		lights();
		background(0);
				
					
			double[] centrePos= boundingBox.getCentrePos();
			pushMatrix();
			translate((float)centrePos[0],(float)centrePos[1],(float)centrePos[2]);
			stroke(255);
			noFill();
			box((float)boundingBox.getLength(), (float)boundingBox.getWidth(), (float)boundingBox.getDepth());
			noStroke();
			popMatrix();
		
		
		//TODO: should have all 3 (more?) chemical fields and combine for drawing (how slow...)
		// Draw chemical before drawing bacteria, or they all disappear into the fog!
		if(fieldIsDisplayed){
			theField = fGoal.getField();
			noStroke();
			
			for (int i=0; i<nBoxX; i++){
				for(int j=0; j<nBoxY; j++){
					for(int k=0; k<nBoxZ;k++){
						boxPos[0] = (i + 0.5)*boundingBox.getLength()/nBoxX;
						boxPos[1] = (j + 0.5)*boundingBox.getWidth()/nBoxY;
						boxPos[2] = (k + 0.5)*boundingBox.getDepth()/nBoxZ;
						
						// This seems to work... but only for evenly divisible by nBox.. grrr
						// TODO: lots of optimisation and fixing
						conc = 0;
						for(int bi = floor((float)i*(float)fieldDimX/(float)nBoxX); bi<ceil(((float)i+1.0f)*(float)fieldDimX/(float)nBoxX); bi++){
							for(int bj = floor((float)j*(float)fieldDimY/(float)nBoxY); bj<ceil(((float)j+1.0f)*(float)fieldDimY/(float)nBoxY); bj++){
								for(int bk = floor((float)k*(float)fieldDimZ/(float)nBoxZ); bk<ceil(((float)k+1.0f)*(float)fieldDimZ/(float)nBoxZ); bk++){
									conc += theField[bi][bj][bk];
								}
							}
						}
						conc = conc/(((float)fieldDimX/(float)nBoxX)*((float)fieldDimY/(float)nBoxY)*((float)fieldDimZ/(float)nBoxZ));
						
						// Cheat (this won't look so good if for example a bacteria 
						// releases a small amount of chemical as it is not an average for the box):
						// conc = fGoal.getConcentration(boxPos);
						
						fill((255*(float)conc), 0, 0,(25*(float)conc));
						pushMatrix();
						translate((float)boxPos[0], (float)boxPos[1], (float)boxPos[2]);
						box((float)boundingBox.getLength()/nBoxX,(float)boundingBox.getWidth()/nBoxY, (float)boundingBox.getDepth()/nBoxZ);
						popMatrix();
					}
				}
			}
		}
	
		for(int i=0;i<bacteria.size();i++){
			BSimBacterium bact = (BSimBacterium)bacteria.elementAt(i);
			//TODO: clean up variables for the rotation part to speed up a bit
			Vector3f worldY = new Vector3f(0,1,0);
			Vector3f bacDirVector = new Vector3f(bact.getDirection());
			Vector3f bacRotVector = new Vector3f();
			bacRotVector.cross(worldY,bacDirVector);
			//normalising slows us down... but method breaks without normalising so obviously some vectors have not been normalised by this point
			bacDirVector.normalize();
			bacRotVector.normalize();
			pushMatrix();
				translate((float)bact.getPosition().x, (float)bact.getPosition().y,(float)bact.getPosition().z);
				fill(0, 255, 0);		
				//fix the rotation on the axis
				//pushMatrix();
					rotate(worldY.angle(bacDirVector), bacRotVector.x, bacRotVector.y, bacRotVector.z);
					drawRodShape((float)0.4, (float)(bact.getRadius()*2),90);
				//popMatrix();
				//Draw the direction
				//translate(bacDirVector.x, bacDirVector.y,bacDirVector.z);
				//fill(255,0,0);
				//sphere(0.25f);
			popMatrix();
		}		
		
		for(int i=0;i<vesicles.size();i++){
			BSimVesicle vesicle = (BSimVesicle)vesicles.elementAt(i);
			pushMatrix();
			translate((float)vesicle.getPosition().x, (float)vesicle.getPosition().y,(float)vesicle.getPosition().z);
			fill(255, 131, 223);
			sphere((float)(vesicle.getRadius()));			
			popMatrix();
		}
		
		//Draw text last so it is on top
		//simulation time
		fill(255, 255, 255);
		textMode(SCREEN);
		text(scene.getFormatedTime(), 10, 30);	
		
		if(scene.getGuiExists()){
			if(scene.getStartVideo()){
				loadPixels();
				if(mm!=null){
					mm.addFrame();		
				}
				updatePixels();
			}
			
			if(scene.getEndVideo()){
				if(mm!=null){
					mm.finish();
					scene.setEndVideo(false);
					scene.setWaitingForVideoClosing(false);
				}
			}
		}
	}
	
	//the RodShape is drawn along the y axis
	public void drawRodShape(float radius, float diameter, int sides) {
		  float angle = 0;
		  float angleIncrement = TWO_PI / sides;
		  // save a bunch of calculations:
		  float diameterRatio = -(diameter/2)+radius;
		  beginShape(QUAD_STRIP);
		  for (int i = 0; i < sides + 1; ++i) {
		    vertex(radius*cos(angle), 0 + diameterRatio, radius*sin(angle));
		    vertex(radius*cos(angle), 0 - diameterRatio, radius*sin(angle));
		    angle += angleIncrement;
		  }
		  endShape();
		  
		  //bottom cap
		  pushMatrix();
		  translate(0,0 + diameterRatio,0);
		  sphere(radius);
		  popMatrix();
		  
		  //top cap
		  pushMatrix();
		  translate(0,0 - diameterRatio,0);
		  sphere(radius);
		  popMatrix();
	}
		
	public void createMovie(String videoFileName){		
		mm = new MovieMaker(this, w, h, videoFileName, frameRecordForSec, MovieMaker.JPEG, MovieMaker.LOSSLESS);
		scene.setWaitingForVideoOpening(false);
	}
	
	public void takeImage(String imageFileName){
		save(imageFileName);
	}
	
	public void addMovieFrame(){
		loadPixels();
		mm.addFrame();
		updatePixels();
	}
	
	public void closeMovie(){
		mm.finish();
		scene.setWaitingForVideoClosing(false);
	}
}
