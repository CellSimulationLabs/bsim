/**
 * BSimChemicalFieldCreate.java
 *
 * Class to hold static methods to generate chemical fields of given properties.
 *
 * Authors: Thomas Gorochowski
 * Created: 25/07/2008
 * Updated: 25/08/2008
 */


//Define the location of the class in the bsim package
package bsim.object;

//Import the bsim packages used
import bsim.*;
import bsim.physics.*;

//Standard packages required by the application
import java.util.*;
import java.awt.*;


public class BSimChemicalFieldCreate {
	
	public static BSimChemicalField createChemicalField (double[] define, double[] setup, 
	                                                     Color col, BSimParameters params) {
		
		// Create an array for the start position
		double[] startPos = new double[2];
		startPos[0] = define[0];
		startPos[1] = define[1];
		
		// Create the new chemical field
		BSimChemicalField newField = new BSimChemicalField((int)define[7], 
											(int)define[8], define[9], startPos, define[2], 
											define[3], (int)define[4], (int)define[5], 
											params.getDtSecs(), define[5], col, params);
		
		// Update if the field has an initial distribution
		if(setup[1] == 1) { newField.setAsLinear(BSimChemicalField.LINEAR_X, setup[2], setup[3]); }
		if(setup[1] == 2) { newField.setAsLinear(BSimChemicalField.LINEAR_Y, setup[2], setup[3]); }
		
		// Update the field visibility
		if(setup[0] == 1) { newField.setDisplayed(true); }
		else { newField.setDisplayed(false); }
		
		// Return the fully setup chemical field
		return newField;
	}
}
