package bsim.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import bsim.BSim;

/**
 * Text file exporter.
 * Can be used to export simulation data to a text file. The during() method
 * must be overwritten to write the required data to file.
 */
public abstract class BSimLogger extends BSimExporter {
	
	/** Object to write output to. */
	protected BufferedWriter bufferedWriter;
	/** Filename of output. */
	protected String filename;
	
	/**
	 * Constructor for a file logger. Will output data to a specified file.
	 * @param sim Associated simulation.
	 * @param filename Output filename.
	 */
	public BSimLogger(BSim sim, String filename) {
		super(sim);
		this.filename = filename;
	}
	
	/**
	 * Called before a simulation starts. Can be extended by a user if necessary.
	 */
	@Override
	public void before() {
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(new File(filename)));
		}
		catch(IOException e){ 
			e.printStackTrace();
		} 
	}
	
	/**
	 * Writes text to the output file.
	 * @param text Text to write to file.
	 */
	public void write(String text) {		
		try {			
			bufferedWriter.write(text);
			bufferedWriter.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	
	
	/**
	 * Called after a simulation ends. Can be extended by a user if necessary.
	 */
	@Override
	public void after(){
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
