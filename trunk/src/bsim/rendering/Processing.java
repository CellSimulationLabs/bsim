/*
 *  Kite class
 * 
 *  By extending PApplet you can use your sweet old P5
 *  calls in a regular Java App. Pretty nice indeed!
 *
 */

package bsim.rendering;

import java.util.Vector;

import processing.core.*;
import peasy.*;

import bsim.drawable.bacteria.*;
import bsim.drawable.boundary.*;

public class Processing extends PApplet {
	
	public int l = 0;
	public int w = 0;
	public int frameForSec = 25;
	protected PeasyCam cam = null;
	public Vector bacteria=null;
	public Vector solidBoxes=null;
	public Vector wrapBoxes=null;
	
	// Constructor
	public Processing(int l, int w, int newFrameForSec) {
		this.l = l;
		this.w = w;
		this.frameForSec = 25;
		bacteria = new Vector();
		solidBoxes = new Vector();
		wrapBoxes = new Vector();
	}

	// Yes, this is the P5 setup()
	public void setup() {
		size(l, w, P3D);
		//frame for sec
		frameRate(frameForSec);
		//noStroke() must be here so every object will be draw without stroke
		noStroke();
		//Peasy Cam
		cam = new PeasyCam(this, 100);
		cam.setMinimumDistance(20);
		cam.setMaximumDistance(1000);
	}

	public void draw() {
		lights();
		background(0);
		
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
			sphere((float)(bact.getRadius()));
			popMatrix();
		}
	}
	
	public void setBacteria(Vector newBacteria)
	{
		bacteria = newBacteria;
	}
	
	public void setSolidBoxes(Vector newSolidBoxes)
	{
		solidBoxes = newSolidBoxes;
	}
	
	public void setWrapBoxes(Vector newWrapBoxes)
	{
		wrapBoxes = newWrapBoxes;
	}
}