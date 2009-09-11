/**
 * BSimScene.java
 *
 * Class that represents the scene of a simulation. It extends a JPanel which allows
 * for it to be easily embedded into any swing based GUI. Additionally, this class 
 * maintains the vectors of all bacteria and beads, as well as the physcis engine 
 * used to update the position and properties of all objects in the simulation. The class
 * is threaded to ensure that the main GUI event thread continues to function and
 * control of animaton is via a semaphre implemented as a notifiable object to ensure that
 * minimal processing is carried out when paused.
 *
 * Authors: Thomas Gorochowski
 *          Ian Miles
 *          Charlie Harrison
 *          Mattia Fazzini
 * Created: 12/07/2008
 * Updated: 26/08/2009
 */
package bsim.scene;

import java.util.Vector;

import javax.vecmath.Vector3d;

import bsim.app.BSimApp;
import bsim.app.gui.BSimGUI;
import bsim.particle.BSimBacterium;


public class BSimSceneExample extends BSimScene{
//public class BSimSceneExample{

	// could have main method already defined and it then runs a setup() method which is overwritten here?
	/**
	 * Main method run when the program starts.
	 * 	~ set up any custom scene parameters, bacteria etc.
	 *	~ create a new BSimApp and set it to simulate the scene.
	 */
	public static void main(String[] args){
		// Set the time step
		BSimScene.dt = 0.01;
		
		// Set display parameters?
		BSimGUI.guiDisable();
		BSimGUI.screenWidth = 800;
		BSimGUI.screenHeight = 600;
		
		// Create a new scene to simulate
		BSimSceneExample scene = new BSimSceneExample();
		//BSimScene scene = new BSimScene();
				
		/*
		 *  Create the bacteria and beads
		 */
		Vector3d startPos = new Vector3d();
		double width = BSimScene.xBound;
		double height = BSimScene.yBound;
		double depth = BSimScene.zBound;
		while(scene.getBacteria().size() < 100) {
			Vector3d position = new Vector3d();
			Vector3d offset = new Vector3d(Math.random()*width, Math.random()*height, Math.random()*depth);			
			position.add(startPos, offset);
			BSimBacterium b = new BSimBacterium(position, 1, new Vector3d(Math.random(),Math.random(),Math.random()), scene);
			if(!scene.intersection(b)) scene.getBacteria().add(b);			
		}
	
		// New instance of a BSimApp that will manage the simulation
		setApp(new BSimApp(scene));
		//app.setRuns(number of runs) etc etc
		
		// Change this to what we decide
		runApp();
	}
	
	
	///////////////////////////////////////////////////////////
	// Inner classes for interactions?
	///////////////////////////////////////////////////////////
	// Or can override update()
	//public void update(){}
	
	/**
	 * Default constructor
	 */
	public BSimSceneExample()
	{
		super(getApp());
	}

}
