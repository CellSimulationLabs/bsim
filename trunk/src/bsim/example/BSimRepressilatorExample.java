/**
 * BSimRepressilatorExample.java
 * 
 * Simulation file containing an example of how to use ODEs inside a particle.
 * The ODEs simulate a repressilator GRN inside a particle. GRNs are linked via an
 * external quorum coupling mechanism.
 *  
 * 14/09/09
 * Basic Implementation
 *  - ODE system (3x mRNA equations, 3x Protein equations, internal AI equation)
 *  - Bits of the framework for ODE/chemical field interaction from old BSim
 *  
 *  Garcia-Ojalvo: 'Modeling a synthetic multicellular clock: Repressilators coupled by quorum sensing'
 *  http://www.pnas.org/content/101/30/10955.full
 */

package bsim.example;

import java.util.Random;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimParticle;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimImageExporter;
import bsim.export.BSimLogger;
import bsim.mixin.BSimBrownianForceMixin;
import bsim.mixin.BSimRunTumbleMixin;
import bsim.ode.BSimOdeSolver;
import bsim.ode.BSimOdeSystem;

public class BSimRepressilatorExample {

	public static void main(String[] args) {

		/**
		 * Create a new simulation object
		 */
		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setSimulationTime(100);
		sim.setTimeFormat("0.00");
		sim.setBound(100,100,100);
		
		
		/**
		 * Extend BSimParticle to a Brownian motion/run-tumble bacterium with
		 * a repressilator GRN inside it.
		 */				
		class BSimRepressilatorParticle extends BSimParticle {
			private BSimRunTumbleMixin runTumbleMixin = new BSimRunTumbleMixin(sim, this);
			private BSimBrownianForceMixin brownianMixin = new BSimBrownianForceMixin(sim, this);
			
			protected QuorumRepressilator repGRN;
			protected double[] y, yNew;
			protected double cellWallDiffusion;			
	
			/*
			 * Constructor for a repressilator GRN bacterium.
			 */
			public BSimRepressilatorParticle(BSim sim, Vector3d position){
				super(sim, position, 1);
				
				//Create the parameters and initial conditions for the ODE system
				repGRN = new QuorumRepressilator();
				repGRN.generateBeta();
				y = repGRN.getICs();
			}

			/*
			 * Brownian motion, run and tumble.
			 * Iterate ODE system for one time-step.
			 */
			@Override
			public void action() {
				brownianMixin.brownianForce();
				runTumbleMixin.runTumble();
				yNew = BSimOdeSolver.rungeKutta45(repGRN, 1, y, sim.getDt());
				y = yNew;
			}
			
			/*
			 * Representation of the repressilator ODE system with quorum coupling
			 * TODO: quorum coupling when we have fields
			 * TODO: Simple implementation using averages as in the paper
			 */
			class QuorumRepressilator implements BSimOdeSystem{
				private int numEq = 7;	
				private double beta;
				private Random r = new Random();
				private double Se = 0;
				
				public double[] derivativeSystem(double x, double[] y) {
					double[] dy = new double[numEq];
					double alpha = 216, nExp = 2.0, eta = 2.0, ks0 = 1, ks1 = 0.01;
					
					// rate of change of a, b, c: mRNA transcribed from tetR, cI, lacI respectively
					dy[0] = -y[0] + alpha/(1 + Math.pow(y[5], nExp));
					dy[1] = -y[1] + alpha/(1 + Math.pow(y[3], nExp));
					//TODO: AUTOINDUCER TERM IN THE DY2 EQUATION *******************************************
					dy[2] = -y[2] + alpha/(1 + Math.pow(y[4], nExp));/*+ AI diffusion terms*/
					
					// rate of change of A, B, C: proteins corresponding to a,b,c above
					dy[3] = beta*(y[0]-y[3]);
					dy[4] = beta*(y[1]-y[4]);
					dy[5] = beta*(y[2]-y[5]);
					
					// rate of change of AI inside the cell
					// TODO: Re-implement external interaction *********************************************
					dy[6] = -ks0*y[6] + ks1*y[3]; //- eta*(y[6] - Se);
					return dy;
				}
				
				//add a method to get and set an external chemical.
				public void setExternalQuorumLevel(double externalQuorumField){
					Se = externalQuorumField;
				}
				
				/*
				 * Create the initial conditions of the ODE system
				 */
				public double[] getICs() {
					// Start synchronised
					double[] ics = {1, 5, 10, 12, 2, 6, 0};
					
					// Start random
//					double[] ics = new double[numEq];
//					for(int i =0;i<numEq-1;i++){
//						ics[i] = 10*r.nextDouble();
//					}
//					ics[6] = 0;
					
					return ics;
				}
				
				/*
				 * Parameter Beta - ratio between mRNA and protein lifetimes
				 */
				public void generateBeta(){
					// Garcia-Ojalvo paper part 1:
//					beta = 1.0 + 0.05*r.nextGaussian();
					
					// faster oscillations (lower amplitude):
					beta = 10.0 + 0.05*r.nextGaussian();
				}
		
				public int getNumEq() {
					return numEq;
				}
			}	
		}
		
		
		/**
		 * Create the vector of all bacteria used in the simulation 
		 */
		final Vector<BSimRepressilatorParticle> GRNParticles = new Vector<BSimRepressilatorParticle>();
		
		// Add bacteria to the vector
		while(GRNParticles.size() < 200) {		
			BSimRepressilatorParticle p = new BSimRepressilatorParticle(sim, 
										  new Vector3d(Math.random()*sim.getBound().x,
													   Math.random()*sim.getBound().y,
													   Math.random()*sim.getBound().z));
			if(!p.intersection(GRNParticles)) GRNParticles.add(p);
		}

		
		/** 
		 * Implement tick() on a BSimTicker and add the ticker to the simulation	  
		 */
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				
				for(BSimRepressilatorParticle p : GRNParticles) {
					p.action();		
					p.updatePosition();
				}
			}		
		});

		
		/** 
		 * Implement draw(Graphics) on a BSimDrawer and add the drawer to the simulation 
		 * 
		 * Draw the particles such that they are green for low levels of lacI mRNA and get more red
		 * as the level increases.
		 */
		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void draw(PGraphics3D p3d) {	
				for(BSimRepressilatorParticle p : GRNParticles) {
					p3d.pushMatrix();					
					Vector3d position = p.getPosition();
					p3d.translate((float)position.x, (float)position.y, (float)position.z);
					
					// Colour the particle by internal level of lacI mRNA (y[2])
					p3d.fill(4*(int)p.y[2],255 - 4*(int)p.y[2],0);
					
					p3d.sphere((float)p.getRadius());
					p3d.popMatrix();
				}			
			}
		});				

		
		/** 
		 * Implement before(), during() and after() on BSimExporters and add them to the simulation
		 */ 	
		/*
		 * BSimMovieExporter 
		 */			
//		BSimMovieExporter movieExporter = new BSimMovieExporter(sim, "results/BSim.mov");
//		movieExporter.setSpeed(2);
//		sim.addExporter(movieExporter);	
		/* 
		 * BSimImageExporter
		 */
		BSimImageExporter imageExporter = new BSimImageExporter(sim, "results");
		imageExporter.setDt(1);
		sim.addExporter(imageExporter);			

		
		/**
		 *  Create a logger: Time series of lacI mRNA concentration for every bacteria.
		 *  TODO: Will be simplified, but handy for testing at the moment
		 *  setDt() to reduce the amount of data.
		 */
		BSimLogger logger = new BSimLogger(sim, "results/repressilatorGRN.csv") {
			@Override
			public void before() {
				super.before();
				write("time,lacI_mRNA"); 
			}
			
			/*
			 * Print the level of lacI mRNA in one bacterium
			 */
			@Override
			public void during() {
				String o = sim.getTime();
				// All bacteria
				String lacI = "";
				for(BSimRepressilatorParticle p: GRNParticles){
					lacI = lacI + ","+p.y[2];
				}
				write(o+lacI);
				// One bacterium
				//write(o + "," + lacI+GRNParticles.elementAt(0).y[2]);
			}
		};
		//logger.setDt(0.5);
		sim.addExporter(logger);

		
		/**
		 * Call sim.preview() to preview the scene or sim.export() to set exporters working 
		 */
		sim.preview();

	}
}
