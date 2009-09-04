/**
 * BSimExport.java
 *
 * Interface for use by export plug-ins.
 *
 * Authors: Thomas Gorochowski
 * Created: 17/08/2008
 * Updated: 17/08/2008
 */
package bsim.export;

import bsim.scene.BSimScene;


public interface BSimExport{
	
	public abstract void exportFrame(BSimScene scene);
	
	public abstract void finishExport(BSimScene scene);
	
}