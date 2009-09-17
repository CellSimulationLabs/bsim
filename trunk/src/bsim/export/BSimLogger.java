package bsim.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import bsim.BSim;

public abstract class BSimLogger extends BSimExporter {
	
	protected BufferedWriter bufferedWriter;
	protected String filename;
	
	public BSimLogger(BSim sim, String filename) {
		super(sim);
		this.filename = filename;
	}
	
	@Override
	public void before() {
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(new File(filename)));
		}
		catch(IOException e){ 
			e.printStackTrace();
		} 
	}
	
	public void write(String text) {		
		try {			
			bufferedWriter.write(text);
			bufferedWriter.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	
	
	@Override
	public void after(){
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
