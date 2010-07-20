/**
 * BSimBoundaryCreate.java
 *
 * Class to hold static methods to generate the different visual aid types.
 *
 * Authors: Thomas Gorochowski
 * Created: 24/08/2008
 * Updated: 24/08/2008
 */


//Define the location of the class in the bsim package
package bsim.object;

//Import the bsim packages used
import bsim.*;

//Standard packages required by the application
import java.util.*;


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
	
	public static BSimVisualAid createParticleTrace(BSimScene scene, double[] args ){
		
		// Return the new object
		return new BSimParticleTrace(scene, (int)args[0]);
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
