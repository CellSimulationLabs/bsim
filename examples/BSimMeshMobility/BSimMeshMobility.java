

import java.awt.Color;

import bsim.geometry.BSimCollision;
import bsim.geometry.BSimMesh;
import bsim.geometry.BSimOBJMesh;
import bsim.geometry.BSimSphereMesh;
import bsim.geometry.BSimMeshUtils;

import java.util.Random;
import java.util.Vector;


import java.util.Scanner;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.OctreeNode;
import bsim.BSimOctreeField;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.BSimOctreeChemicalField;

//import bsim.mesh.BSimFVMesh;
import bsim.geometry.BSimSphereMesh;
import bsim.geometry.BSimTriangle;
import bsim.ode.BSimOdeSolver;
import bsim.ode.BSimOdeSystem;
import bsim.particle.BSimBacterium;
import bsim.particle.BSimVesicle;

import bsim.draw.BSimDrawer;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimLogger;
import bsim.export.BSimMovExporter;
import bsim.export.BSimPngExporter;




/**Simple simulation for use in presentation
 * 
 * @author tt9671
 *
 */
class BSimMeshMobility{
	static BSimOBJMesh theMesh;
	public static void main(String args[]){
		BSim sim = new BSim();		
		sim.setDt(0.1);
		sim.setTimeFormat("0.00");
		sim.setSimulationTime(10);
		sim.setSolid(true,true,true);
		sim.setBound(100,100,100);

		/**
		 * Load the mesh (ensure only one load is uncommented)
		 */
		theMesh = new BSimOBJMesh();
		theMesh.load("Mesh_Intact.obj");
		// theMesh.load("Mesh_20PctRemoved.obj");
		// theMesh.load("Mesh_40PctRemoved.obj");
		
		/**
		 * Place mesh in the correct location for the simulation
		 */
		theMesh.scale(53);
		theMesh.translateAbsolute(new Vector3d(50.0, 50.0, 50.0));
		
	
		class BSimCollidingBacterium extends BSimBacterium{			
			public BSimCollidingBacterium(BSim sim, Vector3d position) {
				super(sim, position);
			}
			@Override
			public void action(){
				super.action();
				BSimCollision.collideAndRepel(this, theMesh);
			}
		}
		
		final Vector<BSimCollidingBacterium> bacteria = new Vector<BSimCollidingBacterium>();

		
			//Set of variables to store the start positions; 
			final double Xco=Math.random()*100;
			final double Yco=Math.random()*100;
			final double Zco=Math.random()*100;
			
			final BSimCollidingBacterium b = new BSimCollidingBacterium(sim, new Vector3d(Xco,Yco,Zco));
			bacteria.add(b);

				
		
		

		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(BSimCollidingBacterium b : bacteria) {
					b.action();		
					b.updatePosition();
				}
			}
		});
		
		//creates a drawer
		BSimP3DDrawer drawer = new BSimP3DDrawer(sim, 800, 600) {
			@Override
			public void scene(PGraphics3D p3d) {	
				for(BSimCollidingBacterium b : bacteria) {
					draw(b,Color.GREEN);
				}
				draw(theMesh, 0);	
			}
		};			
		
		sim.setDrawer(drawer);
	//	BSimPngExporter PNGExp = new BSimPngExporter(sim,drawer,"results/Move2");
		
		
	//	sim.addExporter(PNGExp);
		
		BSimLogger trackerXY = new BSimLogger(sim, "results/trackerXY.csv") {
			@Override
			public void during() {
				//Logs distance moved from start point
				double distance = Math.sqrt(Math.pow(Xco-b.getPosition().x,2)+Math.pow(Yco-b.getPosition().y,2)+Math.pow(Zco-b.getPosition().z,2));
				
				write(distance + "," + sim.getFormattedTime());
			}
		};
		trackerXY.setDt(1.0);
	//	sim.addExporter(trackerXY);

	//	sim.export();
		
		//sim.addExporter(trackerXY);
		
		//sim.export(); 
		
		sim.preview();

		
	}
	
}

