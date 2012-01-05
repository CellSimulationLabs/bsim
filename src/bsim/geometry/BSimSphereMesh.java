/**
 * BSimSphereMesh.java
 * Generate a sphere mesh for boundaries etc.
 * 
 * This method creates a geodesic sphere (icosasphere in this case),
 * which approximates a true sphere, by recursively subdividing an icosahedron.
 * 
 * We initially create a sphere of radius 1 at the origin, which is then scaled and translated.
 * 
 * Inspired by
 * http://www.donhavey.com/blog/tutorials/tutorial-3-the-icosahedron-sphere/
 * http://blog.andreaskahler.com/2009/06/creating-icosphere-mesh-in-code.html
 * among other things.
 */

package bsim.geometry;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.vecmath.Vector3d;

/**
 * Sphere mesh, uses face-vertex representation.
 */
public class BSimSphereMesh extends BSimMesh {

	/**
	 * Temporary cache of edge midpoints used during subdivision to ensure vertices are not duplicated.
	 */
	private Hashtable<Long,Integer> midPointsCache;
	
	/**
	 * Number of subdivision and smoothing iterations to perform.
	 */
	private int recursionThreshold;
	
	/**
	 * Main constructor. Generates the unit sphere on the origin and then transforms it to
	 * conform to the specified parameters.
	 * @param centre	The desired centre coordinates of the sphere in 3D space.
	 * @param radius	The desired sphere radius.
	 * @param subdivisionRecursions	Desired number of subdivision and smoothing iterations
	 * 								(note, no. of faces increases by a factor of 4 for each iteration).
	 */
	public BSimSphereMesh(Vector3d centre, double radius, int subdivisionRecursions) {
		super();
		recursionThreshold = subdivisionRecursions;
		createMesh();
		scale(radius);
		translate(centre);
		cleanUp(false);
	}
	
	/**
	 * Defines the vertices and faces for a geodesic sphere.
	 * Initially defines an icosahedron, which is then recursively subdivided and smoothed.
	 */
	@Override
	protected void createMesh() {
        double golden = (1.0 + Math.sqrt(5.0)) / 2.0;	// Golden ratio
        	 
        /*
         * Add vertices of Icosahedron to main vertex list
         */
        addScaledVertex(new Vector3d(-1,  golden,  0));
        addScaledVertex(new Vector3d( 1,  golden,  0));
        addScaledVertex(new Vector3d(-1, -golden,  0));
        addScaledVertex(new Vector3d( 1, -golden,  0));
 
        addScaledVertex(new Vector3d( 0, -1,  golden));
        addScaledVertex(new Vector3d( 0,  1,  golden));
        addScaledVertex(new Vector3d( 0, -1, -golden));
        addScaledVertex(new Vector3d( 0,  1, -golden));
 
        addScaledVertex(new Vector3d( golden,  0, -1));
        addScaledVertex(new Vector3d( golden,  0,  1));
        addScaledVertex(new Vector3d(-golden,  0, -1));
        addScaledVertex(new Vector3d(-golden,  0,  1));
        
        /*
         * Add faces to main triangle list
         */
        // 5 faces around point 0
        addTriangle(0, 11, 5);
        addTriangle(0, 5, 1);
        addTriangle(0, 1, 7);
        addTriangle(0, 7, 10);
        addTriangle(0, 10, 11);
 
        // 5 adjacent faces
        addTriangle(1, 5, 9);
        addTriangle(5, 11, 4);
        addTriangle(11, 10, 2);
        addTriangle(10, 7, 6);
        addTriangle(7, 1, 8);
 
        // 5 faces around point 3
        addTriangle(3, 9, 4);
        addTriangle(3, 4, 2);
        addTriangle(3, 2, 6);
        addTriangle(3, 6, 8);
        addTriangle(3, 8, 9);
 
        // 5 adjacent faces
        addTriangle(4, 9, 5);
        addTriangle(2, 4, 11);
        addTriangle(6, 2, 10);
        addTriangle(8, 6, 7);
        addTriangle(9, 8, 1);
        
        /*
         * Subdivision of the icosahedron to an approximate sphere.
         * For each level of recursion, a temporary list of faces is created which overwrites
         * the old list upon completion of the subdivision step.
         * New vertices are appended to the full list. Midpoint vertices are stored with a key
         * combined from their two 'parent' vertices' indices, thus ensuring no duplication.
         */
        for(int i = 0; i < this.recursionThreshold; i++){
        	ArrayList<BSimTriangle> tempTriangles = new ArrayList<BSimTriangle>();
        	midPointsCache = new Hashtable<Long,Integer>(faces.size()*2,0.8f);
        	
        	for(BSimTriangle t: faces){
        		// Create the midpoints of the three faces.
        		int a = getMiddle(t.getP1(),t.getP2());
        		int b = getMiddle(t.getP2(),t.getP3());
        		int c = getMiddle(t.getP3(),t.getP1());
        		
        		// Create the four new triangles which will replace the original.
        		tempTriangles.add(new BSimTriangle(t.getP1(), a, c, this));
        		tempTriangles.add(new BSimTriangle(t.getP2(), b, a, this));
        		tempTriangles.add(new BSimTriangle(t.getP3(), c, b, this));
        		tempTriangles.add(new BSimTriangle(a, b, c, this));
        	}
        	// Update the triangle list of the actual mesh.
        	faces = tempTriangles;
        	
        	midPointsCache.clear();
        }
        
        // Calculate normals.
        for(BSimTriangle t:faces){
        	computeNormal(t);
        }
	}
	
	/**
	 * Helper wrapper: ultimately the same method as BSimFVMesh.addVertex(), 
	 * but scales the vertex position to be a unit distance from the origin.
	 * @param p The vertex coordinates in 3D space.
	 */
	protected int addScaledVertex(Vector3d p){
    	double length = Math.sqrt(p.x*p.x + p.y*p.y + p.z*p.z);
    	p.scale(1/length);
    	
    	return addVertex(p);
	}
	
	/**
	 * Create the middle vertex between two vertices if it doesn't already exist.
	 * Accesses BSimSphereMesh's hashed midpoint cache to ensure that vertices are
	 * not duplicated. Symmetry means that multiple faces will share midpoint
	 * vertices so this saves time and storage.
	 * @param p1 First vertex index.
	 * @param p2 Second vertex index.
	 */
	protected int getMiddle(int p1, int p2){
		// Do we already have this midpoint stored from another face?
		boolean firstIsSmaller = p1 < p2;
		long smallerIndex = firstIsSmaller ? p1 : p2;
		long greaterIndex = firstIsSmaller ? p2 : p1;
		// Hashtable key composed of the two (ordered) vertex indices.
		Long key = (smallerIndex << 32) + greaterIndex;
		
		// If this midpoint is already cached then return its index.
		if(midPointsCache.containsKey(key)){
			return midPointsCache.get(key); 
		}
		
		// Otherwise, create the midpoint.
		Vector3d middle = new Vector3d();
		middle.add((vertices.get(p1)).getLocation(), (vertices.get(p2)).getLocation());
		middle.scale(0.5);
		
		// Add the midpoint to the mesh vertices.
		int i = addScaledVertex(middle);
		
		// Cache the index of the new midpoint.
		midPointsCache.put(key, i);
		
		return i;		
	}
}

