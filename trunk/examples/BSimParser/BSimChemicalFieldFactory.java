package BSimParser;

import java.util.HashMap;

import bsim.BSimChemicalField;
import bsim.BSim;

/**
 * Factory to generate a chemical field having been given a
 * set of parameters from the file.
 */
class BSimChemicalFieldFactory {
	
	public static BSimChemicalField parse (String paramString, BSim sim) {
		// get the attribute-value pairs for the chemical field
		HashMap<String, String> params = BSimParser.parseAttributeValuePairs(paramString);

		// set the default chemical field values for discretisation, diffusivity, and chemical decay rate
		int[] numBoxes = {10,10,10};
		double diffusivity = 0;
		double decayRate = 0;
		
		
		// set the chemical field discretisation (number of boxes)
		// Boxes=nBoxesInXDirection;nBoxesInYDirection;nBoxesInZDirection
		if (params.containsKey("Boxes")) {
			// Split the positions on ';' character
			String[] boxes = params.get("Boxes").split(";");
			if (boxes.length != 3) {
				System.err.println("Problem extracting the box dimensions for chemical field");
			}
			else {
				for (int i = 0; i < 3; i++) {
					numBoxes[i] = BSimParser.parseToInt(boxes[i]);
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
				
		// generate the chemical field
		BSimChemicalField theField = new BSimChemicalField(sim, numBoxes, diffusivity, decayRate);
		
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