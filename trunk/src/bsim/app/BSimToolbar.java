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
import javax.swing.JLabel;
import javax.swing.JToolBar;

import bsim.BSimParameters;
import bsim.BSimScene;


public class BSimToolbar extends JToolBar implements ActionListener{

	public final static int BSimToolbarWidth = 1025;
	
	// Other BSim references
	private BSimApp app;
	private BSimScene scene;
	
	// State variable for the playback controls
	private int playState = 0;
	
	// GUI objects
	private JButton btnPlayPause, btnReset, btnSaveRecord, btnStartRecord, btnEndRecord, btnSaveScreenshot, btnTakeScreenshot, btnLoadSim;
	private JLabel labTime;
	private JFileChooser fc;
	
	// Images used on the buttons
	private static final ImageIcon iconPlay = new ImageIcon(BSimToolbar.class.getResource("../resource/play.png"));
	private static final ImageIcon iconPause = new ImageIcon(BSimToolbar.class.getResource("../resource/pause.png"));
	private static final ImageIcon iconReset = new ImageIcon(BSimToolbar.class.getResource("../resource/reset.png"));
	private static final ImageIcon iconLoad = new ImageIcon(BSimToolbar.class.getResource("../resource/load.png"));
	private static final ImageIcon iconSaveRecord = new ImageIcon(BSimToolbar.class.getResource("../resource/saveRecord.png"));
	private static final ImageIcon iconStartRecord = new ImageIcon(BSimToolbar.class.getResource("../resource/startRecord.png"));
	private static final ImageIcon iconEndRecord = new ImageIcon(BSimToolbar.class.getResource("../resource/endRecord.png"));
	private static final ImageIcon iconStartRecordDisabled = new ImageIcon(BSimToolbar.class.getResource("../resource/startRecordDisabled.png"));
	private static final ImageIcon iconEndRecordDisabled = new ImageIcon(BSimToolbar.class.getResource("../resource/endRecordDisabled.png"));
	private static final ImageIcon iconSaveScreenshot = new ImageIcon(BSimToolbar.class.getResource("../resource/saveScreenshot.png"));
	private static final ImageIcon iconTakeScreenshot = new ImageIcon(BSimToolbar.class.getResource("../resource/takeScreenshot.png"));
	private static final ImageIcon iconTakeScreenshotDisabled = new ImageIcon(BSimToolbar.class.getResource("../resource/takeScreenshotDisabled.png"));
	
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
		btnPlayPause = new JButton("Play");
		btnPlayPause.setIcon(iconPlay);
		btnPlayPause.addActionListener(this);
		btnReset = new JButton("Reset");
		btnReset.setIcon(iconReset);
		btnReset.addActionListener(this);
		btnLoadSim = new JButton("Load Simulation");
		btnLoadSim.setIcon(iconLoad);
		btnLoadSim.addActionListener(this);
		labTime = new JLabel("Time: 00:00:00");
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
		this.add(labTime);
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
				scene.getProcessing().createMovie(videoFileName);
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
			scene.getProcessing().takeImage(scene.getImageFileName());
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
								
				BSimParameters oldParams = params;
				
				try{
					// Pass the filename to the BSimApp to write movie to file
					BSimParameters newParams = new BSimParameters(file);
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
