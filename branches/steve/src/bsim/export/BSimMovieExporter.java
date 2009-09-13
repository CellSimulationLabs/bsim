package bsim.export;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import bsim.BSim;

public class BSimMovieExporter extends BSimExporter {

	private QuickTimeOutputStream outputStream;
	private String filename;

	public BSimMovieExporter(BSim sim, String filename) {
		super(sim);
		this.filename = filename;		
	}
	
	public void before() {		
		try {
			outputStream = new QuickTimeOutputStream(new File(filename), QuickTimeOutputStream.VideoFormat.JPG);
			outputStream.setVideoCompressionQuality(1f);
			outputStream.setTimeScale(30);
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
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
	
	public void after() {
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
