/**
 * BSimFVMesh.java
 * Abstract 3-D mesh surface class, Face-Vertex representation.
 * 
 * 	So far a fairly naive implementation of the concept (e.g. accessing parameters
 * 	from outside for example from the renderer could be improved, as could general 
 * 	efficiency)
 */

package bsim.geometry;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

/**
 * Abstract 3-D mesh surface class. Represented as indexed list of vertices,
 * or points in 3-D, of which the mesh faces are composed.
 */
public abstract class BSimMesh {
		
	/** The actual locations (3D coordinates) of all mesh vertices */
	protected ArrayList<BSimVertex> vertices; 
		
	/** List of faces, each face stores the indices of the vertices which compose that face. */
	protected ArrayList<BSimTriangle> faces;
			
	
	/**
	 * Default constructor, initialises the vertex and triangle lists.
	 */
	public BSimMesh(){
		vertices = new ArrayList<BSimVertex>();
		faces = new ArrayList<BSimTriangle>();		
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
	 *  Add a vertex to the vertex list (using a Vector3d).
	 */
	public int addVertex(Vector3d p){
		return addVertex(p.x,p.y,p.z);
	}

	/**
	 * Add a triangular face to the face list.
	 * Parameters are the indices in the vertex list of the three corner points of the triangle.
	 * @param v1 Index in list 'vertices' of face vertex 1.
	 * @param v2 Index in list 'vertices' of face vertex 2.
	 * @param v3 Index in list 'vertices' of face vertex 3.
	 */
	public void addTriangle(int v1, int v2, int v3){
		BSimTriangle t = new BSimTriangle(v1, v2, v3, this);
		addTriangle(t);
	}
	
	/**
	 * Add an existing triangle to the face list
	 * @param t The BSimTriangle to be added 
	 */
	public void addTriangle(BSimTriangle t){
		computeNormal(t);
		faces.add(t);
	}
	
	/**
	 * Compute which faces index each vertex, and store this as a list parameter in each vertex object.
	 *  
	 * Face connectivity from a vertex should be a useful parameter when doing space subdivision etc.
	 */
	protected void calcVertexFaces(){
		int[] tvs = new int[3];
		
		// For each face of the mesh, check which vertices it uses.
		for(int i = 0; i < faces.size(); i++){
			tvs = (faces.get(i)).getPoints();
			
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
	 * Trim down the arrayLists, and compute vertex-face connectivity.
	 * Minimises storage and hopefully increases efficiency (unless we will be changing these lists later).
	 * @param stats (if true, print mesh statistics after clean-up is done.)
	 */
	protected void cleanUp(boolean stats){
		vertices.trimToSize();
		faces.trimToSize();
		
		calcVertexFaces();
		
		if(stats) printStats();
	}
	
	/**
	 * Compute the normal vector of a face.
	 */
	public void computeNormal(BSimTriangle t){
		Vector3d p1 = new Vector3d(vertices.get(t.tVertices[0]).getLocation());
		Vector3d p2 = new Vector3d(vertices.get(t.tVertices[1]).getLocation());
		Vector3d p3 = new Vector3d(vertices.get(t.tVertices[2]).getLocation());

		Vector3d v1 = new Vector3d(); v1.sub(p2, p1);
		Vector3d v2 = new Vector3d(); v2.sub(p3, p1);
		
		Vector3d newNormal = new Vector3d();
				
		newNormal.cross(v1,v2);
		newNormal.normalize();
		
		t.updateNormal(newNormal);
	}
	
	/**
	 * Compute all normals of the mesh
	 */
	public void computeNormals(){
		for(BSimTriangle t:faces){
			computeNormal(t);
		}
	}
	
	/**
	 * Flip normals of all faces
	 */
	public void flipNormals(){
		for(BSimTriangle t: faces){
			t.flipNormal();
		}
	}
	
	/**
	 * Flip normals of selected faces
	 * @param normalsList Array of integer indices corresponding to the faces we wish to flip.
	 */
	public void flipNormals(int[] faceList){
		for(int i = 0; i < faceList.length; i++){
			faces.get(i).flipNormal();
		}
	}
	
	/**
	 * Scale mesh on arbitrary point
	 * @param scaleFactor 	The factor by which the mesh will be scaled 
	 * 						(1.0 = no scaling, 2.0 = double size, 0.5 = half size, etc.)
	 * @param scaleOn		The point from which the mesh will be scaled.
	 */
	public void scale(double scaleFactor, Vector3d scaleOn){
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
		scale(scaleFactor, new Vector3d(0,0,0));
	}
		
	/**
	 * Compute the (unweighted) average centre coordinate of all mesh vertices.
	 * @return
	 */
	public Vector3d averagedCentreOfMesh(){
		double xTotal = 0;
		double yTotal = 0;
		double zTotal = 0;
		
		// Sum x, y, z coords
		for(BSimVertex v: vertices){
			Vector3d vLoc = v.getLocation();
			
			xTotal += vLoc.x;
			yTotal += vLoc.y;
			zTotal += vLoc.z;
		}
		
		// Scale coords by reciprocal of total number of vertices 
		Vector3d centrePos = new Vector3d(xTotal, yTotal, zTotal);
		centrePos.scale(1/vertices.size());
		
		return centrePos;
	}
	
	/**
	 * Translate the whole mesh so that it is centred on a new point in 3d space.
	 * @param newLocation The location on which the mesh will be centred.
	 */
	public void translateAbsolute(Vector3d newLocation){
		// Current average centre of mesh
		Vector3d currentLocation = averagedCentreOfMesh();

		Vector3d translation = new Vector3d();
		
		// Translation vector: new_loc - current_loc
		translation.sub(newLocation, currentLocation);
		
		// Move all vertices by translation vector
		translate(translation);
	}
	
	/**
	 * Translate the mesh in an arbitrary direction.
	 * @param translation The vector by which all vertices are translated
	 */
	public void translate(Vector3d translation){
		for(BSimVertex v: vertices){
			(v.location).add(translation);
		}
	}
	
	
	// TODO: better getters for vertices etc.
	// Getters
	public ArrayList<BSimVertex> getVertices(){ return vertices;}
	public BSimVertex getVertex(int i){ return vertices.get(i);}
	
	public ArrayList<BSimTriangle> getFaces(){ return faces;}
	public BSimTriangle getFace(int i){ return faces.get(i);}

	public Vector3d getVertCoords(int vertIndex){return vertices.get(vertIndex).getLocation();}
	
	/**
	 * Get the vertex coordinates of a given triangle
	 * @param t
	 * @param i Index of the vertex for which to get coordinates (0, 1, 2)
	 * @return
	 */
	public Vector3d getVertCoordsOfTri(BSimTriangle t, int i){
		int index = t.getPoints()[i];
		BSimVertex v = vertices.get(index);
		return (v.getLocation());
	}
	
	/**
	 * Compute the coordinates of the centre of a triangle
	 * @param t
	 * @return
	 */
	public Vector3d getTCentre(BSimTriangle t){
		double x, y, z;
		Vector3d a = vertices.get(t.getP1()).getLocation();
		Vector3d b = vertices.get(t.getP2()).getLocation();
		Vector3d c = vertices.get(t.getP3()).getLocation();
		
		x = (a.x + b.x + c.x)/3;
		y = (a.y + b.y + c.y)/3;
		z = (a.z + b.z + c.z)/3;

		return (new Vector3d(x,y,z));
	}

	
	/**
	 * Print mesh statistics (face vertices, vertex coords, vertex faces, normals...)
	 */
	public void printStats(){
		int n = 0;
		System.out.println("Face | Vertex indices");
		for(BSimTriangle t:faces){
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
		for(BSimTriangle t:faces){
			System.out.print(n + " | ");
			System.out.print(t.getNormal());
			System.out.println();
			n++;
		}
		System.out.println(); n = 0;
	}	
	
}
