package bsim.app.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import bsim.app.BSimApp;
import bsim.render.BSimProcessingRenderer;
import bsim.render.BSimRenderer;

public class BSimGUI extends JFrame{
	
	public static int screenWidth = 800;
	public static int screenHeight = 600;
	public static int toolbarHeight = 32;
	private static boolean guiEnabled = false;
	
	private BSimApp app;
	private BSimToolbar toolbar;
	private BSimDisplayWindow displayWindow;
	private BSimProcessingRenderer renderer;
	
	public BSimGUI(BSimApp newApp){
		// Create window
		super();
		
		app = newApp;
		
		// Setup the frame and its contents
		this.setTitle("BSim GUI");
		//this.setDefaultCloseOperation(EXIT_ON_CLOSE);//TODO: is this still necessary with the confirm box?
		this.getContentPane().setLayout(new BorderLayout());
		
		// Create the toolbar and align to bottom of window
		System.out.println(BSimToolbar.class.getResource("../../resource/icons/play.png"));
		toolbar = new BSimToolbar(app);
		this.getContentPane().add(toolbar, BorderLayout.SOUTH);
		
		// Squeeze the window nicely to the size of our toolbar
		pack();
		
		toolbarHeight = this.getHeight();
		
		// The toolbar is a fixed size
		this.setResizable(false);
		
		this.setVisible(true);
		
		renderer = app.getRenderer();
		renderer.init();
		
		displayWindow = new BSimDisplayWindow(this);
		
	}
	
	/**
	 * Reset the renderer in the display window with new scene data
	 */
	public void resetDisplay(int firstTime){
		displayWindow.reset();
		
		if(firstTime == 0){
			// Remove the PApplet from the display window
			displayWindow.remove(renderer);
			// Stop the animation thread
			renderer.destroy();
		}
		
	    renderer = new BSimProcessingRenderer(app);
	    
	    // Initialise new animation thread and add the PApplet to display window
	    renderer.init();
	    displayWindow.add(renderer);
	}
	
	
    /**
     * override processWindowEvent() method to provide 
     * the user with a handy confirmation box.
     */
    protected void processWindowEvent(WindowEvent e) {

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
        	// Show the confirmation window
            int exit = JOptionPane.showConfirmDialog(this, "This will end the current simulation. Are you sure?",
            												"Exit BSim",JOptionPane.YES_NO_OPTION);
            if (exit == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
           
        } else {
        	// Process whatever other window events e corresponds to
            super.processWindowEvent(e);
        }
    }
    
    /**
     * Enable or disable the GUI (usually in the setup process)
     */
	public static void guiEnable(){ guiEnabled = true; }
	public static void guiDisable(){ guiEnabled = false; }
	public static boolean guiState(){ return guiEnabled; }
	
	/*
	 * Get and set methods
	 */
	public BSimProcessingRenderer getRenderer(){ return renderer; }
}
