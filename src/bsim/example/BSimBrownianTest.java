package bsim.example;

import java.util.Vector;

import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimTicker;
import bsim.export.BSimLogger;
import bsim.particle.BSimVesicle;

public class BSimBrownianTest {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		BSim sim = new BSim();
		sim.setBound(10000,10000,10000);
		sim.setSimulationTime(10);
				
		final int n = 100;		
		final Vector<BSimVesicle> vesicles = new Vector<BSimVesicle>();
		for(int i=0;i<n;i++) vesicles.add(new BSimVesicle(sim, new Vector3d(50,50,50), 0.02));
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(BSimVesicle vesicle : vesicles) {
					vesicle.action();		
					vesicle.updatePosition();
				}
			}
		});
		
		
		sim.addExporter(new BSimLogger(sim, "results/vesicleX.csv") {
			@Override
			public void during() {	
				String o = "";
				for(int i=0;i<n;i++) {
					if(i>0) o += ","; 
					o += vesicles.get(i).getPosition().x+"";
				}
				write(o);		
			}
		});
		
		sim.export();
//		Import into MATLAB
//		radius = 0.02;
//		visc = 2.7e-3;
//		stokesCoefficient = 6*pi*radius*visc;
//		boltzmann = 1.38e-23;
//		temperature = 305;
//		dt = 0.01;
//		t = 10;
//		n = t/dt;
//		x0 = 50;
//
//		x = data;
//
//		plot(0:dt:t,x);
//		figure;
//		plot(0:dt:t,(2*boltzmann*temperature*(0:dt:t)/stokesCoefficient)*1e18);
//		hold all;
//		y = ((x-x0).^2)';
//		plot(0:dt:t,mean(y));
//		legend('theory','experiment');
//
//		mean(x(n+1,:)) % should be 0
//		mean((x(n+1,:)-x0).^2) % should be as below
//		2*boltzmann*temperature*t/stokesCoefficient*1e18
		
	}

}
