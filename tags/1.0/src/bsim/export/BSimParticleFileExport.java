/**
 * BSimParticleFileExport.java
 *
 * Class that implements an file output of individual particle locations.
 *
 * Authors: Thomas Gorochowski
 * Created: 17/08/2008
 * Updated: 18/08/2008
 */


// Define the location of the class in the bsim package
package bsim.export;

// Standard packages required by the application
import bsim.*;
import bsim.object.*;
import java.io.*;
import java.util.*;


public class BSimParticleFileExport extends BSimFileExport {
	
	
	// Number of frames to skip between measurements
	private int frameSkip = 1;
	
	
	/**
	 * General constructor that uses a given file as the bases for the object.
	 */
	public BSimParticleFileExport(File f, int newFrameSkip) {
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
		
		for(int i=0; i<scene.getParticles().size(); i++){
			outStr += ", Particle_" + i + "_x";
			outStr += ", Particle_" + i + "_y";
		}
		
		outStr += ", Particle_Average_x";
		outStr += ", Particle_Average_y";
		
		return outStr;
	}
	
	
	/**
	 * Returns the next output line based on a given scene and parameters.
	 */
	public String nextOutputLine(BSimScene scene, BSimParameters params){
		String outStr;
		Vector particles;
		double[] particlePos;
		double xAv = 0.0, yAv = 0.0;
		double actTime;
		
		// Check to see if the frame needs to be output
		if(scene.getTimeStep() % frameSkip == 0){
			
			particles = scene.getParticles();
			
			// Add the timestep
			actTime = scene.getTimeStep() * scene.getDtSec();
			outStr = "";
			outStr += "" + actTime;
			
			// Loop through all the particles and add their details to the output
			for(int i=0; i<particles.size(); i++){
				
				// Get the position of the particle and add to the output file
				particlePos = ((BSimObject)particles.elementAt(i)).getPosition();
				outStr += ", " + particlePos[0];
				outStr += ", " + particlePos[1];
				
				xAv += particlePos[0];
				yAv += particlePos[1];
			}
			
			// Calaculte the average positions and output
			xAv = xAv / particles.size();
			yAv = yAv / particles.size();
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
