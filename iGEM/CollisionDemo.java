package iGEM;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Vector;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import processing.core.PConstants;
import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimLogger;
import bsim.export.BSimMovExporter;
import bsim.geometry.BSimCollision;
import bsim.geometry.BSimMesh;
import bsim.geometry.BSimMeshUtils;
import bsim.geometry.BSimOBJMesh;
import bsim.geometry.BSimSphereMesh;
import bsim.geometry.BSimTriangle;
import bsim.particle.BSimBacterium;
import bsim.particle.BSimVesicle;

public class CollisionDemo {
	
	public static void main(String[] args) {
	
		final BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setTimeFormat("0.00");
		sim.setSimulationTime(10);
		
		// External boundary is centred on 50,50,50
		sim.setBound(100,100,100);

		
		//final BSimOBJMesh theMesh = new BSimOBJMesh();
		//theMesh.load("C:\\My Dropbox\\_igem2010\\EclipseWS\\sbunny_scaled.obj");
		//theMesh.scale(75);
		//theMesh.translateAbsolute(new Vector3d(50, 50, 50));
		
		final BSimSphereMesh theMesh = new BSimSphereMesh(new Vector3d(50,50,50), 50, 3);		
		
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
				BSimCollision.collideAndReflect(position, p2, theMesh);
								
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
				
				BSimCollision.collideAndRepel(this, theMesh);
			}
		}
		
		
		final Vector<BSimRigidVesicle> vesicles = new Vector<BSimRigidVesicle>();
		final Vector<BSimCollidingBacterium> bacteria = new Vector<BSimCollidingBacterium>();		
		final Vector<BSimCollidingBacterium> children = new Vector<BSimCollidingBacterium>();
		
		while(bacteria.size() < 50) {			
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
			}
		});


		BSimP3DDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {							
				draw(theMesh, 0);
				p3d.noStroke();
				
				for(BSimCollidingBacterium b : bacteria) {
					draw(b,Color.GREEN);
				}
				for(BSimRigidVesicle vesicle : vesicles)
					draw(vesicle,Color.RED);
				
			}
			
			@Override
			public void draw(BSimMesh mesh, double normalScaleFactor){
				p3d.directionalLight(255, 255, 255, 1, 0, 0);
				p3d.fill(128,128,255,100);
				p3d.beginShape(PConstants.TRIANGLES);
				for(BSimTriangle t:mesh.getFaces()){
					vertex(mesh.getTCoords(t,0));
					vertex(mesh.getTCoords(t,1));
					vertex(mesh.getTCoords(t,2));
				}
				p3d.endShape();
				if(normalScaleFactor != 0){
					for(BSimTriangle t:mesh.getFaces()){
						vector(mesh.getTCentre(t),t.getNormal(),normalScaleFactor,(new Color(255,0,0,150)));
					}
				}
				p3d.noLights();
			}
		};	
		sim.setDrawer(drawer);
		
		BSimMovExporter movieExporter = new BSimMovExporter(sim, drawer, "bunny.mov");
		movieExporter.setSpeed(1);
		movieExporter.setDt(0.03);
		sim.addExporter(movieExporter);	
		
		BSimLogger logger = new BSimLogger(sim, System.currentTimeMillis() + ".csv") {
			public void before(){
				super.before();
				write(sim.getFormattedTime()+","+System.currentTimeMillis());
			}
			
			@Override
			public void during() {
			}
			
			public void after(){
				write(sim.getFormattedTime()+","+System.currentTimeMillis());
				super.after();
			}
		};
		sim.addExporter(logger);
		

		sim.preview();
	}
}
