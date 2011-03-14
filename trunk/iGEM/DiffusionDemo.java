package iGEM;

import java.awt.Color;

import bsim.geometry.BSimCollision;
import bsim.geometry.BSimMesh;
import bsim.geometry.BSimOBJMesh;
import bsim.geometry.BSimSphereMesh;
import bsim.geometry.BSimMeshUtils;
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
import bsim.geometry.tests.GeneralMeshTest;
import bsim.particle.BSimBacterium;
import bsim.particle.BSimVesicle;

import bsim.draw.BSimDrawer;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimMovExporter;
import bsim.export.BSimPngExporter;




/**Demonstration of BSim's geometric modelling capabilites
 * 
 * will model a teabag diffusing out into a teapot
 * 
 * @author tt9671
 *
 */
class DiffusionDemo{
	 
	public static void main(String args[]){
		
		final int maxdepth=4;
		final double decayRate = 0.3; 
		final double diffusivity = 2000;
		final double c = 12e8; // molecules

		
		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setTimeFormat("0.00");

		sim.setSimulationTime(100);
		sim.setSolid(true,true,true);
		sim.setBound(100,100,100);
		
		double length = 100; 
		
	//	Vector3d meshPos = new Vector3d(50,50,25);
	//	double meshScale = 19;
	//	BSimMesh newMesh;
		//make a sphere
	//	newMesh = new BSimSphereMesh(meshPos, meshScale, 2);
	//	final BSimMesh theMesh = newMesh;
		
		
		final BSimOBJMesh theMesh = new BSimOBJMesh();
		theMesh.load("prototype1.obj");
		theMesh.scale(15);
		theMesh.translateAbsolute(new Vector3d(50, 25, 50));
		
		//Center of first octree
		Vector3d OTcentre = new Vector3d(50,50,50);		
		
		//Initial Octree
		final BSimOctreeField a = new BSimOctreeField(OTcentre,length);
		
		//final BSimOctreeChemicalField testfield = new BSimOctreeChemicalField(sim,maxdepth,0.1);
		
	//	a.setSpaceLookup(testfield);
		
		//(testfield.getOctree(OTcentre)).setKids(testfield);	
		
		//testfield.FitOctreeFieldToMesh(theMesh, maxdepth);
		//
		
		a.setKids();
		a.getsubNode(0).setKids();
		

		
		
		Vector3d location1 = new Vector3d(12,90,12);
		
		a.nodeFinder(a, location1).setColor(Color.CYAN);
		
		System.out.print (a.nodeFinder(a, location1));
		System.out.print("\n ");
		System.out.print (location1);
		System.out.print("\n ");
		System.out.print (a.nodeFinder(a,location1).getCentre());
		System.out.print("\n ");
		System.out.print (a.nodeFinder(a,location1).getDepth());
		
	//	for(int i=0; i<maxdepth; i++){
	//	a.setNodestoMesh(theMesh, a);}
		
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				
				/*
				if(Math.random() < 0.5){
					
					OctreeNode temp = a.NodeFinder(a,maxdepth);
					
					temp.quantity+=1e8;

				}*/
				a.decay(a,decayRate, 0.01);
				a.diffuse(a, diffusivity, 0.01,maxdepth);
			}
		});
		
	
		//dimensions for drawer
		int width=1024;
		int height=768;
		
		//creates a drawe5r
		BSimP3DDrawer drawer = new BSimP3DDrawer(sim, width,height) {

		
			
			public float rot = -1.20f;
			
			@Override
			public void scene(PGraphics3D p3d) {	
				p3d.scale(0.60f);
				//sets sim to rotate lazily
				p3d.rotateY(rot * (float)(Math.PI/10));
				p3d.rotateX(rot * (float)(Math.PI/15));
				rot += 0.02;
				p3d.translate(-50, -50, -50);
				
				//Draw calls for the octree and the mesh
			
			//	draw(a,  Color.BLUE,(float)(255/c));	
				draw(a,(float)(20));
			//	draw(theMesh, 0);
				
			
				
			}
		};			
		
		sim.setDrawer(drawer);
		BSimPngExporter PNGExp = new BSimPngExporter(sim,drawer,"diffEx");
		
		sim.addExporter(PNGExp);
		
		//sim.addExporter(logger);
		
		//sim.export(); 
		
		sim.preview();

		
	}
	
}

