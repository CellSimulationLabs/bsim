package bsim.export;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import bsim.BSim;
import bsim.draw.BSimDrawer;
import bsim.export.quicktime.QuickTimeOutputStream;

/**
 * Movie file exporter.
 */
public class BSimMovExporter extends BSimExporter {

	/** Movie that is output to. */
	protected QuickTimeOutputStream outputStream;
	/** Filename of the output movie. */
	protected String filename;
	/** Speed of the movie. */
	protected int speed = 1;
	/** Drawer to generate a movie frame. */
	protected BSimDrawer drawer;

	/**
	 * Constructor for the movie exporter.
	 * @param sim Associated simulation.
	 * @param drawer Drawer to generate movie frames.
	 * @param filename Output movie filename.
	 */
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
	
	/**
	 * Called before the simulation starts.
	 */
	@Override
	public void before() {		
		try {
			outputStream = new QuickTimeOutputStream(new File(filename), QuickTimeOutputStream.VideoFormat.JPG);			
			int timescale = (int)((double)speed*(1.0/(double)this.getDt()));
			if (timescale < 1) timescale = 1;
			outputStream.setTimeScale(timescale);
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
	/**
	 * Called at each timestep of the simulation.
	 */
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
	
	/**
	 * Called after the simulation ends.
	 */
	@Override
	public void after() {
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
