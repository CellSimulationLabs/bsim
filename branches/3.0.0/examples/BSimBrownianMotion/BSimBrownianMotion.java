package BSimBrownianMotion;

import java.util.Vector;

import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimTicker;
import bsim.BSimUtils;
import bsim.export.BSimLogger;
import bsim.particle.BSimParticle;

/**
 * Tests whether the magnitude of the Brownian force is correct
 */
public class BSimBrownianMotion {
	
	/*********************************************************
	 * Simulation Definition
	 *********************************************************/
	public static void main(String[] args) {

		/*********************************************************
		 * Create a new simulation object and set up simulation settings
		 */
		BSim sim = new BSim();
		double bx = 1000;
		double by = 1000;
		double bz = 1000;
		sim.setBound(bx,by,bz);
		sim.setSimulationTime(10);
		
		/*********************************************************
		 * Set up the particles
		 */		
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
		
		/*********************************************************
		 * Create a new directory for the simulation results
		 */
		String resultsDir = BSimUtils.generateDirectoryPath("./results/");			

		/*********************************************************
		 * Set up exporters
		 */
		sim.addExporter(new BSimLogger(sim, resultsDir + "particleX" + System.currentTimeMillis() + ".csv") {
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
	}

}
