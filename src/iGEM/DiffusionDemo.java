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




/**Demonstration of BSim's geometric modelling capabilites
 * 
 * will model a teabag diffusing out into a teapot
 * 
 * @author tt9671
 *
 */
class DiffusionDemo{
	 
	public static void main(String args[]){
		
		int maxdepth=2;
		final double decayRate = 0.5; 
		final double diffusivity = 890;
		final double c = 12e8; // molecules

		
		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setTimeFormat("0.00");
		sim.setSolid(true,true,true);
		sim.setBound(100,100,100);
		
		double length = 100; 
		
		//Center of first octree
		Vector3d OTcentre = new Vector3d(50,50,50);		
		
		//Initial Octree
		final OctreeNode a = new OctreeNode(OTcentre,length);
		
		final BSimOctreeChemicalField testfield = new BSimOctreeChemicalField(sim,maxdepth,0.1);

		
		a.setKids(testfield);
		
		for(int i=0; i<8; i++){
			a.getsubNode(i).setKids(testfield);
		}
		
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				if(Math.random() < 0.1){
					Vector3d temp = new Vector3d(50,70,85);
					testfield.addQuantity(temp, 1e9);

				}
				a.decay(a,decayRate, 0.01);
				a.diffuse(a, diffusivity, 0.01);
			}
		});
		
	
		//dimensions for drawer
		int width=1024;
		int height=768;
		
		//creates a drawer
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
			
				draw(a,  Color.BLUE,(float)(255/c));	
				draw(a,(float)(20));
				
			
				
			}
		};			
		
		sim.setDrawer(drawer);
		
		
		sim.preview();

		
	}
	
}

