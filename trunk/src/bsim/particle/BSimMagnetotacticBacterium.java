/**
 * BSimMagnetotacticBacterium.java
 *
 * Class that represents a magnetotactic bacterium in our simulation. 
 * 
 * Author: Emily Nicoli
 *  
 * Created: 07/09/2009
 */
package bsim.particle;

import javax.vecmath.Vector3d;

import bsim.scene.BSimScene;


public class BSimMagnetotacticBacterium extends BSimBacterium {
	
	public static double bactForceMagnetic    = 0.42; // pico newtons
	public static double[] magneticFieldDirection     = {1,1,1};

	public BSimMagnetotacticBacterium(Vector3d newPosition, double newRadius, Vector3d newDirection, BSimScene newScene) {
		super(newPosition, newRadius, newDirection, newScene);		
	}
	
	public void action() {		
	
		Vector3d f = new Vector3d();
		Vector3d m = new Vector3d(BSimMagnetotacticBacterium.magneticFieldDirection[0],BSimMagnetotacticBacterium.magneticFieldDirection[1],BSimMagnetotacticBacterium.magneticFieldDirection[2]);
		m.normalize();
		f.scale(BSimMagnetotacticBacterium.bactForceMagnetic, m);		
		this.addForce(f);
		
		vesiculate();
	}	
}
