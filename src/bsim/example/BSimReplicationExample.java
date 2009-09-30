package bsim.example;

import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.particle.BSimBacterium;

public class BSimReplicationExample {

	public static void main(String[] args) {


		BSim sim = new BSim();		
				
		class BSimReplicatingBacterium extends BSimBacterium {
			
			public BSimReplicatingBacterium(BSim sim, Vector3d position) {
				super(sim, position); // default radius is 1 micron			
			}
			
			@Override
			public void replicate() {
				radius = replicationRadius/2;
				BSimReplicatingBacterium child = new BSimReplicatingBacterium(sim, new Vector3d(position));		
				child.setRadiusGrowthRate(Math.random());
				child.setChildList(childList);
				childList.add(child);
			}
		}
		
		final Vector<BSimReplicatingBacterium> bacteria = new Vector<BSimReplicatingBacterium>();
		final Vector<BSimReplicatingBacterium> children = new Vector<BSimReplicatingBacterium>();
		while(bacteria.size() < 5) {		
			BSimReplicatingBacterium b = new BSimReplicatingBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
			b.setChildList(children);
			b.setRadiusGrowthRate(Math.random());
			if(!b.intersection(bacteria)) bacteria.add(b);		
		}

		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(BSimReplicatingBacterium b : bacteria) {
					b.action();		
					b.updatePosition();					
				}
				bacteria.addAll(children);
				children.clear();
			}		
		});


		BSimP3DDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {						
				for(BSimReplicatingBacterium b : bacteria) {
					draw(b, Color.GREEN);
				}			
			}
		};
		sim.setDrawer(drawer);				

		
		sim.preview();

	}




}
