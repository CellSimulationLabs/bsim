package bsim.rendering;

import java.io.File;
import java.util.Vector;

import processing.core.*;
import processing.video.MovieMaker;
import peasy.*;

import bsim.BSimScene;
import bsim.drawable.bacteria.*;
import bsim.drawable.boundary.*;



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
	protected PeasyCam cam = null;
	
	//object used to start a video recording
	protected MovieMaker mm = null;	
	
	//core of the simulation
	public BSimScene scene=null;
	
	//simulation objects
	public Vector bacteria=null;
	public Vector solidBoxes=null;
	public Vector wrapBoxes=null;
	
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
		solidBoxes = scene.getSolidBoxes();
		wrapBoxes = scene.getWrapBoxes();
		

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
		
		myFont = createFont("FFScala", 20);
		textFont(myFont);
	}

	public void draw() {
		lights();
		background(0);
		
		//simulation time
		fill(255, 255, 255);
		textMode(SCREEN);
		text(scene.getFormatedTime(), 10, 30);
		
		
		for(int i=0;i<solidBoxes.size();i++){
			BSimBoxBoundary sb= (BSimBoxBoundary)solidBoxes.elementAt(i);
			double[] centrePos= sb.getCentrePos();
			pushMatrix();
			translate((float)centrePos[0],(float)centrePos[1],(float)centrePos[2]);
			stroke(255);
			noFill();
			box((float)sb.getLength(), (float)sb.getWidth(), (float)sb.getDepth());
			noStroke();
			popMatrix();
		}
		
		for(int i=0;i<wrapBoxes.size();i++){
			BSimBoxBoundary sb= (BSimBoxBoundary)wrapBoxes.elementAt(i);
			double[] centrePos= sb.getCentrePos();
			pushMatrix();
			translate((float)centrePos[0],(float)centrePos[1],(float)centrePos[2]);
			stroke(239,39,19);
			noFill();
			box((float)sb.getLength(), (float)sb.getWidth(), (float)sb.getDepth());
			noStroke();
			popMatrix();
		}
		
		for(int i=0;i<bacteria.size();i++){
			BSimBacterium bact = (BSimBacterium)bacteria.elementAt(i);
			pushMatrix();
			translate((float)bact.getPosition()[0], (float)bact.getPosition()[1],(float)bact.getPosition()[2]);
			fill(0, 255, 0);
			//fix the rotation on the axis
			rotateX((float)Math.acos(Math.sqrt(Math.pow(bact.getDirection()[0],2.0)+Math.pow(bact.getDirection()[1],2.0))/Math.sqrt(Math.pow(bact.getDirection()[0],2.0)+Math.pow(bact.getDirection()[1],2.0)+Math.pow(bact.getDirection()[2],2.0))));
			rotateZ((float)Math.acos(bact.getDirection()[1]/Math.sqrt(Math.pow(bact.getDirection()[0],2.0)+Math.pow(bact.getDirection()[1],2.0))));
			//radius of the roadShape, diameter (including caps), how many face has the cylinder.
			drawRodShape((float)0.4, (float)(bact.getRadius()*2),90);
			popMatrix();
		}		
		
		if(scene.getStartVideo()){
			loadPixels();
			mm.addFrame(pixels,width,height);
			updatePixels();
		}
		
		if(scene.getEndVideo()){
			scene.setEndVideo(false);
			mm.finish();
		}
	}
	
	
	//the RodShape is drawn along the y axis
	public void drawRodShape(float radius, float diameter, int sides) {
		  float angle = 0;
		  float angleIncrement = TWO_PI / sides;
		  beginShape(QUAD_STRIP);
		  for (int i = 0; i < sides + 1; ++i) {
		    vertex(radius*cos(angle), 0-(diameter/2)+radius, radius*sin(angle));
		    vertex(radius*cos(angle), 0+(diameter/2)-radius, radius*sin(angle));
		    angle += angleIncrement;
		  }
		  endShape();
		  
		  //bottom cap
		  pushMatrix();
		  translate(0,0-(diameter/2)+radius,0);
		  sphere(radius);
		  popMatrix();
		  
		  //top cap
		  pushMatrix();
		  translate(0,0+(diameter/2)-radius,0);
		  sphere(radius);
		  popMatrix();
	}
	
	public void createMovie(String videoFileName){		
		mm = new MovieMaker(this, width, height, videoFileName, frameRecordForSec, MovieMaker.JPEG, MovieMaker.LOSSLESS);
	}
	
	public void takeImage(String imageFileName){
		save(imageFileName);
	}
}
