package bsim.draw;

import java.awt.Graphics;

import javax.vecmath.Vector3d;

import processing.core.PFont;
import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimDrawer;

public abstract class BSimP3DDrawer extends BSimDrawer {

	PGraphics3D p3d;
	PFont font;
	
	public BSimP3DDrawer(BSim sim, int width, int height) {
		super(sim, width, height);				
		/* See 'Subclassing and initializing PGraphics objects'
		 * http://dev.processing.org/reference/core/ */
		p3d = new PGraphics3D();
		p3d.setPrimary(true); 
		p3d.setSize(width, height);				
		font = new PFont(PFont.findFont("Tahoma").deriveFont((float)20), true, PFont.DEFAULT_CHARSET);
	}

	public void draw(Graphics g) {			
		p3d.beginDraw();
		
		p3d.textFont(font);
		p3d.textMode(p3d.SCREEN);

		p3d.sphereDetail(10);
		p3d.noStroke();		
		p3d.background(0, 0, 0);
		
		Vector3d bound = new Vector3d(sim.getBound());
		Vector3d boundCentre = new Vector3d(bound);
		boundCentre.scale(0.5);
		
		p3d.beginCamera();
		p3d.camera((float)bound.x, (float)bound.y, (float)bound.z*2, (float)boundCentre.x, (float)boundCentre.y, (float)boundCentre.z, 0, 1, 0);
		p3d.endCamera();
			
		draw(p3d);
		
		p3d.fill(128, 128, 255, 25);
		p3d.stroke(255);
		p3d.pushMatrix();
		p3d.translate((float)boundCentre.x,(float)boundCentre.y,(float)boundCentre.z);
		p3d.box((float)bound.x, (float)bound.y, (float)bound.z);
		p3d.popMatrix();
		p3d.noStroke();
		
		p3d.fill(255);
		p3d.text(sim.getTime(), 100, 100);
		
		p3d.endDraw();		
		g.drawImage(p3d.image, 0,0, null);
	}
	
	/**
	 * Draws remaining scene objects to the PGraphics3D object
	 */
	public abstract void draw(PGraphics3D p3d);

}
