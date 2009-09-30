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
		sim.setSimulationTime(3);
				
		final Vector<BSimVesicle> vesicles = new Vector<BSimVesicle>();
		final Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>();		
		while(bacteria.size() < 30) {		
			BSimBacterium b = new BSimBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
			b.pVesicle(0.2);
			b.setVesicleList(vesicles);
			if(!b.intersection(bacteria)) bacteria.add(b);		
		}
		

		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(BSimBacterium b : bacteria) {
					b.action();		
					b.updatePosition();
				}
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
		
		BSimPngExporter pngExporter = new BSimPngExporter(sim, drawer, "results");
		pngExporter.setDt(0.5);
		sim.addExporter(pngExporter);	
		
		sim.preview();
	}

}
