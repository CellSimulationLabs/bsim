package bsim.app;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;

import bsim.BSimParameters;
import bsim.render.BSimProcessingRenderer;

public class BSimDisplayWindow extends JFrame implements ComponentListener{

	BSimProcessingRenderer renderer;
	BSimApp app;
	/*
	 * Default constructor for a display window.
	 * Creates a display window with an embedded Processing P3D renderer.
	 */
	BSimDisplayWindow(BSimProcessingRenderer newRenderer, BSimApp newApp){
		super();
		app = newApp;
		renderer = newRenderer;
		
		this.setTitle("BSim Display");
		this.setSize(BSimParameters.screenWidth, BSimParameters.screenHeight);
		
		this.add(renderer);
		
		// Place the display window below the toolbar (BSimApp). Probably best as the toolbar
		// is guaranteed to start in the top left, therefore both windows should be visible when the app starts.
		this.setLocation(0, app.getHeight());
		
		this.setVisible(true);
		
	}
	
	/*
	 * Resets the display window
	 */
	void reset(){
		// Things here soon...
	}
	
	/**
	 * Check for resize events: 
	 * moved from BSimScene - this is probably not needed as PApplet seems to have it built in.
	 */
	public void componentResized(ComponentEvent e) {
		
	    // Check to see if it is the panel and if so reset the camera
//		if(e.getComponent() == this){
//			app.processingRenderer.resetCamera();
//		}         
	}
	
	/**
	 * Not overwritten by this class.
	 */
	public void componentHidden(ComponentEvent e) {}
	/**
	 * Not overwritten by this class.
	 */
	public void componentMoved(ComponentEvent e) {}
	/**
	 * Not overwritten by this class.
	 */
	public void componentShown(ComponentEvent e) {}
	
}
