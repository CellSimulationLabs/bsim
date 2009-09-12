package bsim;

import java.awt.Graphics;

public abstract class BSimDrawer {
	
	protected int width;
	protected int height;
	
	public BSimDrawer(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public abstract void draw(Graphics g);
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
}
