package bsim.export;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import bsim.BSim;
import bsim.BSimExporter;

public class BSimImageExporter extends BSimExporter {

	private String directory;
	
	public BSimImageExporter(BSim sim, String directory) {
		super(sim);
		this.directory = directory;
	}
	
	@Override
	public void before() {}
	
	@Override
	public void during() {				
		BufferedImage img = new BufferedImage(sim.getWidth(), sim.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = img.createGraphics();				
		sim.draw(g);
		
		try {
			File file = new File(directory + "/" + sim.getTime() + ".png");
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void after() {}

}
