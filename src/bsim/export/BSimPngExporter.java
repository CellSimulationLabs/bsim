package bsim.export;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import bsim.BSim;
import bsim.draw.BSimDrawer;

public class BSimPngExporter extends BSimExporter {

	protected String directory;
	protected BSimDrawer drawer;
	
	public BSimPngExporter(BSim sim, BSimDrawer drawer, String directory) {
		super(sim);
		this.drawer = drawer;
		this.directory = directory;
	}
	
	@Override
	public void before() {}
	
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
	
	@Override
	public void after() {}

}
