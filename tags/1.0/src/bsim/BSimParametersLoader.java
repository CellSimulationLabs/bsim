/**
 * BSimParametersLoader.java
 *
 * Class to create a BSimParameters object from a physical file.
 *
 * Authors: Thomas Gorochowski
 *          Ian Miles
 * Created: 18/08/2008
 * Updated: 24/08/2008
 */


// Define the location of the class in the bsim package
package bsim;

// Import the bsim packages used
import bsim.*;

// Standard packages required by the application
import java.util.*;
import java.io.*;
import java.util.Scanner;


public class BSimParametersLoader {
	
	
	// The parameter file to use
	private File paramFile;
	
	
	/**
	 * General constructor. Stores the file location to be used.
	 */
	public BSimParametersLoader(File f){
		paramFile = f;
	}
	
	
	/**
	 * Function that parses the associated parameter file and returns an equivalent 
	 * BSimParameters object.
	 */
	public BSimParameters parseFile(){
		// Parameters object to hold the parsed file contents
		BSimParameters params = new BSimParameters();
		Scanner scanner;
		int lineCounter = 1;
		try { scanner = new Scanner(paramFile);
			try {
				while(scanner.hasNextLine()) {
					processLine(scanner.nextLine().split("\t"), params,lineCounter);
					lineCounter++;
				}
			} finally { scanner.close(); }
		} catch(FileNotFoundException e) {System.err.println("Parameter file not found"); }
		
		
		// Return the output parameters object
		return params;
	}
	
	
	/**
	 * Use data following line header to set parameters in BSimParameters object
	 */
	private void processLine(String[] line, BSimParameters p, int lineNo) {
		double[] args = parseLine(line);
		int temp;
		
		if     (line[0].equals("DT:")) p.setDtSecs(args[0]);
		
		// singles arguments: [xpos ypos]
		// set arguements: [NWxpos NWypos width length n] (assume constant radius, speed, etc)
		else if(line[0].equals("PARTICLE_RADIUS:")) p.setPartRadius(args[0]);
		else if(line[0].equals("CREATE_PARTICLE_SINGLE:")) p.addSingleParticle(args);
		else if(line[0].equals("CREATE_PARTICLE_SET:"))	p.addParticleSet(args);

		else if(line[0].equals("BACTERIA_RADIUS:")) p.setBactRadius(args[0]);
		else if(line[0].equals("CREATE_BACTERIUM_SINGLE:")) p.addSingleBacterium(args);
		else if(line[0].equals("CREATE_BACTERIA_SET:")) p.addBacteriaSet(args);
		else if(line[0].equals("BACTERIA_FORCE_UP:")) p.setBactForceUp(args[0]);
		else if(line[0].equals("BACTERIA_FORCE_DOWN:")) p.setBactForceDown(args[0]);
		else if(line[0].equals("BACTERIA_SPEED:")) p.setBactSpeed(args[0]);
		else if(line[0].equals("UP_RUN_LENGTH:")) p.setUpRunLength(args[0]);
		else if(line[0].equals("DOWN_RUN_LENGTH:")) p.setDownRunLength(args[0]);
		else if(line[0].equals("ISO_RUN_LENGTH:")) p.setIsoRunLength(args[0]);
			
		else if(line[0].equals("VISCOSITY:")) p.setViscosity(args[0]);
		
		else if(line[0].equals("SCREEN_HEIGHT:")) p.setScreenHeight((int)args[0]);
		else if(line[0].equals("SCREEN_WIDTH:")) p.setScreenWidth((int)args[0]);
		else if(line[0].equals("SCREEN_ZOOM:")) p.setScreenZoom((double)args[0]);
		else if(line[0].equals("SCREEN_MOVE:")) p.setScreenMove((double)args[0], (double)args[1]);
		
		else if(line[0].equals("PHYSICS_WELL_WIDTH_BACT_BACT:")) p.setWellWidthBactBact((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_BACT_BACT:")) p.setWellDepthBactBact((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_WIDTH_BACT_PART:")) p.setWellWidthBactPart((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_BACT_PART:")) p.setWellDepthBactPart((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_WIDTH_PART_PART:")) p.setWellWidthPartPart((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_PART_PART:")) p.setWellDepthPartPart((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_WIDTH_PART_BDRY:")) p.setWellWidthPartBdry((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_PART_BDRY:")) p.setWellDepthPartBdry((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_WIDTH_BACT_BDRY:")) p.setWellWidthBactBdry((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_BACT_BDRY:")) p.setWellDepthBactBdry((double)args[0]);
		else if(line[0].equals("PHYSICS_REACT_FORCE:")) p.setReactForce((double)args[0]);
		
		else if(line[0].equals("CREATE_BOUNDARY_SOLID:")) p.addSolidBoundary(args);
		else if(line[0].equals("CREATE_BOUNDARY_WRAP:")) p.addWrapBoundary(args);
		
		else if(line[0].equals("FIELD_GOAL_DEFINE:")) p.setCfGoalDefine(args);
		else if(line[0].equals("FIELD_GOAL_SETUP:")) p.setCfGoalSetup(args);
		else if(line[0].equals("FIELD_COORD_DEFINE:")) p.setCfCoordDefine(args);
		else if(line[0].equals("FIELD_COORD_SETUP:")) p.setCfCoordSetup(args);
		else if(line[0].equals("FIELD_RECRUIT_DEFINE:")) p.setCfRecruitDefine(args);
		else if(line[0].equals("FIELD_RECRUIT_SETUP:")) p.setCfRecruitSetup(args);
		
		else if(line[0].equals("VISUAL_AID_BACTERIA_TRACE:")) p.addBacteriaTrace(args);
		else if(line[0].equals("VISUAL_AID_AVG_BACTERIA_TRACE:")) p.addAvgBacteriaTrace(args);
		else if(line[0].equals("VISUAL_AID_PARTICLE_TRACE:")) p.addParticleTrace(args);
		else if(line[0].equals("VISUAL_AID_CLOCK:")) p.addClock(args);
		else if(line[0].equals("VISUAL_AID_SCALE:")) p.addScale(args);
		
		else if(line[0].equals("VIDEO_FRAMES_SKIP:")) p.setVideoFramesSkip((int)args[0]);
		else if(line[0].equals("RECORD_VIDEO:")) {
			if((int)args[0] == 1) p.setRecordVideo(true);
			else p.setRecordVideo(false);
		}
		
		else if(line[0].equals("DATA_FRAMES_SKIP:")) p.setDataFramesSkip((int)args[0]);
		
		else if(line[0].equals("SIMULATION_LENGTH:")) p.setSimLength((int)args[0]);
		else if(line[0].equals("SIMULATION_RUNS:")) p.setSimRuns((int)args[0]);
		
		else if(line[0].equals("EXPORT_DIR:")) p.setExportDir(line[1]);
		else if(line[0].equals("NUMBER_OF_THREADS:")) p.setNumOfThreads((int)args[0]);
		
		else if(line[0].equals("***"))  temp = 0; // Do nothing - comment
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
	
	
	/**
	 * Standard get methods
	 */
	public File getParameterFile(){ return paramFile; }
	
	
	/**
	 * Standard set methods
	 */
	public void setParameterFile(File newFile){ paramFile = newFile; }
}