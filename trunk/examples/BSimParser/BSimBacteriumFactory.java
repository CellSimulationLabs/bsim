package BSimParser;

import java.awt.Color;
import java.util.HashMap;
import java.util.Vector;

import javax.vecmath.Vector3d;

import bsim.BSim;

/**
 * Factory to generate set of bacteria having been given a
 * set of parameters from the file.
 */
class BSimBacteriumFactory {
	
	public static Vector<BSimFromFileBacterium> parse (String paramString, BSim sim) {
		// get the attribute-value pairs for the bacteria
		HashMap<String, String> params = BSimParser.parseAttributeValuePairs(paramString);
		
		// set default parameters
		int 		populationSize = 100;
		Vector3d 	bndStartVec = new Vector3d(0, 0, 0);
		Vector3d 	bndEndVec = new Vector3d(1,1,1);
		
		String 		chemotaxisGoalField = "";
		String 		inputChemicalField = "";
		String 		outputChemicalField = "";
				
		double 		chemicalInRate = 1;
		double 		chemicalOutRate = 1;
		
		Color 		bacColor = new Color(34, 187, 34);

		
		// read in the desired population size
		if (params.containsKey("Population")) {
			populationSize = BSimParser.parseToInt(params.get("Population"));
		}

		// Positions of the form BoundStart=0.1;2.4;5.1
		BSimParser.assignParamToVector3d(params, "BoundStart", bndStartVec);
		
		// Positions of the form BoundEnd=0.1;2.4;5.1
		BSimParser.assignParamToVector3d(params, "BoundEnd", bndEndVec);
		
		// Field name for chemotaxis
		if (params.containsKey("ChemotaxisField")) {
			chemotaxisGoalField = params.get("ChemotaxisField");
		}
		
		// ChemicalField name for input from 
		if (params.containsKey("InputChemicalField")) {
			// Chemical input rate
			if (params.containsKey("InputChemicalRate")) {
				chemicalInRate = BSimParser.parseToDouble(params.get("InputChemicalRate"));
			}
			
			inputChemicalField = params.get("InputChemicalField");
		}

		// ChemicalField name for output to
		if (params.containsKey("OutputChemicalField")) {
			// Chemical output rate
			if (params.containsKey("OutputChemicalRate")) {
				chemicalOutRate = BSimParser.parseToDouble(params.get("OutputChemicalRate"));
			}

			outputChemicalField = params.get("OutputChemicalField");
		}
		
		// update the colour of the bacteria population
		Color tempColor = BSimParser.getColorFromParam(params, "Color");
		if (tempColor != null) { bacColor = tempColor; }

		// Generate all of the bacteria
		Vector<BSimFromFileBacterium> bacteria = new Vector<BSimFromFileBacterium>(populationSize);
		for (int i = 0; i < populationSize; i++) {
			
			BSimFromFileBacterium b = new BSimFromFileBacterium(sim, BSimParser.randomVector3d(bndStartVec, bndEndVec), bacColor);
			
			// assign the names of chemical fields with which the bacteria interact
			if (!chemotaxisGoalField.equals("")) b.setChemotaxisGoalFieldName(chemotaxisGoalField);
			if (!inputChemicalField.equals("")) b.setChemicalInputName(inputChemicalField);
			if (!outputChemicalField.equals("")) b.setChemicalOutputName(outputChemicalField);
			
			// set the chemical transfer rates between the bacterium and its environment
			b.setInputRate(chemicalInRate);
			b.setOutputRate(chemicalOutRate);			
			
			bacteria.add(b);
		}
		
		return bacteria;
	}
}
