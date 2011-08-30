package BSimParser;

import java.awt.Color;
import java.util.HashMap;

import bsim.BSim;

/**
 * Factory to generate a chemical field having been given a
 * set of parameters from the file.
 */
class BSimChemicalFieldFactory {
	
	public static BSimFromFileChemicalField parse (String paramString, BSim sim) {
		// get the attribute-value pairs for the chemical field
		HashMap<String, String> params = BSimParser.parseAttributeValuePairs(paramString);

		// set the default chemical field values for discretisation, diffusivity, and chemical decay rate
		int[] 		numBoxes = {10,10,10};
		double 		diffusivity = 0;
		double 		decayRate = 0;
		
		Color 		chemFieldCol = new Color(128, 128, 255);
		double 		alphaPerUnit = 1.0;
		double		alphaMax = 200.0;
		
		
		// set the chemical field discretisation (number of boxes)
		// Boxes=nBoxesInXDirection;nBoxesInYDirection;nBoxesInZDirection
		if (params.containsKey("Boxes")) {
			// Split the positions on ';' character
			String[] paramArray = params.get("Boxes").split(";");
			
			int dimension = numBoxes.length;
			
			if (paramArray.length != dimension) {
				System.err.println("Problem extracting " + "Boxes" + " for chemical field");
			}
			else {
				for (int i = 0; i < dimension; i++) {
					numBoxes[i] = BSimParser.parseToInt(paramArray[i]);
				}
			}
		}
		
		// Update the diffusivity
		if (params.containsKey("Diffusivity")) {
			diffusivity = BSimParser.parseToDouble(params.get("Diffusivity"));
		}

		// Update the decay rate
		if (params.containsKey("DecayRate")) {
			decayRate = BSimParser.parseToDouble(params.get("DecayRate"));
		}
		
		// Update the alpha per unit
		if (params.containsKey("AlphaPerUnit")) {
			alphaPerUnit = BSimParser.parseToDouble(params.get("AlphaPerUnit"));
		}

		// Update the max alpha
		if (params.containsKey("AlphaMax")) {
			alphaMax = BSimParser.parseToDouble(params.get("AlphaMax"));
		}
				
		Color tempCol = BSimParser.getColorFromParam(params, "Color");
		if (tempCol != null) { chemFieldCol = tempCol; }
		
		// generate the chemical field
		BSimFromFileChemicalField theField = new BSimFromFileChemicalField(sim, numBoxes, diffusivity, decayRate, chemFieldCol, alphaPerUnit, alphaMax);
				
		// set up chemical field gradient if one is defined
		if (params.containsKey("GradientDirection")) {
			if (params.containsKey("GradientLimits")) {
				// Split the minimum and maximum values on ';'
				String[] gradientMinMax = params.get("GradientLimits").split(";");
				
				if (gradientMinMax.length != 2) {
					System.err.println("Problem extracting the chemical field gradient limits");
				}
				else {
					String gradientDirection = params.get("GradientDirection");
					
					// Setup the gradient direction 
					int axis = -1;
					if (gradientDirection.equals("x") || gradientDirection.equals("X")) axis = 0;
					else if (gradientDirection.equals("y") || gradientDirection.equals("Y")) axis = 1;
					else if (gradientDirection.equals("z") || gradientDirection.equals("Z")) axis = 2;
					else System.err.println("Invalid chemical gradient direction");
					
					theField.linearGradient(axis, BSimParser.parseToDouble(gradientMinMax[0]),  BSimParser.parseToDouble(gradientMinMax[1]));
				}
			} else {
				System.err.println("Need to specify 'GradientDirection' *and* 'Gradient'");
			}
		}

		return theField;
	}	
}