/**
 * BSimCollisionPhysics.java
 *
 * Class that implements a physics engine for the simulation that uses a potential function
 * to model interactions between objects in the scene. The potential function allows for
 * both attractive (binding strength) and repulsive (collisions) forces to be included.
 *
 * Authors: Ian Miles
 *          Thomas Gorochowski
 *          Charlie Harrison
 * Created: 13/08/2008
 * Updated: 24/08/2008
 */


//Define the location of the class in the bsim package
package bsim.physics;

//Import the bsim packages used
import bsim.*;
import bsim.object.*;

//Standard packages required by the application
import java.util.*;


public class BSimCollisionPhysics extends BSimPhysics{

	/**
	 * Variables for interactive forces between objects; these govern the shape
	 * of the potential function in the getReactionForce() method
	 */
	protected double wellWidthBactBact;
	protected double wellDepthBactBact;

	protected double wellWidthBactPart;
	protected double wellDepthBactPart;

	protected double wellWidthPartPart;
	protected double wellDepthPartPart; 
	
	protected double wellWidthPartBdry;
	protected double wellDepthPartBdry; 
	
	protected double wellWidthBactBdry;
	protected double wellDepthBactBdry; 

	// Vector of resolved external force vectors on objects
	protected double[][] externalForces;
	protected boolean[][] collisionTypes;
	
	// Matrices used during calculations
	protected double[][][] newForceMat;
	
	protected static BSimParameters params;
	
	public static int MAX_WORKER_THREADS = 1;
	
	public static int BOUNDARY_TYPE = -1;
	
	private boolean allocMem = true;
	
	private double reactForce = 0.0;
	
	
	/**
	 * Constructor for BSimCollisionPhysics class
	 */	
	public BSimCollisionPhysics(BSimScene newScene, BSimParameters p){
		
		super(newScene);
		params = p;
		
		wellWidthBactBact = params.getWellWidthBactBact();
		wellWidthBactPart = params.getWellWidthBactPart();
		wellWidthPartPart = params.getWellWidthPartPart();
		wellWidthPartBdry = params.getWellWidthPartBdry();
		wellWidthBactBdry = params.getWellWidthBactBdry();
		
		wellDepthBactBact = params.getWellDepthBactBact();
		wellDepthBactPart = params.getWellDepthBactPart();
		wellDepthPartPart = params.getWellDepthPartPart();
		wellDepthPartBdry = params.getWellDepthPartBdry();
		wellDepthBactBdry = params.getWellDepthBactBdry();
		
		reactForce = params.getReactForce();
		
		MAX_WORKER_THREADS = params.getNumOfThreads();
		
		allocMem = true;
	}
	
	
	/*
	 * Update the postion of objects in the scene
	 */
	public void updateProperties() {
		int i, xStart, xEnd;
		Vector bacteria = scene.getBacteria();
		Vector particles = scene.getParticles();
		Vector solidBoundaries = scene.getSolidBoundaries();

		int n = bacteria.size();
		int m = particles.size();
		int b = solidBoundaries.size();
		int tTotal = n+m;
		
		// Check to see if memory needs to be allocated
		if(allocMem){
			// Create the force matrix
			newForceMat = new double[n+m][n+m+b][2];
			// Create the boolean set to represent the collision types
			collisionTypes = new boolean[n+m][3];
			// External Forces
			externalForces = new double[n+m][2];
			// No new allocation required
			allocMem = false;
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
			workerThreads[i] = new BSimCollisionPhysicsThread(bacteria, particles, solidBoundaries, newForceMat, xStart, xEnd);
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
		
		// Variables to hold references to bacteria, particles and the new force matrix
		Vector bacteria, particles, solidBoundaries;
		double[][][] newForceMat;
		
		// The thread number and total number of threads
		int xStart, xEnd;
		
		
		/**
		 * General constructor.
		 */
		public BSimCollisionPhysicsThread(Vector newBacteria, Vector newParticles, 
		                                  Vector newSolidBoundaries, 
		                                  double[][][] newNewForceMat, 
		                                  int newXStart, int newXEnd){
			
			// Update the internal variables
			xStart = newXStart;
			xEnd = newXEnd;
			bacteria = newBacteria;
			particles = newParticles;
			solidBoundaries = newSolidBoundaries;
			newForceMat = newNewForceMat;
		}
		
		
		/**
		 * Function to carry out the job of the worker thread. Works on a partition of
		 * the force matrix.
		 */
		public void run(){
			int n = bacteria.size();
			int m = particles.size();
			int obTotal = n + m;
			int b = solidBoundaries.size();
			double[] relativePos = new double[2];
			double centreDist, edgeDist;
			double reactionForce;
			BSimObject objectI, objectJ;
			BSimBacterium objectBac;
			BSimParticle objectPart;
			BSimBoundary objectBdry;
			double[] bdryDist;
			
			// The internal force, generated by the object
			double internalForce[] = {0.0, 0.0};
			
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
					}
					// Find distance between two different points
					else {
						
						// Decide from which population to get first object
						if (i<n) objectI = (BSimObject)(bacteria.elementAt(i));
						else objectI = (BSimObject)(particles.elementAt(i-n));
						

						// Decide from which population to get second object
						if (j<n) objectJ = (BSimObject)(bacteria.elementAt(j));
						else if (j<n+m) objectJ = (BSimObject)(particles.elementAt(j-n));
						else objectJ = null;
						
						// Check to see if interaction is with boundary (ObjectI->Boundary)
						if(j >= n+m){
							
							objectBdry = (BSimBoundary)(solidBoundaries.elementAt(j-obTotal));
							
							// Calculate the boundary distance and unit vector
							bdryDist = calcDistFromBoundary(objectBdry.getP1(), objectBdry.getP2(), objectI.getCentrePos());
							
							// Distance from edge of object to boundary
							edgeDist = bdryDist[0] - (objectI.getSize()/2) - 5; // Boundaries are 4 micros in width
							
							relativePos[0] = bdryDist[1];
							relativePos[1] = bdryDist[2];
							
							reactionForce = getReactionForce(BOUNDARY_TYPE,objectI.getType(),edgeDist);
							
							newForceMat[i][j][0] = relativePos[0]*reactionForce;
							newForceMat[i][j][1] = relativePos[1]*reactionForce;

							if (newForceMat[i][j][0] != 0.0){
								// Force exists between a boundary so update flag
								collisionTypes[i][2] = true;
							}
						}
						else {
							// Bacteria or Particle Interaction
							
							centreDist = distBetweenPoints(objectJ.getCentrePos(),objectI.getCentrePos());
							edgeDist = centreDist - (objectI.getSize() + objectJ.getSize())/2;

							// normalised vector from current object to others
							relativePos[0] = (objectI.getCentrePos()[0] - objectJ.getCentrePos()[0])/centreDist;
							relativePos[1] = (objectI.getCentrePos()[1] - objectJ.getCentrePos()[1])/centreDist;

							reactionForce = getReactionForce(objectJ.getType(),objectI.getType(),edgeDist);

							newForceMat[i][j][0] = -relativePos[0]*reactionForce;
							newForceMat[i][j][1] = -relativePos[1]*reactionForce;

							newForceMat[j][i][0] = -newForceMat[i][j][0];
							newForceMat[j][i][1] = -newForceMat[i][j][1];
						
						
							// Check for bacteria interactions
							if((j < n) && 
							   (newForceMat[j][i][0] != 0.0 || newForceMat[j][i][1] != 0.0)){
								// Force exists between a bacteria so update flag
								collisionTypes[j][0] = true;
								collisionTypes[i][0] = true;
							}
						
							// Check for particle interactions
							if((j >= n) && 
							   (newForceMat[j][i][0] != 0.0 || newForceMat[j][i][1] != 0.0)){
								// Force exists between a bacteria so update flag
								collisionTypes[j][1] = true;
								collisionTypes[i][1] = true;
							}
						}
					}
				}
			
				// Resolve the forces for the current object
				resolveExternalForces(newForceMat, i); 
				
				// Find the internal force of the object
				if(i < n) {
					// The current object is a bacteria so call the required function
					objectBac = (BSimBacterium)scene.getBacteria().elementAt(i);
					internalForce = objectBac.runLogic(collisionTypes[i][0], collisionTypes[i][1], collisionTypes[i][2]);
					linearMotion(objectBac,internalForce, i);
				}
				else{
					// The current object is a particle so call the required functions (no internal force)
					internalForce[0] = 0.0;
					internalForce[1] = 0.0;
					linearMotion((BSimParticle)scene.getParticles().elementAt(i-n),internalForce, i-n+n); 
				}
			}
		}


		/**
		 * Piecewise linear approximation to a potential function, modelling the force exerted on
		 * two objects during a collision
		 */
		private double getReactionForce(int type1, int type2, double d) {
			double wellWidth = 0.0, wellDepth = 0.0;
			
			// Select the right parameters according to object type
			// Bacterium-Particle interaction
			if (((type1 == BSimObject.OBTYPE_BACT) && (type2 == BSimObject.OBTYPE_PART)) ||
			    ((type2 == BSimObject.OBTYPE_BACT) && (type1 == BSimObject.OBTYPE_PART))) {
				wellWidth = wellWidthBactPart;
				wellDepth = wellDepthBactPart;
			// Bacterium-Bacterium interaction
			} else if ((type1 == BSimObject.OBTYPE_BACT) && (type2 == BSimObject.OBTYPE_BACT)) {
				wellWidth = wellWidthBactBact;
				wellDepth = wellDepthBactBact;
			// Particle-Particle interaction
			} else if ((type1 == BSimObject.OBTYPE_PART) && (type2 == BSimObject.OBTYPE_PART)) {
				wellWidth = wellWidthPartPart;
				wellDepth = wellDepthPartPart;
			// Bacterium-Boundary interaction
			}else if (((type1 == BSimObject.OBTYPE_BACT) && (type2 == BOUNDARY_TYPE)) ||
				    ((type1 == BOUNDARY_TYPE) && (type2 == BSimObject.OBTYPE_BACT))) {
					wellWidth = wellWidthBactBdry;
					wellDepth = wellDepthBactBdry;	
			// Particle-Boundary interaction
			}else if (((type1 == BSimObject.OBTYPE_PART) && (type2 == BOUNDARY_TYPE)) ||
				    ((type1 == BOUNDARY_TYPE) && (type2 == BSimObject.OBTYPE_PART))) {
					wellWidth = wellWidthPartBdry;
					wellDepth = wellDepthPartBdry;
			} else { System.err.println("Type error in getReactionForce()"); System.exit(1);}
			
			// Calculate reaction force
			if (d>wellWidth || d == 0) return 0.0;
			else if(d>(wellWidth/2.0)) return -wellDepth + (d-(wellWidth/2.0))*wellDepth/(wellWidth/2.0);
			else if(d>=0.0) return -(d*2.0*wellDepth/wellWidth);
			// TODO Make sure this is large enough based on timestep.
			else return d * reactForce;
		}
		
		
		/**
		 * Find the resultant (net) external 2D force on an object
		 */
		private void resolveExternalForces(double[][][] forceMat, int i) {
			// Find the size of the force matrix
			int forceMatWidth = forceMat[0].length;
			double forceXSum, forceYSum;

			// For the chosen element, sum the components
			forceXSum = 0;
			forceYSum = 0;

			for (int j = 0; j < forceMatWidth; j++) {
				forceXSum += forceMat[i][j][0];
				forceYSum += forceMat[i][j][1];
			}
			externalForces[i][0] = forceXSum;
			externalForces[i][1] = forceYSum;
		}
		
		
		/**
		 * Resolve forces and iterate the linear motion of an object
		 */
		private void linearMotion(BSimObject x, double[] internalForce, int index) {
			double[] totalForce = new double[2];
			double[] velocity = new double[2];
			double[] curPosition = x.getPosition();
			double[] newPosition= new double[2];

			// Calculate the total force for the object
			totalForce[0] = externalForces[index][0] + internalForce[0];
			totalForce[1] = externalForces[index][1] + internalForce[1];
			
			// Calculate the velocity using Stoke's rule
			velocity = force2Velocity2D(totalForce, x.getRadius(), params.getViscosity());
			
			//System.out.println("RRAAHHH" + velocity[0] + " " + velocity[1] + "    " + totalForce[0] + " " + totalForce[1]);
			
			// Calculate the new position and update the object
			newPosition[0] = curPosition[0] + 
			(velocity[0]*(double)scene.getDtSec());
			newPosition[1] = curPosition[1] + 
			(velocity[1]*(double)scene.getDtSec());
			x.setPosition(newPosition);
		}
		
		
		/**
		 * Returns the 2D velocity vector of an object by solving Stokes' Law; force applied
		 * to an object is assumed to equal drag.
		 * N.B. Units are S.I.; e.g. for F in micro Newtons, velocity is in microns per metre
		 */
		public double[] force2Velocity2D(double[] F, double r, double visc) {
			double[] v = new double[2];
			v[0] = F[0]/(6.0*Math.PI*r*visc);
			v[1] = F[1]/(6.0*Math.PI*r*visc);
			return v;
		}
		
		
		/**
		 * Calculate the distance between two points.
		 */
		protected double distBetweenPoints(double[] p1, double[] p2) {

			// Length = sqrt(a^2 + b^2)
			return Math.sqrt(Math.pow(p1[0] - p2[0], 2) + Math.pow(p1[1] - p2[1], 2));
		}
		
		
		/**
		 * Calculates the distance from the boundary. If the point falls within the line
		 * segment then perpendicular distance returned, otherwise shortest distance to end
		 * points is returned. An array of double[3] is returned with indexes equalling
		 * 0 = the shortest distance,
		 * 1 = x component of unit vector between points
		 * 2 = y component of unit vector between points
		 */
		protected double[] calcDistFromBoundary(double[] p1, double[] p2, double[] p3) {
			double u, d1, d2;
			double[] p4 = new double[2];
			double[] c = new double[3];
			double [] uv = {0.0, 0.0};
			
			// Calculate distance on the line segment
			u = (((p3[0] - p1[0])*(p2[0] - p1[0])) +
			        ((p3[1] - p1[1])*(p2[1] - p1[1]))) / Math.pow((p2[0] - p1[0]) + (p2[1] - p1[1]), 2.0);
			
			if(Double.isNaN(u)){
				u = 0.0;
			}

			// Check to see if point falls on line segment
			if(u >= 0 || u <= 1 ) {

				// Find the point on the line segment
				p4[0] = p1[0] + (u * (p2[0] - p1[0]));
				p4[1] = p1[1] + (u * (p2[1] - p1[1]));
				
				c[0] = distBetweenPoints(p3, p4);
				uv = unitVecBetweenPoints(p3, p4);
				c[1] = uv[0];
				c[2] = uv[1];				
				
				// Return the perpendicular distance
				return c;
			}
			else{
				// Calculate the distances to end points
				d1 = distBetweenPoints(p1, p3);
				d2 = distBetweenPoints(p2, p3);

				// Return the smallest
				if(d1 <= d2) { 
					c[0] = d1;
					uv = unitVecBetweenPoints(p1, p3);
					c[1] = uv[0];
					c[2] = uv[1];
					return c; 
				}
				else { 
					c[0] = d2;
					uv = unitVecBetweenPoints(p2, p3);
					c[1] = uv[0];
					c[2] = uv[1];
					return c; 
				}
			}
		}
		
		
		/**
		 * Calculates a unit vector of the straight line between two points
		 */
		protected double[] unitVecBetweenPoints(double[] p1, double[] p2) {
			double[] p3 = new double[2];
			double dist = distBetweenPoints(p1, p2);
			
			if(!Double.isNaN(dist)){
				// Normalise the vector between the two points
				p3[0] = (p2[0] - p1[0]) / dist;
				p3[1] = (p2[1] - p1[1]) / dist;
			}
			else{
				p3[0] = 0;
				p3[1] = 0;
			}
			
			return p3;
		}
	}	
}