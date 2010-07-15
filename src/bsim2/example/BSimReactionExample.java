package bsim.example;

import java.awt.Color;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimLogger;
import bsim.particle.BSimBacterium;

public class BSimReactionExample {
	
	public static void main(String[] args) {
	
	BSim sim = new BSim();
	sim.setSimulationTime(1);
	sim.setBound(10,10,10);
	sim.setTemperature(0);
	
	final BSimBacterium a = new BSimBacterium(sim, new Vector3d(0,5,5));
	a.setDirection(new Vector3d(-1,0,0));
	a.pEndRunElse(0);
	final BSimBacterium b = new BSimBacterium(sim, new Vector3d(10,5,5));
	b.setDirection(new Vector3d(1,0,0));
	b.pEndRunElse(0);
	sim.setTicker(new BSimTicker() {
		@Override
		public void tick() {
			a.action();
			b.action();
			
			double od = a.outerDistance(b);
			if(od < 0) {
				double d = a.distance(b);
				a.logReaction(b, 1);
			}
			
			a.updatePosition();
			b.updatePosition();
		}
	});
	
	sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
		@Override
		public void scene(PGraphics3D p3d) {							
			draw(a,Color.RED);
			draw(b,Color.GREEN);
		}
	});	
	
	BSimLogger logger = new BSimLogger(sim, "results/reaction.csv") {
		@Override
		public void during() {
			// equilibrium distance d(1) = 2/e = 0.7357;
			write(sim.getFormattedTime()+","+a.distance(b));
		}
	};
	sim.addExporter(logger);	
	
	sim.preview();
	
	}
}

