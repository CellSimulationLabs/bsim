package bsim.draw;

import java.awt.Graphics2D;

import bsim.BSim;

/**
 * Drawer base class.
 * Does not implement any drawing itself, but should be extended by the user.
 */
public abstract class BSimDrawer {
	
	/** The simulation. */
	protected BSim sim;
	/** Width of the display (pixels). */
	protected int width;
	/** Height of the display (pixels). */
	protected int height;	
	
	/**
	 * Constructor for a drawer. Sets internal references and size of the display.
	 * @param sim The simulation.
	 * @param width Width of the display (pixels).
	 * @param height Height of the display (pixels).
	 */
	public BSimDrawer(BSim sim, int width, int height) {
		this.sim = sim;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Abstract method to draw the simulation to a graphics context (to be overwritten by the user).
	 * @param g Graphics context to draw the scene to.
	 */
	public abstract void draw(Graphics2D g);
	
	/** Return the width of the display (pixels). */
	public int getWidth() { return width; }
	/** Return the height of the display (pixels). */
	public int getHeight() { return height; }
}
