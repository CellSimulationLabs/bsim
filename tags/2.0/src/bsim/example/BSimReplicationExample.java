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
						
		final Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>();
		final Vector<BSimBacterium> children = new Vector<BSimBacterium>();
		while(bacteria.size() < 1) {		
			BSimBacterium b = new BSimBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
			b.setRadius();
			b.setSurfaceAreaGrowthRate(9);
			b.setChildList(children);
			bacteria.add(b);		
		}

		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(BSimBacterium b : bacteria) {
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
				for(BSimBacterium b : bacteria) {
					draw(b, Color.GREEN);
				}			
			}
		};
		sim.setDrawer(drawer);				

	
		sim.preview();

	}
}
