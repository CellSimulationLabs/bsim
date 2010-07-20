/**
 * BSimFileExport.java
 *
 * Abstract class that handles the file aspects of an export allowing for two methods that
 * return String objects can be used by the developer to output a file of a required
 * type.
 *
 * Authors: Thomas Gorochowski
 * Created: 17/08/2008
 * Updated: 17/08/2008
 */


// Define the location of the class in the bsim package
package bsim.export;

// Standard packages required by the application
import bsim.*;
import java.util.*;
import java.io.*;


public abstract class BSimFileExport implements BSimExport {
	
	
	// Variables to hold the file writer objects
	protected FileWriter outFileWriter;
	protected BufferedWriter outBufferedWriter;
	
	// Header written flag
	private Boolean headerWritten = false;
	
	
	/**
	 * General constructor that uses a given file as the bases for the object.
	 */
	public BSimFileExport(File f) {
		
		// Write the header to the file
		try{
		
			// Create file writer and buffered writer to save output to
			outFileWriter = new FileWriter(f);
			outBufferedWriter = new BufferedWriter(outFileWriter);
		}
		catch(IOException e){ 
			System.err.println("Error writing to file (BSimFileExport.BSimFileExport)");
			e.printStackTrace();
		} 
	}
	
	
	/**
	 * Implements the abstract method for the BSimExport interface.
	 */
	public void exportFrame(BSimScene scene, BSimParameters params) {
		String nextStr;
		
		// Write the next output to the file if null ignore
		try{
			
			// Check to see if header written and if not add now
			if(!headerWritten){
				
				// Write the header
				outBufferedWriter.write(getHeaderLine(scene, params));
				outBufferedWriter.newLine();
				
				// Set the header written flag (we only want a single header)
				headerWritten = true;
			}
			
			// Check to see if the next output line exists and if so write to file
			nextStr = nextOutputLine(scene, params);
			if(nextStr != null){
				outBufferedWriter.write(nextStr);
				outBufferedWriter.newLine();
			}
		}
		catch(IOException e){ 
			System.err.println("Error writing to file (BSimFileExport.exportFrame)");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Implements the abstract method for the BSimExport interface.
	 */
	public void finishExport(BSimScene scene, BSimParameters params){
		String finalStr;
		
		// Finish the export by closing the file and writing the last line
		try{
			finalStr = finalOutputLine(scene, params);
			if(finalStr != null){
				outBufferedWriter.write(finalStr);
			}
			
			// Close the file
			outBufferedWriter.close();
		}
		catch(IOException e){ 
			System.err.println("Error closing file (BSimFileExport.finishExport)");
			e.printStackTrace();
		}
	}
	

	/**
	 * Abstract method to get the header row for the file. Only called once on object
	 * creation.
	 */
	public abstract String getHeaderLine(BSimScene scene, BSimParameters params);
	
	
	/**
	 * Returns the next output line based on a given scene and parameters.
	 */
	public abstract String nextOutputLine(BSimScene scene, BSimParameters params);
	
	
	/**
	 * Returns the final line (if necessary) in the file. Only use this for calculations
	 * where you only need a single output for the whole simulation.
	 */
	public abstract String finalOutputLine(BSimScene scene, BSimParameters params);
}
