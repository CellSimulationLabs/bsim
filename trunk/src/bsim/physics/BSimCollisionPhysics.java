/**
 * BSimCollisionPhysics.java
 *
 * Class that implements a physics engine for the simulation that uses a potential function
 * to model interactions between particles in the scene. The potential function allows for
 * both attractive (binding strength) and repulsive (collisions) forces to be included.
 *
 * Authors: Ian Miles
 *          Thomas Gorochowski
 *          Charlie Harrison
 *          Mattia Fazzini(Update)
 * Created: 13/08/2008
 * Updated: 12/08/2009
 */
package bsim.physics;

import java.util.Vector;

import bsim.BSimParameters;
import bsim.BSimScene;
import bsim.drawable.bacteria.BSimBacterium;
import bsim.drawable.bead.BSimBead;
import bsim.drawable.boundary.BSimBoundary;
import bsim.drawable.boundary.BSimSolidPlaneBoundary;


public class BSimCollisionPhysics extends BSimPhysics{

	/**
	 * Variables for interactive forces between particles; these govern the shape
	 * of the potential function in the getReactionForce() method
	 */
	protected double wellWidthBactBact;
	protected double wellDepthBactBact;

	protected double wellWidthBactBead;
	protected double wellDepthBactBead;

	protected double wellWidthBeadBead;
	protected double wellDepthBeadBead; 
	
	protected double wellWidthBeadBdry;
	protected double wellDepthBeadBdry; 
	
	protected double wellWidthBactBdry;
	protected double wellDepthBactBdry; 

	// Vector of resolved external force vectors on particles
	protected double[][] externalForces = null;
	protected boolean[][] collisionTypes = null;
	
	// Matrices used during calculations
	protected double[][][] newForceMat = null;
	
	protected static BSimParameters params;
	
	public static int MAX_WORKER_THREADS = 1;
	
	public static int BOUNDARY_TYPE = -1;
	
	private double reactForce = 0.0;
	
	private double[] magnStrength = {0.0, 0.0, 0.0};
	
	private double[][] vesiclesForcesBeads=null;
	
	/**
	 * Constructor for BSimCollisionPhysics class
	 */	
	public BSimCollisionPhysics(BSimScene newScene, BSimParameters p){
		
		super(newScene);
		params = p;
		
		wellWidthBactBact = params.getWellWidthBactBact();
		wellWidthBactBead = params.getWellWidthBactBead();
		wellWidthBeadBead = params.getWellWidthBeadBead();
		wellWidthBeadBdry = params.getWellWidthBeadBdry();
		wellWidthBactBdry = params.getWellWidthBactBdry();
		
		wellDepthBactBact = params.getWellDepthBactBact();
		wellDepthBactBead = params.getWellDepthBactBead();
		wellDepthBeadBead = params.getWellDepthBeadBead();
		wellDepthBeadBdry = params.getWellDepthBeadBdry();
		wellDepthBactBdry = params.getWellDepthBactBdry();
		
		reactForce = params.getReactForce();
		
		MAX_WORKER_THREADS = params.getNumOfThreads();
		
		scene.setReallocateNewForceMat(true);
	}
	
	
	/*
	 * Update the postion of particles in the scene
	 */
	public void updateProperties() {
		int i, xStart, xEnd;
		Vector bacteria = scene.getBacteria();
		Vector beads = scene.getBeads();
		Vector solidBoundaries = scene.getSolidBoundaries();

		int n = bacteria.size();
		int m = beads.size();
		int b = solidBoundaries.size();
		int tTotal = n+m;
		
		// Check to see if memory needs to be allocated
		if(scene.getReallocateNewForceMat()){
			// Create the force matrix
			//useful for the memory allocation and the garbage collector work
			newForceMat = null;
			newForceMat = new double[n+m][n+m+b][3];
			// Create the boolean set to represent the collision types
			//useful for the memory allocation and the garbage collector work
			collisionTypes = null;
			collisionTypes = new boolean[n+m][3];
			// External Forces
			//useful for the memory allocation and the garbage collector work
			externalForces = null;
			externalForces = new double[n+m][3];
			// No new allocation required
			scene.setReallocateNewForceMat(false);
		}
		
		//forces on the beads caused by the vesicles
		vesiclesForcesBeads=scene.getVesiclesForcesBeads();
			
		// Create array of worker threads
		Thread[] workerThreads = new Thread[MAX_WORKER_THREADS];
		
		// Create each of the worker threads and set them runing.
		for(i=0; i<MAX_WORKER_THREADS; i++) {
			
			// Calculate the start and end indexes for the partition
			xStart = (int)(tTotal / MAX_WORKER_THREADS) * i;
			if (i == MAX_WORKER_THREADS - 1) {
				xEnd = tTotal;
			}
			else {
				xEnd = (tTotal / MAX_WORKER_THREADS) * (i + 1);
			}
			
			// Create and start the actual threads with the required parameters
			workerThreads[i] = new BSimCollisionPhysicsThread(bacteria, beads, solidBoundaries, newForceMat, xStart, xEnd);
			workerThreads[i].start();
		}
		
		// Wait for all threads to finish execution before continuing
		for(i=0; i<MAX_WORKER_THREADS; i++) {
			try{
				workerThreads[i].join();
			} catch (InterruptedException ignore) { }
		}
	}

	
	/**
	 * Worker thread used to calculate the forces over a partition of the full
	 * space.
	 */
	protected class BSimCollisionPhysicsThread extends Thread {
		
		// Variables to hold references to bacteria, beads and the new force matrix
		Vector bacteria, beads, solidBoundaries;
		double[][][] newForceMat;
		
		// The thread number and total number of threads
		int xStart, xEnd;
		
		
		/**
		 * General constructor.
		 */
		public BSimCollisionPhysicsThread(Vector newBacteria, Vector newBeads, 
		                                  Vector newSolidBoundaries, 
		                                  double[][][] newNewForceMat, 
		                                  int newXStart, int newXEnd){
			
			// Update the internal variables
			xStart = newXStart;
			xEnd = newXEnd;
			bacteria = newBacteria;
			beads = newBeads;
			solidBoundaries = newSolidBoundaries;
			newForceMat = newNewForceMat;
		}
		
		
		/**
		 * Function to carry out the job of the worker thread. Works on a partition of
		 * the force matrix.
		 */
		public void run(){
			int n = bacteria.size();
			int m = beads.size();
			int obTotal = n + m;
			int b = solidBoundaries.size();
			double[] relativePos = new double[3];
			double centreDist, edgeDist;
			double reactionForce;
			BSimParticle partI, partJ;
			BSimBacterium partBac;
			BSimBead partBead;
			BSimSolidPlaneBoundary bdry;
			double[] bdryDist;
			
			// The internal force, generated by the particle
			double internalForce[] = {0.0, 0.0, 0.0};

			
			// Ensure all flags start as false
			for(int g=0; g<(n+m); g++){
				collisionTypes[g][0] = false;
				collisionTypes[g][1] = false;
				collisionTypes[g][2] = false;
			}

			// Get2Ddist on each pair; the matrix is symmetrical, so only calculate
			// distance for one half, then replicate
			for (int i=xStart; i<xEnd; i++) {
				for (int j = i; j<(n+m+b); j++) {
					// Distance between a point and itself is always 0
					if (i==j) {
						newForceMat[i][j][0] = 0.0;
						newForceMat[i][j][1] = 0.0;
						newForceMat[i][j][2] = 0.0;
					}
					// Find distance between two different points
					else {
						
						// Decide from which population to get first particle
						if (i<n) partI = (BSimParticle)(bacteria.elementAt(i));
						else partI = (BSimParticle)(beads.elementAt(i-n));
						

						// Decide from which population to get second particle
						if (j<n) partJ = (BSimParticle)(bacteria.elementAt(j));
						else if (j<n+m) partJ = (BSimParticle)(beads.elementAt(j-n));
						else partJ = null;
						
						//TODO maybe with boxes
						// Check to see if interaction is with boundary (ParticleI->Boundary)
						if(j >= n+m){
							
							bdry = (BSimSolidPlaneBoundary)(solidBoundaries.elementAt(j-obTotal));
							
							// Calculate the boundary distance and unit vector
							bdryDist = bdry.calcInfoFromBoundary(partI.getPosition());
							
							// Distance from edge of particle to boundary
							//TODO: check if is important the border width
							edgeDist = bdryDist[0] - (partI.getRadius()) - 5; // Boundaries are 4 microns in width
							
							relativePos[0] = bdryDist[1];
							relativePos[1] = bdryDist[2];
							relativePos[2] = bdryDist[3];
							
							
							reactionForce = getReactionForce(BOUNDARY_TYPE,partI.getType(),edgeDist);
							
							newForceMat[i][j][0] = relativePos[0]*reactionForce;
							newForceMat[i][j][1] = relativePos[1]*reactionForce;
							newForceMat[i][j][2] = relativePos[2]*reactionForce;
							
							//fixed bug because one component could be zero
							if (newForceMat[i][j][0] != 0.0 || newForceMat[i][j][1] != 0.0 || newForceMat[i][j][2] != 0.0){
								// Force exists between a boundary so update flag
								collisionTypes[i][2] = true;
							}
						}
						//here there are the new 3d concepts
						else {
							// Bacteria or Bead Interaction
							
							centreDist = distBetweenPoints(partJ.getPosition(),partI.getPosition());
							edgeDist = centreDist - (partI.getRadius() + partJ.getRadius());

							// normalized vector from current particle to others
							relativePos[0] = (partI.getPosition()[0] - partJ.getPosition()[0])/centreDist;
							relativePos[1] = (partI.getPosition()[1] - partJ.getPosition()[1])/centreDist;
							relativePos[2] = (partI.getPosition()[2] - partJ.getPosition()[2])/centreDist;

							reactionForce = getReactionForce(partJ.getType(),partI.getType(),edgeDist);

							newForceMat[i][j][0] = -relativePos[0]*reactionForce;
							newForceMat[i][j][1] = -relativePos[1]*reactionForce;
							newForceMat[i][j][2] = -relativePos[2]*reactionForce;

							newForceMat[j][i][0] = -newForceMat[i][j][0];
							newForceMat[j][i][1] = -newForceMat[i][j][1];
							newForceMat[j][i][2] = -newForceMat[i][j][2];
						
						
							// Check for bacteria interactions
							if((j < n) && 
							   (newForceMat[j][i][0] != 0.0 || newForceMat[j][i][1] != 0.0 || newForceMat[j][i][2] != 0.0)){
								// Force exists between a bacteria so update flag
								collisionTypes[j][0] = true;
								collisionTypes[i][0] = true;
							}
						
							// Check for bead interactions
							if((j >= n) && 
							   (newForceMat[j][i][0] != 0.0 || newForceMat[j][i][1] != 0.0	|| newForceMat[j][i][2] != 0.0)){
								// Force exists between a bacteria so update flag
								collisionTypes[j][1] = true;
								collisionTypes[i][1] = true;
							}
						}
					}
				}
			
				// Resolve the forces for the current particle
				
				
				if(i>=n && i<n+m){
					//case if we are resolving a force for a beads
					//considering the component of the vesicles for the beads
					resolveExternalForces(newForceMat, i, vesiclesForcesBeads[i-n]); 
				}
				else{
					//in all the other cases
					resolveExternalForces(newForceMat, i); 
				}
				// Find the internal force of the particle
				if(i < n) {
					// The current particle is a bacteria so call the required function
					partBac = (BSimBacterium)scene.getBacteria().elementAt(i);
					internalForce = partBac.runLogic(collisionTypes[i][0], collisionTypes[i][1], collisionTypes[i][2]);
					linearMotion(partBac,internalForce, i);

					magnStrength = params.getMagnStrength();

				}
				else{
					// The current particle is a bead so call the required functions (no internal force)
					internalForce[0] = 0.0;
					internalForce[1] = 0.0;
					internalForce[2] = 0.0;
					
					magnStrength[0] = 0.0;
					magnStrength[1] = 0.0;
					magnStrength[2] = 0.0;
					
					linearMotion((BSimBead)scene.getBeads().elementAt(i-n),internalForce, i-n+n); 
				}
			}
		}


		/**
		 * Piecewise linear approximation to a potential function, modelling the force exerted on
		 * two particles during a collision
		 */
		private double getReactionForce(int type1, int type2, double d) {
			double wellWidth = 0.0, wellDepth = 0.0;
			
			// Select the right parameters according to particle type
			// Bacterium-Bead interaction
			if (((type1 == BSimParticle.PART_BACT) && (type2 == BSimParticle.PART_BEAD)) ||
			    ((type2 == BSimParticle.PART_BACT) && (type1 == BSimParticle.PART_BEAD))) {
				wellWidth = wellWidthBactBead;
				wellDepth = wellDepthBactBead;
			// Bacterium-Bacterium interaction
			} else if ((type1 == BSimParticle.PART_BACT) && (type2 == BSimParticle.PART_BACT)) {
				wellWidth = wellWidthBactBact;
				wellDepth = wellDepthBactBact;
			// Bead-Bead interaction
			} else if ((type1 == BSimParticle.PART_BEAD) && (type2 == BSimParticle.PART_BEAD)) {
				wellWidth = wellWidthBeadBead;
				wellDepth = wellDepthBeadBead;
			// Bacterium-Boundary interaction
			}else if (((type1 == BSimParticle.PART_BACT) && (type2 == BOUNDARY_TYPE)) ||
				    ((type1 == BOUNDARY_TYPE) && (type2 == BSimParticle.PART_BACT))) {
					wellWidth = wellWidthBactBdry;
					wellDepth = wellDepthBactBdry;	
			// Bead-Boundary interaction
			}else if (((type1 == BSimParticle.PART_BEAD) && (type2 == BOUNDARY_TYPE)) ||
				    ((type1 == BOUNDARY_TYPE) && (type2 == BSimParticle.PART_BEAD))) {
					wellWidth = wellWidthBeadBdry;
					wellDepth = wellDepthBeadBdry;
			} else { System.err.println("Type error in getReactionForce()"); System.exit(1);}
			
			// Calculate reaction force
			if (d>wellWidth || d == 0) return 0.0;
			else if(d>(wellWidth/2.0)) return -wellDepth + (d-(wellWidth/2.0))*wellDepth/(wellWidth/2.0);
			else if(d>=0.0) return -(d*2.0*wellDepth/wellWidth);
			// TODO Make sure this is large enough based on timestep.
			else return d * reactForce;
		}
		
		
		/**
		 * Find the resultant (net) external 3D force on an particle
		 */
		private void resolveExternalForces(double[][][] forceMat, int i) {
			// Find the size of the force matrix
			int forceMatWidth = forceMat[0].length;
			double forceXSum, forceYSum, forceZSum;

			// For the chosen element, sum the components
			forceXSum = 0;
			forceYSum = 0;
			forceZSum = 0;
			

			for (int j = 0; j < forceMatWidth; j++) {
				forceXSum += forceMat[i][j][0];
				forceYSum += forceMat[i][j][1];
				forceZSum += forceMat[i][j][2];
			}
			externalForces[i][0] = forceXSum;
			externalForces[i][1] = forceYSum;
			externalForces[i][2] = forceZSum;
		}
		
		/**
		 * Find the resultant (net) external 3D force on an particle
		 */
		private void resolveExternalForces(double[][][] forceMat, int i, double[] vesiclesForces) {
			// Find the size of the force matrix
			int forceMatWidth = forceMat[0].length;
			double forceXSum, forceYSum, forceZSum;

			// For the chosen element, sum the components
			forceXSum = 0 + vesiclesForces[0];
			forceYSum = 0 + vesiclesForces[1];
			forceZSum = 0 + vesiclesForces[2];
			

			for (int j = 0; j < forceMatWidth; j++) {
				forceXSum += forceMat[i][j][0];
				forceYSum += forceMat[i][j][1];
				forceZSum += forceMat[i][j][2];
			}
			externalForces[i][0] = forceXSum;
			externalForces[i][1] = forceYSum;
			externalForces[i][2] = forceZSum;
		}
		
		
		/**
		 * Resolve forces and iterate the linear motion of an particle
		 */
		private void linearMotion(BSimParticle x, double[] internalForce, int index) {
			double[] totalForce = new double[3];
			double[] velocity = new double[3];
			double[] curPosition = x.getPosition();
			double[] newPosition= new double[3];

			// Calculate the total force for the particle
			totalForce[0] = externalForces[index][0] + internalForce[0] + magnStrength[0];
			totalForce[1] = externalForces[index][1] + internalForce[1] + magnStrength[1];
			totalForce[2] = externalForces[index][2] + internalForce[2] + magnStrength[2];
			
			// Calculate the velocity using Stoke's rule
			velocity = force2Velocity3D(totalForce, x.getRadius(), params.getViscosity());
			
			//System.out.println("RRAAHHH" + velocity[0] + " " + velocity[1] + "    " + totalForce[0] + " " + totalForce[1]);
			
			// Calculate the new position and update the particle
			newPosition[0] = curPosition[0] + 
			(velocity[0]*(double)scene.getDtSec());
			newPosition[1] = curPosition[1] + 
			(velocity[1]*(double)scene.getDtSec());
			newPosition[2] = curPosition[2] + 
			(velocity[2]*(double)scene.getDtSec());
			x.setPosition(newPosition);
		}
		
		
		/**
		 * Returns the 3D velocity vector of an particle by solving Stokes' Law; force applied
		 * to an particle is assumed to equal drag.
		 * N.B. Units are S.I.; e.g. for F in micro Newtons, velocity is in microns per metre
		 */
		public double[] force2Velocity3D(double[] F, double r, double visc) {
			double[] v = new double[3];
			v[0] = F[0]/(6.0*Math.PI*r*visc);
			v[1] = F[1]/(6.0*Math.PI*r*visc);
			v[2] = F[2]/(6.0*Math.PI*r*visc);
			return v;
		}
		
		
		/**
		 * Calculate the distance between two points.
		 */
		protected double distBetweenPoints(double[] p1, double[] p2) {

			// Length = sqrt(a^2 + b^2 + z^2)
			return Math.sqrt(Math.pow(p1[0] - p2[0], 2) + Math.pow(p1[1] - p2[1], 2)+ Math.pow(p1[2] - p2[2], 2));
		}
		
		

	}	
}