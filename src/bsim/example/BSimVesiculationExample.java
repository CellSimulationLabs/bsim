package bsim.example;

import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimDrawer;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimPngExporter;
import bsim.particle.BSimBacterium;
import bsim.particle.BSimVesicle;

public class BSimVesiculationExample {
	
	public static void main(String[] args) {

		BSim sim = new BSim();	
				
		final Vector<BSimVesicle> vesicles = new Vector<BSimVesicle>();
		final Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>();		
		final Vector<BSimBacterium> children = new Vector<BSimBacterium>();
		while(bacteria.size() < 10) {		
			BSimBacterium b = new BSimBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
			b.setRadius();
			b.setSurfaceAreaGrowthRate();
			b.setChildList(children);
			b.pVesicle(0.2);
			b.setVesicleList(vesicles);
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
				for(BSimVesicle vesicle : vesicles) {
					vesicle.action();	
					vesicle.updatePosition();		
				}
			}
		});
		
		BSimDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {							
				for(BSimBacterium b : bacteria) {
					draw(b,Color.GREEN);
				}
				for(BSimVesicle vesicle : vesicles)
					draw(vesicle,Color.RED);
			}
		};	
		sim.setDrawer(drawer);	
		
		sim.preview();
	}

}
