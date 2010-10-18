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
class TeapotDemo{
	 
	public static void main(String args[]){
		
		//parameters
		int maxdepth = 3; 
		final double c = 12e8; // molecules
		final double decayRate = 0.9;
		final double diffusivity = 890; // (microns)^2/sec

		//New BSim preamble....
		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setTimeFormat("0.00");
		sim.setSolid(true,true,true);
		sim.setBound(100,100,100);
		sim.setSimulationTime(25);
		//Mesh preamble for example
		String importPath1 = "sphere.obj";
		String importPath2 = "teapot.obj";

		Vector3d meshPos1 = new Vector3d(50,50,50);
		Vector3d meshPos2 = new Vector3d(50,50,50);
		
		double meshScale1 = 10;
		double meshScale2 = 25;
		
		BSimMesh newBag;
		BSimMesh newTeapot;
		
		BSimOBJMesh Bag = new BSimOBJMesh();
		BSimOBJMesh Teapot = new BSimOBJMesh();
		Bag.load(importPath1);
		Teapot.load(importPath2);
		Bag.scale(meshScale1);
		Teapot.scale(meshScale2);
		Bag.translateAbsolute(meshPos1);
		Teapot.translateAbsolute(meshPos2);
		newBag = Bag;
		newTeapot = Teapot; 
		final BSimMesh theBag = newBag;
		final BSimMesh theTeapot = newTeapot;
		
		
		
		//size of initial octree (same as sim)
		double OctBagLength = 25;
		double OctPotLength = 100; 
		
		//Center of first octree
		Vector3d OctBagCentre = new Vector3d(50,50,50);
		Vector3d OctPotCentre = new Vector3d(50,50,50);
		
		//Initial Octree
		final OctreeNode OctBag = new OctreeNode(OctBagCentre,OctBagLength);
		final OctreeNode OctPot = new OctreeNode(OctPotCentre,OctPotLength);
		
		final BSimOctreeChemicalField OctBagField = new BSimOctreeChemicalField(sim,maxdepth,decayRate);
		final BSimOctreeChemicalField OctPotField = new BSimOctreeChemicalField(sim,maxdepth,decayRate);

		
		
		//Syntax - a is the octree, OctField is the chemical field
		OctBag.setSpaceLookup(OctBagField);
		OctPot.setSpaceLookup(OctPotField);
		
		//Syntax - a is the octree, B is the chem field. 
		
		//splits initial field into 8
		(OctBagField.getOctree(OctBagCentre)).setKids(OctBagField);		
		(OctPotField.getOctree(OctPotCentre)).setKids(OctPotField);		
		
		//OctBagField.FitOctreeFieldToMesh(theBag, maxdepth);

		OctPotField.FitOctreeFieldToMesh(theTeapot, maxdepth);
		
		class BSimRigidVesicle extends BSimVesicle{
			
			public BSimRigidVesicle(BSim sim, Vector3d position, double radius) {
				super(sim, position, radius); // default radius is 1 micron
			}
			
			@Override
			public void updatePosition() {
				
				Vector3d velocity = new Vector3d();
				velocity.scale(1/stokesCoefficient(), force); // pN/(micrometers*Pa sec) = micrometers/sec
				
				Vector3d p2 = new Vector3d();	
				p2.scaleAdd(sim.getDt(), velocity, position);
				
				// Because collisions etc depend on p2
				BSimCollision.collideAndReflect(position, p2, theTeapot);
								
				position.set(p2);
				force.set(0,0,0);
				
				if(position.x > sim.getBound().x) xAbove();
				if(position.x < 0) xBelow();
				if(position.y > sim.getBound().y) yAbove();
				if(position.y < 0) yBelow();
				if(position.z > sim.getBound().z) zAbove();
				if(position.z < 0) zBelow();
			}
		}
		
		class BSimCollidingBacterium extends BSimBacterium{			

			public BSimCollidingBacterium(BSim sim, Vector3d position) {
				super(sim, position); // default radius is 1 micron
			}
			
			@Override
			public void vesiculate() {
				double r = vesicleRadius();
				vesicleList.add(new BSimRigidVesicle(sim, new Vector3d(position), r));
				setRadiusFromSurfaceArea(getSurfaceArea()-surfaceArea(r));
			}
			
			@Override
			public void replicate() {
				setRadiusFromSurfaceArea(surfaceArea(replicationRadius)/2);
				BSimCollidingBacterium child = new BSimCollidingBacterium(sim, new Vector3d(position));
				child.setRadius(radius);
				child.setSurfaceAreaGrowthRate(surfaceAreaGrowthRate);
				child.setChildList(childList);
				/* Overwrite to allow inheritance of other properties */
				childList.add(child);
			}	

			@Override
			public void action(){
				super.action();
				if (Math.random() < 0.1)
					OctPotField.addQuantity(position, 5); //used to be 1e9
				BSimCollision.collideAndRepel(this, theTeapot);
			}
		}
		
		
		
		
		
		final Vector<BSimRigidVesicle> vesicles = new Vector<BSimRigidVesicle>();
		final Vector<BSimCollidingBacterium> bacteria = new Vector<BSimCollidingBacterium>();		
		final Vector<BSimCollidingBacterium> children = new Vector<BSimCollidingBacterium>();
		
		
		
		
		while(bacteria.size() < 5) {			
			BSimCollidingBacterium b = new BSimCollidingBacterium(sim, new Vector3d((Math.random()-0.5)*25+50,
																	(Math.random()-0.5)*25+50,
																	(Math.random()-0.5)*25+50));
			
			b.setRadius();
			b.setSurfaceAreaGrowthRate();
			b.setChildList(children);
			b.pVesicle(0.01);
			b.setVesicleList(vesicles);
			if(!b.intersection(bacteria)) bacteria.add(b);	
		}	
		
		
		
		
	
		
		//do all the faces, test for intersect
		
		
		
		
		
		
		final Color BagColor = Color.cyan;
		final Color PotColor = Color.pink; 
		
		
		//set central octree to have a high concentration
		OctBagField.setConc(OctBagCentre, 100); 
		
		//System.out.print(OctBagField.getOctree(OctBagCentre).quantity);
		
		
		
		
		
		
		
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(BSimCollidingBacterium b : bacteria) {
					b.action();		
					b.updatePosition();
				}
				bacteria.addAll(children);
				children.clear();
				for(BSimRigidVesicle vesicle : vesicles) {
					vesicle.action();	
					vesicle.updatePosition();		
				}
				OctPot.decay(OctPot,decayRate, 0.01);
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
				//draw(OctBag, (float)(20));
				
				draw(OctPot, (float)(255/c));	
				
				
				for(BSimBacterium p : bacteria) draw(p, Color.GREEN);
				
				//draw(theBag, 0);
				draw(theTeapot, 0);
				
			}
		};			
		
		sim.setDrawer(drawer);
		
		BSimMovExporter movieExporter = new BSimMovExporter(sim, drawer , "teapot.mov");
		movieExporter.setSpeed(1);
		movieExporter.setDt(0.01);
		sim.addExporter(movieExporter);	
		
		//sim.export();
		
		
		sim.preview();
	 

	 
	 
	 
 } //end of main
	
}//end of class