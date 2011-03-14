package iGEM;

import java.awt.Color;
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
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.BSimOctreeChemicalField;

import bsim.mesh.BSimFVMesh;
import bsim.geometry.BSimSphereMesh;
import bsim.geometry.BSimTriangle;
import bsim.geometry.tests.GeneralMeshTest;
import bsim.particle.BSimBacterium;

import bsim.draw.BSimDrawer;
import bsim.draw.BSimP3DDrawer;




 class OctreeFunc{
	
	public static void main(String args[]){
		//oh i don't know, definitely deleteable though: 
		OctreeFunc program = new OctreeFunc();
		
		//octree divide count
		int divcount =0; 
		
		//parameters
		int maxdepth = 2; 
		
		//New BSim preamble....
		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setTimeFormat("0.00");
		sim.setSolid(true,true,true);
		sim.setBound(100,100,100);
		
		//Mesh preamble for example
		String importPath = "C:\\Documents and Settings\\tt9671\\My Documents\\iGem\\obj files\\teapot.obj";

		Vector3d meshPos = new Vector3d(50,20,50);
		double meshScale = 25;
		BSimMesh newMesh;
		//make a sphere
	//	newMesh = new BSimSphereMesh(meshPos, meshScale, 2);
	//	final BSimMesh theMesh = newMesh;
		//end of sphere
		
		//make an imported teapot
		BSimOBJMesh mesh = new BSimOBJMesh();
		mesh.load(importPath);
		mesh.scale(meshScale);
		mesh.translateAbsolute(meshPos);
		newMesh = mesh;
		final BSimMesh theMesh = newMesh;
		//end of teapot

		//make a triangle mesh
	//	newMesh = program.new BSimTriMesh();
	//	final BSimMesh theMesh = newMesh;
		//end of triangle mesh
		
		
		//size of initial octree (same as sim)
		double length = 100; 
		
		//Center of first octree
		Vector3d OTcentre = new Vector3d(50,50,50);		
		
		//Initial Octree
		final OctreeNode a = new OctreeNode(OTcentre,length);

		//Create a new octree chemical field, max depth of 3 due to memory 	
		final BSimOctreeChemicalField OctField = new BSimOctreeChemicalField(sim,maxdepth);
					
		//Syntax - a is the octree, OctField is the chemical field
		a.setSpaceLookup(OctField);
		//Syntax - a is the octree, octfield is the chem field. 
		
		//splits initial field into 8
		(OctField.getOctree(OTcentre)).setKids(OctField);		
		
		//do all the faces, test for intersect
		
		
		
		
		OctField.FitOctreeFieldToMesh(theMesh, maxdepth);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Vector3d TestLocation2 = new Vector3d(50,50,50);				
		//Goes from a location to the appropriate record in the SpaceL lookup matrix
		System.out.print(OctField.getOctree(TestLocation2));
		System.out.print("\n");
		
		
		Vector3d TestLocation3 = new Vector3d(10,10,10);				
		System.out.print(OctField.getOctree(TestLocation3));
		System.out.print("\n");
		
		//can use this accessor method to set kids! amazing!
		//(OctField.getOctree(TestLocation3)).setKids(OctField);
		
		OctreeNode temp = OctField.getOctree(TestLocation3);	
		temp.visit(temp);
		System.out.print("\n");

		Vector3d TestLocation4 = new Vector3d(0,0,0);
		OctreeNode temp2 = OctField.getOctree(TestLocation4);	
		temp2.visit(temp2);
		System.out.print("\n");

		System.out.print("Amount of octree divisions: ");
		System.out.print(divcount);
		
		temp2.quantity = 255;
		
		//now just need to code a simple routine that tests a vector, and then splits it or not!
		
		
		//Need to split up ocree if it is intersected by mesh object
		
		
		
		
		
		
		
		
		OctField.diffuse();
		
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				OctField.diffuse();

			}	
			
		});
		
		//dimensions for drawer
		int width=1024;
		int height=768;
		
		//creates a drawer
		sim.setDrawer(new BSimP3DDrawer(sim, width,height) {
			
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
				draw(a, (float)(20));	
				
				//draw(theMesh, 0);
				
			}
		});					

		sim.preview();
		
		
		
		
		
		
		
}

	
/////////////////////////////
/////////////////////////////

//
	
	//troubleshooting triangle
	
		private class BSimTriMesh extends BSimMesh {
		
		public BSimTriMesh(){
			super();
			createMesh();
			cleanUp(false);
		}
		
		@Override
		protected void createMesh() {
			addVertex(50, 25, 25);
			addVertex(80, 50, 75);
			addVertex(50, 75, 25);
			
			addTriangle(0,1,2);
		}
	}

	
	
}