/**
 * BSimBeadFileExport.java
 *
 * Class that implements an file output of individual bead locations.
 *
 * Authors: Thomas Gorochowski
 * Created: 17/08/2008
 * Updated: 18/08/2008
 */
package bsim.export;

import java.io.File;
import java.util.Vector;

import bsim.BSimParameters;
import bsim.BSimScene;
import bsim.physics.BSimParticle;


public class BSimBeadFileExport extends BSimFileExport {
	
	
	// Number of frames to skip between measurements
	private int frameSkip = 1;
	
	
	/**
	 * General constructor that uses a given file as the bases for the object.
	 */
	public BSimBeadFileExport(File f, int newFrameSkip) {
		super(f);
		
		// Update internal parameter
		frameSkip = newFrameSkip;
	}
	
	
	/**
	 * Abstract method to get the header row for the file. Only called once on object
	 * creation.
	 */
	public String getHeaderLine(BSimScene scene, BSimParameters params){
		String outStr;
		
		outStr = "Timestep";
		
		for(int i=0; i<scene.getBeads().size(); i++){
			outStr += ", Bead_" + i + "_x";
			outStr += ", Bead_" + i + "_y";
		}
		
		outStr += ", Bead_Average_x";
		outStr += ", Bead_Average_y";
		
		return outStr;
	}
	
	
	/**
	 * Returns the next output line based on a given scene and parameters.
	 */
	public String nextOutputLine(BSimScene scene, BSimParameters params){
		String outStr;
		Vector beads;
		double[] beadPos;
		double xAv = 0.0, yAv = 0.0;
		double actTime;
		
		// Check to see if the frame needs to be output
		if(scene.getTimeStep() % frameSkip == 0){
			
			beads = scene.getBeads();
			
			// Add the timestep
			actTime = scene.getTimeStep() * scene.getDtSec();
			outStr = "";
			outStr += "" + actTime;
			
			// Loop through all the beads and add their details to the output
			for(int i=0; i<beads.size(); i++){
				
				// Get the position of the bead and add to the output file
				beadPos = ((BSimParticle)beads.elementAt(i)).getPosition();
				outStr += ", " + beadPos[0];
				outStr += ", " + beadPos[1];
				
				xAv += beadPos[0];
				yAv += beadPos[1];
			}
			
			// Calaculte the average positions and output
			xAv = xAv / beads.size();
			yAv = yAv / beads.size();
			outStr += ", " + xAv;
			outStr += ", " + yAv;
			
			return outStr;
		}
		else{
			return null;
		}
	}
	
	
	/**
	 * Returns the final line (if necessary) in the file. Only use this for calculations
	 * where you only need a single output for the whole simulation.
	 */
	public String finalOutputLine(BSimScene scene, BSimParameters params){
		
		// No final line required
		return null;
	}
}
