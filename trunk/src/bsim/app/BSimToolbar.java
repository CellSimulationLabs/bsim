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
package bsim.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JToolBar;

import bsim.BSimParameters;
import bsim.scene.BSimScene;


public class BSimToolbar extends JToolBar implements ActionListener{

	public final static int BSimToolbarWidth = 1025;
	
	// Other BSim references
	private BSimApp app;
	private BSimScene scene;
	
	// State variable for the playback controls
	private int playState = 0;
	
	// GUI objects
	private JButton btnPlayPause, btnReset, btnSaveRecord, btnStartRecord, btnEndRecord, btnSaveScreenshot, btnTakeScreenshot, btnLoadSim;	
	private JFileChooser fc;
	
	// Images used on the buttons
	private static final ImageIcon iconPlay = new ImageIcon(BSimToolbar.class.getResource("/bsim/resource/icons/play.png"));
	private static final ImageIcon iconPause = new ImageIcon(BSimToolbar.class.getResource("/bsim/resource/icons/pause.png"));
	private static final ImageIcon iconReset = new ImageIcon(BSimToolbar.class.getResource("/bsim/resource/icons/reset.png"));
	private static final ImageIcon iconLoad = new ImageIcon(BSimToolbar.class.getResource("/bsim/resource/icons/load.png"));
	private static final ImageIcon iconSaveRecord = new ImageIcon(BSimToolbar.class.getResource("/bsim/resource/icons/saveRecord.png"));
	private static final ImageIcon iconStartRecord = new ImageIcon(BSimToolbar.class.getResource("/bsim/resource/icons/startRecord.png"));
	private static final ImageIcon iconEndRecord = new ImageIcon(BSimToolbar.class.getResource("/bsim/resource/icons/endRecord.png"));
	private static final ImageIcon iconStartRecordDisabled = new ImageIcon(BSimToolbar.class.getResource("/bsim/resource/icons/startRecordDisabled.png"));
	private static final ImageIcon iconEndRecordDisabled = new ImageIcon(BSimToolbar.class.getResource("/bsim/resource/icons/endRecordDisabled.png"));
	private static final ImageIcon iconSaveScreenshot = new ImageIcon(BSimToolbar.class.getResource("/bsim/resource/icons/saveScreenshot.png"));
	private static final ImageIcon iconTakeScreenshot = new ImageIcon(BSimToolbar.class.getResource("/bsim/resource/icons/takeScreenshot.png"));
	private static final ImageIcon iconTakeScreenshotDisabled = new ImageIcon(BSimToolbar.class.getResource("/bsim/resource/icons/takeScreenshotDisabled.png"));	
	
	/**
	 * Creates a new toolbar for a given BSimApp and BSimScene.
	 */
	public BSimToolbar(BSimApp newApp, BSimScene newScene){
		super();
		
		// Update internal variables
		app = newApp;
		scene = newScene;
		
		// Create the toolbar and make it floatable
		setupToolBar();
		/////////////////////////////************************///////////////////////////
		// TODO: setOrientation (or whatever) based on a button (flip vertical/horizontal)
		setFloatable(false);		
	}
	
	
	/**
	 * Create the toolbar.
	 */
	private void setupToolBar(){
		// Create the default file chooser
		fc = new JFileChooser();
		
		// Create GUI controls with initial properties
		btnPlayPause = new JButton("Play");
		btnPlayPause.setIcon(iconPlay);
		btnPlayPause.addActionListener(this);
		btnReset = new JButton("Reset");
		btnReset.setIcon(iconReset);
		btnReset.addActionListener(this);
		btnLoadSim = new JButton("Load Simulation");
		btnLoadSim.setIcon(iconLoad);
		btnLoadSim.addActionListener(this);		
		// TODO: A button to create a new display window?
		// TODO: don't really need quite so many different buttons for setting directories etc. i.e. should just be 'take screenshot' for example
		// Create a folder with the date & time: export_01_09_09_1244\screenshots, .\movies, .\data for example
		btnSaveScreenshot = new JButton("Screenshot");
		btnSaveScreenshot.setIcon(iconSaveScreenshot);
		btnSaveScreenshot.addActionListener(this);
		btnTakeScreenshot = new JButton("Save Screenshot");
		btnTakeScreenshot.setIcon(iconTakeScreenshotDisabled);
		btnTakeScreenshot.addActionListener(this);
		btnSaveRecord = new JButton("Record");
		btnSaveRecord.setIcon(iconSaveRecord);
		btnSaveRecord.addActionListener(this);
		btnStartRecord = new JButton("Start Record");
		btnStartRecord.setIcon(iconStartRecordDisabled);
		btnStartRecord.addActionListener(this);
		btnEndRecord = new JButton("End Record");
		btnEndRecord.setIcon(iconEndRecordDisabled);
		btnEndRecord.addActionListener(this);
		
		
		// Add objects to the toolbar
		// Playback controls
		this.add(btnPlayPause);
		this.add(btnReset);
		// Parameters
		this.add(btnLoadSim);
		this.addSeparator();
		// Screenshot controls
		this.add(btnSaveScreenshot);
		this.add(btnTakeScreenshot);
		btnTakeScreenshot.setEnabled(false);
		this.addSeparator();
		// Movie record controls
		this.add(btnSaveRecord);
		this.add(btnStartRecord);
		this.add(btnEndRecord);		
		btnStartRecord.setEnabled(false);
		btnEndRecord.setEnabled(false);
		
	}
	
	
	/**
	 * Update the enabled property for GUI objects
	 */
	public void setEnabled(boolean enabled){
		// Update the enabled property for all items on the toolbar
		super.setEnabled(enabled);
		btnPlayPause.setEnabled(enabled);
		btnReset.setEnabled(enabled);
		btnLoadSim.setEnabled(enabled);
		btnSaveScreenshot.setEnabled(enabled);
		btnSaveRecord.setEnabled(enabled);
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
		}else if (e.getSource() == btnSaveRecord) {		
			// Variable to check if cancel is pressed (also displays the file dialog)
			int returnVal = fc.showSaveDialog(this);
				
			// If OK is pressed
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// Get the file that has been entered
				File file = fc.getSelectedFile();
				String videoFileName = file.getPath();
				videoFileName = videoFileName + ".mov";
				file.delete();
				file=null;
				btnSaveRecord.setEnabled(false);
				btnStartRecord.setEnabled(true);
				btnStartRecord.setIcon(iconStartRecord);
				//scene.getProcessing().createMovie(videoFileName);
			}
		}else if (e.getSource() == btnStartRecord) {		
			btnStartRecord.setEnabled(false);
			btnStartRecord.setIcon(iconStartRecordDisabled);
			btnEndRecord.setEnabled(true);
			btnEndRecord.setIcon(iconEndRecord);
			scene.setStartVideo(true);
		}else if (e.getSource() == btnEndRecord) {		
			btnEndRecord.setEnabled(false);
			btnEndRecord.setIcon(iconEndRecordDisabled);
			scene.setStartVideo(false);
			scene.setEndVideo(true);
			btnSaveRecord.setEnabled(true);
		}else if(s == btnSaveScreenshot){
			//Screenshot
			int returnVal = fc.showSaveDialog(this);
				
			// If OK is pressed
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// Get the file that has been entered
				File file = fc.getSelectedFile();
				String imageFileName = file.getPath();
				imageFileName = imageFileName + ".png";
				file.delete();
				file=null;
				scene.setImageFileName(imageFileName);
				btnTakeScreenshot.setEnabled(true);
				btnTakeScreenshot.setIcon(iconTakeScreenshot);
				btnSaveScreenshot.setEnabled(false);
			}
		}else if(s == btnTakeScreenshot){
			//scene.getProcessing().takeImage(scene.getImageFileName());
			btnTakeScreenshot.setEnabled(false);
			btnTakeScreenshot.setIcon(iconTakeScreenshotDisabled);
			btnSaveScreenshot.setEnabled(true);		
		//Load simulation
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
										
				try{
					// Pass the filename to the BSimApp to write movie to file
					new BSimParameters(file);
					scene.updateParams();					
				} catch (Exception ex) { 
					System.err.println("Error Loading Simulation (See stack trace)");
					ex.printStackTrace();
				}
			}
		}
	}
}
