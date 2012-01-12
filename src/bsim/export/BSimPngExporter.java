package bsim.export;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import bsim.BSim;
import bsim.draw.BSimDrawer;

/**
 * Image file exporter.
 * Images are generated for each timestep and output to a given directory.
 * These are named with a timestamp and output in a PNG format.
 */
public class BSimPngExporter extends BSimExporter {

	/** Directory to output images to. */
	protected String directory;
	/** Drawer to generate each image. */
	protected BSimDrawer drawer;
	
	/**
	 * Constructor for the image exporter
	 * @param sim Associated simulation.
	 * @param drawer Drawer to generate images.
	 * @param directory Directory to output images to.
	 */
	public BSimPngExporter(BSim sim, BSimDrawer drawer, String directory) {
		super(sim);
		this.drawer = drawer;
		this.directory = directory;
	}
	
	/**
	 * Called before a simulation starts.
	 */
	@Override
	public void before() {}
	
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
			File file = new File(directory + "/" + sim.getFormattedTime() + ".png");
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Called after the simulation ends.
	 */
	@Override
	public void after() {}

}
