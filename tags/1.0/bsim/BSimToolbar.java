/**
 * BSimToolbar.java
 *
 * Class to generate toolbar for the BSim user interface. This is the main method to 
 * interact with the simulation.
 *
 * Authors: Thomas Gorochowski
 * Created: 14/07/2008
 * Updated: 24/07/2008
 */


// Define the location of the class in the bsim package
package bsim;

// Import the bsim packages used
import bsim.*;

// Standard packages required by the application
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;


public class BSimToolbar extends JToolBar implements ActionListener{

	// Other BSim references
	private BSimApp app;
	private BSimScene scene;
	
	// State variable for the playback controls
	private int playState = 0;
	
	// GUI objects
	private JButton btnPlayPause, btnReset, btnRecord,
		btnPan, btnZoom, btnScreenshot, btnLoadSim;
	private JTextField txtRecLength, txtRecFrameSkip;
	private JLabel labRecLength, labRecFrameSkip, labTime;
	private JFileChooser fc;
	
	// Images used on the buttons
	private static final ImageIcon iconPause = new ImageIcon(BSimToolbar.class.getResource("icons/PauseSmall.gif"));
	private static final ImageIcon iconPlay = new ImageIcon(BSimToolbar.class.getResource("icons/PlaySmall.gif"));
	private static final ImageIcon iconToStart = new ImageIcon(BSimToolbar.class.getResource("icons/ToStartSmall.gif"));
	private static final ImageIcon iconRecord = new ImageIcon(BSimToolbar.class.getResource("icons/RecordSmall.gif"));
	private static final ImageIcon iconZoomIn = new ImageIcon(BSimToolbar.class.getResource("icons/ZoomInSmall.gif"));
	private static final ImageIcon iconZoomAct = new ImageIcon(BSimToolbar.class.getResource("icons/ZoomActSmall.gif"));
	private static final ImageIcon iconPanIn = new ImageIcon(BSimToolbar.class.getResource("icons/PanInSmall.gif"));
	private static final ImageIcon iconPanAct = new ImageIcon(BSimToolbar.class.getResource("icons/PanActSmall.gif"));
	private static final ImageIcon iconScreenshot = new ImageIcon(BSimToolbar.class.getResource("icons/ScreenshotSmall.gif"));
	private static final ImageIcon iconLoadSim = new ImageIcon(BSimToolbar.class.getResource("icons/PrefsSmall.gif"));
	
	private static BSimParameters params;
	
	/**
	 * Creates a new toolbar for a given BSimApp and BSimScene.
	 */
	public BSimToolbar(BSimApp newApp, BSimScene newScene, BSimParameters newParams){
		super();
		
		// Update internal variables
		app = newApp;
		scene = newScene;
		params = newParams;
		
		// Create the toolbar and make it floatable
		setupToolBar();
		setFloatable(true);
	}
	
	
	/**
	 * Create the toolbar.
	 */
	private void setupToolBar(){
		// Create the default file chooser
		fc = new JFileChooser();
		
		// Create GUI controls with initial properties
		btnPlayPause = new JButton("Play ");
		btnPlayPause.setIcon(iconPlay);
		btnPlayPause.addActionListener(this);
		btnReset = new JButton("Reset ");
		btnReset.setIcon(iconToStart);
		btnReset.addActionListener(this);
		labTime = new JLabel("Time: 00:00:00");
		btnPan = new JButton();
		btnPan.setIcon(iconPanAct);
		btnPan.addActionListener(this);
		btnZoom = new JButton();
		btnZoom.setIcon(iconZoomIn);
		btnZoom.addActionListener(this);
		labRecLength = new JLabel("Movie Length:");
		txtRecLength = new JTextField("100", 4);
		labRecFrameSkip = new JLabel("Skip:");
		txtRecFrameSkip = new JTextField("10", 4);
		btnRecord = new JButton("Record... ");
		btnRecord.setIcon(iconRecord);
		btnRecord.addActionListener(this);
		btnScreenshot = new JButton("Screenshot... ");
		btnScreenshot.setIcon(iconScreenshot);
		btnScreenshot.addActionListener(this);
		btnLoadSim = new JButton("Load Simulation... ");
		btnLoadSim.setIcon(iconLoadSim);
		btnLoadSim.addActionListener(this);
		
		
		// Add objects to the toolbar
		// Playback controls
		this.add(btnPlayPause);
		this.add(btnReset);
		this.addSeparator();
		this.add(labTime);
		this.addSeparator();
		// Display controls
		this.add(btnPan);
		this.add(btnZoom);
		this.addSeparator();
		// Movie record controls
		this.add(labRecLength);
		this.add(txtRecLength);
		this.add(labRecFrameSkip);
		this.add(txtRecFrameSkip);
		this.add(btnRecord);
		this.addSeparator();
		// Screenshot controls
		this.add(btnScreenshot);
		this.addSeparator();
		// Parameters
		this.add(btnLoadSim);
	}
	
	
	/**
	 * Update the enabled property for GUI objects
	 */
	public void setEnabled(boolean enabled){
		// Update the enabled property for all items on the toolbar
		super.setEnabled(enabled);
		btnPlayPause.setEnabled(enabled);
		btnReset.setEnabled(enabled);
		txtRecLength.setEnabled(enabled);
		txtRecFrameSkip.setEnabled(enabled);
		btnRecord.setEnabled(enabled);
		btnPan.setEnabled(enabled);
		btnZoom.setEnabled(enabled);
		btnScreenshot.setEnabled(enabled);
		btnLoadSim.setEnabled(enabled);
	}
	
	
	/**
	 * Update the simluation time label.
	 */
	public void updateTime(String newTime){
		// Update the simulation time with the new value
		labTime.setText(newTime);
	}
	
	
	/**
	 * Handle events for the toolbar.
	 */
	public void actionPerformed(ActionEvent e){
		// Get the object that was clicked
		Object s = e.getSource();
		
		// Play button - switch between play and pause
		if(s == btnPlayPause){
			if(playState == 0){
				playState = 1;
				btnPlayPause.setIcon(iconPause);
				btnPlayPause.setText("Pause");
				app.play();
			}
			else{
				playState = 0;
				btnPlayPause.setIcon(iconPlay);
				btnPlayPause.setText("Play");
				app.pause();
			}
			
		// Reset button
		}else if(s == btnReset){
			// Reset the current simulation
			playState = 0;
			btnPlayPause.setIcon(iconPlay);
			btnPlayPause.setText("Play");
			app.reset();
			
		//Record button
		}else if (e.getSource() == btnRecord) {
			// Pause the current simulation first
			playState = 0;
			btnPlayPause.setIcon(iconPlay);
			btnPlayPause.setText("Play");
			app.pause();
			
			// Variable to check if cancel is pressed (also displays the file dialog)
			int returnVal = fc.showSaveDialog(this);
			
			// If OK is pressed
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// Get the file that has been entered
				File file = fc.getSelectedFile();
				
				// Pass the filename to the BSimApp to write movie to file
				app.createMovie(file.getPath(), 
					Integer.parseInt(txtRecLength.getText()), 
					Integer.parseInt(txtRecFrameSkip.getText()));		
			}
		
		// Screenshot button
		}else if(s == btnScreenshot){
			// Pause the current simulation first
			playState = 0;
			btnPlayPause.setIcon(iconPlay);
			btnPlayPause.setText("Play");
			app.pause();
			
			// Variable to check if cancel is pressed (also displays the file dialog)
			int returnVal = fc.showSaveDialog(this);
			
			// If OK is pressed
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// Get the file that has been entered
				File file = fc.getSelectedFile();
				
				// Pass the filename to the BSimApp to write movie to file
				app.createImage(file.getPath());		
			}
		
		// Pan button
		}else if(s == btnPan){
			// Set the scene view state
			scene.setViewState(scene.VIEW_PAN);
			
			// Update the buttons to show pan being active
			btnZoom.setIcon(iconZoomIn);
			btnPan.setIcon(iconPanAct);
		
		// Zoom button
		}else if(s == btnZoom){
			// Set the scene view state
			scene.setViewState(scene.VIEW_ZOOM);
			
			// Update the buttons to show zoom being active
			btnZoom.setIcon(iconZoomAct);
			btnPan.setIcon(iconPanIn);
		
		// Load Simulation
		}else if(s == btnLoadSim) {
			// Pause the current simulation first
			playState = 0;
			btnPlayPause.setIcon(iconPlay);
			btnPlayPause.setText("Play");
			app.pause();
			
			// Variable to check if cancel is pressed (also displays the file dialog)
			int returnVal = fc.showOpenDialog(this);
			
			// If OK is pressed
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// Get the file that has been entered
				File file = fc.getSelectedFile();
				
				BSimParametersLoader paramLoader = new BSimParametersLoader(file);
				BSimParameters oldParams = params;
				
				try{
					// Pass the filename to the BSimApp to write movie to file
					BSimParameters newParams = paramLoader.parseFile();
					scene.updateParams(newParams);
					params = newParams;
				} catch (Exception ex) { 
					System.err.println("Error Loading Simulation (See stack trace)");
					ex.printStackTrace();
					// Use the old parameter file
					scene.updateParams(oldParams);
				}
			}
		}
	}
}
