package BSimParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.awt.Color;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class BSimParser {
	
	private static Random rng = new Random();
	
	public static void main (String[] args) {
		
		File paramFile = null;
		
		try {
			// Open the file from args
			paramFile = new File(args[0]);
			
		} catch(ArrayIndexOutOfBoundsException e){
			System.err.println("No arguments specified?\n" +
					"Usage: 'java BSimParser.BSimParser parameterFileToLoad'\n");
			System.out.println("Please specify a parameter file to load:");
			
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				paramFile = new File(br.readLine()); 
				br.close();
				
			} catch(IOException excIO){
				System.err.println("I/O error in parameter file loader");
				System.exit(1);
			}
		}
		
        // Parse the file to generate a simulation
        BSimFromFile sim = BSimParser.parseFile(paramFile);
	
        // Run the simulation
        sim.run();
	}

	/**
     * Parses a parameter file and returns a new simulation.
     */
    public static BSimFromFile parseFile (File paramFile){

    	// Parameters object to hold the parsed file contents
    	BSimFromFile sim = new BSimFromFile();
    	Scanner scanner;
    	int lineCounter = 1;
    	
    	// Attempt to scan each line and process it
    	try { 
    		scanner = new Scanner(paramFile);
	    	try {
	    		while(scanner.hasNextLine()) {
	    			processLine(scanner.nextLine().split(">"), sim, lineCounter);
	    			lineCounter++;
	    		}
	    	} finally { 
	    		scanner.close(); 
	    	}
    	} catch(FileNotFoundException e) {
    		System.err.println("Input file not found");
    		System.exit(1);
    	}

    	// Now that valid objects have been created, try to assign chemical fields to bacteria
    	sim.assignBacteriaChemicalFieldsFromNames();
    	
    	// Return the output parameters object
    	return sim;
    }
    
    /**
     * Use data following line header to set parameters in BSimFromFile object
     */
    private static void processLine (String[] line, BSimFromFile sim, int lineNo) {
    	
    	// --------------------------------------------------------------
    	// Simulation Parameters (inc. meshes, chemical fields, etc)
    	// --------------------------------------------------------------
    	
    	// Time Step
    	if (line[0].equals("DT")){
    		sim.getSim().setDt(parseToDouble(line[1]));
    	}
    	
    	// Simulation bounds
    	else if (line[0].equals("BOUNDS")) {
    		HashMap<String,String> boundParams = parseAttributeValuePairs(line[1]);

    		// Extract the bounds from the parameters and update simulation
    		Vector3d bounds = new Vector3d(100.0,100.0,100.0);
    		BSimParser.assignParamToVector3d(boundParams, "Size", bounds);
    		sim.getSim().setBound(bounds.x, bounds.y, bounds.z);
    		
    		// Extract if the boundaries are solid or wrapping (by default all solid) and update simulation
    		Vector3d solid = new Vector3d(1.0,1.0,1.0);
    		BSimParser.assignParamToVector3d(boundParams, "Solid", solid);
    		sim.getSim().setSolid((solid.x == 1.0) ? true : false,
    								(solid.y == 1.0) ? true : false,
    								(solid.z == 1.0) ? true : false);
    	}
    	
    	// Length of the simulation (ignored if preview selected)
    	else if (line[0].equals("LENGTH")) {
    		sim.getSim().setSimulationTime(parseToDouble(line[1]));
    	}
    	
    	// Create a new chemical field, options of the form CHEM_FIELD:name:param1=value1,param2=value2...
    	// See BSimChemicalFieldFactory for more details.
    	else if(line[0].equals("MESH"))
    		sim.setMesh(line[1]);
    	
    	// Create a new bacterial population, options of the form BACTERIA:name:param1=value1,param2=value2...
    	// See BSimChemicalFieldFactory for more details.
    	else if(line[0].equals("BACTERIA"))
    		sim.addBacteria(line[1], BSimBacteriumFactory.parse(line[2], sim.getSim()));
    	
    	// Create a new particle population, options of the form PARTICLES:name:param1=value1,param2=value2...
    	// See BSimChemicalFieldFactory for more details.
    	else if(line[0].equals("PARTICLES"))
    		sim.addParticles(line[1], BSimParticleFactory.parse(line[2], sim.getSim()));
    	
    	// Create a new chemical field, options of the form CHEM_FIELD:name:param1=value1,param2=value2...
    	// See BSimChemicalFieldFactory for more details.
    	else if(line[0].equals("CHEM_FIELD"))
    		sim.addChemicalField(line[1], BSimChemicalFieldFactory.parse(line[2], sim.getSim()));
    	
    	// --------------------------------------------------------------
    	// Output Related Parameters
    	// --------------------------------------------------------------
    	
    	else if (line[0].equals("PREVIEW")) {
    		if (parseToInt(line[1]) == 1) { sim.setPreviewMode(true); }
    		else { sim.setPreviewMode(false); }
    	}
    	
    	else if (line[0].equals("OUTPUT_PATH")) 
    		sim.setOutputPath(line[1]);
    	
    	else if (line[0].equals("OUTPUT_DATA")) {
    		if (parseToInt(line[1]) == 1) { sim.setOutputData(true); }
    		else { sim.setOutputData(false); }
    	}
    	
    	else if (line[0].equals("OUTPUT_MOVIE")) {
    		if (parseToInt(line[1]) == 1) { sim.setOutputMovie(true); }
    		else { sim.setOutputMovie(false); }
    	}
    	
    	else if (line[0].equals("DATA_FILENAME")) 
    		sim.setDataFileName(line[1]);
    	
    	else if (line[0].equals("MOVIE_FILENAME")) 
    		sim.setMovieFileName(line[1]);
    	
    	else if (line[0].equals("MOVIE_SPEED")) 
    		sim.setMovieSpeed(parseToInt(line[1]));
    	
    	else if (line[0].equals("MOVIE_DT")) 
    		sim.setMovieDt(parseToDouble(line[1]));
    	
    	else if (line[0].equals("MOVIE_WIDTH")) 
    		sim.setMovieWidth(parseToInt(line[1]));
    	
    	else if (line[0].equals("MOVIE_HEIGHT")) 
    		sim.setMovieHeight(parseToInt(line[1]));
    	
    	else if (line[0].equals("MOVIE_CAMERA")) {
    		Vector3f pos = new Vector3f(0.0f,0.0f,0.0f);
    		assignStringToVector3f(line[1],pos);
    		sim.setMovieCameraPosition(pos);
    	}
    	
    	// --------------------------------------------------------------
    	// Unknown Parameters
    	// --------------------------------------------------------------
    	
    	// Generic comments of lines that are not use
    	else if(line[0].equals("***")) { } // Do nothing - comment
    	else System.err.println("Line " + lineNo + " not Read in Parameter File");
    }
    
    /**
     * Convert String into a double handling possible errors (returning 0.0 if problem found)
     */
    public static double parseToDouble (String str) {
    	double result;
    	try{ result = Double.parseDouble(str); }
    	catch(Exception e){ 
    		System.err.println("Problem converting to double");
    		result = 0.0;
    	}
    	return result;
    }
    
    /**
     * Convert String into an int handling possible errors (returning 0 if problem found)
     */
    public static int parseToInt (String str) {
    	int result;
    	try{ result = Integer.parseInt(str); }
    	catch(Exception e){ 
    		System.err.println("Problem converting to int");
    		result = 0;
    	}
    	return result;
    }
    
    /** Create a map of attribute names to their values (held as the original Strings) */
    public static HashMap<String,String> parseAttributeValuePairs (String str) {
    	HashMap<String,String> pairList = new HashMap<String,String>();
    	
    	// Split the string on ',' character to get pairs and the split 
    	// again on '=' to get an individual pair
    	
    	String [] strPairList = str.split(",");
    	for(String strPair : strPairList ){		
    		String [] strAttVal = strPair.split("=");
    		if (strAttVal.length != 2) {
    			System.err.println("Encountered invalid attribute value pair");
    			break;
    		}
    		else {
    			// Create the pair in the map
    			pairList.put(strAttVal[0], strAttVal[1]);
    		}
    	}
    	return pairList;
    }
    
    /** Assign integer variable from string parameters */
    public static void assignParamToInt(HashMap<String, String> params, String paramName, int variable) {
		if (params.containsKey(paramName)) {
			variable = BSimParser.parseToInt(params.get(paramName));
		}
    }
    
    /** Assign double variable from string parameters */
    public static void assignParamToDouble(HashMap<String, String> params, String paramName, double variable) {
		if (params.containsKey(paramName)) {
			variable = BSimParser.parseToDouble(params.get(paramName));
		}
    }
    
    /** Assign Vector3d variable from string parameters */
    public static void assignParamToVector3d(HashMap<String, String> params, String paramName, Vector3d variable) {
		if (params.containsKey(paramName)) {
			// Split the positions on ';' character
			String[] vectorTriplet = params.get(paramName).split(";");
			if (vectorTriplet.length != 3) {
				System.err.println("Problem extracting the " + paramName + ", Line: ");
			}
			else {
				variable.set(BSimParser.parseToDouble(vectorTriplet[0]),
								BSimParser.parseToDouble(vectorTriplet[1]),
								BSimParser.parseToDouble(vectorTriplet[2]));
			}
		}
    } 
    
    /** Assign Vector3f variable from string */
    public static void assignStringToVector3f(String str, Vector3f variable) {
    	// Split the positions on ';' character
    	String[] vectorTriplet = str.split(";");
    	if (vectorTriplet.length != 3) {
    		System.err.println("Problem extracting the Vector3f from string, Line: ");
    	}
    	else {
    		variable.set((float)BSimParser.parseToDouble(vectorTriplet[0]),
    				(float)BSimParser.parseToDouble(vectorTriplet[1]),
    				(float)BSimParser.parseToDouble(vectorTriplet[2]));
    	}
    } 
    
    /** Return the Color for a particular parameter (necessary because Colors are immutable - i.e. no set methods) */
    public static Color getColorFromParam(HashMap<String, String> params, String paramName) {
		Color newCol = null;
    	if (params.containsKey(paramName)) {
			// Split the positions on ';' character
			String[] colorQuad = params.get(paramName).split(";");
			if (colorQuad.length != 4) {
				System.err.println("Problem extracting the " + paramName + ", Line: ");
			}
			else {
				newCol = new Color(BSimParser.parseToInt(colorQuad[0]),
									BSimParser.parseToInt(colorQuad[1]),
									BSimParser.parseToInt(colorQuad[2]),
									BSimParser.parseToInt(colorQuad[3]));
			}
		}
    	return newCol;
    } 
    
    /** Generate a random position vector within specified bounds */
    public static Vector3d randomVector3d(Vector3d boundStart, Vector3d boundEnd) { 
    	double newX = 0.0;
    	double newY = 0.0;
    	double newZ = 0.0;

    	// Generate new random positions in the range
    	newX = boundStart.x + ((boundEnd.x-boundStart.x) * rng.nextDouble());
    	newY = boundStart.y + ((boundEnd.y-boundStart.y) * rng.nextDouble());
    	newZ = boundStart.z + ((boundEnd.z-boundStart.z) * rng.nextDouble());

    	return new Vector3d(newX, newY, newZ);
    }
}
