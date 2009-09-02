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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;


public class BSimParameters {	
	
	// bsim
	public static double dt = 0.001; // seconds	
		
	// bsim.scene
	public static Vector<double[]> bacteriaSingles = new Vector(); 
	public static Vector<double[]> bacteriaSets = new Vector();
	public static Vector<double[]> beadSingles = new Vector();
	public static Vector<double[]> beadSets	= new Vector(); 	  
	public static double[] cfGoalDefine = {0, 0, 0, 10, 10, 10, 10, 10, 10, 0.001, 1, 1, 0.8};
	public static double[] cfGoalSetup = {0, 0, 0, 0, 0, 0};
	public static double[] cfCoordDefine = {0, 0, 0, 10, 10, 10, 10, 10, 10, 0.001, 1, 1, 0.8};
	public static double[] cfCoordSetup = {0, 0, 0, 0, 0, 0};
	public static double[] cfRecruitDefine = {0, 0, 0, 10, 10, 10, 10, 10, 10, 0.001, 1, 1, 0.8};
	public static double[] cfRecruitSetup = {0, 0, 0, 0, 0, 0};
	public static double[] cfQuorumDefine = {0, 0, 0, 10, 10, 10, 10, 10, 10, 0.001, 1, 1, 0.8};
	public static double[] cfQuorumSetup = {0, 0, 0, 0, 0, 0};	
	public static double[] boundingBoxDefine = {0, 0, 0, 0, 0, 0};
	public static double   screenZoom = 1; 
	public static double[] screenMove = {0.0, 0.0};
	
	// bsim.particle
	public static double reactForce = 0.0;
	public static double visc = Math.pow(10.0,-3.0); // Pascal seconds
	public static double wellWidthBactBead = 0.0;
	public static double wellDepthBactBead = 0.0;
	
	// bsim.particle.bacterium
	public static double bactRadius       = 1.4;	// microns
	public static double bactForceUp      = 0.4387; // pico newtons
	public static double bactForceDown    = 0.41; // pico newtons		
	public static double runLengthUp      = 1.07;	// seconds
	public static double runLengthDown    = 0.8;	// seconds
	public static double runLengthIso     = 0.86; // seconds
	
	// bsim.particle.bead
	public static double   beadRadius       = 10.0;	// microns
		
	// bsim.field
	public static int      numOfThreads     = 2;	
	
	// bsim.app
	public static int      screenWidth     	 	= 1025; // pixels
	public static int      screenHeight    		= 700; // pixels	
	
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
		if     (line[0].equals("DT:")) dt = args[0];
		
		// bsim.scene
		else if(line[0].equals("CREATE_BEAD_SINGLE:")) beadSingles.add(args);
		else if(line[0].equals("CREATE_BEAD_SET:"))	beadSets.add(args);
		else if(line[0].equals("CREATE_BACTERIUM_SINGLE:")) bacteriaSingles.add(args);
		else if(line[0].equals("CREATE_BACTERIA_SET:")) bacteriaSets.add(args);		
		else if(line[0].equals("FIELD_GOAL_DEFINE:")) cfGoalDefine = args;
		else if(line[0].equals("FIELD_GOAL_SETUP:")) cfGoalSetup = args;
		else if(line[0].equals("FIELD_COORD_DEFINE:")) cfCoordDefine = args;
		else if(line[0].equals("FIELD_COORD_SETUP:")) cfCoordSetup = args;
		else if(line[0].equals("FIELD_RECRUIT_DEFINE:")) cfRecruitDefine = args;
		else if(line[0].equals("FIELD_RECRUIT_SETUP:")) cfRecruitSetup = args;
		else if(line[0].equals("FIELD_QUORUM_DEFINE:")) cfQuorumDefine = args;
		else if(line[0].equals("FIELD_QUORUM_SETUP:")) cfQuorumSetup = args;		
		else if(line[0].equals("BOUNDING_BOX_DEFINE:")) boundingBoxDefine = args;
		else if(line[0].equals("SCREEN_MOVE:")) screenMove = args;		
		else if(line[0].equals("SCREEN_ZOOM:")) screenZoom = args[0];
				
		// bsim.particle
		else if(line[0].equals("PHYSICS_REACT_FORCE:")) reactForce = args[0];
		else if(line[0].equals("VISCOSITY:")) visc = args[0];
		else if(line[0].equals("PHYSICS_WELL_WIDTH_BACT_BEAD:")) wellWidthBactBead = args[0];
		else if(line[0].equals("PHYSICS_WELL_DEPTH_BACT_BEAD:")) wellDepthBactBead = args[0];
		
		// bsim.particle.bacteria
		else if(line[0].equals("BACTERIA_RADIUS:")) bactRadius = args[0];
		else if(line[0].equals("BACTERIA_FORCE_UP:")) bactForceUp = args[0];
		else if(line[0].equals("BACTERIA_FORCE_DOWN:")) bactForceDown = args[0];		
		else if(line[0].equals("UP_RUN_LENGTH:")) runLengthUp = args[0];
		else if(line[0].equals("DOWN_RUN_LENGTH:")) runLengthDown = args[0];
		else if(line[0].equals("ISO_RUN_LENGTH:")) runLengthIso = args[0];
		
		// bsim.particle.bead
		else if(line[0].equals("BEAD_RADIUS:")) beadRadius = args[0];
		
		// bsim.field
		else if(line[0].equals("NUMBER_OF_THREADS:")) numOfThreads = (int)args[0];		
		
		// bsim.app					
		else if(line[0].equals("SCREEN_WIDTH:")) screenWidth = (int)args[0];
		else if(line[0].equals("SCREEN_HEIGHT:")) screenHeight = (int)args[0];
		
		// bsim.batch		
		else if(line[0].equals("DATA_FRAMES_SKIP:")) dataFramesSkip = (int)args[0];
		else if(line[0].equals("VIDEO_FRAMES_SKIP:")) videoFramesSkip = (int)args[0];
		else if(line[0].equals("SIMULATION_RUNS:")) simRuns = (int)args[0];
		else if(line[0].equals("SIMULATION_LENGTH:")) simLength = (int)args[0];
		else if(line[0].equals("RECORD_VIDEO:")) { if((int)args[0] == 1) { recordVideo = true; } else { recordVideo = false; } }
		else if(line[0].equals("SCREEN_FRAME_RECORD_FOR_SECOND:")) frameRecordForSec = (int)args[0];
		else if(line[0].equals("EXPORT_DIR:")) exportDir = line[1];
	
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
