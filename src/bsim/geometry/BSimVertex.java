/**
 * BSimVertex.java
 * A mesh vertex.
 */

package bsim.geometry;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

/**
 *  Mesh vertex.
 *  Defines the location of a mesh vertex in 3-D space as a Vector3d,
 *  and also stores a cached list of the faces which are attached to
 *  this vertex.
 */
public class BSimVertex {
	
	/** The Cartesian coordinates of the vertex in 3-D space. */
	protected Vector3d location;
	
	/**
	 * List of the indices of the faces which use this vertex.
	 * Currently this is not updated from within BSimVertex but from the
	 * surface mesh implementation (e.g BSimFVMesh).
	 */
	protected ArrayList<Integer> faces = new ArrayList<Integer>(1);
	
	/**
	 * Constructor: create a new mesh vertex from three points; x,y,z
	 */
	public BSimVertex(double newX, double newY, double newZ){
		Vector3d newLocation = new Vector3d(newX,newY,newZ);
		location = newLocation;
	}
	
	/**
	 * Constructor: create a new mesh vertex from a Vector3d
	 */
	public BSimVertex(Vector3d newLocation){
		location = newLocation;
	}
	
	// Parameter getters.
	public Vector3d getLocation(){ return location;}
	public ArrayList<Integer> getFaces(){ return faces;}
}

