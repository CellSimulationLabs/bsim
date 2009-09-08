/**
 * BSimDisplayWindow.java
 * 
 * A new frame (window) to display a rendered viewport of the current scene.
 * 
 */

package bsim.app;

import javax.swing.JFrame;

public class BSimDisplayWindow extends JFrame{
	BSimApp app;
	
	/**
	 * Default constructor for a display window.
	 * Creates a display window with an embedded Processing P3D renderer.
	 */
	BSimDisplayWindow(BSimApp newApp){
		super();
		app = newApp;
		
		this.setTitle("BSim Display");
		this.setSize(BSimApp.screenWidth, BSimApp.screenHeight);
		this.add(app.getRenderer());
		
		// Place the display window below the toolbar (BSimApp). Probably best as the toolbar
		// is guaranteed to start in the top left, therefore both windows should be visible when the app starts.
		this.setLocation(0, app.getHeight());
		this.setVisible(true);		
	}
	
	/**
	 * Resets the display window
	 */
	void reset(){
		this.setSize(BSimApp.screenWidth, BSimApp.screenHeight);
	}
	
	
	/**
	 * Check for resize events: 
	 * Moved these from BSimScene - this is probably not needed as PApplet seems to have it built in.
	 * However, there are still issues with peasyCam not keeping its position on resize.
	 */
//	public void componentResized(ComponentEvent e) {
//		
//	    // Check to see if it is the panel and if so reset the camera
//		// This fix doesn't appear to work at the moment.
//		if(e.getComponent() == this){
//			app.processingRenderer.resetCamera();
//			app.processingRenderer.setCamState();
//		}
//	}
//	
//	/**
//	 * Not overwritten by this class.
//	 */
//	public void componentHidden(ComponentEvent e) {}
//	/**
//	 * Not overwritten by this class.
//	 */
//	public void componentMoved(ComponentEvent e) {}
//	/**
//	 * Not overwritten by this class.
//	 */
//	public void componentShown(ComponentEvent e) {}
	
}
