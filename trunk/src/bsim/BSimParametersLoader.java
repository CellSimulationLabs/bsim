/**
 * BSimParametersLoader.java
 *
 * Class to create a BSimParameters object from a physical file.
 *
 * Authors: Thomas Gorochowski
 *          Ian Miles
 *          Mattia Fazzini(Update)
 * Created: 18/08/2008
 * Updated: 09/08/2009
 */
package bsim;

import java.io.File;
import java.io.FileNotFoundException;
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
		else if(line[0].equals("BEAD_RADIUS:")) p.setBeadRadius(args[0]);
		else if(line[0].equals("CREATE_BEAD_SINGLE:")) p.addSingleBead(args);
		else if(line[0].equals("CREATE_BEAD_SET:"))	p.addBeadSet(args);

		else if(line[0].equals("BACTERIA_RADIUS:")) p.setBactRadius(args[0]);
		else if(line[0].equals("CREATE_BACTERIUM_SINGLE:")) p.addSingleBacterium(args);
		else if(line[0].equals("CREATE_BACTERIA_SET:")) p.addBacteriaSet(args);
		else if(line[0].equals("BACTERIA_FORCE_UP:")) p.setBactForceUp(args[0]);
		else if(line[0].equals("BACTERIA_FORCE_DOWN:")) p.setBactForceDown(args[0]);		
		else if(line[0].equals("UP_RUN_LENGTH:")) p.setUpRunLength(args[0]);
		else if(line[0].equals("DOWN_RUN_LENGTH:")) p.setDownRunLength(args[0]);
		else if(line[0].equals("ISO_RUN_LENGTH:")) p.setIsoRunLength(args[0]);
			
		else if(line[0].equals("VISCOSITY:")) p.setViscosity(args[0]);
		
		else if(line[0].equals("SCREEN_HEIGHT:")) p.setScreenHeight((int)args[0]);
		else if(line[0].equals("SCREEN_WIDTH:")) p.setScreenWidth((int)args[0]);
		else if(line[0].equals("SCREEN_ZOOM:")) p.setScreenZoom((double)args[0]);
		else if(line[0].equals("SCREEN_MOVE:")) p.setScreenMove((double)args[0], (double)args[1]);
		
		else if(line[0].equals("SCREEN_MINIMUM_DISTANCE:")) p.setMinimumDistance((double)args[0]);
		else if(line[0].equals("SCREEN_MAXIMUM_DISTANCE:")) p.setMaximumDistance((double)args[0]);
		else if(line[0].equals("SCREEN_DEFAULT_DISTANCE:")) p.setDefaultDistance((double)args[0]);
		else if(line[0].equals("SCREEN_FRAME_FOR_SECOND:")) p.setFrameForSec((int)args[0]);
		else if(line[0].equals("SCREEN_FRAME_RECORD_FOR_SECOND:")) p.setFrameRecordForSec((int)args[0]);
		
		else if(line[0].equals("PHYSICS_WELL_WIDTH_BACT_BACT:")) p.setWellWidthBactBact((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_BACT_BACT:")) p.setWellDepthBactBact((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_WIDTH_BACT_BEAD:")) p.setWellWidthBactBead((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_BACT_BEAD:")) p.setWellDepthBactBead((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_WIDTH_BEAD_BEAD:")) p.setWellWidthBeadBead((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_BEAD_BEAD:")) p.setWellDepthBeadBead((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_WIDTH_BEAD_BDRY:")) p.setWellWidthBeadBdry((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_BEAD_BDRY:")) p.setWellDepthBeadBdry((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_WIDTH_BACT_BDRY:")) p.setWellWidthBactBdry((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_BACT_BDRY:")) p.setWellDepthBactBdry((double)args[0]);
		
		else if(line[0].equals("PHYSICS_WELL_WIDTH_VES_BDRY:")) p.setWellWidthVesBdry((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_VES_BDRY:")) p.setWellDepthVesBdry((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_WIDTH_VES_BEAD:")) p.setWellWidthVesBead((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_VES_BEAD:")) p.setWellDepthVesBead((double)args[0]);
		
		else if(line[0].equals("PHYSICS_REACT_FORCE:")) p.setReactForce((double)args[0]);				
		
		else if(line[0].equals("FIELD_GOAL_DEFINE:")) p.setCfGoalDefine(args);
		else if(line[0].equals("FIELD_GOAL_SETUP:")) p.setCfGoalSetup(args);
		else if(line[0].equals("FIELD_COORD_DEFINE:")) p.setCfCoordDefine(args);
		else if(line[0].equals("FIELD_COORD_SETUP:")) p.setCfCoordSetup(args);
		else if(line[0].equals("FIELD_RECRUIT_DEFINE:")) p.setCfRecruitDefine(args);
		else if(line[0].equals("FIELD_RECRUIT_SETUP:")) p.setCfRecruitSetup(args);
		else if(line[0].equals("FIELD_QUORUM_DEFINE:")) p.setCfQuorumDefine(args);
		else if(line[0].equals("FIELD_QUORUM_SETUP:")) p.setCfQuorumSetup(args);
		
		else if(line[0].equals("BOUNDING_BOX_DEFINE:")) p.setBoundingBoxDefine(args);
		
		else if(line[0].equals("MAGN_FIELD_STRENGTH")) p.setMagnStrength(args);
		
		else if(line[0].equals("VISUAL_AID_BACTERIA_TRACE:")) p.addBacteriaTrace(args);
		else if(line[0].equals("VISUAL_AID_AVG_BACTERIA_TRACE:")) p.addAvgBacteriaTrace(args);
		else if(line[0].equals("VISUAL_AID_BEAD_TRACE:")) p.addBeadTrace(args);
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