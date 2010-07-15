package bsim.draw;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.vecmath.Vector3d;

import processing.core.PFont;
import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.particle.BSimParticle;
import bsim.particle.BSimVesicle;

public abstract class BSimP3DDrawer extends BSimDrawer {

	protected PGraphics3D p3d;
	protected PFont font;
	protected Vector3d bound;
	protected Vector3d boundCentre;

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
	public void draw(Graphics2D g) {			
		p3d.beginDraw();

		p3d.textFont(font);
		p3d.textMode(p3d.SCREEN);

		p3d.sphereDetail(10);
		p3d.noStroke();		
		p3d.background(0, 0, 0);	

		scene(p3d);
		boundaries();
		time();

		p3d.endDraw();
		g.drawImage(p3d.image, 0,0, null);
	}
			
	/**
	 * Draws remaining scene objects to the PGraphics3D object
	 */
	public abstract void scene(PGraphics3D p3d);
	
	public void boundaries() {
		p3d.fill(128, 128, 255, 50);
		p3d.stroke(128, 128, 255);
		p3d.pushMatrix();
		p3d.translate((float)boundCentre.x,(float)boundCentre.y,(float)boundCentre.z);
		p3d.box((float)bound.x, (float)bound.y, (float)bound.z);
		p3d.popMatrix();
		p3d.noStroke();
	}
	
	public void boundaryOutline() {
		p3d.noFill();
		p3d.stroke(128, 128, 255);
		p3d.pushMatrix();
		p3d.translate((float)boundCentre.x,(float)boundCentre.y,(float)boundCentre.z);
		p3d.box((float)bound.x, (float)bound.y, (float)bound.z);
		p3d.popMatrix();
		p3d.noStroke();
	}
	
	public void time() {
		p3d.fill(255);
		p3d.text(sim.getFormattedTime(), 50, 50);
	}

	public void draw(BSimParticle p, Color c) {
		if (p.getRadius() < 1) point(p.getPosition(), c);
		else sphere(p.getPosition(), p.getRadius(), c, 255);
	}
		
	public void draw(BSimVesicle v, Color c) {	
		sphere(v.getPosition(), 100*v.getRadius(), c, 50);
		point(v.getPosition(),c);
	}

	public void sphere(Vector3d position, double radius, Color c, int alpha) {
		p3d.pushMatrix();
		p3d.translate((float) position.x, (float) position.y, (float) position.z);
		p3d.fill(c.getRed(), c.getGreen(), c.getBlue(), alpha);
		p3d.sphere((float) radius);
		p3d.popMatrix();
	}
	
	public void point(Vector3d position, Color c) {
		p3d.stroke(c.getRed(), c.getGreen(), c.getBlue());
		p3d.point((float)position.x, (float)position.y,(float)position.z);
		p3d.noStroke();
	}

	/**
	 * Draws a chemical field with alpha per unit concentration alphaGrad
	 */
	public void draw(BSimChemicalField field, Color c, float alphaGrad) {
		int[] boxes = field.getBoxes();
		double[] boxSize = field.getBox();				
		for(int i=0; i < boxes[0]; i++)
			for(int j=0; j < boxes[1]; j++)
				for(int k=0; k < boxes[2]; k++) {							
					p3d.pushMatrix();					
					p3d.translate((float)(boxSize[0]*i+boxSize[0]/2), (float)(boxSize[1]*j+boxSize[1]/2), (float)(boxSize[2]*k+boxSize[2]/2));
					p3d.fill(c.getRed(),c.getGreen(),c.getBlue(),alphaGrad*(float)field.getConc(i,j,k));
					p3d.box((float)boxSize[0],(float)boxSize[1],(float)boxSize[2]);
					p3d.popMatrix();
				}
	}

}
