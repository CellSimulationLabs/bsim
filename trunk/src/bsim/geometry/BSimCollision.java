package bsim.geometry;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import bsim.particle.BSimParticle;

/**
 * Collision related methods.
 */
public class BSimCollision {
		protected double t;
		protected Vector3d pos;
		public static boolean recursiveCollisions = false;
		
		/** Constructor for an empty collision. */
		public BSimCollision(){
			t = Double.NaN;
			pos = new Vector3d();
		}
		
		public void set(BSimTriangle tri, double tVal, double u, double v, double w){
			t = tVal;
			
			Vector3d newpos = new Vector3d();
			newpos.scale(u,tri.getVertCoords(0));
			newpos.scaleAdd(v, tri.getVertCoords(1),newpos);
			newpos.scaleAdd(w, tri.getVertCoords(2),newpos);
			pos.set(newpos);
		}
		
		public void set(double tVal, Vector3d qp, Vector3d startPos){
			t = tVal;
			Vector3d newP = new Vector3d();
			newP.scaleAdd(-t, qp, startPos);
			pos.set(newP);
		}
		
		/** Set the collision values. */
		public void set(BSimCollision col){
			t = col.getTVal();
			pos.set(col.getLocation());
		}
		
		/** Return the t value. */
		public double getTVal(){ return t;}
		/** Return location of collision. */
		public Vector3d getLocation(){return pos;}
		
		/** Set whether recursive collisions are enabled. */
		public static void setRecursiveCollisions(boolean recursiveCollisionsActive){ recursiveCollisions = recursiveCollisionsActive;}

		/**
		 * Check for collision between particle and mesh, and add repulsion force.
		 * @param p Particle to check.
		 * @param theMesh Mesh to check for intersection with.
		 */
		public static void collideAndRepel(BSimParticle p, BSimMesh theMesh){
			boolean meshIntersection = false;
			
			Vector3d pOnTri = new Vector3d();
			Vector3d norm = new Vector3d();
			Vector3d pa = new Vector3d();
			ArrayList<Integer> potentialIntersections = new ArrayList<Integer>();			
			
			// Plane collisions - less expensive than doing just triangles (~2.85x speedup)
			for(int i = 0; i < theMesh.getFaces().size(); i++){
				
				BSimTriangle t = theMesh.getFaces().get(i);
				
				// Plane corresponding to a triangle of the mesh
				if(BSimMeshUtils.intersectSpherePlane(p, t.getNormal(),new Vector3d(t.getVertCoords(0)))){
					potentialIntersections.add(i);
				}
			}
			
			// Check the candidate triangles for intersection w/ sphere
			for(Integer i:potentialIntersections){
				BSimTriangle t = theMesh.getFaces().get(i);

				meshIntersection = BSimMeshUtils.intersectSphereTriangle(p, t.getVertCoords(0),
												theMesh.getVertCoordsOfTri(t, 1), 
												theMesh.getVertCoordsOfTri(t, 2),
												pOnTri);

				if(meshIntersection){
					norm = t.getNormal();
					pa = new Vector3d(theMesh.getVertCoordsOfTri(t, 0));
					
					double dist = Math.abs(p.getPosition().dot(norm) - norm.dot(pa)); //(d = normal.dot.some_point)
					
					double m = Math.log(dist/p.getRadius());

					Vector3d f = new Vector3d(norm);
					f.scale(m);
					
					p.addForce(f);
				}
			}	
		}

		
		// NOTE that this is colliding a ray with a triangle (NOT in fact a swept sphere vs triangle)
		// and therefore is less accurate for larger particles.
		/**
		 * Check to see if intersection with mesh and reflect. End point (p2) is updated to the correct
		 * reflected position. Note that this is colliding a ray with a triangle (NOT in fact 
		 * a swept sphere vs triangle).
		 * @param p1 Start point
		 * @param p2 End point
		 * @param theMesh Mesh to check for intersection with.
		 */
		public static void collideAndReflect(Vector3d p1, Vector3d p2, BSimMesh theMesh){

			BSimCollision iPos = new BSimCollision();
			
			// TODO: Needs to be more robust.
			// At the moment this just uses the first collision that is detected (in tri list order, not spatially)
			// This may not be robust enough for complex boundaries where a ray might intersect multiple triangles
			// Solution: change the method so that all triangles are checked and the first intersection (spatially)
			// is used i.e. the closest point of intersection to the ray origin.
			// May be possible to achieve a speedup by testing against planes first.
			// Judicious use of kdtree/octree will reduce the overhead of no longer having the early-out 
			// which results from breaking when a triangle is hit
			for(BSimTriangle t: theMesh.getFaces()){					
				if(BSimMeshUtils.intersectVectorTriangle(p1, p2, t, iPos)){

					double scaleFactor;
					
					Vector3d reflected = new Vector3d();
					Vector3d dir = new Vector3d();
					
					dir.sub(p2, p1);
					
					Vector3d normal = new Vector3d();
					normal = t.getNormal();
											
					scaleFactor = -2*dir.dot(normal);
					
					double s2 = normal.dot(normal);
					
					scaleFactor = scaleFactor/s2;
					
					reflected.set(normal);
					reflected.scale(scaleFactor);
					reflected.add(dir);
											
					reflected.scale(1-iPos.getTVal());

					reflected.add(iPos.getLocation());
					
					p2.set(reflected);
					
					// Recursion to take into account case of grazing angle resulting
					// in escape through adjacent triangle
					// TODO: optimisation
					// (although the recursion doesn't slow this down massively compared to increasing the mesh size)
					if(recursiveCollisions){
						collideAndReflect(iPos.getLocation(), p2, theMesh);
					}
					
					break;
				}
			}
		}
		
		/**
		 * Check if mesh is crossed.
		 * @param p1 Start point.
		 * @param p2 End point
		 * @param theMesh Mesh to check for intersection with.
		 * @return Boolean whether crossing occurs.
		 */
		public static boolean collideAndCross(Vector3d p1, Vector3d p2, BSimMesh theMesh){
			BSimCollision iPos = new BSimCollision();

			for (BSimTriangle t: theMesh.getFaces()) {					
				if (BSimMeshUtils.intersectVectorTriangle(p1, p2, t, iPos)) {
					return true;
				}
			}
			return false;
		}
}
