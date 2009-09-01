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

import bsim.field.BSimChemicalField;
import bsim.field.BSimChemicalFieldCreate;
import bsim.particle.bacterium.BSimBacteriaCreate;
import bsim.particle.bead.BSimBeadsCreate;
import bsim.render.visualaid.BSimVisualAidCreate;


public class BSimParameters {


	private static double   beadRadius       = 10.0;	// microns

	private static double   bactRadius       = 1.4;	// microns
	private static double   bactForceUp      = 0.4387; // pico newtons
	private static double   bactForceDown    = 0.41; // pico newtons
	private static double   bactSpeed        = 50.0; // microns per second
	
	private static double   runLengthUp      = 1.07;	// seconds
	private static double   runLengthDown    = 0.8;	// seconds
	private static double   runLengthIso     = 0.86; // seconds
	
	private static double   visc             = Math.pow(10.0,-3.0); // Pascal seconds
	private static int      screenWidth      = 1025; // pixels
	private static int      screenHeight     = 700; // pixels

	private static double   dt               = 0.001; // seconds
	
	private static Vector<double[]> bacteriaSingles 	  = new Vector(); 
	private static Vector<double[]> bacteriaSets 		  = new Vector();
	private static Vector<double[]> beadSingles 		  = new Vector();
	private static Vector<double[]> beadSets			  = new Vector(); 	              	               
	private static Vector<double[]> vaBacteriaTraces 	  = new Vector();
	private static Vector<double[]> vaAvgBacteriaTraces   = new Vector();
	private static Vector<double[]> vaBeadTraces 		  = new Vector();
	private static Vector<double[]> vaClocks 			  = new Vector();
	private static Vector<double[]> vaScales			  = new Vector();

	private static double[] cfGoalDefine = {0, 0, 0, 10, 10, 10, 10, 10, 10, 0.001, 1, 1, 0.8};
	private static double[] cfGoalSetup = {0, 0, 0, 0, 0, 0};
	private static double[] cfCoordDefine = {0, 0, 0, 10, 10, 10, 10, 10, 10, 0.001, 1, 1, 0.8};
	private static double[] cfCoordSetup = {0, 0, 0, 0, 0, 0};
	private static double[] cfRecruitDefine = {0, 0, 0, 10, 10, 10, 10, 10, 10, 0.001, 1, 1, 0.8};
	private static double[] cfRecruitSetup = {0, 0, 0, 0, 0, 0};
	private static double[] cfQuorumDefine = {0, 0, 0, 10, 10, 10, 10, 10, 10, 0.001, 1, 1, 0.8};
	private static double[] cfQuorumSetup = {0, 0, 0, 0, 0, 0};
	
	private static double[] boundingBoxDefine = {0, 0, 0, 0, 0, 0};	
	
	private static double[] magnStrength = {0.0, 0.0, 0.0};
	
	private static double   screenZoom       = 1, 
	                screenMove[]     = {0.0, 0.0};
	
	//parameters needed to control the processing camera
	private static double   minimumDistance  = 0.001;
	private static double   maximumDistance  = 1500;
	private static double   defaultDistance  = 1000;
	private static int      frameForSec      = 25;
	private static int      frameRecordForSec= 25;
	
	private static int      dataFramesSkip   = 1;
	private static int      videoFramesSkip  = 1;
	private static int      simRuns          = 1;
	private static int      simLength        = 1;
	private static boolean  recordVideo      = true;
	private static String   exportDir;
	private static int      numOfThreads     = 2;
	
	private static double   wellWidthBactBact = 0.0;
	private static double   wellDepthBactBact = 0.0;
	private static double   wellWidthBactBead = 0.0;
	private static double   wellDepthBactBead = 0.0;
	private static double   wellWidthBeadBead = 0.0;
	private static double   wellDepthBeadBead = 0.0;
	private static double   wellWidthBeadBdry = 0.0;
	private static double   wellDepthBeadBdry = 0.0;
	private static double   wellWidthBactBdry = 0.0;
	private static double   wellDepthBactBdry = 0.0;
	private static double   wellWidthVesBdry = 0.0;
	private static double   wellDepthVesBdry = 0.0;
	private static double   wellWidthVesBead = 0.0;
	private static double   wellDepthVesBead = 0.0;
	
	private static double	reactForce = 0.0;
		
	
	public BSimParameters() {	
	}
		
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
				
		if     (line[0].equals("DT:")) setDtSecs(args[0]);
		
		else if(line[0].equals("BEAD_RADIUS:")) setBeadRadius(args[0]);
		else if(line[0].equals("CREATE_BEAD_SINGLE:")) addSingleBead(args);
		else if(line[0].equals("CREATE_BEAD_SET:"))	addBeadSet(args);

		else if(line[0].equals("BACTERIA_RADIUS:")) setBactRadius(args[0]);
		else if(line[0].equals("CREATE_BACTERIUM_SINGLE:")) addSingleBacterium(args);
		else if(line[0].equals("CREATE_BACTERIA_SET:")) addBacteriaSet(args);
		else if(line[0].equals("BACTERIA_FORCE_UP:")) setBactForceUp(args[0]);
		else if(line[0].equals("BACTERIA_FORCE_DOWN:")) setBactForceDown(args[0]);		
		else if(line[0].equals("UP_RUN_LENGTH:")) setUpRunLength(args[0]);
		else if(line[0].equals("DOWN_RUN_LENGTH:")) setDownRunLength(args[0]);
		else if(line[0].equals("ISO_RUN_LENGTH:")) setIsoRunLength(args[0]);
			
		else if(line[0].equals("VISCOSITY:")) setViscosity(args[0]);
		
		else if(line[0].equals("SCREEN_HEIGHT:")) setScreenHeight((int)args[0]);
		else if(line[0].equals("SCREEN_WIDTH:")) setScreenWidth((int)args[0]);
		else if(line[0].equals("SCREEN_ZOOM:")) setScreenZoom((double)args[0]);
		else if(line[0].equals("SCREEN_MOVE:")) setScreenMove((double)args[0], (double)args[1]);
		
		else if(line[0].equals("SCREEN_MINIMUM_DISTANCE:")) setMinimumDistance((double)args[0]);
		else if(line[0].equals("SCREEN_MAXIMUM_DISTANCE:")) setMaximumDistance((double)args[0]);
		else if(line[0].equals("SCREEN_DEFAULT_DISTANCE:")) setDefaultDistance((double)args[0]);
		else if(line[0].equals("SCREEN_FRAME_FOR_SECOND:")) setFrameForSec((int)args[0]);
		else if(line[0].equals("SCREEN_FRAME_RECORD_FOR_SECOND:")) setFrameRecordForSec((int)args[0]);
		
		else if(line[0].equals("PHYSICS_WELL_WIDTH_BACT_BACT:")) setWellWidthBactBact((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_BACT_BACT:")) setWellDepthBactBact((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_WIDTH_BACT_BEAD:")) setWellWidthBactBead((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_BACT_BEAD:")) setWellDepthBactBead((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_WIDTH_BEAD_BEAD:")) setWellWidthBeadBead((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_BEAD_BEAD:")) setWellDepthBeadBead((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_WIDTH_BEAD_BDRY:")) setWellWidthBeadBdry((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_BEAD_BDRY:")) setWellDepthBeadBdry((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_WIDTH_BACT_BDRY:")) setWellWidthBactBdry((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_BACT_BDRY:")) setWellDepthBactBdry((double)args[0]);
		
		else if(line[0].equals("PHYSICS_WELL_WIDTH_VES_BDRY:")) setWellWidthVesBdry((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_VES_BDRY:")) setWellDepthVesBdry((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_WIDTH_VES_BEAD:")) setWellWidthVesBead((double)args[0]);
		else if(line[0].equals("PHYSICS_WELL_DEPTH_VES_BEAD:")) setWellDepthVesBead((double)args[0]);
		
		else if(line[0].equals("PHYSICS_REACT_FORCE:")) setReactForce((double)args[0]);				
		
		else if(line[0].equals("FIELD_GOAL_DEFINE:")) setCfGoalDefine(args);
		else if(line[0].equals("FIELD_GOAL_SETUP:")) setCfGoalSetup(args);
		else if(line[0].equals("FIELD_COORD_DEFINE:")) setCfCoordDefine(args);
		else if(line[0].equals("FIELD_COORD_SETUP:")) setCfCoordSetup(args);
		else if(line[0].equals("FIELD_RECRUIT_DEFINE:")) setCfRecruitDefine(args);
		else if(line[0].equals("FIELD_RECRUIT_SETUP:")) setCfRecruitSetup(args);
		else if(line[0].equals("FIELD_QUORUM_DEFINE:")) setCfQuorumDefine(args);
		else if(line[0].equals("FIELD_QUORUM_SETUP:")) setCfQuorumSetup(args);
		
		else if(line[0].equals("BOUNDING_BOX_DEFINE:")) setBoundingBoxDefine(args);
		
		else if(line[0].equals("MAGN_FIELD_STRENGTH")) setMagnStrength(args);
		
		else if(line[0].equals("VISUAL_AID_BACTERIA_TRACE:")) addBacteriaTrace(args);
		else if(line[0].equals("VISUAL_AID_AVG_BACTERIA_TRACE:")) addAvgBacteriaTrace(args);
		else if(line[0].equals("VISUAL_AID_BEAD_TRACE:")) addBeadTrace(args);
		else if(line[0].equals("VISUAL_AID_CLOCK:")) addClock(args);
		else if(line[0].equals("VISUAL_AID_SCALE:")) addScale(args);
		
		else if(line[0].equals("VIDEO_FRAMES_SKIP:")) setVideoFramesSkip((int)args[0]);
		else if(line[0].equals("RECORD_VIDEO:")) {
			if((int)args[0] == 1) setRecordVideo(true);
			else setRecordVideo(false);
		}
		
		else if(line[0].equals("DATA_FRAMES_SKIP:")) setDataFramesSkip((int)args[0]);
		
		else if(line[0].equals("SIMULATION_LENGTH:")) setSimLength((int)args[0]);
		else if(line[0].equals("SIMULATION_RUNS:")) setSimRuns((int)args[0]);
		
		else if(line[0].equals("EXPORT_DIR:")) setExportDir(line[1]);
		else if(line[0].equals("NUMBER_OF_THREADS:")) setNumOfThreads((int)args[0]);
		
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
		
	/**
	 * Standard get methods.
	 */
	public double 	getDtSecs() {return dt;}	
	public double	getBactRadius() {return bactRadius;}	
	public double 	getBeadRadius() {return beadRadius;}
	public double 	getBactForceUp() {return bactForceUp;}
	public double 	getBactForceDown() {return bactForceDown;}
	public double 	getBactSpeed() {return bactSpeed;}
	public double 	getViscosity() {return visc;}
	public int 		getScreenHeight() {return screenHeight;}
	public int 		getScreenWidth() {return screenWidth;}
	public double 	getUpRunLength() {return runLengthUp;}
	public double 	getDownRunLength() {return runLengthDown;}
	public double 	getIsoRunLength() {return runLengthIso;}
	public Vector<double[]> 	getSingleBacteria() { return bacteriaSingles; }
	public Vector<double[]> 	getSingleBead() { return beadSingles; }
	public Vector<double[]> 	getBacteriaSet() { return bacteriaSets; }
	public Vector<double[]> 	getBeadSet() { return beadSets; }	
	
	public Vector<double[]>   getBacteriaTraces() { return vaBacteriaTraces; }
	public Vector<double[]>   getAvgBacteriaTraces() { return vaAvgBacteriaTraces; }
	public Vector<double[]>   getBeadTraces() { return vaBeadTraces; }
	public Vector<double[]>   getClocks() { return vaClocks; }
	public Vector<double[]>	getScales() { return vaScales; }
	
	public double	getScreenZoom() {return screenZoom;}
	
	//parameters needed to control the processing camera
	public double   getMinimumDistance() {return minimumDistance;}
	public double   getMaximumDistance() {return maximumDistance;}
	public double   getDefaultDistance() {return defaultDistance;}
	public int      getFrameForSec() {return frameForSec;}
	public int      getFrameRecordForSec() {return frameRecordForSec;}
	
	public double[]	getScreenMove() {return screenMove;}
	public int		getDataFramesSkip() {return dataFramesSkip;}
	public int		getVideoFramesSkip() {return videoFramesSkip;}
	public int		getSimRuns() {return simRuns;}
	public int		getSimLength() {return simLength;}
	public boolean  getRecordVideo() {return recordVideo;}
	public String	getExportDir() {return exportDir;}
	public int		getNumOfThreads() {return numOfThreads;}
	
	public double   getWellWidthBactBact () { return wellWidthBactBact; }
	public double   getWellWidthBactBead () { return wellWidthBactBead; }
	public double   getWellWidthBeadBead () { return wellWidthBeadBead; }
	public double   getWellWidthBeadBdry () { return wellWidthBeadBdry; }
	public double   getWellWidthBactBdry () { return wellWidthBactBdry; }
	
	public double   getWellDepthBactBact () { return wellDepthBactBact; }
	public double   getWellDepthBactBead () { return wellDepthBactBead; }
	public double   getWellDepthBeadBead () { return wellDepthBeadBead; }
	public double   getWellDepthBeadBdry () { return wellDepthBeadBdry; }
	public double   getWellDepthBactBdry () { return wellDepthBactBdry; }
	
	public double   getWellWidthVesBdry () { return wellWidthVesBdry; }
	public double   getWellDepthVesBdry () { return wellDepthVesBdry; }
	public double   getWellWidthVesBead () { return wellWidthVesBead; }
	public double   getWellDepthVesBead () { return wellDepthVesBead; }
	
	
	public double	getReactForce () { return reactForce; }
	
	public double[]	 getMagnStrength () {return magnStrength; }
	
	public double[]  getCfGoalDefine () { return cfGoalDefine; }
	public double[]  getCfGoalSetup () { return cfGoalSetup; }
	public double[]  getCfCoordDefine () { return cfCoordDefine; }
	public double[]  getCfCoordSetup () { return cfCoordSetup; }
	public double[]  getCfRecruitDefine () { return cfRecruitDefine; }
	public double[]  getCfRecruitSetup () { return cfRecruitSetup; }
	public double[]  getCfQuorumDefine () { return cfQuorumDefine; }
	public double[]  getCfQuorumSetup () { return cfQuorumSetup; }
	
	public double[]  getBoundingBoxDefine() { return boundingBoxDefine; }
	
	
	/**
	 * Standard set methods.
	 */
	public void 	setDtSecs(double newDt) {dt = newDt;}	
	public void		setBactRadius(double r) {bactRadius = r;}	
	public void 	setBeadRadius(double r) {beadRadius = r;}
	public void 	setBactForceUp(double f) {bactForceUp = f;}
	public void 	setBactForceDown(double f) {bactForceDown = f;}
	public void 	setBactSpeed(double s) {bactSpeed = s;}
	public void 	setViscosity(double v) {visc = v;}
	public void 	setScreenHeight(int h) {screenHeight =h;}
	public void 	setScreenWidth(int w) {screenWidth = w;}
	public void 	setUpRunLength(double l) {runLengthUp = l;}
	public void 	setDownRunLength(double l) {runLengthDown = l;}
	public void 	setIsoRunLength(double l) {runLengthIso = l;}

	public void 	addSingleBacterium(double[] b) { bacteriaSingles.add(b); }
	public void 	addSingleBead(double[] p) { beadSingles.add(p); }
	public void 	addBacteriaSet(double[] b) { bacteriaSets.add(b); }
	public void 	addBeadSet(double[] p) { beadSets.add(p); }
		
	public void 	addBacteriaTrace(double[] p) { vaBacteriaTraces.add(p); }
	public void 	addAvgBacteriaTrace(double[] p) { vaAvgBacteriaTraces.add(p); }
	public void 	addBeadTrace(double[] p) { vaBeadTraces.add(p); }
	public void 	addClock(double[] p) { vaClocks.add(p); }
	public void		addScale(double[] p) { vaScales.add(p); }
	
	public void		setScreenZoom(double z) { screenZoom = z;}
	public void		setScreenMove(double x, double y) { screenMove[0] = x; screenMove[1] = y; }
	public void		setDataFramesSkip(int x) { dataFramesSkip = x;}
	public void	 	setVideoFramesSkip(int x) { videoFramesSkip = x;}
	public void	 	setSimRuns(int x) { simRuns = x;}
	public void	 	setSimLength(int x) { simLength = x;}
	public void  	setRecordVideo(boolean x) { recordVideo = x;}
	public void	 	setExportDir(String x) { exportDir = x;}
	public void 	setNumOfThreads(int x) { numOfThreads = x;}
	
	//parameters needed to control the processing camera
	public void     setMinimumDistance( double x) {minimumDistance = x;}
	public void     setMaximumDistance( double x) {maximumDistance = x;}
	public void     setDefaultDistance( double x) {defaultDistance = x;}
	public void     setFrameForSec(int x) {frameForSec = x;}
	public void     setFrameRecordForSec(int x) {frameRecordForSec = x;}
	
	public void     setWellWidthBactBact ( double x) { wellWidthBactBact = x; }
	public void     setWellWidthBactBead ( double x) { wellWidthBactBead = x; }
	public void     setWellWidthBeadBead ( double x) { wellWidthBeadBead = x; }
	public void     setWellWidthBeadBdry ( double x) { wellWidthBeadBdry = x; }
	public void     setWellWidthBactBdry ( double x) { wellWidthBactBdry = x; }
	
	public void     setWellDepthBactBact ( double x) { wellDepthBactBact = x; }
	public void     setWellDepthBactBead ( double x) { wellDepthBactBead = x; }
	public void     setWellDepthBeadBead ( double x) { wellDepthBeadBead = x; }
	public void     setWellDepthBeadBdry ( double x) { wellDepthBeadBdry = x; }
	public void     setWellDepthBactBdry ( double x) { wellDepthBactBdry = x; }
	
	public void     setWellWidthVesBdry (double x) { wellWidthVesBdry = x; }
	public void     setWellDepthVesBdry (double x) { wellDepthVesBdry = x; }
	public void     setWellWidthVesBead (double x) { wellWidthVesBead = x; }
	public void     setWellDepthVesBead (double x) { wellDepthVesBead = x; }
	
	public void 	setReactForce ( double x) { reactForce = x; }
	
	public void		setMagnStrength (double[] x) {magnStrength = x; }
	
	public void     setCfGoalDefine ( double[] x) { cfGoalDefine = x; }
	public void     setCfGoalSetup ( double[] x) { cfGoalSetup = x; }
	public void     setCfCoordDefine ( double[] x) { cfCoordDefine = x; }
	public void     setCfCoordSetup ( double[] x) { cfCoordSetup = x; }
	public void     setCfRecruitDefine ( double[] x) { cfRecruitDefine = x; }
	public void     setCfRecruitSetup ( double[] x) { cfRecruitSetup = x; }
	public void     setCfQuorumDefine ( double[] x) { cfQuorumDefine = x; }
	public void     setCfQuorumSetup ( double[] x) { cfQuorumSetup = x; }
	
	public void     setBoundingBoxDefine( double[] x) { boundingBoxDefine = x; }

}
