package bsim.export.visual;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import bsim.export.PngEncoder;

public class BSimScreenshotPNG {
	
	// TODO: get these to record an image from the renderer rather than from BSimScene (graphics)? Or create new methods.
	public void createImage(String filename) {
		
		// Ensure that the filename has the correct extension
		filename = filename + ".png";
		
		// Create the image required to hold the output
        // TODO: Broke this to get compiling on Mac
		//BufferedImage img = new BufferedImage(scene.getWidth(), 
		//	scene.getHeight(), 
		//	BufferedImage.TYPE_INT_RGB);
		BufferedImage img = new BufferedImage(100, 
			100, 
			BufferedImage.TYPE_INT_RGB);
					
		byte[] pngbytes;
		// PngEncoder.ENCODE_ALPHA, PngEncoder.NO_ALPHA
		PngEncoder png =  new PngEncoder( img,
			PngEncoder.NO_ALPHA, 0, 1);

        try
        {
            FileOutputStream outfile = new FileOutputStream( filename );
            pngbytes = png.pngEncode();
            if (pngbytes == null)
            {
                System.out.println("Null image");
            }
            else
            {
                outfile.write( pngbytes );
            }
            outfile.flush();
            outfile.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}
}
