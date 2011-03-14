/**
 * BSimTriangle.java
 * A triangular face, used to construct a mesh surface.
 * 	This will most likely be integrated more tightly with BSimFVMesh soon,
 * 	to ensure that parameters are easily accessible for rendering, 
 * 	collision detection, etc.
 */


package bsim.geometry;

import javax.vecmath.Vector3d;

/**
 * Triangular face of a 3-D mesh surface.
 * Stores the indices of its three vertices, and a cached face normal vector.
 */
public class BSimTriangle {
	protected int[] tVertices = new int[3];
	
	/** Triangle normal. Assumed to be normalised on creation. */
	protected Vector3d normal = new Vector3d();
	protected BSimMesh parentMesh;
	
	/**
	 * Constructor: New triangular face from three individual vertex indices.
	 */
	public BSimTriangle(int newP1Index, int newP2Index, int newP3Index, BSimMesh mesh){
		tVertices[0] = newP1Index;
		tVertices[1] = newP2Index;
		tVertices[2] = newP3Index;
		setMesh(mesh);
	}
	
	/**
	 * Constructor: New triangular face from array of vertex indices.
	 */
	public BSimTriangle(int[] newPoints, BSimMesh mesh){
		if(newPoints.length == 3){
			tVertices = newPoints;
			setMesh(mesh);
		} else {
			System.err.println("Error: Triangle requires *three* vertex indices.");
			System.exit(1);
		}
	}
	
	/**
	 * Update the normal vector of this face with a new vector.
	 */
	protected void updateNormal(Vector3d newNormal){
		normal = newNormal;
	}
	
	/**
	 * Flip the face normal if you want it to point the other way.
	 */
	protected void flipNormal(){
		normal.negate();
	}
		
	/**
	 * Get the vertex coordinates of a given triangle
	 * @param t
	 * @param i Index of the vertex for which to get coordinates (0, 1, 2)
	 * @return
	 */
	public Vector3d getTCoords(int i){
		int index = this.getPoints()[i];
		BSimVertex v = parentMesh.vertices.get(index);
		return (v.getLocation());
	}
	
	/**
	 * Set parent mesh to which this face belongs.
	 * @param mesh Parent BSimMesh
	 */
	protected void setMesh(BSimMesh mesh){
		parentMesh = mesh;
	}
	
	/*
	 * Getter methods for triangle parameters.
	 */
	/** Gets the triangle normal vector. Assumed normalised in BSimMesh	 */
	public Vector3d getNormal(){ return normal;}
	
	public BSimMesh getParentMesh(){ return parentMesh; }
	public int getP1(){ return tVertices[0];}
	public int getP2(){ return tVertices[1];}
	public int getP3(){ return tVertices[2];}
	public int[] getPoints(){ return tVertices;}
}