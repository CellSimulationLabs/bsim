package bsim.example;

import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;

import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimLogger;
import bsim.particle.BSimBacterium;

public class BSimRotationalDiffusionTest {
	
	public static void main(String[] args) {

		BSim sim = new BSim();
		sim.setDt(0.01);
		sim.setSimulationTime(10);
				
		final int n = 100;		
		final Vector3d startDirection = new Vector3d(1,1,1);		
		final Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>();
		for(int i=0;i<n;i++) {
			BSimBacterium bacterium = new BSimBacterium(sim, new Vector3d(50,50,50));
			bacterium.pEndRunElse(0);
			bacterium.setDirection(new Vector3d(startDirection));
			bacteria.add(bacterium);		
		}
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(BSimBacterium bacterium : bacteria) {
					bacterium.action();		
					bacterium.updatePosition();
				}
			}
		});
		
		
		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {	
				for(BSimBacterium bacterium : bacteria)
					draw(bacterium,Color.GREEN);
			}
		});	
		
		sim.addExporter(new BSimLogger(sim, "results/bacteriumTheta" + System.currentTimeMillis() +  ".csv") {
			@Override
			public void during() {				
				String o = "";
				for(int i=0;i<n;i++) {
					if(i>0) o += ","; 
					o += bacteria.get(i).getDirection().angle(startDirection)+"";
				}
				write(o);		
			}
		});
		
		
		sim.export();
		
//		radius = 1;
//		visc = 2.7e-3;
//		rotationalStokesCoefficient = 8*pi*radius^3*visc;
//		boltzmann = 1.38e-23;
//		temperature = 305;
//		dt = 0.01;
//		t = 10;
//		n = t/dt;
//
//		% we have actually measured mod_pi(abs(theta)
//		x = bacteriumTheta;
//
//		plot(0:dt:t,x);
//		figure;
//		plot(0:dt:t,(4*boltzmann*temperature*(0:dt:t)/rotationalStokesCoefficient)*1e18);
//		hold all;
//		y = (x.^2)';
//		plot(0:dt:t,mean(y));
//		legend('theory','experiment');
		


		
	}

}
