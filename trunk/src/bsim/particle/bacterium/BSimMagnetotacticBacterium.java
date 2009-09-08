/**
 * BSimMagnetotacticBacterium.java
 *
 * Class that represents a magnetotactic bacterium in our simulation. 
 * 
 * Author: Emily Nicoli
 *  
 * Created: 07/09/2009
 */
package bsim.particle.bacterium;

import javax.vecmath.Vector3d;

import bsim.BSimParameters;
import bsim.BSimUtils;
import bsim.scene.BSimScene;


public class BSimMagnetotacticBacterium extends BSimBacterium {

	public BSimMagnetotacticBacterium(Vector3d newPosition, double newRadius, Vector3d newDirection, BSimScene newScene) {
		super(newPosition, newRadius, newDirection, newScene);		
	}
	
	public void action() {		
	
		Vector3d f = new Vector3d();
		Vector3d m = new Vector3d(BSimParameters.magneticFieldDirection[0],BSimParameters.magneticFieldDirection[1],BSimParameters.magneticFieldDirection[2]);
		m.normalize();
		f.scale(BSimParameters.bactForceMagnetic, m);		
		this.addForce(f);
		
		vesiculate();
	}	
}
