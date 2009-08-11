/**
 * BSimDrawable.java
 *
 * Interface requiring the implementation of a redraw method to display an item
 *
 */
package bsim.drawable;

import java.awt.Graphics;

public interface BSimDrawable {
	
	
	/**
	 * Abstract method that should be over written to display the object as required.
	 */
	public abstract void redraw(Graphics g);

}
