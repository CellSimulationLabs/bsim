/**
 * BSimFVMesh.java
 * Abstract 3-D mesh surface class, Face-Vertex representation.
 * 
 * 	So far a fairly naive implementation of the concept (e.g. accessing parameters
 * 	from outside for example from the renderer could be improved, as could general 
 * 	efficiency)
 */

package bsim.mesh;

import java.util.ArrayList;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Abstract 3-D mesh surface class, represented as indexed list of vertices,
 * or points in 3-D, of which the mesh faces are composed.
 */
public abstract class BSimFVMesh {
	
	/**
	 *  The actual locations (3D coordinates) of all mesh vertices
	 */
	protected ArrayList<BSimVertex> vertices; 
		
	/**
	 * List of faces, each face stores the indices of the vertices which compose that face.
	 */
	protected ArrayList<BSimTriangle> triangles;

	/**
	 * Default constructor, initialises the vertex and triangle lists.
	 */
	public BSimFVMesh(){
		vertices = new ArrayList<BSimVertex>();
		triangles = new ArrayList<BSimTriangle>();		
	}
	
	/**
	 * Trim down the arrayLists, and compute vertex-face connectivity.
	 * Minimises storage and hopefully increases efficiency (unless we will be changing these lists later).
	 * @param stats		(true: print mesh statistics after clean-up is done.)
	 */
	protected void cleanUp(boolean stats){
		vertices.trimToSize();
		triangles.trimToSize();
		
		calcVertexFaces();
		
		if(stats) printStats();
	}
	
	/**
	 * Abstract method in which the vertices and faces of the mesh should be defined.
	 */
	protected abstract void createMesh();
	
	/**
	 *  Add a vertex to the vertex list (based on x,y,z coordinates).
	 */
	public int addVertex(double newX, double newY, double newZ){
		vertices.add(new BSimVertex(newX,newY,newZ));
		return (vertices.size() - 1);
	}
	
	/**
	 *  Add a vertex to the vertex list (using a Point3d).
	 */
	public int addVertex(Point3d p){
		return addVertex(p.x,p.y,p.z);
	}

	/**
	 * Add a triangular face to the face list.
	 * Parameters are the indices in the vertex list of the three corner points of the triangle.
	 */
	public void addTriangle(int v1, int v2, int v3){
		BSimTriangle t = new BSimTriangle(v1, v2, v3);
		computeNormal(t);
		triangles.add(t);
	}
	
	/**
	 * Compute which faces index each vertex, and store this as a list parameter in each vertex object.
	 */
	protected void calcVertexFaces(){
		int[] tvs = new int[3];
		
		// For each face of the mesh, check which vertices it uses.
		for(int i = 0; i < triangles.size(); i++){
			tvs = (triangles.get(i)).getPoints();
			
			// If one of the three vertices does not index this face yet, add the face index to the list.
			for(int n = 0; n < 3; n++){
				BSimVertex v = vertices.get(tvs[n]);
				if(!(v.faces.contains(i))){
					v.faces.add(i);
				}
			}
		}
		
		// Trim the face index list of each vertex as we will not be using them further.
		for(BSimVertex v:vertices){
			v.faces.trimToSize();
		}
	}
	
	/**
	 * Compute the (normalised) normal vector of a face.
	 */
	public void computeNormal(BSimTriangle t){
		Vector3d p1 = new Vector3d(vertices.get(t.tVertices[0]).getLocation());
		Vector3d p2 = new Vector3d(vertices.get(t.tVertices[1]).getLocation());
		Vector3d p3 = new Vector3d(vertices.get(t.tVertices[2]).getLocation());

		Vector3d v1 = new Vector3d(); v1.sub(p2, p1);
		Vector3d v2 = new Vector3d(); v2.sub(p3, p2);
		
		Vector3d newNormal = new Vector3d();
				
		newNormal.cross(v1,v2);
		newNormal.normalize();
		
		t.updateNormal(newNormal);
	}
	
	/**
	 * Scale mesh on arbitrary point
	 * @param scaleFactor 	The factor by which the mesh will be scaled 
	 * 						(1.0 = no scaling, 2.0 = double size, 0.5 = half size, etc.)
	 * @param scaleOn		The point from which the mesh will be scaled.
	 */
	public void scale(double scaleFactor, Point3d scaleOn){
		for(BSimVertex v:vertices){
			Vector3d current = new Vector3d(v.getLocation());
			Vector3d scaleVec = new Vector3d();
			
			scaleVec.sub(current, scaleOn);
			scaleVec.scale(scaleFactor - 1.0);
			
			(v.location).add(scaleVec);
		}
	}
	
	/**
	 * Scale mesh on origin (0, 0, 0).
	 * @param scaleFactor	The factor by which the mesh will be scaled.
	 */
	public void scale(double scaleFactor){
		scale(scaleFactor, new Point3d(0,0,0));
	}
		
	// Getters
	public ArrayList<BSimTriangle> getTriangles(){ return triangles;}
	
	
	/*
	 * Brute force fetching of parameters for the renderer.
	 * TODO: make this aspect of the code much nicer...
	 */
	public Point3d getTCoords(BSimTriangle t, int i){
		int index = t.getPoints()[i];
		BSimVertex v = vertices.get(index);
		return (v.getLocation());
	}
	public Point3d getTCentre(BSimTriangle t){
		double x, y, z;
		Point3d a = vertices.get(t.getP1()).getLocation();
		Point3d b = vertices.get(t.getP2()).getLocation();
		Point3d c = vertices.get(t.getP3()).getLocation();
		
		x = (a.x + b.x + c.x)/3;
		y = (a.y + b.y + c.y)/3;
		z = (a.z + b.z + c.z)/3;

		return (new Point3d(x,y,z));
	}

	
	/**
	 * Print mesh statistics (face vertices, vertex coords, vertex faces, normals...)
	 */
	public void printStats(){
		int n = 0;
		System.out.println("Face | Vertex indices");
		for(BSimTriangle t:triangles){
			System.out.print(n + " | ");
			for(Integer i:t.getPoints()){
				System.out.print(i.toString() + ", ");
			}
			System.out.println();
			n++;
		}
		System.out.println(); n = 0;
		
		System.out.println("Vertex | (x, y, z)");
		for(BSimVertex v:vertices){
			System.out.print(n + " | ");
			System.out.print(v.location.toString());
			System.out.println(); 
			n++;
		}
		System.out.println(); n = 0;

		System.out.println("Vertex | Face list");
		for(BSimVertex v:vertices){
			System.out.print(n + " | ");
			for(Integer i:v.faces){
				System.out.print(i.toString()+", ");
			}
			System.out.println();
			n++;
		}
		System.out.println(); n = 0;

		System.out.println("Face | Normal");
		for(BSimTriangle t:triangles){
			System.out.print(n + " | ");
			System.out.print(t.getNormal());
			System.out.println();
			n++;
		}
		System.out.println(); n = 0;
	}	
	
}
