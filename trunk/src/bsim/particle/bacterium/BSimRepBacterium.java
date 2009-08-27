/**
 * BSimGRNBacterium.java
 * 
 * Class that represents a bacterium that will by default move randomly in the
 * environment. The bacterium incorporates generic GRN activity specified in an 
 * class which may or not affect the behaviour
 * interacts with the chemical field...
 * should this then be a subclass that defines the interactions with environment?
 * 
 * Author: Antoni Matyjaszkiewicz
 * Created: 13/08/2009
 */

package bsim.particle.bacterium;

import java.awt.Graphics;
import java.util.Random;

import javax.vecmath.Vector3d;

import bsim.BSimParameters;
import bsim.BSimScene;
import bsim.ode.BSimOdeSolver;
import bsim.ode.BSimOdeSystem;

public class BSimRepBacterium extends BSimBacterium {

	protected QuorumRepressilator repGRN;
	protected double[] y, yNew;
	protected double cellWallDiffusion;

	/*
	 * Constructor should have a parameter for an ODE? 
	 * can then be passed from an inheriting class to the solver (here)
	 */
	/**
	 * General constructor.
	 */
	public BSimRepBacterium(Vector3d newPosition, double newRadius,
			Vector3d newDirection, double newForceMagnitudeDown, double newForceMagnitudeUp,
			int newState, double newTumbleSpeed, int newRemDt, 
			BSimScene newScene, BSimParameters newParams) {

		// Call the parent constructor with the basic properties	
		super(newPosition, newRadius, newDirection,newForceMagnitudeDown, newForceMagnitudeUp,
				newState, newTumbleSpeed, newRemDt, newScene, newParams);

		repGRN = new QuorumRepressilator();
		repGRN.generateBeta();
		y = repGRN.getICs();
		
		cellWallDiffusion = 2;

	}

	public void action() {

		double h = super.scene.getDtSec();
		int tIndex = super.scene.getTimeStep();
		double tNow = tIndex * h;
		double externalChem, deltaChem;
		externalChem = super.scene.getQuorumField().getConcentration(this.getPosition());
		// Get the external chem field level for the GRN ode system later on
		repGRN.setExternalQuorumLevel(externalChem);
		// Solve the ode system
		yNew = BSimOdeSolver.rungeKutta45(repGRN, tNow, y, h);
		y = yNew;
		
		// Adjust the external chemical field
		deltaChem = externalChem - y[6];
		//System.out.println(deltaChem);
		if( deltaChem < 0){
			super.scene.getQuorumField().addChemical(cellWallDiffusion*(-deltaChem), this.getPosition());
		}else if(deltaChem > 0){
			super.scene.getQuorumField().removeChemical(cellWallDiffusion*deltaChem, this.getPosition());
		}
		
		super.action();

	}

	/**
	 * Redraws the bacterium.
	 */
	public void redraw(Graphics g) {
	}
	
	/**
	 * Returns the current y vector.
	 */
	public double[] getY(){
		return y;
	}

	public class QuorumRepressilator implements BSimOdeSystem {
		private int numEq = 7;	
		private double beta;
		private Random r = new Random();
		private double Se = 0;
		
		public double[] derivativeSystem(double x, double[] y) {
			double[] dy = new double[numEq];
			double alpha = 216, nExp = 2.0, eta = 2.0, ks0 = 1, ks1 = 0.01;
			// rate of change of a, b, c
			dy[0] = -y[0] + alpha/(1 + Math.pow(y[5], nExp));
			dy[1] = -y[1] + alpha/(1 + Math.pow(y[3], nExp));
			dy[2] = -y[2] + alpha/(1 + Math.pow(y[4], nExp));/*+ diffusion terms*/
			// rate of change of A, B, C
			dy[3] = beta*(y[0]-y[3]);
			dy[4] = beta*(y[1]-y[4]);
			dy[5] = beta*(y[2]-y[5]);
			// rate of change of autoinducer (inside the cell!)
			dy[6] = -ks0*y[6] + ks1*y[3] - eta*(y[6] - Se);
			return dy;
		}
		
		//add a method to get and set an external chemical.
		public void setExternalQuorumLevel(double externalQuorumField){
			Se = externalQuorumField;
		}
		
		public double[] getICs() {
			//double[] ics = {1, 5, 10, 12, 2, 6};
			double[] ics = new double[numEq];
			for(int i =0;i<numEq-1;i++){
				ics[i] = 10*r.nextDouble();
			}
			ics[6] = 0;
			return ics;
		}
		
		public void generateBeta(){
			beta = 1.0 + 0.05*r.nextGaussian();
		}

		public int getNumEq() {
			return numEq;
		}
	}

}
