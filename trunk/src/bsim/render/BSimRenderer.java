/**
 * BSimRenderer.java
 * 
 * BSim Renderer - an interface which defines the basic drawing requirements for a 
 * renderer implemented into BSim.
 * 
 * As future versions of BSim may want to use a different renderer, it is simpler (hopefully)
 * to override drawing methods in a new renderer class rather than coding new 'redraw()'
 * methods within each object class specifically for the new renderer.
 * 
 * note: 	it might be useful to use an abstract class, as then loops etc can be defined
 * 			in standard draw() as well as useful BSim variables, however this may impose 
 * 			unnecessary constraints and may not be compatible with renderers which must 
 * 			already extend a class.
 * 
 */

package bsim.render;

import bsim.field.BSimChemicalField;
import bsim.particle.BSimParticle;
import bsim.particle.bacterium.BSimBacterium;
import bsim.particle.bacterium.BSimRepBacterium;
import bsim.particle.bead.BSimBead;
import bsim.particle.vesicle.BSimVesicle;

public interface BSimRenderer {
	
	/*
	 *  Draw the scene
	 */
	public void draw();
	
	/*
	 * Draw the boundary
	 */
	// TODO: a boundary draw method here...
	
	/*
	 *  Draw BSimParticle based objects
	 */
	public void draw(BSimParticle g);
	// Bacteria
	public void draw(BSimBacterium g);
	public void draw(BSimRepBacterium g);
	// Beads
	public void draw(BSimBead g);
	// Vesicles
	public void draw(BSimVesicle g);
	
	/*
	 * Draw chemical field
	 */
	public void draw(BSimChemicalField g);

	/*
	 * Draw visual aids
	 */
	//TODO: public void draw(Some visual Aid);
}
