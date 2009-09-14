package bsim.draw;

import java.awt.Graphics;

import javax.vecmath.Vector3d;

import processing.core.PFont;
import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimDrawer;

public abstract class BSimP3DDrawer extends BSimDrawer {

	private PGraphics3D p3d;
	private PFont font;
	private Vector3d bound;
	private Vector3d boundCentre;
	
	public BSimP3DDrawer(BSim sim, int width, int height) {
		super(sim, width, height);		
		bound = sim.getBound();
		boundCentre = new Vector3d();
		boundCentre.scale(0.5, bound);		
		/* See 'Subclassing and initializing PGraphics objects'
		 * http://dev.processing.org/reference/core/ */
		p3d = new PGraphics3D();
		p3d.setPrimary(true); 
		p3d.setSize(width, height);				
		p3d.camera(-(float)bound.x*0.7f, -(float)bound.y*0.3f, -(float)bound.z*0.5f, (float)bound.x, (float)bound.y, (float)bound.z, 0, 1, 0);
		
		font = new PFont(PFont.findFont("Trebuchet MS").deriveFont((float)20), true, PFont.DEFAULT_CHARSET);
	}

	@Override
	public void draw(Graphics g) {			
		p3d.beginDraw();
		
		p3d.textFont(font);
		p3d.textMode(p3d.SCREEN);
		
		p3d.sphereDetail(10);
		p3d.noStroke();		
		p3d.background(0, 0, 0);	
			
		draw(p3d);
		
		p3d.fill(128, 128, 255, 50);
		p3d.stroke(128, 128, 255);
		p3d.pushMatrix();
		p3d.translate((float)boundCentre.x,(float)boundCentre.y,(float)boundCentre.z);
		p3d.box((float)bound.x, (float)bound.y, (float)bound.z);
		p3d.popMatrix();
		p3d.noStroke();
		
		p3d.fill(255);
		p3d.text(sim.getTime(), 50, 50);
		
		p3d.endDraw();		
		g.drawImage(p3d.image, 0,0, null);
	}
	
	/**
	 * Draws remaining scene objects to the PGraphics3D object
	 */
	public abstract void draw(PGraphics3D p3d);
}
