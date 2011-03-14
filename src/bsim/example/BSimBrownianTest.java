package bsim.example;

import java.util.Calendar;
import java.util.Vector;

import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimTicker;
import bsim.export.BSimLogger;
import bsim.particle.BSimParticle;

/**
 * Tests whether the magnitude of the Brownian force is correct
 */
public class BSimBrownianTest {
	
	public static void main(String[] args) {

		BSim sim = new BSim();
		double bx = 1000;
		double by = 1000;
		double bz = 1000;
		sim.setBound(bx,by,bz);
		sim.setSimulationTime(10);
		
		
		class BSimBrownianParticle extends BSimParticle {
			public BSimBrownianParticle (BSim sim, Vector3d position, double radius) {
				super(sim, position, radius);
			}
		}
				
		final int n = 100;		
		final Vector<BSimBrownianParticle> particles = new Vector<BSimBrownianParticle>();
		for(int i=0;i<n;i++) particles.add(new BSimBrownianParticle(sim, new Vector3d(bx/2,by/2,bz/2), 0.02));
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(BSimBrownianParticle particle : particles) {
					particle.action();		
					particle.updatePosition();
				}
			}
		});
		
		
		sim.addExporter(new BSimLogger(sim, "results/particleX" + System.currentTimeMillis() + ".csv") {
			private double[] x = new double[n];
			private Vector<Vector3d> lastPosition = new Vector<Vector3d>();
			double dx;
			@Override
			public void before() {
				super.before();
				for(int i=0;i<n;i++) {		
					x[i] = particles.get(i).getPosition().x;
					lastPosition.add(new Vector3d(particles.get(i).getPosition()));
				}
			}			
			@Override
			public void during() {	
				String o = "";
				for(int i=0;i<n;i++) {					
					dx = particles.get(i).getPosition().x - lastPosition.get(i).x;
					x[i] += dx;
					lastPosition.set(i, new Vector3d(particles.get(i).getPosition()));
					
					if(i>0) o += ","; 					
					o += x[i]+"";
				}
				write(o);		
			}
		});
		
		sim.export();
		
//		radius = 0.02;
//		visc = 2.7e-3;
//		stokesCoefficient = 6*pi*radius*visc;
//		boltzmann = 1.38e-23;
//		temperature = 305;
//		dt = 0.01;
//		t = 10;
//		n = t/dt;
//		x0 = 500;
//
//		x = particleX;
//
//		plot(0:dt:t,x);
//		figure;
//		plot(0:dt:t,(2*boltzmann*temperature*(0:dt:t)/stokesCoefficient)*1e18);
//		hold all;
//		xx = ((x-x0).^2)';
//		plot(0:dt:t,mean(xx));
//		legend('theory','experiment');		
		
	}

}
