package bsim.draw;

import java.awt.Graphics2D;

import bsim.BSim;

public abstract class BSimDrawer {
	
	protected BSim sim;
	protected int width;
	protected int height;	
	
	public BSimDrawer(BSim sim, int width, int height) {
		this.sim = sim;
		this.width = width;
		this.height = height;
	}
	
	public abstract void draw(Graphics2D g);
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
}
