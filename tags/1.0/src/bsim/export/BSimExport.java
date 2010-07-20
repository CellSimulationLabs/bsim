/**
 * BSimExport.java
 *
 * Interface for use by export plug-ins.
 *
 * Authors: Thomas Gorochowski
 * Created: 17/08/2008
 * Updated: 17/08/2008
 */


// Define the location of the class in the bsim package
package bsim.export;

// Standard packages required by the application
import bsim.*;


public interface BSimExport{
	
	public abstract void exportFrame(BSimScene scene, BSimParameters params);
	
	public abstract void finishExport(BSimScene scene, BSimParameters params);
	
}