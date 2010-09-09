/**
 * Maths functions for collisions
 * 
 * Many things left to change, i.e. a bit of refactoring with changes in BSimMesh 
 * and better function definitions (maths functions vs. colliding BSim objects)
 * 
 * Also change methods back to returning BSimCollision objects instead of writing to them :p 
 * 
 */

package bsim.geometry;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import bsim.particle.BSimParticle;

public class BSimMeshUtils {
	
	/**
	 * Intersection of a sphere with a triangle 
	 * @param s 
	 * @param a
	 * @param b
	 * @param c
	 * @param p the point of intersection
	 * @return true = the sphere does intersect the triangle
	 */
	public static boolean intersectSphereTriangle(BSimParticle s, Point3d a, Point3d b, Point3d c, Point3d p){
		// p = point on triangle closest to the sphere centre
		p.set(closestPtPointTriangle(s.getPosition(),a,b,c));

		Vector3d  v = new Vector3d();
		v.sub(p,s.getPosition());
		
		return (v.dot(v) <= s.getRadius()*s.getRadius());
	}
	
	/**
	 * Closest point on triangle to a given point in 3d space.
	 * RTCD chapter 5.
	 * @param p Point to test
	 * @param a Triangle vertex a (v0)
	 * @param b Triangle vertex b (v1)
	 * @param c Triangle vertex c (v2)
	 * @return
	 */
	public static Point3d closestPtPointTriangle(Vector3d p, Point3d a, Point3d b, Point3d c){
		// Check if P in vertex region outside A
		Vector3d ab = new Vector3d(); ab.sub(b,a);
		Vector3d ac = new Vector3d(); ac.sub(c,a);
		Vector3d ap = new Vector3d(); ap.sub(p,a);
				
		double d1 = ab.dot(ap);
		double d2 = ac.dot(ap);
		if (d1 <= 0.0 && d2 <= 0.0) return a; // Vertex a (barycentric coordinates (1,0,0))

		// Check if P in vertex region outside B
		Vector3d bp = new Vector3d(); bp.sub(p,b);
		double d3 = ab.dot(bp);
		double d4 = ac.dot(bp);
		if (d3 >= 0.0 && d4 <= d3) return b; // Vertex b (barycentric coordinates (0,1,0))

		Point3d rTemp = new Point3d();

		// Check if P in edge region of AB, if so return projection of P onto AB
		double vc = d1*d4 - d3*d2;
		if (vc <= 0.0 && d1 >= 0.0 && d3 <= 0.0){
			double v = d1 / (d1 - d3);
			// v*ab + a
			rTemp.scaleAdd(v, ab, a);
			return rTemp; // barycentric coordinates (1-v,v,0)
		}

		// Check if P in vertex region outside C
		Vector3d cp = new Vector3d(); cp.sub(p,c);
		double d5 = ab.dot(cp);
		double d6 = ac.dot(cp);
		if (d6 >= 0.0 && d5 <= d6) return c; // Vertex c (barycentric coordinates (0,0,1))

		// Check if P in edge region of AC, if so return projection of P onto AC
		double vb = d5*d2 - d1*d6;
		if (vb <= 0.0 && d2 >= 0.0 && d6 <= 0.0){
			double w = d2 / (d2 - d6);
			
			//a + w * ac 
			rTemp.scaleAdd(w, ac, a);
			return rTemp;// barycentric coordinates (1-w,0,w)
		}

		// Check if P in edge region of BC, if so return projection of P onto BC
		double va = d3*d6 - d5*d4;
		if (va <= 0.0 && (d4 - d3) >= 0.0 && (d5 - d6) >= 0.0){
			double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			
			// b + w * (c - b)
			rTemp.sub(c,b);
			rTemp.scaleAdd(w,b);
			return rTemp; // barycentric coordinates (0,1-w,w)
		}

		// P inside face region. Compute Q through its barycentric coordinates (u,v,w)
		double denom = 1.0 / (va + vb + vc);
		double v = vb * denom;
		double w = vc * denom;
		
		// Q from barycentric coordinates: Q = a + ab * v + ac * w = u*a + v*b + w*c
		// (last barycentric coordinate u = va * denom = 1.0f - v - w)
		rTemp.scale(v, ab);
		rTemp.scaleAdd(w,ac,rTemp);
		rTemp.add(a);
		return rTemp;
	}
	
	/**
	 * Computes the intersection of a sphere with a plane
	 * @param s the particle to test
	 * @param planeNormal normal of the plane to be tested
	 * @param a a point in the plane
	 * @return
	 */
	public static boolean intersectSpherePlane(BSimParticle s, Vector3d planeNormal, Vector3d a){
		double dist = (s.getPosition()).dot(planeNormal) - planeNormal.dot(a); //(d = normal.dot.some_point)
		return (Math.abs(dist) <= s.getRadius());
	}
	
	/**
	 * Computes intersection of a vector in 3d space (e.g. the direction a particle is moving in)
	 * and a triangle (e.g. a boundary, substrate, etc)
	 * @param v the vector to be tested
	 * @param t the triangle to be tested
	 */
	//TODO wrapper for particle, iVT(particle.location, particle.dirn, triangle)
	public static boolean intersectVectorTriangle(Vector3d startPos, Vector3d endPos, BSimTriangle tri, BSimCollision coll){
		Vector3d ab = new Vector3d();
		Vector3d ac = new Vector3d();
		Vector3d qp = new Vector3d();
		
		ab.sub(tri.parentMesh.getTCoords(tri, 1), tri.parentMesh.getTCoords(tri, 0));
		ac.sub(tri.parentMesh.getTCoords(tri, 2), tri.parentMesh.getTCoords(tri, 0));
		qp.sub(startPos, endPos);
		
		// ******* If this is changed to precomputed (stored) normals, make sure they're 
		// recomputed after any mesh scale operations, as this method uses unnormalised vectors!
		Vector3d normal = new Vector3d();
		normal.cross(ab, ac);
		
		// Compute denominator. If denom <= 0, segment is parallel to or points
		// away from triangle, so exit early
		double denom = qp.dot(normal);
		
		// Try using within 'epsilon'?
		if (denom == 0.0) return false;
		
		// Compute intersection t value of pq with plane of triangle. A ray
		// intersects iff 0 <= t. Segment intersects iff 0 <= t <= 1. Delay
		// dividing by d until intersection has been found to pierce triangle
	
		Vector3d ap = new Vector3d();
		ap.sub(startPos, tri.parentMesh.getTCoords(tri, 0));
		
		double oodenom = 1.0/denom;
		
		double t = ap.dot(normal) * oodenom;
//		if (Math.abs(t) > Math.abs(denom)) return false; 
		if(t < 0.0 || t > 1.0) return false;
				
		// Compute barycentric coordinate components and test if within bounds of triangle
		Vector3d e = new Vector3d();
		e.cross(qp, ap);
				
		double v = ac.dot(e) * oodenom;
		if (v < 0.0 || v > 1.0) return false;
		
		double w = -ab.dot(e) * oodenom;
		if (w < 0.0 || v + w > 1.0) return false;
		
		// Segment/ray intersects triangle.
		t *= oodenom;
		double u = 1.0f - v - w;
		
		coll.set(t, qp, startPos);
		
		return true;
	
	}
	
	/**
	 * Intersection of a vector in 3d space with a plane
	 */
	public static void intersectVectorPlane(){
		
	}
	
	/**
	 * Intersection of a vector (line segment) with a BSimCollisionOctree
	 */
	public static void intersectVectorOctree(){

	}
	
	/**
	 * Intersection of a vector with an axis aligned bounding box
	 */
	public static void intersectVectorAABB(){

	}
	
	/**
	 * Intersection of a sphere (BSimParticle) with a BSimCollisionOctree
	 */
	public static void intersectSphereOctree(){

	}
	
	/**
	 * Intersection of a sphere with and AABB
	 */
	public static void intersectSphereAABB(){

	}
	
	
	/**
	 * Test if a point in 3d space is inside a <b>*convex*</b> polyhedron.
	 */
	public static void pointInPolyhedron(){
		
	}
	
	/**
	 * Test if a vector in 3d space intersects a sphere
	 * @param v 
	 * @param s
	 */
	public static void intersectVectorSphere(Vector3d v, BSimParticle s){

	}
	
	/**
	 * Test for intersection of moving sphere vs moving sphere
	 * @param s1 First sphere
	 * @param s2 Second sphere
	 */
	public static void intersectSphereSphereDynamic(BSimParticle s1, BSimParticle s2){

	}
	
}
