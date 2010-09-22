/**
 * OBJ mesh importer.
 *  
 * Uses Java OBJ parsing libraries from http://www.pixelnerve.com/processing/libraries/objimport/
 * However could probably be stripped down a bit for our purposes as we ignore data for 
 * normals, textures etc at the moment anyway.
 * 
 * 
 * Original libraries by Fabien Sanglard
 * 
 * This Java OBJ Loader support:
 * Groups (+scene management implemented)
 * Vertex, Normal, Texture coordinates
 * MTL (material) references.
 * 
 * This Java OBJ Loader DOES NOT support:
 * Relative vertex references.
 * Anything other than GL_TRIANGLE and GL_QUAD polygons
 */


package bsim.geometry;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import com.obj.Face;
import com.obj.Group;
import com.obj.Vertex;
import com.obj.WavefrontObject;

/**
 * Wavefront OBJ importer.
 */
public class BSimOBJMesh extends BSimMesh{
	
	/**
	 * Mesh constructor
	 */
	public BSimOBJMesh(){
		super();
	}
	
	/**
	 * Loads an OBJ file from disk and puts relevant parameters into a BSimMesh
	 * @param filename Path to the OBJ file
	 */
	public void load(String filename) {
		
	    WavefrontObject obj = new WavefrontObject(filename);
	    
	    // Groups - should not be using groups at the moment (in mesh files) as we are generally 
	    // 			only looking to import a single mesh
	    ArrayList<Group> groups = obj.getGroups();
	    for( int gi = 0; gi < groups.size(); gi++ )
	    {
	      Group g = (Group)groups.get(gi);
	      
	      // Set up and add vertices to the BSimMesh
	      for( int vi = 0; vi < obj.getVertices().size(); vi++ )
	      {
	        Vertex v = (Vertex)obj.getVertices().get(vi);
	        this.addVertex(new Vector3d(v.getX(), v.getY(), v.getZ()));
	      }
	      
	      // Set up and add faces (needs to be after vertices at the moment as we need to
	      // compute face normals.
	      for( int fi = 0; fi < g.getFaces().size(); fi++)
	      {
	        Face f = g.getFaces().get(fi);
	        int[] idx = f.vertIndices;

	        BSimTriangle face = new BSimTriangle(idx[0], idx[1], idx[2], this);

	        this.addTriangle(face);
	      }
	    }
	    
	    // Trim lists
	    cleanUp(false);
	}

	@Override
	protected void createMesh() {
		// TODO Auto-generated method stub
		
	}

}
