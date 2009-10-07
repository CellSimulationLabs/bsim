package bsim.example;

import java.awt.Color;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimLogger;
import bsim.particle.BSimBacterium;

public class BSimTrackingExample {

	public static void main(String[] args) {

		BSim sim = new BSim();
		sim.setBound(1000,1000,1000);
		sim.setSimulationTime(30);

		final BSimBacterium bacterium = new BSimBacterium(sim, new Vector3d(500,500,500));
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				bacterium.action();		
				bacterium.updatePosition();
			}
		});

		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {							
				draw(bacterium,Color.GREEN);
			}
		});	

		BSimLogger trackerXY = new BSimLogger(sim, "results/trackerXY.csv") {
			@Override
			public void during() {
				write(bacterium.getPosition().x+","+bacterium.getPosition().y);
			}
		};
		trackerXY.setDt(0.1);
		sim.addExporter(trackerXY);

		sim.export();

	}

}
