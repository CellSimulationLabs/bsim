/**
 * BSimParameters.java
 *
 * Class containing all simulation parameters of interest and get/set methods for each one
 * 
 * Authors: Ian Miles
 *          Thomas Gorochowski (Updates)
 * Created: 05/08/2008
 * Updated: 22/08/2008
 */


//Define the location of the class in the bsim package
package bsim;

// Import the bsim packages used
import bsim.*;
import bsim.object.*;

//Standard packages required by the application
import java.util.*;
import java.awt.*;


public class BSimParameters {


	public double   partSize         = 10.0;	// microns (diameter)

	public double   bactSize         = 1.4;	// microns (diameter)
	public double   bactForceUp      = 0.4387; // micro newtons
	public double   bactForceDown    = 0.41; // micro newtons
	public double   bactSpeed        = 50.0; // microns per second
	
	public double   runLengthUp      = 1.07;	// seconds
	public double   runLengthDown    = 0.8;	// seconds
	public double   runLengthIso     = 0.86; // seconds
	
	public double   visc             = Math.pow(10.0,-3.0); // Pascal seconds
	public int      screenWidth      = 1000; // pixels
	public int      screenHeight     = 700; // pixels

	public double   dt               = 0.005; // seconds (was 0.01 but too big), jumpping boundaries
	
	public Vector   bacteriaSingles, 
	                bacteriaSets, 
	                particleSingles, 
	                particleSets, 
	                solidBoundaries,
	                wrapBoundaries,
	                vaBacteriaTraces,
	                vaAvgBacteriaTraces,
	                vaParticleTraces,
	                vaClocks,
					vaScales;

	public double[] cfGoalDefine = {0, 0, 10, 10, 10, 10, 0.001, 1, 1, 0.8};
	public double[] cfGoalSetup = {0, 0, 0, 0, 0, 0};
	public double[] cfCoordDefine = {0, 0, 10, 10, 10, 10, 0.001, 1, 1, 0.8};
	public double[] cfCoordSetup = {0, 0, 0, 0, 0, 0};
	public double[] cfRecruitDefine = {0, 0, 10, 10, 10, 10, 0.001, 1, 1, 0.8};
	public double[] cfRecruitSetup = {0, 0, 0, 0, 0, 0};
	
	public double   screenZoom       = 1, 
	                screenMove[]     = {0.0, 0.0};
	
	public int      dataFramesSkip   = 1;
	public int      videoFramesSkip  = 1;
	public int      simRuns          = 1;
	public int      simLength        = 1;
	public boolean  recordVideo      = true;
	public String   exportDir;
	public int      numOfThreads     = 2;
	
	public double   wellWidthBactBact = 0.0;
	public double   wellDepthBactBact = 0.0;
	public double   wellWidthBactPart = 0.0;
	public double   wellDepthBactPart = 0.0;
	public double   wellWidthPartPart = 0.0;
	public double   wellDepthPartPart = 0.0;
	public double   wellWidthPartBdry = 0.0;
	public double   wellDepthPartBdry = 0.0;
	public double   wellWidthBactBdry = 0.0;
	public double   wellDepthBactBdry = 0.0;
	
	public double	reactForce = 0.0;
	
	
	public BSimParameters() {

		// Create the vectors for the objects that will need to be created by the scene
		bacteriaSingles = new Vector();
		bacteriaSets = new Vector();
		particleSingles = new Vector();
		particleSets = new Vector();
		solidBoundaries = new Vector();
        wrapBoundaries = new Vector();
        vaBacteriaTraces = new Vector();
        vaAvgBacteriaTraces = new Vector();
        vaParticleTraces = new Vector();
        vaClocks = new Vector();
		vaScales = new Vector();
		
		// need to join bactForce,bactSpeed, size & visc
		// - maybe make speed (or force ) a function of force (or speed) , viscosity and size?
	}
	
	
	/**
	 * Standard get methods.
	 */
	public double 	getDtSecs() {return dt;}
	public double 	getBactSize() {return bactSize;}
	public double	getBactRadius() {return bactSize/2.0;}
	public double 	getPartSize() {return partSize;}
	public double 	getPartRadius() {return partSize/2.0;}
	public double 	getBactForceUp() {return bactForceUp;}
	public double 	getBactForceDown() {return bactForceDown;}
	public double 	getBactSpeed() {return bactSpeed;}
	public double 	getViscosity() {return visc;}
	public int 		getScreenHeight() {return screenHeight;}
	public int 		getScreenWidth() {return screenWidth;}
	public double 	getUpRunLength() {return runLengthUp;}
	public double 	getDownRunLength() {return runLengthDown;}
	public double 	getIsoRunLength() {return runLengthIso;}
	public Vector 	getSingleBacteria() { return bacteriaSingles; }
	public Vector 	getSingleParticle() { return particleSingles; }
	public Vector 	getBacteriaSet() { return bacteriaSets; }
	public Vector 	getParticleSet() { return particleSets; }
	public Vector   getSolidBoundaries() { return solidBoundaries; }
	public Vector   getWrapBoundaries() { return wrapBoundaries; }
	
	public Vector   getBacteriaTraces() { return vaBacteriaTraces; }
	public Vector   getAvgBacteriaTraces() { return vaAvgBacteriaTraces; }
	public Vector   getParticleTraces() { return vaParticleTraces; }
	public Vector   getClocks() { return vaClocks; }
	public Vector	getScales() { return vaScales; }
	
	public double	geScreenZoom() {return screenZoom;}
	public double[]	getScreenMove() {return screenMove;}
	public int		getDataFramesSkip() {return dataFramesSkip;}
	public int		getVideoFramesSkip() {return videoFramesSkip;}
	public int		getSimRuns() {return simRuns;}
	public int		getSimLength() {return simLength;}
	public boolean  getRecordVideo() {return recordVideo;}
	public String	getExportDir() {return exportDir;}
	public int		getNumOfThreads() {return numOfThreads;}
	
	public double   getWellWidthBactBact () { return wellWidthBactBact; }
	public double   getWellWidthBactPart () { return wellWidthBactPart; }
	public double   getWellWidthPartPart () { return wellWidthPartPart; }
	public double   getWellWidthPartBdry () { return wellWidthPartBdry; }
	public double   getWellWidthBactBdry () { return wellWidthBactBdry; }
	
	public double   getWellDepthBactBact () { return wellDepthBactBact; }
	public double   getWellDepthBactPart () { return wellDepthBactPart; }
	public double   getWellDepthPartPart () { return wellDepthPartPart; }
	public double   getWellDepthPartBdry () { return wellDepthPartBdry; }
	public double   getWellDepthBactBdry () { return wellDepthBactBdry; }
	
	public double	getReactForce () { return reactForce; }
	
	public double[]  getCfGoalDefine () { return cfGoalDefine; }
	public double[]  getCfGoalSetup () { return cfGoalSetup; }
	public double[]  getCfCoordDefine () { return cfCoordDefine; }
	public double[]  getCfCoordSetup () { return cfCoordSetup; }
	public double[]  getCfRecruitDefine () { return cfRecruitDefine; }
	public double[]  getCfRecruitSetup () { return cfRecruitSetup; }
	
	
	/**
	 * Standard set methods.
	 */
	public void 	setDtSecs(double newDt) {dt = newDt;}
	public void 	setBactSize(double s) {bactSize = s;}
	public void		setBactRadius(double r) {bactSize = 2.0*r;}
	public void 	setPartSize(double s) {partSize = s;}
	public void 	setPartRadius(double r) {partSize = 2.0*r;}
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
	public void 	addSingleParticle(double[] p) { particleSingles.add(p); }
	public void 	addBacteriaSet(double[] b) { bacteriaSets.add(b); }
	public void 	addParticleSet(double[] p) { particleSets.add(p); }
	
	public void 	addSolidBoundary(double[] p) { solidBoundaries.add(p); }
	public void 	addWrapBoundary(double[] p) { wrapBoundaries.add(p); }
	
	public void 	addBacteriaTrace(double[] p) { vaBacteriaTraces.add(p); }
	public void 	addAvgBacteriaTrace(double[] p) { vaAvgBacteriaTraces.add(p); }
	public void 	addParticleTrace(double[] p) { vaParticleTraces.add(p); }
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
	
	public void     setWellWidthBactBact ( double x) { wellWidthBactBact = x; }
	public void     setWellWidthBactPart ( double x) { wellWidthBactPart = x; }
	public void     setWellWidthPartPart ( double x) { wellWidthPartPart = x; }
	public void     setWellWidthPartBdry ( double x) { wellWidthPartBdry = x; }
	public void     setWellWidthBactBdry ( double x) { wellWidthBactBdry = x; }
	
	public void     setWellDepthBactBact ( double x) { wellDepthBactBact = x; }
	public void     setWellDepthBactPart ( double x) { wellDepthBactPart = x; }
	public void     setWellDepthPartPart ( double x) { wellDepthPartPart = x; }
	public void     setWellDepthPartBdry ( double x) { wellDepthPartBdry = x; }
	public void     setWellDepthBactBdry ( double x) { wellDepthBactBdry = x; }
	
	public void 	setReactForce ( double x) { reactForce = x; }
	
	public void     setCfGoalDefine ( double[] x) { cfGoalDefine = x; }
	public void     setCfGoalSetup ( double[] x) { cfGoalSetup = x; }
	public void     setCfCoordDefine ( double[] x) { cfCoordDefine = x; }
	public void     setCfCoordSetup ( double[] x) { cfCoordSetup = x; }
	public void     setCfRecruitDefine ( double[] x) { cfRecruitDefine = x; }
	public void     setCfRecruitSetup ( double[] x) { cfRecruitSetup = x; }
	
	
	/**
	 * Create methods to create the sets of items that are required by the scene.
	 */
	
	public Vector createNewBacteriaVec(BSimScene scene) {
		int i;
		
		// Vector to hold the new objects
		Vector newVec = new Vector();
		
		// Create a new single particle for every item in the list
		for(i=0; i<bacteriaSingles.size(); i++){
			newVec.add(BSimBacteriaCreate.createBacterium((double[])bacteriaSingles.elementAt(i), scene, this));
		}
		
		// Create a new particle set for every item in the list
		for(i=0; i<bacteriaSets.size(); i++){
			newVec.addAll(BSimBacteriaCreate.createBacteriaSet((double[])bacteriaSets.elementAt(i), scene, this));
		}
		
		// Return the new vector
		return newVec;
	}
	
	public Vector createNewParticleVec() {
		int i;
		
		// Vector to hold the new objects
		Vector newVec = new Vector();
		
		// Create a new single particle for every item in the list
		for(i=0; i<particleSingles.size(); i++){
			newVec.add(BSimParticlesCreate.createParticle((double[])particleSingles.elementAt(i), this));
		}
		
		// Create a new particle set for every item in the list
		for(i=0; i<particleSets.size(); i++){
			newVec.addAll(BSimParticlesCreate.createParticleSet((double[])particleSets.elementAt(i), this));
		}
		
		// Return the new vector
		return newVec;
	}
	
	public Vector createNewSolidBoundariesVec() {
		
		// Vector to hold the new objects
		Vector newVec = new Vector();
		
		// Create a new solid boundary for every item in the list
		for(int i=0; i<solidBoundaries.size(); i++){
			newVec.add(BSimBoundaryCreate.createSolidBoundary((double[])solidBoundaries.elementAt(i)));
		}
		
		// Return the new vector
		return newVec;
	}
	
	public Vector createNewWrapBoundariesVec() {
		
		// Vector to hold the new objects
		Vector newVec = new Vector();
		
		// Create a new solid boundary for every item in the list
		for(int i=0; i<wrapBoundaries.size(); i++){
			newVec.add(BSimBoundaryCreate.createWrapBoundary((double[])wrapBoundaries.elementAt(i)));
		}
		
		// Return the new vector
		return newVec;
	}
	
	public Vector createNewVisualAidsVec(BSimScene scene) {
		int i;
		
		// Vector to hold the new objects
		Vector newVec = new Vector();
		
		// Loop through each of the visual aid types, create them and add to the same vector
		for(i=0; i<vaBacteriaTraces.size(); i++){
			newVec.add(BSimVisualAidCreate.createBacteriaTrace(scene, (double[])vaBacteriaTraces.elementAt(i)));
		}
		for(i=0; i<vaAvgBacteriaTraces.size(); i++){
			newVec.add(BSimVisualAidCreate.createAvgBacteriaTrace(scene, (double[])vaAvgBacteriaTraces.elementAt(i)));
		}
		for(i=0; i<vaParticleTraces.size(); i++){
			newVec.add(BSimVisualAidCreate.createParticleTrace(scene, (double[])vaParticleTraces.elementAt(i)));
		}
		for(i=0; i<vaClocks.size(); i++){
			newVec.add(BSimVisualAidCreate.createSceneClock(scene));
		}
		for(i=0; i<vaScales.size(); i++){
			newVec.add(BSimVisualAidCreate.createSceneScale(scene, (double[])vaScales.elementAt(i)));
		}
		
		// Return the new vector
		return newVec;
	}
	
	public BSimChemicalField createNewGoalChemicalField() {
		
		// Create the new chemical field
		return BSimChemicalFieldCreate.createChemicalField (cfGoalDefine, cfGoalSetup,
		                                               new Color(0.8f, 0.1f, 0.1f), this);
	}
	
	public BSimChemicalField createNewCoordChemicalField() {
		
		// Create the new chemical field
		return BSimChemicalFieldCreate.createChemicalField (cfCoordDefine, cfCoordSetup, 
		                                               new Color(0.1f, 0.1f, 0.8f), this);
	}
	
	public BSimChemicalField createNewRecruitChemicalField() {
		
		// Create the new chemical field
		return BSimChemicalFieldCreate.createChemicalField (cfRecruitDefine, cfRecruitSetup, 
		                                               new Color(0.1f, 0.8f, 0.1f), this);
	}
}
