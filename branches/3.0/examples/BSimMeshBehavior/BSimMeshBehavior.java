package BSimMeshBehavior;

import java.awt.Color;
import java.io.File;
import java.util.*;
import javax.vecmath.Vector3d;
import processing.core.PGraphics3D;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import bsim.*;
import bsim.draw.*;
import bsim.export.*;
import bsim.particle.*;
import bsim.geometry.*;

/**
 * BSimMeshBehavior.java
 * 
 * Simulation that illustrates how a mesh can be used to alter the behavior of bacteria. This could
 * be used in practice to represent spatially varying parameters.
 */
class BSimMeshBehavior{
	static BSimOBJMesh theMesh;
	static boolean exportData;
	static String filePath;
	public static void main(String args[]){
		
		/**
		 * Create a new simulation object
		 */
		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setTimeFormat("0.00");
		sim.setSimulationTime(10);
		sim.setSolid(true,true,true);
		sim.setBound(100,100,100);
		sim.setSimulationTime(30);
		
		/**
		 * Choose if you require output (update this path to the location that results should be stored)
		 */
		exportData = true;
		if (exportData)
			filePath = BSimUtils.generateDirectoryPath("/Users/Tom/Desktop/Results/" + BSimUtils.timeStamp() +"/");

		/**
		 * Load the mesh (ensure only one load is uncommented)
		 */
		theMesh = new BSimOBJMesh();
		theMesh.load("Mesh_Torus.obj");
		
		/**
		 * Place mesh in the correct location for the simulation
		 */
		theMesh.scale(45);
		theMesh.translateAbsolute(new Vector3d(50.0, 80.0, 50.0));
		
		/**
		 * A type of bacteria where motility speed is altered by location to the mesh
		 */
		class BSimMeshSensitiveBacterium extends BSimBacterium{	
			public boolean inside = false;	
			public Vector3d oldPos;
			public BSimMeshSensitiveBacterium(BSim sim, Vector3d position) {
				super(sim, position);
				oldPos = new Vector3d(position.x, position.y, position.z);
			}
			@Override
			public void updatePosition(){
				// Copy old position
				oldPos.x = position.x; oldPos.y = position.y; oldPos.z = position.z;
				// Update position
				super.updatePosition();
				// Check for crossing and update state if necessary
				if (BSimCollision.collideAndCross(oldPos, position, theMesh)) inside = !inside;
			}
		}
		
		/**
		 * Create a set of bacteria for the simulation
		 */
		final Vector<BSimMeshSensitiveBacterium> bacteria = new Vector<BSimMeshSensitiveBacterium>();
		while(bacteria.size() < 2000) {		
			BSimMeshSensitiveBacterium p = new BSimMeshSensitiveBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, 
				10.0, Math.random()*sim.getBound().z));
			bacteria.add(p);		
		}
		
		/**
		 * Create the ticker for the simulation
		 */
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(BSimMeshSensitiveBacterium b : bacteria) {
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
				for(BSimMeshSensitiveBacterium b : bacteria) {
					if (b.inside) draw(b,Color.GREEN);
					else draw(b,Color.BLUE);
				}
				draw(theMesh, 0);	
			}
		};			
		sim.setDrawer(drawer);
		
		/**
		 * Set up the exporter for video
		 */
		BSimMovExporter movExporter = new BSimMovExporter(sim, drawer, filePath + "BSimLogicMovie.mov");
		movExporter.setSpeed(1);
		movExporter.setDt(0.1);
		if(exportData) sim.addExporter(movExporter);
		
		/**
		 * Set up the exporter for an image at a set time point
		 */
		BSimPngExporter pngExporter = new BSimPngExporter(sim, drawer, filePath) {
			@Override
			public void during() {
				System.out.println("Output a picture...");
				BufferedImage img = new BufferedImage(drawer.getWidth(), drawer.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g = img.createGraphics();                            
                drawer.draw(g);
                g.dispose();
                try {
                        File file = new File(directory + "/" + sim.getFormattedTime() + ".png");
                        ImageIO.write(img, "png", file);
                } catch (IOException e) {
                        e.printStackTrace();
                }
			}		
		};
		pngExporter.setDt(5.0);
		sim.addExporter(pngExporter);
		
		/**
		 * Either export data or preview
		 */
		if(exportData) sim.export();
		else sim.preview();
	}	
}

