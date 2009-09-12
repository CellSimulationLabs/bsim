package bsim.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class BSimLogger extends BSimExporter {
	
	public void finishExport() {
		close();
	}
	
	private BufferedWriter bufferedWriter;
	
	public BSimLogger(String filename) {				
		try{
			bufferedWriter = new BufferedWriter(new FileWriter(new File(filename)));
		}
		catch(IOException e){ 
			e.printStackTrace();
		} 
	}
	
	public void write(String text) {	
		try {
			System.out.println(text);
			bufferedWriter.write(text);
			bufferedWriter.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	
	
	public void close(){
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
