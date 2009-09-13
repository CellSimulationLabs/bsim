package bsim;

import java.awt.Graphics;

public abstract class BSimDrawer {
	
	protected BSim sim;
	protected int width;
	protected int height;	
	
	public BSimDrawer(BSim sim, int width, int height) {
		this.sim = sim;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Draws the scene to a graphics object
	 */
	public abstract void draw(Graphics g);
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
}
