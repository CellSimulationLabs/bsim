package BSimMultiThreaded;

import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.particle.BSimBacterium;

public class BSimMultiThreaded {
	
	public static void main(String[] args) {

		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setSimulationTime(10);
		sim.setTimeFormat("0.00");
		sim.setBound(100,100,100);
			
		final Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>();		
		while(bacteria.size() < 100) {		
			BSimBacterium b = new BSimBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
			if(!b.intersection(bacteria)) bacteria.add(b);		
		}
		
		
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(BSimBacterium b : bacteria) {
					b.action();		
					b.updatePosition();
				}
			}
		});
		
		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {
				for(BSimBacterium b : bacteria) {
					draw(b,Color.YELLOW);
				}
			}
		});	
		
		sim.preview();
	}

}
