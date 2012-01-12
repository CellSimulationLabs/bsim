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

import javax.vecmath.Vector3d;

import bsim.BSimOctreeField;
import bsim.particle.BSimParticle;

/**
 * Utility functions for meshes.
 */
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
	public static boolean intersectSphereTriangle(BSimParticle s, Vector3d a, Vector3d b, Vector3d c, Vector3d p){
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
	public static Vector3d closestPtPointTriangle(Vector3d p, Vector3d a, Vector3d b, Vector3d c){
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

		Vector3d rTemp = new Vector3d();

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
		
		ab.sub(tri.getVertCoords(1), tri.getVertCoords(0));
		ac.sub(tri.getVertCoords(2), tri.getVertCoords(0));
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
		ap.sub(startPos, tri.getVertCoords(0));
		
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
		//double u = 1.0f - v - w;
		
		coll.set(t, qp, startPos);
		
		return true;
	
	}
		
	/**
	 * Compute the intersection of a vector p1 + t*dir (line segment) and a plane
	 * @param p1 Line segment origin
	 * @param direction Line segment direction (UNNORMALISED = p2 - p1)
	 * @param normalPlane Plane normal
	 * @param dPlane Plane d value = DOT(normal, point_on_plane)
	 * @return
	 */
	public static BSimCollision intersectVectorPlane(Vector3d p1, Vector3d direction, Vector3d normalPlane, double dPlane)
	{
		// Compute the t value for the directed line (p1 + t*direction) intersecting the plane
		double t = (dPlane - normalPlane.dot(p1)) / normalPlane.dot(direction);
		// If t in the interval [0..1] compute and return intersection point
		if (t >= 0.0 && t <= 1.0){
			
			BSimCollision col = new BSimCollision();
			col.set(t, direction, p1);
		
			return col;
		}
		// Else no intersection
		return null;
	}	
	
	/**
	 * Test for intersection of a triangle against an octree node
	 * @param t BSimTriangle to test
	 * @param n Octree node to test against
	 * @return true if an intersection occurs
	 */
	public static boolean intersectTriOctreeNode(BSimTriangle t, BSimOctreeField n){
		double length = n.getLength();
		return intersectTriangleAAB(t, n.getCentre(), new Vector3d(length, length, length));
	}
	
	/**
	 * Test for intersection of a triangle against an axis aligned box
	 * @param t BSimTriangle to test
	 * @param boxCentre Centre coordinate of the box
	 * @param boxDim Box length in x,y,z directions 
	 * @return true if an intersection occurs
	 */
	public static boolean intersectTriangleAAB(BSimTriangle t, Vector3d boxCentre, Vector3d boxDim){
		// Algorithm based on 'Real Time Rendering 3rd Ed.' pp 760 (based on a separating axes test)
		// Ideas for optimisation based on C code from the author's website: 
		// http://www.cs.lth.se/home/Tomas_Akenine_Moller/code/
		// Beastly long method, sorry...

		// Box extents away from the centre (half lengths)
		double hLengthX = boxDim.x * 0.5;
		double hLengthY = boxDim.y * 0.5;
		double hLengthZ = boxDim.z * 0.5;

		// Triangle vertices
		Vector3d v0 = new Vector3d(t.getVertCoords(0));
		Vector3d v1 = new Vector3d(t.getVertCoords(1));
		Vector3d v2 = new Vector3d(t.getVertCoords(2));

		// Translate triangle - (conceptually moving box to the origin)
		v0.sub(boxCentre);
		v1.sub(boxCentre); 
		v2.sub(boxCentre);

		// Compute edge vectors for triangle
		Vector3d e0 = new Vector3d();
		Vector3d e1 = new Vector3d();
		Vector3d e2 = new Vector3d();
		e0.sub(v1, v0);
		e1.sub(v2, v1);
		e2.sub(v0, v2);

		// Test the 9 separating axes
		// Yup they're all inlined because its simpler to use our local variables this way...doh!
		double p0, p1, p2, radius;
		double min, max;
		double fex = Math.abs(e0.getX());
		double fey = Math.abs(e0.getY());
		double fez = Math.abs(e0.getZ());

		// Begin horrible code.....
			p0 = e0.getZ()*v0.getY() - e0.getY()*v0.getZ();			       	    
			p2 = e0.getZ()*v2.getY() - e0.getY()*v2.getZ();			       	    
		        if(p0 < p2){
		        	min = p0; max = p2;
		        }else{
		        	min = p2; max = p0;
		        }  
			radius = fez * hLengthY + fey * hLengthZ;    
			if(min > radius || max < -radius) return false;
			
			p0 = -e0.getZ()*v0.getX() + e0.getX()*v0.getZ();		      	    
			p2 = -e0.getZ()*v2.getX() + e0.getX()*v2.getZ();	       	       	    
		        if(p0 < p2){
		        	min = p0; max = p2;
		        }else{
		        	min = p2; max = p0;
		        }  
			radius = fez * hLengthX + fex * hLengthZ;    
			if(min > radius || max < -radius) return false;
			
			p1 = e0.getY()*v1.getX() - e0.getX()*v1.getY();			            
			p2 = e0.getY()*v2.getX() - e0.getX()*v2.getY();			       	    
		        if(p2 < p1){
		        	min = p2; max = p1;
		        }else{
		        	min = p1; max = p2;
		        }  
			radius = fey * hLengthX + fex * hLengthY;    
			if(min > radius || max < -radius) return false;
			
			
			fex = Math.abs(e1.getX());
			fey = Math.abs(e1.getY());
			fez = Math.abs(e1.getZ());
			
			p0 = e1.getZ()*v0.getY() - e1.getY()*v0.getZ();			       	    
			p2 = e1.getZ()*v2.getY() - e1.getY()*v2.getZ();			       	    
		        if(p0 < p2){
		        	min = p0; max = p2;
		        } else {
		        	min = p2; max = p0;
		        }  
			radius = fez * hLengthY + fey * hLengthZ;    
			if(min > radius || max < -radius) return false;
			
			p0 = -e1.getZ()*v0.getX() + e1.getX()*v0.getZ();		      	    
			p2 = -e1.getZ()*v2.getX() + e1.getX()*v2.getZ();	       	       	    
		        if(p0 < p2) {
		        	min = p0; max = p2;
		        } else {
		        	min = p2; max = p0;
		        }  
			radius = fez * hLengthX + fex * hLengthZ;    
			if(min > radius || max < -radius) return false;
	
			p0 = e1.getY()*v0.getX() - e1.getX()*v0.getY();				    
			p1 = e1.getY()*v1.getX() - e1.getX()*v1.getY();			            
		        if(p0 < p1){
		        	min = p0; max = p1;
		        }else{
		        	min = p1; max = p0;
		        }
			radius = fey * hLengthX + fex * hLengthY;    
			if(min > radius || max < -radius) return false;
			
			fex = Math.abs(e2.getX());
			fey = Math.abs(e2.getY());
			fez = Math.abs(e2.getZ());
			
			p0 = e2.getZ()*v0.getY() - e2.getY()*v0.getZ();			            
			p1 = e2.getZ()*v1.getY() - e2.getY()*v1.getZ();			       	    
		        if(p0 < p1){
		        	min = p0; max = p1;
		        }else{
		        	min = p1; max = p0;
		        }  
			radius = fez * hLengthY + fey * hLengthZ;    
			if(min>radius || max<-radius) return false;
			
			p0 = -e2.getZ()*v0.getX() + e2.getX()*v0.getZ();		      	    
			p1 = -e2.getZ()*v1.getX() + e2.getX()*v1.getZ();	     	       	    
		        if(p0 < p1){
		        	min = p0; max = p1;
		        }else{
		        	min = p1; max = p0;
		        }  
			radius = fez * hLengthX + fex * hLengthZ;    
			if(min > radius || max < -radius) return false;
			
			p1 = e2.getY()*v1.getX() - e2.getX()*v1.getY();			            
			p2 = e2.getY()*v2.getX() - e2.getX()*v2.getY();			       	    
		        if(p2 < p1){
		        	min = p2; max = p1;
		        }else{
		        	min = p1; max = p2;
		        }  
			radius = fey * hLengthX + fex * hLengthY;    
			if(min > radius || max < -radius) return false;
		// End horrible code...

		// check RTCD pp.169
		// Test the three axes corresponding to the face normals of box
		// Exit if...
		// ... [-lengthX, lengthX] and [min(v0.x,v1.x,v2.x), max(v0.x,v1.x,v2.x)] do not overlap
		if (Math.max(v0.x, Math.max(v1.x, v2.x)) < -hLengthX || Math.min(v0.x, Math.min(v1.x, v2.x)) > hLengthX) return false;
		// ... [-lengthY, lengthY] and [min(v0.y,v1.y,v2.y), max(v0.y,v1.y,v2.y)] do not overlap
		if (Math.max(v0.y, Math.max(v1.y, v2.y)) < -hLengthY || Math.min(v0.y, Math.min(v1.y, v2.y)) > hLengthY) return false;
		// ... [-lengthZ, lengthZ] and [min(v0.z,v1.z,v2.z), max(v0.z,v1.z,v2.z)] do not overlap
		if (Math.max(v0.z, Math.max(v1.z, v2.z)) < -hLengthZ || Math.min(v0.z, Math.min(v1.z, v2.z)) > hLengthZ) return false;
		
		// Test separating axis corresponding to triangle face normal
		
		Vector3d normal = t.getNormal();
		double d = normal.dot(v0);
		
		return intersectPlaneAAB(normal, d, boxCentre, boxDim);
	}
	
	/**
	 * Test for intersection between a plane and an axis-aligned box.
	 * @param normal Plane normal
	 * @param d Plane d value (from plane equation, d = normal.dot(v0)) where v0 is some point on the plane
	 * @param boxCentre Box centre coordinates
	 * @param boxDim Box side lengths in x,y,z directions
	 * @return true if an intersection occurs
	 */
	public static boolean intersectPlaneAAB(Vector3d normal, double d, Vector3d boxCentre, Vector3d boxDim)
	{
		Vector3d e = new Vector3d(boxDim);
		e.scale(0.5);
		e.absolute(); // Just in case.
		
		// Compute the projection interval radius of b onto L(t) = b.c + t * p.n
		double r = e.getX()*Math.abs(normal.getX()) 
					+ e.getY()*Math.abs(normal.getY()) 
					+ e.getZ()*Math.abs(normal.getZ());
		
		// Compute distance of box centre from plane
		double s = normal.dot(boxCentre) - d;
		
		// Intersection occurs when distance s falls within [-r,+r] interval
		return (Math.abs(s) <= r);
	}
}
