/**
 * BSimSolidPlaneBoundary.java
 *
 * Class that represents simulation boundaries. These are solid planes that act as walls
 * restricting the movement that both beads and bacteria can make.
 *
 * Authors: Mattia Fazzini
 * Created: 08/08/2009
 * Updated: 08/08/2009
 */
package bsim.drawable.boundary;

import java.awt.Color;

import java.awt.Graphics;

import bsim.drawable.BSimDrawable;


public class BSimSolidPlaneBoundary implements BSimDrawable {

	
	//plane points
	protected double[] p1 = {0.0,0.0,0.0};
	protected double[] p2 = {0.0,0.0,0.0};
	protected double[] p3 = {0.0,0.0,0.0};
	protected double[] p4 = {0.0,0.0,0.0};
	//plane variables
	protected double a=0;
	protected double b=0;
	protected double c=0;
	protected double d=0;
	
	// Colour of all boundaries
	protected Color boundColour = Color.GRAY;
	
	/**
	 * General constructor.
	 */
	public BSimSolidPlaneBoundary(double[] newP1, double[] newP2, double[] newP3, double[] newP4) {
		
		// Update the internal variables
		p1[0] = newP1[0];
		p1[1] = newP1[1];
		p1[2] = newP1[2];
		p2[0] = newP2[0];
		p2[1] = newP2[1];
		p2[2] = newP2[2];
		p3[0] = newP3[0];
		p3[1] = newP3[1];
		p3[2] = newP3[2];
		p4[0] = newP4[0];
		p4[1] = newP4[1];
		p4[2] = newP4[2];
		
		//ax+by+cz+d=0 solved the determinant
		a=(p3[2]*p2[1])-(p3[2]*p1[1])-(p1[2]*p2[1])+(p1[2]*p1[1])-(p3[1]*p2[2])+(p3[1]*p1[2])+(p1[1]*p2[2])-(p1[1]*p1[2]);
		b=(p3[0]*p2[2])-(p3[0]*p1[2])-(p1[0]*p2[2])+(p1[0]*p1[2])-(p3[2]*p2[0])+(p3[2]*p1[0])+(p1[2]*p2[0])-(p1[2]*p1[0]);
		c=(p3[1]*p2[0])-(p3[1]*p1[0])-(p1[1]*p2[0])+(p1[1]*p1[0])-(p3[0]*p2[1])+(p3[0]*p1[1])+(p1[0]*p2[1])-(p1[0]*p1[1]);
		d=((-1)*p3[2]*p1[0]*p2[1])+(p3[2]*p1[0]*p1[1])+(p1[2]*p1[0]*p2[1])-(p1[2]*p1[0]*p1[1])+
			((-1)*p3[0]*p1[1]*p2[2])+(p3[0]*p1[1]*p1[2])+(p1[0]*p1[1]*p2[2])-(p1[0]*p1[1]*p1[2])+
			((-1)*p3[1]*p1[2]*p2[0])+(p3[1]*p1[2]*p1[0])+(p1[1]*p1[2]*p2[0])-(p1[1]*p1[2]*p1[0])+
			(p1[2]*p3[0]*p2[1])-(p1[2]*p3[0]*p1[1])-(p1[2]*p1[0]*p2[1])+(p1[2]*p1[0]*p1[1])+
			(p1[0]*p3[1]*p2[2])-(p1[0]*p3[1]*p1[2])-(p1[0]*p1[1]*p2[2])+(p1[0]*p1[1]*p1[2])+
			(p1[1]*p3[2]*p2[0])-(p1[1]*p3[2]*p1[0])-(p1[1]*p1[2]*p2[0])+(p1[1]*p1[2]*p1[0]);
		}
	
	/**
	 * Calculates the info from the solid plane boundary.
	 * info[0]=distance from the plane to the point;
	 * info[1]=x component of the unit vector;
	 * info[2]=y component of the unit vector;
	 * info[2]=z component of the unit vector;
	 */
	public synchronized double[] calcInfoFromBoundary(double[] p) {
		double[] info= {0.0,0.0,0.0,0.0};
		double dist= 0.0;
		dist = Math.abs((a*p[0])+(b*p[1])+(c*p[2])+d)/Math.sqrt(Math.pow(a, 2.0)+Math.pow(b, 2.0)+Math.pow(c, 2.0));
		info[0]=dist;		
		//point that fall into the plane
		double[] pp= {0.0,0.0,0.0};
		pp=perpendicularPointFallIntoPlane(p);
		
		if(!Double.isNaN(dist)){
			// Normalize the vector between the two points
			info[1]=(pp[0] - p[0])/dist;
			info[2]=(pp[1] - p[1])/dist;
			info[3]=(pp[2] - p[2])/dist;
		}
		else{
			info[1] = 0;
			info[2] = 0;
			info[3] = 0;
		}
		
		return info;
	}
	
	/*
	 * Point that fall on the plane following the perpendicular distance
	 * 
	 * point on the plane where the perpendicular distance fall
	 * 
	 * ax+by+cz+d=0
	 * x=x1+at
	 * y=y1+bt
	 * z=z2+ct
	 * 
	 */
	public synchronized double[] perpendicularPointFallIntoPlane(double[] p) {
		double t= ( ((-1)*a*p[0])+((-1)*b*p[1])+((-1)*c*p[2])+((-1)*d))/((a*a)+(b*b)+(c*c));
		
		//point into the plane
		double[] pp= {0.0,0.0,0.0};
		pp[0]=p[0]+a*t;
		pp[1]=p[1]+b*t;
		pp[2]=p[2]+c*t;
		
		return pp;
	}
	
	
	
	/**
	 * Standard get methods
	 */
	public double[] getP1() { return p1; }
	public double[] getP2() { return p2; }
	public double[] getP3() { return p3; }
	public double[] getP4() { return p4; }
	
	
	/**
	 * Draw the boundary.
	 */
	public void redraw(Graphics g) {
		g.setColor(boundColour);
		g.drawLine((int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1]);
	}
}

