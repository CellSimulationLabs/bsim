package bsim.export;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import org.freehep.graphicsio.pdf.PDFGraphics2D;

import bsim.BSim;
import bsim.draw.BSimDrawer;

/**
 * Exports graphics to a PDF, best if the drawer draws the raw shape information (not an image)
 */
public class BSimPdfExporter extends BSimDrawingExporter {

	protected String directory;
	
	public BSimPdfExporter(BSim sim, BSimDrawer drawer, String directory) {
		super(sim, drawer);
		this.directory = directory;
	}
	
	@Override
	public void before() {}
	
	@Override
	public void during() {						
		PDFGraphics2D g;
		try {
			g = new PDFGraphics2D(new File(directory + "/"+ sim.getFormattedTime()+".pdf"), new Dimension(drawer.getWidth(),drawer.getHeight()));
		    g.startExport(); 			
			drawer.draw(g);
			g.endExport();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public void after() {}

}
