package bsim.draw;

import java.awt.Graphics;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimDrawer;

public abstract class BSimP3DDrawer extends BSimDrawer {

	public BSimP3DDrawer(BSim sim, int width, int height) {
		super(sim, width, height);
	}

	public void draw(Graphics g) {
		/* See 'Subclassing and initializing PGraphics objects'
		 * http://dev.processing.org/reference/core/ */
		PGraphics3D p3d = new PGraphics3D();
		p3d.setPrimary(true); 
		p3d.setSize(width, height);
		p3d.beginDraw();

		p3d.sphereDetail(10);
		p3d.noStroke();		
		p3d.background(0, 0, 0);

		particles(p3d);
		
		p3d.endDraw();		
		g.drawImage(p3d.image, 0,0, null);
	}
	
	public abstract void particles(PGraphics3D p3d);

}
