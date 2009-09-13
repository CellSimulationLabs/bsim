package bsim.export;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import bsim.BSim;
import bsim.BSimExporter;
import bsim.export.quicktime.QuickTimeOutputStream;

public class BSimMovieExporter extends BSimExporter {

	private QuickTimeOutputStream outputStream;
	private String filename;
	private int speed = 1;

	public BSimMovieExporter(BSim sim, String filename) {
		super(sim);
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
			outputStream.setVideoCompressionQuality(1f);
			outputStream.setTimeScale(speed*(int)(1/this.getDt()));
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
	@Override
	public void during() {			
		BufferedImage img = new BufferedImage(sim.getWidth(), sim.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = img.createGraphics();
		sim.draw(g);
        
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
