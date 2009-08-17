package bsim.physics;

import java.util.Vector;

import bsim.BSimParameters;
import bsim.BSimScene;
import bsim.drawable.bacteria.BSimBacterium;
import bsim.drawable.bead.BSimBead;
import bsim.drawable.boundary.BSimSolidPlaneBoundary;
import bsim.physics.BSimCollisionPhysics.BSimCollisionPhysicsThread;



public class BSimFusionPhysics extends BSimPhysics{
	
	protected static BSimParameters params;
	public static int MAX_WORKER_THREADS = 1;
	public static int BOUNDARY_TYPE = -1;
	
	// Matrices used during sticking process
	protected double[][][] newForceMat = null;
	protected boolean[] fusionExists = null;
	protected boolean[][] collisionTypes = null;
	protected double[][] externalForces = null;
	protected double[][] vesiclesForcesBeads = null;
	
	private double reactForce = 0.0;
	
	
	public BSimFusionPhysics(BSimScene newScene, BSimParameters p){
		
		super(newScene);
		
		params = p;
		reactForce = params.getReactForce();
		
		//brownianForce = params.getReactForce();
		
		MAX_WORKER_THREADS = params.getNumOfThreads();
		
		scene.setReallocateNewFusionExists(true);
	}
	
	/*
	 * Update the postion of particles in the scene
	 */
	public void updateProperties() {
		int i, xStart, xEnd;
		Vector bacteria = scene.getBacteria();
		Vector beads = scene.getBeads();
		Vector solidBoundaries = scene.getSolidBoundaries();
		Vector vesicles = scene.getVesicles();

		int l = vesicles.size();
		int n = bacteria.size();
		int m = beads.size();
		int b = solidBoundaries.size();
			
		int tTotal = l;
		
		// Check to see if memory needs to be allocated
		if(scene.getReallocateNewFusionExists()){
			// Create the force matrix
			//useful for the memory allocation and the garbage collector work
			newForceMat = null;
			newForceMat = new double[l][m+b][3];
			//useful for the memory allocation and the garbage collector work
			fusionExists = null;
			fusionExists = new boolean[l];
			//useful for the memory allocation and the garbage collector work
			collisionTypes = null;
			collisionTypes = new boolean[m+b][2];
			//useful for the memory allocation and the garbage collector work
			externalForces = null;
			externalForces = new double[n+m][3];
			vesiclesForcesBeads = null;
			vesiclesForcesBeads = new double[m][3];
			
			// No new allocation required
			scene.setReallocateNewFusionExists(false);
		}
		
		// Ensure forces components starts from zero
		for(int k=0; k<m; k++){
			vesiclesForcesBeads[k][0]=0;
			vesiclesForcesBeads[k][1]=0;
			vesiclesForcesBeads[k][2]=0;
		}
		
			
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
			workerThreads[i] = new BSimFusionPhysicsThread(vesicles, bacteria, beads, solidBoundaries, newForceMat, xStart, xEnd);
			workerThreads[i].start();
		}
		
		// Wait for all threads to finish execution before continuing
		for(i=0; i<MAX_WORKER_THREADS; i++) {
			try{
				workerThreads[i].join();
			} catch (InterruptedException ignore) { }
		}
		
		//check if there is a fusion between two particle
		for(i=0; i<l; i++) {
			if(fusionExists[l]==true){
				vesicles.removeElementAt(l);
				scene.setReallocateNewFusionExists(true);
			}
		}
		
		scene.setVesiclesForcesBeads(vesiclesForcesBeads);
		
	}
	
	/**
	 * Worker thread used to calculate the forces over a partition of the full
	 * space.
	 */
	protected class BSimFusionPhysicsThread extends Thread {
		
		// Variables to hold references to bacteria, beads and the new force matrix
		Vector vesicles, bacteria, beads, solidBoundaries;
		double[][][] newForceMat;
		
		// The thread number and total number of threads
		int xStart, xEnd;
		
		
		/**
		 * General constructor.
		 */
		public BSimFusionPhysicsThread(Vector newVesicles, Vector newBacteria, Vector newBeads, 
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
			int l = vesicles.size();
			int n = bacteria.size();
			int m = beads.size();
			int obTotal = n+l+m;
			int fusionTotal = n+l;
			int b = solidBoundaries.size();
			BSimParticle partI, partJ;
			BSimSolidPlaneBoundary bdry;
			double[] bdryDist;
			double centreDist, edgeDist;
			double[] relativePos = new double[3];
			double reactionForce;
			BSimVesicle partVesicle;
			BSimBacterium partBacterium;
			//BSimBead partBead;
			
			// The internal force, generated by the particle
			double internalForce[] = {0.0, 0.0, 0.0};

			
			// Ensure all flags start as false
			for(int g=0; g<(n+m); g++){
				fusionExists[g] = false;
				collisionTypes[g][0] = false;
				collisionTypes[g][1] = false;
			}

			// Get2Ddist on each pair; the matrix is symmetrical, so only calculate
			// distance for one half, then replicate
			//the external cycle is only on the vesicles
			for (int i=xStart; i<xEnd; i++) {
				for (int j = 0; j<(n+l+m+b); j++) {
					if(i==j){
						continue;
					}
					//the first particle is always a vesicle
					partI=(BSimParticle)(bacteria.elementAt(i));
					
					// Decide from which population to get second particle
					if (j<n) partJ = (BSimParticle)(bacteria.elementAt(j));
					else if (j<n+l) partJ = (BSimParticle)(vesicles.elementAt(j-n));
					else if (j<n+l+m) partJ = (BSimParticle)(beads.elementAt(j-(n+l)));
					else partJ = null;
					
					if(j >= (n+l) ){					
						if(fusionExists[i]){
							//a fusion exists between the i vesicle and a bacteria or another vesicle
							//so we don t need to calculate the force matrix between the vesicle and the 
							//other non fusion particles
							break;
						}
						else{
							//the vesicle has no fusion with anything so we need to calculate the forces 
							//with the non fusing objects
							if(j >= (n+l+m) ){
								//the interaction is with a boundary
								bdry = (BSimSolidPlaneBoundary)(solidBoundaries.elementAt(j-(obTotal)));
								
								// Calculate the boundary distance and unit vector
								bdryDist = bdry.calcInfoFromBoundary(partI.getPosition());
								
								// Distance from edge of particle to boundary
								//TODO: check if is important the border width
								edgeDist = bdryDist[0] - (partI.getRadius()) - 5; // Boundaries are 4 microns in width
								
								relativePos[0] = bdryDist[1];
								relativePos[1] = bdryDist[2];
								relativePos[2] = bdryDist[3];
								
								
								reactionForce = getReactionForce(BOUNDARY_TYPE, partI.getType(), edgeDist);
								
								newForceMat[i][j-fusionTotal][0] = relativePos[0]*reactionForce;
								newForceMat[i][j-fusionTotal][1] = relativePos[1]*reactionForce;
								newForceMat[i][j-fusionTotal][2] = relativePos[2]*reactionForce;
								
								
								//fixed bug because one component could be zero and the other one not
								if (newForceMat[i][j-fusionTotal][0] != 0.0 || newForceMat[i][j-fusionTotal][1] != 0.0 || newForceMat[i][j-fusionTotal][2] != 0.0){
									// Force exists between a boundary so update flag
									collisionTypes[i][1] = true;
								}
							}
							else {
								//Bead Interaction
								
								centreDist = distBetweenPoints(partJ.getPosition(),partI.getPosition());
								edgeDist = centreDist - (partI.getRadius() + partJ.getRadius());

								// normalized vector from current particle to others
								relativePos[0] = (partI.getPosition()[0] - partJ.getPosition()[0])/centreDist;
								relativePos[1] = (partI.getPosition()[1] - partJ.getPosition()[1])/centreDist;
								relativePos[2] = (partI.getPosition()[2] - partJ.getPosition()[2])/centreDist;

								reactionForce = getReactionForce(partJ.getType(),partI.getType(),edgeDist);

								newForceMat[i][j-fusionTotal][0] = -relativePos[0]*reactionForce;
								newForceMat[i][j-fusionTotal][1] = -relativePos[1]*reactionForce;
								newForceMat[i][j-fusionTotal][2] = -relativePos[2]*reactionForce;
								
								//externalForces on the beads caused by the vesicles 
								// i don t know if there is there is a minus or a plus when u add this component
								vesiclesForcesBeads[j-(n+l)][0] = vesiclesForcesBeads[j-(n+l)][0]+newForceMat[i][j-fusionTotal][0];
								vesiclesForcesBeads[j-(n+l)][1] = vesiclesForcesBeads[j-(n+l)][1]+newForceMat[i][j-fusionTotal][1];
								vesiclesForcesBeads[j-(n+l)][2] = vesiclesForcesBeads[j-(n+l)][2]+newForceMat[i][j-fusionTotal][2];
							
								// Check for bacteria interactions
								if((j < n+l+m) &&
									(newForceMat[i][j-fusionTotal][0] != 0.0 || newForceMat[i][j-fusionTotal][1] != 0.0 || newForceMat[i][j-fusionTotal][2] != 0.0)){
									// Force exists between a bacteria so update flag
									collisionTypes[i][0] = true;
								}
							}
						
							// Resolve the forces for the current particle
							resolveExternalForces(newForceMat, i);
							
							// The current particle is a vesicle so call the required function
							partVesicle = (BSimVesicle)scene.getVesicles().elementAt(i);
							internalForce = partVesicle.runLogic(collisionTypes[i][0], collisionTypes[i][1]);
							linearMotion(partVesicle,internalForce, i);
							
						}
					}
					else{
						//calculating the distance between the centrePos
						centreDist = distBetweenPoints(partJ.getPosition(),partI.getPosition());
						//calculation the edge distance
						edgeDist = centreDist - (partI.getRadius() + partJ.getRadius());
						
						
						//edge distance less than zero
						if(edgeDist<0){
							
							//vesicle-bacteria fusion
							//first condition verify if there was a vesicle-bacteria fusion
							//second condition vesicle-bacteria interaction
							if( (!fusionExists[i]) && (j<n) ){
								fusionExists[i]=true;
								partBacterium = (BSimBacterium)scene.getBacteria().elementAt(j);
								partBacterium.addVesicleProperties(scene.getVesicles().elementAt(i));
								
							}
							
							//vesicle-vesicle fusion
							//first condition verify if there was a vesicle-bacteria fusion
							//second condition verify if there was a vesicle-vesicle fusion
							//third condition vesicle-vesicle interaction
							if( (!fusionExists[i]) && (!fusionExists[j-n]) && (j>=n) ){
								fusionExists[i]=true;
								partVesicle = (BSimVesicles)scene.getVesicles().elementAt(j-n);
								partVesicle.addVesicleProperties(scene.getVesicles().elementAt(i));
								
							}
							
						}
					}
				}	
			}			
		}


		/**
		 * Piecewise linear approximation to a potential function, modelling the force exerted on
		 * two particles during a collision
		 */
		private double getReactionForce(int type1, int type2, double d) {
			double wellWidth = 0.0, wellDepth = 0.0;
			
			if ((type1 == BOUNDARY_TYPE) && (type2 == BSimParticle.PART_VES)) {
				wellWidth = wellWidthVesBdry;
				wellDepth = wellDepthVesBdry;
			// Bacterium-Boundary interaction
			}else if (((type1 == BSimParticle.PART_BEAD) && (type2 == BSimParticle.PART_VES))){
					wellWidth = wellWidthVesBry;
					wellDepth = wellDepthVesBry;
			// Bead-Boundary interaction
			} else { System.err.println("Type error in getReactionForce()"); System.exit(1);}
			
			// Calculate reaction force
			if (d>wellWidth || d == 0) return 0.0;
			else if(d>(wellWidth/2.0)) return -wellDepth + (d-(wellWidth/2.0))*wellDepth/(wellWidth/2.0);
			else if(d>=0.0) return -(d*2.0*wellDepth/wellWidth);
			// TODO Make sure this is large enough based on timestep.
			else return d * reactForce;
		}
		
		
		/**
		 * Find the resultant (net) external 2D force on an particle
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
		 * Resolve forces and iterate the linear motion of an particle
		 */
		private void linearMotion(BSimParticle x, double[] internalForce, int index) {
			double[] totalForce = new double[3];
			double[] velocity = new double[3];
			double[] curPosition = x.getPosition();
			double[] newPosition= new double[3];

			// Calculate the total force for the particle
			totalForce[0] = externalForces[index][0] + internalForce[0];
			totalForce[1] = externalForces[index][1] + internalForce[1];
			totalForce[2] = externalForces[index][2] + internalForce[2];
			
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
