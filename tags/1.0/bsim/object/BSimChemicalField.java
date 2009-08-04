/**
 * BSimChemicalField.java
 *
 * Class to hold a chemical field. Two type are available, fixed and difuse. Fixed fields
 * will merely remain static and have a use for the goal chemoattractant. Difuse fields
 * will alter over time with concentrtions spreading out over the field. Boundary
 * conditions are specified by the user and allow for removal of chemical (by difusion out
 * the area) or conservation (keeping all chemicals within the field area).
 *
 * Authors: Thomas Gorochowski
 * Created: 20/07/2008
 * Updated: 20/07/2008
 */


//Define the location of the class in the bsim package
package bsim.object;

// Import the bsim packages used
import bsim.*;

//Standard packages required by the application
import java.awt.*;


public class BSimChemicalField {


	// The type of field
	// TYPE_FIXED  = fixed field (no diffusion)
	// TYPE_DIFUSE = diffuses
	public static int TYPE_FIXED   = 1;
	public static int TYPE_DIFFUSE = 2;
	protected int fieldType        = TYPE_FIXED;

	// The boundary type
	public static int BOUNDARY_LEAK     = 1;
	public static int BOUNDARY_CONSERVE = 2;
	protected int boundaryType          = BOUNDARY_LEAK;

	// The discrete 2D field holding concentration values
	protected double[][] field;

	// The diffusion rate between field elements (this is per unit squared area)
	// IMPORTANT: Field blocks may not be square in shape so rate must be altered 
	//            for each side
	protected double rate = 0.0;

	// Position of the field in the simulation space
	protected double[] startPos;
	protected double width, height;

	// Number of discrete divisions along each axis
	protected int xBoxes, yBoxes;
	protected double boxWidth, boxHeight;

	// Time step length
	protected double dt;

	// Colour of the field when it is displayed
	protected Color colour;

	// Maximum concentration (should be less than 1 as this will be the alpha value)
	protected float maxCon = 0.3f;

	// Flag stating whether the field is displayed
	protected boolean isDisplayed = true;

	// The direction of a linear field (constants)
	public static int LINEAR_X = 1;
	public static int LINEAR_Y = 2;

	// The type of diffusion used (if required)
	public static int DIFFUSE_X2  = 1;
	public static int DIFFUSE_EXP = 2;
	protected int diffuseType     = DIFFUSE_X2;
	
	// The minimum change in concentration that bacteria can detect 
	protected double threshold;
	
	// Parameters for the simulation
	protected BSimParameters params;
	
	// Max number of threads to create
	protected int MAX_WORKER_THREADS = 2;


	/**
	 * General constructor.
	 */
	public BSimChemicalField (int newFieldType, int newBoundaryType, double newRate, 
			double[] newStartPos, double newWidth, double newHeight, int newXBoxes,
			int newYBoxes, double newDt, double newThreshold, Color newColour,
			BSimParameters p){

		// Set all internal variables
		
		params = p;
		MAX_WORKER_THREADS = params.getNumOfThreads();

		fieldType = newFieldType;
		boundaryType = newBoundaryType;
		rate = newRate;

		startPos = newStartPos;
		width = newWidth;
		height = newHeight;

		xBoxes = newXBoxes;
		yBoxes = newYBoxes;

		boxWidth  = width/xBoxes;
		boxHeight = height/yBoxes;

		dt = newDt;
		colour = newColour;
		
		threshold = newThreshold;

		// Create the field of the required size (this is fixed for the duration of the object)
		field = new double[xBoxes][yBoxes];
	}


	/**
	 * Setup the field as linear. Direction should use one of the constants
	 * defined in this class. All concentrations should be positive or will be rounded
	 * to zero. The gradient is linear between start and end concentrations.
	 */
	public void setAsLinear (int dir, double startCon, double endCon){
		int i, j;

		// Variable to hold the value of each box along the gradient
		double[] linVals;

		// Check that the concentration values are positive (if not set ot zero)
		if(startCon < 0){ startCon = 0;}
		if(endCon < 0){ endCon = 0;}


		// If an X directional field
		if(dir == LINEAR_X){

			// We will have x boxes worth of values
			linVals = new double[xBoxes];

			// Calculate the difference in concentration between boxes
			double conDelta = (endCon - startCon) / (double)xBoxes;

			// Find the concentration for each box
			for(i=0; i<xBoxes; i++){
				// Calculate the concentration for the current box
				linVals[i] = startCon + (i * conDelta);
			}

			// Update the field
			for(i=0; i<xBoxes; i++){
				for(j=0; j<yBoxes; j++){
					// Update the field with the calculated value
					field[i][j] = linVals[i];
				}
			}
		}
		// If a Y directional field
		else if(dir == LINEAR_Y){

			// We will have x boxes worth of values
			linVals = new double[yBoxes];

			// Calculate the difference in concentation between boxes
			double conDelta = (endCon - startCon) / (double)yBoxes;

			// Find the concentration for each box
			for(i=0; i<yBoxes; i++){
				// Calculate the concentration for the current box
				linVals[i] = startCon + (i * conDelta);
			}

			// Update the field
			for(i=0; i<xBoxes; i++){
				for(j=0; j<yBoxes; j++){
					// Update the field with the calculated value
					field[i][j] = linVals[j];
				}
			}
		}
		// An invalid direction must have been given
		else{
			System.err.println("Invalid direction for static linear field.");
		}
	}


	/**
	 * Set if the field should be displayed.
	 */
	public void setDisplayed (boolean newIsDisplayed){ 
		isDisplayed = newIsDisplayed; 
	}


	/**
	 * Set the diffusion scheme used.
	 */
	public void setDiffuseType (int newDiffuseType){
		diffuseType = newDiffuseType;
	}


	/**
	 * Draw the field to a given graphics context.
	 */
	public void redraw (Graphics g) {

		// Check to see if the field is displayed and redraw
		if(isDisplayed) {

			// Get the components of the colour
			// Required because alpha needs to be varied later on
			float rComp = (float)(colour.getRed()/255.0);
			float gComp = (float)(colour.getGreen()/255.0);
			float bComp = (float)(colour.getBlue()/255.0);
			float aComp  = 0;
			
			int fWidth, fHeight;

			// Loop through all x and y boxes
			for(int x=0; x<xBoxes; x++) {
				for(int y=0; y<yBoxes; y++) {

					fWidth = (int)boxWidth;
					fHeight = (int)boxHeight;
					
					// Check to see if the field is visible (i.e. not 0 concentration)
					if(field[x][y] != 0.0f){
						
						aComp = (float)(field[x][y] * maxCon);
						
						// Set the colour of the field (alpha is the concentration)
						g.setColor(new Color(rComp, gComp, bComp, aComp));

						// Draw a box of the field
						g.fillRect((int)(startPos[0] + (x * boxWidth)), 
								(int)(startPos[1] + (y * boxHeight)), 
								fWidth, fHeight);
					}
				}
			}
		}
	}


	/**
	 * Update the field for a single time step.
	 */
	public void updateField (){
		int i;
		int xStart, xEnd;

		// Update rules for diffuse fields
		if(fieldType == TYPE_DIFFUSE){

			// Array to hold the updated field
			double[][] newField = new double[xBoxes][yBoxes];

			// Ratios of each side (for weighting, used later but only calculated once)
			double xRat = (width  * 2) / (width + height);
			double yRat = (height * 2) / (width + height);
			
			// Create array of worker threads
			Thread[] workerThreads = new Thread[MAX_WORKER_THREADS];
			
			// Create each of the worker threads and set them runing.
			for(i=0; i<MAX_WORKER_THREADS; i++) {
				
				// Calculate the start and end indexes for the partition
				xStart = (int)(xBoxes / MAX_WORKER_THREADS) * i;
				if (i == MAX_WORKER_THREADS - 1) {
					xEnd = xBoxes;
				}
				else {
					xEnd = (xBoxes / MAX_WORKER_THREADS) * (i + 1);
				}
				
				// Create and start the actual threads with the required parameters
				workerThreads[i] = new BSimChemicalFieldThread(newField, xRat, yRat, xStart, xEnd);
				workerThreads[i].start();
			}
			
			// Wait for all threads to finish execution before continuing
			for(i=0; i<MAX_WORKER_THREADS; i++) {
				try{
					workerThreads[i].join();
				} catch (InterruptedException ignore) { }
			}

			// Now that the new field has been claculated swap for old one
			field = newField;
		}
	}
	
	
	/**
	 * Worker thread used to calculate the diffusion over a partition of the full
	 * space.
	 */
	protected class BSimChemicalFieldThread extends Thread {
		
		
		// The field to work on (this will be a reference because arrays are objects)
		// This means a single instance of the array will by all the threads.
		double[][] newField;
		
		// Ratios of the edges
		double xRat, yRat;
		
		// The thread number and total number of threads
		int xStart, xEnd;
		
		
		/**
		 * General constructor.
		 */
		public BSimChemicalFieldThread(double[][] newNewField, double newXRat,
		                               double newYRat, int newXStart, int newXEnd){
			
			// Update local values for the worker thread to work over
			newField = newNewField;
			xRat = newXRat;
			yRat = newYRat;
			xStart = newXStart;
			xEnd = newXEnd;
		}
		
		
		/**
		 * Function run when thread starts. Updates the progress monitor window.
		 */
		public void run(){
			
			// Variables used in calculating the amount of diffusion
			double curVal, curDelta, 
			valN, valE, valS, valW, 
			dN, dE, dS, dW;

			// Flags to handle boundaries
			boolean noN, noE, noS, noW;

			// Loop through all x and y boxes
			for(int x=xStart; x<xEnd; x++) {
				for(int y=0; y<yBoxes; y++) {

					// Reset the edge constraints
					noN = false;
					noE = false;
					noS = false;
					noW = false;

					// Reset other variables
					valN = 0.0;
					valE = 0.0;
					valS = 0.0;
					valW = 0.0;
					dN = 0.0;
					dE = 0.0;
					dS = 0.0;
					dW = 0.0;

					// Get the current value of the field at the given box
					curVal = field[x][y];

					// Calculate the diffusion coefficients for each edge
					// and update the boolean flags if edges are not present
					if(y == 0){ 
						noN = true;
						dN = diffuseCoeff(curVal, 0);
					}
					else{ 
						valN = field[x][y-1];
						dN = diffuseCoeff(curVal, valN); 
					}

					if(y == yBoxes-1){ 
						noS = true;
						dS = diffuseCoeff(curVal, 0);
					}
					else{
						valS = field[x][y+1];
						dS = diffuseCoeff(curVal, valS); 
					}

					if(x == 0){ 
						noW = true;
						dW = diffuseCoeff(curVal, 0);
					}
					else{ 
						valW = field[x-1][y];
						dW = diffuseCoeff(curVal, valW); 
					}

					if(x == xBoxes-1){ 
						noE = true;
						dE = diffuseCoeff(curVal, 0);
					}
					else{ 
						valE = field[x+1][y];
						dE = diffuseCoeff(curVal, valE); 
					}

					// Reset the current delta
					curDelta = 0.0;

					// Weight the edge deltas based on their length	
					// Required because fields may not be uniform in structure
					dN = dN * xRat;
					dS = dS * xRat;
					dE = dE * yRat;
					dW = dW * yRat;

					// Add up the edge contributions
					if(boundaryType == BOUNDARY_LEAK){
						curDelta = dN * (curVal - valN) + 
						dE * (curVal - valE) +
						dS * (curVal - valS) +
						dW * (curVal - valW);
					}
					else{
						if(!noN){
							curDelta += dN * (curVal - valN);
						}
						if(!noE){
							curDelta += dE * (curVal - valE);
						}
						if(!noS){
							curDelta += dS * (curVal - valS);
						}
						if(!noW){
							curDelta += dW * (curVal - valW);
						}
					}

					// Update the new field
					newField[x][y] = curVal - (dt * curDelta);
				}
			}
		}
		
		// Calculate diffusion coefficients
		private double diffuseCoeff(double val1, double val2) {

			// Variable to hold the calculated diffusion coefficient
			double diffCoeff = 0.0;

			// Check to see if the values are 0 (minimise computation)
			if(val1 == 0.0 && val2 == 0.0){
				return diffCoeff;
			}

			// The different diffusion methods (DIFFUSE_X2 is the default)
			if (diffuseType == DIFFUSE_X2) {
				diffCoeff = (rate * rate) /
				((rate * rate) + Math.pow(Math.abs(val1 - val2),2));
			}
			else if (diffuseType == DIFFUSE_EXP) {
				diffCoeff = Math.exp(-1 * Math.pow((Math.abs(val1 - val2) / rate),2));
			}
			else {
				diffCoeff = 0.0;
			}

			// Return the diffusion coefficient
			return diffCoeff;
		}
	}


	/**
	 * Add an amount of chemical to a given point in simulation space.
	 * The amount is the increased concentration, this can be greater than 1
	 * if your field boxes are greater than a unit square.
	 */
	public void addChemical (double amount, double[] position){

		// Variable to hold the found concentration
		double con, newCon;

		// Check to see if the position falls in the field
		if(position[0]<startPos[0] || position[1]<startPos[1] || 
				position[0]>(startPos[0] + width) || position[1]>(startPos[1] + height)) {
			// Outside the bound of the field so do nothing
		}
		else{

			// Find the square that the position falls in and get the concentration
			int xNum = (int)((position[0] - startPos[0])/boxWidth);
			int yNum = (int)((position[1] - startPos[1])/boxHeight);
			con = field[xNum][yNum];

			// Weight the new concentration by the area of the box
			newCon = con + (amount/(width/height));

			// Ensure that concentration does not exceed 1
			if(newCon > 1.0) {
				newCon = 1.0;
			}

			// Update the field
			field[xNum][yNum] = newCon;
		}
	}


	/**
	 * Get the concentration at a given point. The co-ordinates are of the simulation
	 * space and therefore if they fall outside the range of the field zero will be
	 * returned.
	 */
	public double getConcentration (double[] position) {

		// Variable to hold the found concentration
		double con;

		// Check to see if the position falls in the field
		if(position[0]<startPos[0] || position[1]<startPos[1] || 
				position[0]>(startPos[0] + width) || position[1]>(startPos[1] + height)) {

			// Outside the bound of the field so return 0
			con = 0.0;
		}
		else{

			// Find the square that the position falls in and return concentration
			int xNum = (int)((position[0] - startPos[0])/boxWidth);
			int yNum = (int)((position[1] - startPos[1])/boxHeight);
			con = field[xNum][yNum];
		}

		// Return the concentration
		return con;
	}


	/**
	 * Standard get methods for the class.
	 */
	public int getFieldType (){ return fieldType; }
	public int getBoundaryType (){ return boundaryType; }
	public double getRate (){ return rate; }
	public double[][] getField (){ return field; }
	public double getThreshold() {return threshold;}
}
