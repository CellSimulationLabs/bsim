/**
 * Random testing and demo of mesh functions as I implement them.
 * This will most likely be removed when everything is working nicely.
 * 
 * NOTE: for less messy rendering, comment out line 79 in BSimP3DDrawer.java
 * to get rid of boundary drawing and see the mesh better.
 * also note... Meshes are not yet boundaries: no collision detection has been implemented!
 */


package bsim.geometry.assertions;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.geometry.BSimMesh;


public class MeshTest {
	
	public static void main(String[] args) {
		/*
		 * CUBE:
		 */
		MeshTest program = new MeshTest();
		final BSimTestMesh theMesh = program.new BSimTestMesh();
		theMesh.scale(10);
		
		/*
		 * SPHERE:
		 * (comment out if using the cube and vice versa)
		 */
//		final BSimSphereMesh theMesh = new BSimSphereMesh(new Point3d(0,0,0), 25, 2);

		theMesh.printStats();
		
		
		/*
		 *  Bits and bobs to get something up on screen:
		 */
		BSim sim = new BSim();		
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
			}
		});
		
		// Draw the mesh.
		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {							
				draw(theMesh, 5);
			}
		});	
		
		sim.preview();
	}
	
	/**
	 * Test cube to check things were working before I did the sphere.
	 */
	private class BSimTestMesh extends BSimMesh {
		
		public BSimTestMesh(){
			super();
			createMesh();
			cleanUp(false);
		}
		
		@Override
		protected void createMesh() {
			addVertex(0, 0, 0);
			addVertex(1, 0, 0);
			addVertex(1, 1, 0);
			addVertex(0, 1, 0);
			addVertex(0, 0, 1);
			addVertex(1, 0, 1);
			addVertex(1, 1, 1);
			addVertex(0, 1, 1);
			
			addTriangle(0, 4, 5);
			addTriangle(0, 5, 1);
			addTriangle(1, 5, 6);
			addTriangle(1, 6, 2);
			addTriangle(2, 6, 7);
			addTriangle(2, 7, 3);
			addTriangle(3, 7, 4);
			addTriangle(3, 4, 0);
			addTriangle(0, 1, 3);
			addTriangle(1, 2, 3);
			addTriangle(5, 4, 7);
			addTriangle(5, 7, 6);
		}
	}
}
