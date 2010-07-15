package bsim.export;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import bsim.BSim;
import bsim.draw.BSimDrawer;
import bsim.export.quicktime.QuickTimeOutputStream;

public class BSimMovExporter extends BSimExporter {

	protected QuickTimeOutputStream outputStream;
	protected String filename;
	protected int speed = 1;
	protected BSimDrawer drawer;

	public BSimMovExporter(BSim sim, BSimDrawer drawer, String filename) {
		super(sim);
		this.drawer = drawer;
		this.filename = filename;		
	}
	
	/**
	 * Speeds up the movie relative to simulation time by a factor f, that is,
	 * 1 second in the simulation will last 1/f seconds in the movie
	 */
	public void setSpeed(int f) {
		speed = f;		
	}
	
	@Override
	public void before() {		
		try {
			outputStream = new QuickTimeOutputStream(new File(filename), QuickTimeOutputStream.VideoFormat.JPG);			
			outputStream.setTimeScale(speed*(int)(1/this.getDt()));
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
	@Override
	public void during() {			
		BufferedImage img = new BufferedImage(drawer.getWidth(), drawer.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		drawer.draw(g);
		g.dispose();
		try {
			outputStream.writeFrame(img, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void after() {
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
