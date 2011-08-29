package BSimParser;

import java.util.HashMap;
import java.util.Vector;

import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.particle.BSimBacterium;

/**
 * Factory to generate set of bacteria having been given a
 * set of parameters from the file.
 */
class BSimBacteriumFactory {
	
	public static Vector<BSimBacterium> parse (String paramString, BSim sim) {
		// get the attribute-value pairs for the bacteria
		HashMap<String, String> params = BSimParser.parseAttributeValuePairs(paramString);
		
		// set default parameters
		int populationSize = 100;
		Vector3d boundaryStartVec = new Vector3d(0, 0, 0);
		Vector3d boundaryEndVec = new Vector3d(1,1,1);
		
		String chemotaxisGoalField = "";
		String inputChemicalField = "";
		String outputChemicalField = "";
		
		double chemicalInRate = 1;
		double chemicalOutRate = 1;
		
		// read in the desired population size
		if (params.containsKey("PopulationSize")) {
			populationSize = BSimParser.parseToInt(params.get("PopulationSize"));
		}

		// read in the boundaries of the creation region
		// Positions of the form BoundStart=0.1;2.4;5.1
		if (params.containsKey("BoundStart")) {
			// Split the positions on ';' character
			String[] boundaryStart = params.get("BoundStart").split(";");
			if (boundaryStart.length != 3) {
				System.err.println("Problem extracting the BoundStart for a bacterial population");
			}
			else {
				boundaryStartVec.set(BSimParser.parseToDouble(boundaryStart[0]),
								BSimParser.parseToDouble(boundaryStart[1]),
								BSimParser.parseToDouble(boundaryStart[2]));
			}
		}
		
		// Positions of the form BoundEnd=0.1;2.4;5.1
		if (params.containsKey("BoundEnd")) {
			// Split the positions on ';' character
			String[] boundaryEnd = params.get("BoundEnd").split(";");
			if (boundaryEnd.length != 3) {
				System.err.println("Problem extracting the BoundEnd for a bacterial population");
			}
			else {
				boundaryEndVec.set(BSimParser.parseToDouble(boundaryEnd[0]),
						      BSimParser.parseToDouble(boundaryEnd[1]),
						      BSimParser.parseToDouble(boundaryEnd[2]));
			}
		}
		
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

		// Generate all of the bacteria
		Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>(populationSize);
		for (int i = 0; i < bacteria.size(); i++) {
			
			BSimFromFileBacterium b = new BSimFromFileBacterium(sim, BSimParser.randomVector3d(boundaryStartVec, boundaryEndVec));
			
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
