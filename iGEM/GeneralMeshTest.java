package bsim.geometry.tests;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.geometry.BSimMesh;
import bsim.geometry.BSimOBJMesh;
import bsim.geometry.BSimSphereMesh;

/**
 * General mesh tests (import, rendering, classes)
 * See main() to choose which mesh we want to test...
 * Useful for checking scales and whether everything is where it should be.
 */
public class GeneralMeshTest {

	final static int SPHERE = 1;
	final static int TRI = 2;
	final static int CUBE = 3;
	final static int IMPORT = 4;

	public int meshToTest = 0;
	public void setTestMesh(int testMesh){meshToTest = testMesh;}
	
	
	
	public static void main(String[] args) {
		GeneralMeshTest program = new GeneralMeshTest();
		
		// ******** Set the mesh to be drawn. (SPHERE, TRI, CUBE, IMPORT)
		program.setTestMesh(TRI);
		
		// ******** Path of OBJ file if using IMPORT
		String importPath = "C:\\My Dropbox\\_igem2010\\EclipseWS\\xyz.obj";
		
		// ******** Mesh position and scale
		Vector3d meshPos = new Vector3d(50,50,50);
		double meshScale = 50;
		
		
		BSimMesh newMesh;
		
		switch(program.meshToTest){
		case CUBE: 
			newMesh = program.new BSimCubeMesh();
			newMesh.scale(meshScale);
			newMesh.translateAbsolute(meshPos);
			break;
		case SPHERE:
			newMesh = new BSimSphereMesh(meshPos, meshScale, 2);
			break;
		case TRI:
			newMesh = program.new BSimTriMesh();
			break;
		case IMPORT:
			BSimOBJMesh mesh = new BSimOBJMesh();
			mesh.load(importPath);
			mesh.scale(meshScale);
			mesh.translateAbsolute(meshPos);
			newMesh = mesh;
			break;
		default:
			System.out.println("No mesh set.... Using default");
			newMesh = new BSimSphereMesh(meshPos, meshScale, 2);
		}
				
		final BSimMesh theMesh = newMesh;
		//theMesh.printStats();

		
		// ******** Bits and bobs to get something up on screen:
		BSim sim = new BSim();		
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
			}
		});
		
		// Draw the mesh.
		BSimP3DDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {							
				draw(theMesh, 2);
			}
			
		// Lit and shaded mesh with no stroke.
//			@Override
//			public void draw(BSimMesh mesh, double normalScaleFactor){
//				p3d.directionalLight(255, 255, 255, 1, 0, 0);
//				p3d.fill(128,128,255,100);
//				p3d.beginShape(PConstants.TRIANGLES);
//				for(BSimTriangle t:mesh.getFaces()){
//					vertex(mesh.getTCoords(t,0));
//					vertex(mesh.getTCoords(t,1));
//					vertex(mesh.getTCoords(t,2));
//				}
//				p3d.endShape();
//				if(normalScaleFactor != 0){
//					for(BSimTriangle t:mesh.getFaces()){
//						vector(mesh.getTCentre(t),t.getNormal(),normalScaleFactor,(new Color(255,0,0,150)));
//					}
//				}
//				p3d.noLights();
//			}
		};	
		sim.setDrawer(drawer);
		
		sim.preview();
	}
	
	/**
	 * Test cube mesh.
	 */
	private class BSimCubeMesh extends BSimMesh {
		
		public BSimCubeMesh(){
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
	
	/**
	 * Test triangle...
	 */
	private class BSimTriMesh extends BSimMesh {
		
		public BSimTriMesh(){
			super();
			createMesh();
			cleanUp(false);
		}
		
		@Override
		protected void createMesh() {
			addVertex(50, 25, 25);
			addVertex(50, 50, 75);
			addVertex(50, 75, 25);
			
			addTriangle(0,1,2);
		}
	}


}
