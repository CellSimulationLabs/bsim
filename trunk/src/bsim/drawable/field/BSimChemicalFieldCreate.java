/**
 * BSimChemicalFieldCreate.java
 *
 * Class to hold static methods to generate chemical fields of given properties.
 *
 * Authors: Thomas Gorochowski
 * 			Mattia Fazzini
 * Created: 25/07/2008
 * Updated: 07/08/2009
 */
package bsim.drawable.field;

import java.awt.Color;

import bsim.BSimParameters;


public class BSimChemicalFieldCreate {
	
	public static BSimChemicalField createChemicalField (double[] define, double[] setup, 
	                                                     Color col, BSimParameters params) {
		
		// Create an array for the start position
		double[] startPos = new double[3];
		startPos[0] = define[0];
		startPos[1] = define[1];
		startPos[1] = define[2];
		
		// Create the new chemical field
		BSimChemicalField newField = new BSimChemicalField((int)define[10], 
											(int)define[11], define[12], startPos, define[3], 
											define[4],	define[5], (int)define[6], (int)define[7], (int)define[8],
											params.getDtSecs(), define[9], col, params);
		
		// Update if the field has an initial distribution
		if(setup[1] == 1) { newField.setAsLinear(BSimChemicalField.LINEAR_X, setup[2], setup[3]); }
		if(setup[1] == 2) { newField.setAsLinear(BSimChemicalField.LINEAR_Y, setup[2], setup[3]); }
		if(setup[1] == 3) { newField.setAsLinear(BSimChemicalField.LINEAR_Z, setup[2], setup[3]); }
		
		// Update the field visibility
		if(setup[0] == 1) { newField.setDisplayed(true); }
		else { newField.setDisplayed(false); }
		
		// Return the fully setup chemical field
		return newField;
	}
}
