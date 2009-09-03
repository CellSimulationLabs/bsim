/**
 * BSimParameters.java
 *
 * Class containing all simulation parameters of interest and get/set methods for each one
 * 
 * Authors: Ian Miles
 *          Thomas Gorochowski (Updates)
 *          Mattia Fazzini(Update)
 * Created: 05/08/2008
 * Updated: 09/08/2009
 */
package bsim;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

import javax.vecmath.Vector3d;

import bsim.field.BSimChemicalField;


public class BSimParameters {	
	// TODO no doubles[] or Vectors, use official Properties class

	// bsim
	public static double dt = 0.001; // seconds	
	public static int screenWidth 	= 1025; // pixels
	public static int screenHeight 	= 700; // pixels	
		
	// bsim.scene
	public static Vector<double[]> bacteria = new Vector();	
	public static Vector<double[]> beads = new Vector();
	
	public static double fGoalFieldType 	= BSimChemicalField.TYPE_FIXED;
	public static double fGoalBoundaryType 	= BSimChemicalField.BOUNDARY_LEAK;
	public static double fGoalRate 			= 0;
	public static double[] fGoalStartPos 	= {0,0,0};
	public static double fGoalWidth	 		= 0;
	public static double fGoalHeight		= 0;
	public static double fGoalDepth	 		= 0;
	public static double fGoalXBoxes	 	= 0;
	public static double fGoalYBoxes	 	= 0;
	public static double fGoalZBoxes	 	= 0;
	public static double fGoalThreshold	 		= 0;
	
	public static double fQuorumFieldType 		= BSimChemicalField.TYPE_FIXED;
	public static double fQuorumBoundaryType 	= BSimChemicalField.BOUNDARY_LEAK;
	public static double fQuorumRate 			= 0;
	public static double[] fQuorumStartPos 		= {0,0,0};
	public static double fQuorumWidth	 		= 0;
	public static double fQuorumHeight			= 0;
	public static double fQuorumDepth	 		= 0;
	public static double fQuorumXBoxes	 		= 0;
	public static double fQuorumYBoxes	 		= 0;
	public static double fQuorumZBoxes	 		= 0;
	public static double fQuorumThreshold	 	= 0;
			
	public static double   screenZoom = 1; 
	public static double[] screenMove = {0.0, 0.0};		
	
	// bsim.particle
	public static double reactForce = 0.0;
	public static double visc = Math.pow(10.0,-3.0); // Pascal seconds
	public static double wellWidthBactBead = 0.0;
	public static double wellDepthBactBead = 0.0;
	
	// bsim.particle.bacterium
	public static double bactForceUp      = 0.4387; // pico newtons
	public static double bactForceDown    = 0.41; // pico newtons
	public static double runLengthUp      = 1.07;	// seconds
	public static double runLengthDown    = 0.8;	// seconds
	public static double runLengthIso     = 0.86; // seconds
			
	// bsim.field
	public static int      numOfThreads     = 2;	
		
	// bsim.batch
	public static int      dataFramesSkip   = 1;
	public static int      videoFramesSkip  = 1;
	public static int      simRuns          = 1;
	public static int      simLength        = 1;
	public static boolean  recordVideo      = true;
	public static int      frameRecordForSec	= 25;
	public static String   exportDir;	
				
	public BSimParameters(File f) {
		
		Scanner scanner;
		int lineCounter = 1;
		try { scanner = new Scanner(f);
			try {
				while(scanner.hasNextLine()) {
					processLine(scanner.nextLine().split("\t"), lineCounter);
					lineCounter++;
				}
			} finally { scanner.close(); }
		} catch(FileNotFoundException e) {System.err.println("Parameter file not found"); }
		
	}
		
	private void processLine(String[] line, int lineNo) {
		double[] args = parseLine(line);
				
		// bsim
		if     (line[0].equals("dt:")) dt = args[0];
		else if(line[0].equals("screenWidth:")) screenWidth = (int)args[0];
		else if(line[0].equals("screenHeight:")) screenHeight = (int)args[0];
		
		// bsim.scene
		else if(line[0].equals("bacterium:")) bacteria.add(args);
		else if(line[0].equals("bead:")) beads.add(args);
		
		else if(line[0].equals("fGoalFieldType:")) fGoalFieldType = args[0];
		else if(line[0].equals("fGoalBoundaryType:")) fGoalBoundaryType = args[0];
		else if(line[0].equals("fGoalRate:")) fGoalRate = args[0];
		else if(line[0].equals("fGoalStartPos:")) fGoalStartPos = args;
		else if(line[0].equals("fGoalWidth:")) fGoalWidth = args[0];
		else if(line[0].equals("fGoalHeight:")) fGoalHeight = args[0];
		else if(line[0].equals("fGoalDepth:")) fGoalDepth = args[0];
		else if(line[0].equals("fGoalXBoxes:")) fGoalXBoxes = args[0];
		else if(line[0].equals("fGoalYBoxes:")) fGoalYBoxes = args[0];
		else if(line[0].equals("fGoalZBoxes:")) fGoalZBoxes = args[0];
		else if(line[0].equals("fGoalThreshold:")) fGoalThreshold = args[0];
		
		else if(line[0].equals("fQuorumFieldType:")) fQuorumFieldType = args[0];
		else if(line[0].equals("fQuorumBoundaryType:")) fQuorumBoundaryType = args[0];
		else if(line[0].equals("fQuorumRate:")) fQuorumRate = args[0];
		else if(line[0].equals("fQuorumStartPos:")) fQuorumStartPos = args;
		else if(line[0].equals("fQuorumWidth:")) fQuorumWidth = args[0];
		else if(line[0].equals("fQuorumHeight:")) fQuorumHeight = args[0];
		else if(line[0].equals("fQuorumDepth:")) fQuorumDepth = args[0];
		else if(line[0].equals("fQuorumXBoxes:")) fQuorumXBoxes = args[0];
		else if(line[0].equals("fQuorumYBoxes:")) fQuorumYBoxes = args[0];
		else if(line[0].equals("fQuorumZBoxes:")) fQuorumZBoxes = args[0];
		else if(line[0].equals("fQuorumThreshold:")) fQuorumThreshold = args[0];
		
		else if(line[0].equals("screenZoom:")) screenZoom = args[0];		
		else if(line[0].equals("screenMove:")) screenMove = args;
				
		// bsim.particle
		else if(line[0].equals("reactForce:")) reactForce = args[0];
		else if(line[0].equals("visc:")) visc = args[0];
		else if(line[0].equals("wellWidthBactBead:")) wellWidthBactBead = args[0];
		else if(line[0].equals("wellDepthBactBead:")) wellDepthBactBead = args[0];
		
		// bsim.particle.bacteria		
		else if(line[0].equals("bactForceUp:")) bactForceUp = args[0];
		else if(line[0].equals("bactForceDown:")) bactForceDown = args[0];	
		else if(line[0].equals("runLengthUp:")) runLengthUp = args[0];
		else if(line[0].equals("runLengthDown:")) runLengthDown = args[0];
		else if(line[0].equals("runLengthIso:")) runLengthIso = args[0];
				
		// bsim.field
		else if(line[0].equals("numOfThreads:")) numOfThreads = (int)args[0];		
		 
		// bsim.batch		
		else if(line[0].equals("dataFramesSkip:")) dataFramesSkip = (int)args[0];
		else if(line[0].equals("videoFramesSkip:")) videoFramesSkip = (int)args[0];
		else if(line[0].equals("simRuns:")) simRuns = (int)args[0];
		else if(line[0].equals("simLength:")) simLength = (int)args[0];
		else if(line[0].equals("recordVideo:")) { if((int)args[0] == 1) { recordVideo = true; } else { recordVideo = false; } }
		else if(line[0].equals("frameRecordForSec:")) frameRecordForSec = (int)args[0];
		else if(line[0].equals("exportDir:")) exportDir = line[1];
	
		else if(line[0].equals("***"))  {} // Do nothing
		else System.err.println("Line " + lineNo + " not Read in Parameter File");
	}
	
			
	/**
	 * Convert array of Strings into an array of doubles
	 */
	double[] parseLine(String[] line) {
		// Parse each line converting what can into doubles
		double[] parsedLine = new double[line.length-1];
		for(int i=1; i<line.length; i++) {
			try{
				parsedLine[i-1] = Double.parseDouble(line[i]);
			}
			catch(Exception e){
				// Could not convert type so leave as null;
				parsedLine[i-1] = 0.0;
			}
		}
		return parsedLine;
	}			

}
