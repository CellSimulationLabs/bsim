package BSimMeshMobility;

/**
 * BSimMeshMobility.java
 * 
 * Simulation that illustrates the effect that complex microscale environments can have on bacterial
 * motility. Work derived from the BCCS-Bristol iGEM 2010 project 'argEcoli', see the following URL
 * for further details:
 *                            http://2010.igem.org/Team:BCCS-Bristol
 */

import java.awt.Color;
import java.io.File;
import java.util.*;
import javax.vecmath.Vector3d;
import processing.core.PGraphics3D;

import bsim.*;
import bsim.draw.*;
import bsim.export.*;
import bsim.particle.*;
import bsim.geometry.*;

class BSimMeshMobility {
	static BSimOBJMesh theMesh;
	public static void main(String args[]){
		
		/**
		 * Create a new simulation object
		 */
		BSim sim = new BSim();		
		sim.setDt(0.2);
		sim.setTimeFormat("0.00");
		sim.setSimulationTime(1000.0);
		sim.setSolid(true,true,true);
		sim.setBound(100000,100000,100000);

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
		theMesh.scale(55000);
		theMesh.translateAbsolute(new Vector3d(50000,50000,50000));
		
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
		while(bacteria.size() < 1000) {		
			BSimCollidingBacterium b = new BSimCollidingBacterium(sim, new Vector3d(50000.0,50000.0,50000.0));
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
		BSimLogger tracker = new BSimLogger(sim, "results.csv") {
			@Override
			public void before() {
				int i = 1;
				super.before();
				String buffer = new String();
				buffer = "time(seconds)";
				for(BSimCollidingBacterium b : bacteria) {
					buffer = buffer + ",Bac" + i;
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
					buffer = buffer + Math.sqrt(Math.pow(50000.0-b.getPosition().x,2) + 
						Math.pow(50000.0-b.getPosition().y,2) + 
						Math.pow(50000.0-b.getPosition().z,2));
					i++;
				}
				write(sim.getFormattedTime() + "," + buffer);
			}
		};
		tracker.setDt(1.0);
		sim.addExporter(tracker);
		sim.export();

		// sim.preview();	
	}	
}
