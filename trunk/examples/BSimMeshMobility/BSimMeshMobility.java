package BSimMeshMobility;

import java.awt.Color;
import java.util.*;
import javax.vecmath.Vector3d;
import processing.core.PGraphics3D;

import bsim.*;
import bsim.draw.*;
import bsim.export.*;
import bsim.particle.*;
import bsim.geometry.*;

/**
 * BSimMeshMobility.java
 * 
 * Simulation that illustrates the effect that complex microscale environments can have on bacterial
 * motility. Work derived from the BCCS-Bristol iGEM 2010 project 'argEcoli', see the following URL
 * for further details:
 *                            http://2010.igem.org/Team:BCCS-Bristol
 */
class BSimMeshMobility {
	static BSimOBJMesh theMesh;
	public static void main(String args[]){
		
		/**
		 * Create a new simulation object
		 */
		BSim sim = new BSim();		
		sim.setDt(0.015);
		sim.setTimeFormat("0.00");
		sim.setSimulationTime(9000.0); // 2.5 hours
		sim.setSolid(true,true,true);
		sim.setBound(8000,8000,8000);

		/**
		 * Load the mesh (ensure only one load is uncommented)
		 */
		theMesh = new BSimOBJMesh();
		//theMesh.load("./examples/BSimMeshMobility/Mesh_Intact.obj");
		theMesh.load("./examples/BSimMeshMobility/Mesh_20PctRemoved.obj");
		//theMesh.load("./examples/BSimMeshMobility/Mesh_40PctRemoved.obj");
		
		/**
		 * Place mesh in the correct location for the simulation
		 */
		theMesh.scale(4400);
		theMesh.translateAbsolute(new Vector3d(4000,4000,4000));
		
		/**
		 * A type of bacteria that is repelled by the mesh surface
		 */
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

		/**
		 * Create set of bacteria for the simulation. All start at same location (centre) but
		 * due to random nature of the run-and-tumble movement will search the space 
		 * independantly.
		 */
		final Vector<BSimCollidingBacterium> bacteria = new Vector<BSimCollidingBacterium>();	
		while(bacteria.size() < 100) {		
			BSimCollidingBacterium b = new BSimCollidingBacterium(sim, 
					new Vector3d(1500.0 + Math.random()*5000.0, 
							     1500.0 + Math.random()*5000.0, 
							     1500.0 + Math.random()*5000.0));
			bacteria.add(b);
		}

		/**
		 * Create the ticker for the simulation
		 */
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(BSimCollidingBacterium b : bacteria) {
					b.action();		
					b.updatePosition();
				}
			}
		});
		
		/**
		 * Create the drawer
		 */
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
		
		/**
		 * Create exporter of the distance traveled from start position
		 */
		BSimLogger tracker = new BSimLogger(sim, "/Users/Tom/Desktop/BSim/results.csv") {
			@Override
			public void before() {
				int i = 1;
				super.before();
				String buffer = new String();
				buffer = "time(seconds)";
				for(@SuppressWarnings("unused") BSimCollidingBacterium b : bacteria) {
					buffer = buffer + ",Bac" + i + "X" + ",Bac" + i + "Y" + ",Bac" + i + "Z";
					i++;
				}
				write(buffer); 
			}
			@Override
			public void during() {
				int i = 0;
				String buffer = new String();
				// Logs distance moved from start point
				for(BSimCollidingBacterium b : bacteria) {
					if (i > 0) buffer = buffer + ",";
					buffer = buffer + b.getPosition().x + "," + b.getPosition().y + ","+ b.getPosition().z;
					i++;
				}
				write(sim.getFormattedTime() + "," + buffer);
			}
		};
		tracker.setDt(10.0);
		sim.addExporter(tracker);
		
		// Uncomment to export movement data
		//sim.export();

		sim.preview();	
	}	
}
