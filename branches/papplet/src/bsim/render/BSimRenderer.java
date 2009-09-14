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
import bsim.particle.BSimBacterium;
import bsim.particle.BSimBead;
import bsim.particle.BSimParticle;
import bsim.particle.BSimRepBacterium;
import bsim.particle.BSimVesicle;
import bsim.particle.BSimVesicleAcceptor;

public interface BSimRenderer {
	// A note. This interface is a bit of a mess in terms of extensibility
	// (i.e. renderers have to be specifically defined as a processing renderer
	// at the moment for us to be able to pass them to classes/methods)
	// Therefore this should also specify some form of GUI 'embedability' e.g. an applet or graphics object.
	// Thus we would be able to specify BSimRenderer objects throughout and only actually define the
	// specific renderer type in the setup of the scene.
	
	/**
	 * These bad boys may be useful for recording movies, screenshots etc.
	 * Remember that BitSet could be used, possibly, for settings? Or just our own bitwise flags.
	 */
//	public static final int RENDERING_DISABLED = 1;
//	public static final int RENDERING_ENABLED = 2;
//	
//	public static final int RENDERING_LOOP_OFF = 1;
//	public static final int RENDERING_LOOP_ON = 2;
//	
//	public void renderingStateSet(int newState);
//	public int renderingStateGet();
//	public void loopStateSet(int newState);
//	public int loopStateGet();
	
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
	// Vesicle acceptor
	public void draw(BSimVesicleAcceptor g);
	
	// Boundaries (for now)
	public void drawBoundary();
	
	/*
	 * Draw chemical field
	 */
	public void draw(BSimChemicalField g);

	/*
	 * Draw visual aids
	 */
	//TODO: public void draw(Some visual Aid);
}
