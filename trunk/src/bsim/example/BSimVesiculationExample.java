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
		sim.setSimulationTime(10);
				
		final Vector<BSimVesicle> vesicles = new Vector<BSimVesicle>();
		final BSimBacterium bacterium = new BSimBacterium(sim, new Vector3d(50,50,50));
		bacterium.setVesicleList(vesicles);
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				bacterium.action();		
				bacterium.updatePosition();
				for(BSimVesicle vesicle : vesicles) 
					vesicle.updatePosition();				
			}
		});
		
		BSimDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {							
				draw(bacterium,Color.GREEN);
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
