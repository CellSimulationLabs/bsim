package bsim.draw;

import java.awt.Graphics2D;

import processing.core.PGraphicsJava2D;

import bsim.BSim;

public abstract class BSimRawP3DDrawer extends BSimP3DDrawer {
	
	protected PGraphicsJava2D echo;
	
	public BSimRawP3DDrawer(BSim sim, int width, int height) {
		super(sim,width,height);
	}

	@Override
	public void draw(Graphics2D g) {
		/* Start clever hack */
		echo = new PGraphicsJava2D();
		echo.setSize(width, height);
		echo.g2 = g;
		/* End clever hack */
		
		p3d.beginRaw(echo);		
		p3d.beginDraw();
		p3d.sphereDetail(10);
		p3d.noStroke();	
		
		scene(p3d);
		boundaryOutline();
		
		p3d.endDraw();
		p3d.endRaw();
	}
	
}
