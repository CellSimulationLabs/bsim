/**
 * BSimBoundingBox.java
 *
 * Class that represents a bounding box.
 */
package bsim;

import javax.vecmath.Vector3d;

public class BSimBoundingBox {
       
		private Vector3d startPos;
		private double width, height, depth;
       
        public BSimBoundingBox(Vector3d newStartPos, double newWidth, double newHeight, double newDepth) {               
    		startPos = newStartPos;
    		width = newWidth;
    		height = newHeight;
    		depth = newDepth;
        }      
       
        public Vector3d getStartPos() { return startPos; }        
        public double getWidth() { return width; }
        public double getHeight() { return height; }
        public double getDepth() { return depth; }       
}