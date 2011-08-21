package BSimParser;

import java.io.*;
import java.util.*;

public class BSimParser {
	
	public static void main(String[] args) {
		
		// Open the file from args
        File paramFile = new File(args[0]);        
        
        // Parse the file to generate a simulation
        BSimFromFile sim = BSimParser.parseFile(paramFile);
	
        // Run the simulation
        sim.run();
	}

	/**
     * Parses a parameter file and returns a new simulation.
     */
    public static BSimFromFile parseFile(File paramFile){

    	// Parameters object to hold the parsed file contents
    	BSimFromFile sim = new BSimFromFile();
    	Scanner scanner;
    	int lineCounter = 1;
    	
    	// Attempt to scan each line and process it
    	try { scanner = new Scanner(paramFile);
    	try {
    		while(scanner.hasNextLine()) {
    			processLine(scanner.nextLine().split(":"), sim, lineCounter);
    			lineCounter++;
    		}
    	} finally { scanner.close(); }
    	} catch(FileNotFoundException e) {System.err.println("Input file not found"); }

    	// Return the output parameters object
    	return sim;
    }
    
    /**
     * Use data following line header to set parameters in BSimFromFile object
     */
    private static void processLine(String[] line, BSimFromFile sim, int lineNo) {

    	// For each type of parameter run the associated loader
    	
    	// Time Step
    	if (line[0].equals("DT")) 
    		sim.getSim().setDt(parseToDouble(line[1]));

    	// Create a new chemical field, options of the form CHEM_FIELD:name:param1=value1,param2=value2...
    	else if(line[0].equals("CHEM_FIELD"))
    		sim.addChemicalField(line[1], BSimChemicalFieldFactory.parse(line[2]));
    	
    	// Add all the other possible inputs here
    	
    	// Generic comments of lines that are not use
    	else if(line[0].equals("***")) { } // Do nothing - comment
    	else System.err.println("Line " + lineNo + " not Read in Parameter File");
    }
    
    /**
     * Convert String into a double handling possible errors (returning 0.0 if problem found)
     */
    private static double parseToDouble(String str) {
    	double result;
    	try{ result = Double.parseDouble(str); }
    	catch(Exception e){ 
    		System.err.println("Problem converting to double");
    		result = 0.0;
    	}
    	return result;
    }
}
