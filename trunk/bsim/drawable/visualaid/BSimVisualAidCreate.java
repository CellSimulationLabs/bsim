/**
 * BSimBoundaryCreate.java
 *
 * Class to hold static methods to generate the different visual aid types.
 *
 * Authors: Thomas Gorochowski
 * Created: 24/08/2008
 * Updated: 24/08/2008
 */
package bsim.drawable.visualaid;

import bsim.BSimScene;


public class BSimVisualAidCreate {
	
	
	/**
	 * Methods to create each of the different visual aid types
	 */
	
	public static BSimVisualAid createBacteriaTrace(BSimScene scene, double[] args ){
		
		// Return the new object
		return new BSimBacteriaTrace(scene, (int)args[0]);
	}
	
	public static BSimVisualAid createAvgBacteriaTrace(BSimScene scene, double[] args ){
		
		// Return the new object
		return new BSimAverageBacteriaTrace(scene, (int)args[0]);
	}
	
	public static BSimVisualAid createBeadTrace(BSimScene scene, double[] args ){
		
		// Return the new object
		return new BSimBeadTrace(scene, (int)args[0]);
	}
	
	public static BSimVisualAid createSceneClock(BSimScene scene){
	
		// Return the new object
		return new BSimSceneClock(scene);
	}
	
	public static BSimVisualAid createSceneScale(BSimScene scene, double[] args){
	
		// Return the new object
		return new BSimSceneScale(scene, args[0], args[1], args[2]);
	}
	
	
}
