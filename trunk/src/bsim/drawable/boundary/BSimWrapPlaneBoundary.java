/**
 * BSimWrapPlaneBoundary.java
 *
 * Class that represents the plane of the area that is wrapped around on itself.
 * 
 * N.B. When creating wrapping boundaries, care must be taken to avoid offsets that place 
 * objects outside of the desired area.  It is also advisable to make the area 
 * large enough so that beads are very unlikely to reach the edges. 
 * The offset need to be always positive and not so big.
 *
 * Authors: Mattia Fazzini
 * Created: 09/08/2009
 * Updated: 09/08/2009
 */
package bsim.drawable.boundary;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.Vector;

import bsim.drawable.BSimDrawable;
import bsim.drawable.bacteria.BSimBacterium;
import bsim.drawable.bead.BSimBead;
import bsim.physics.BSimParticle;

public class BSimWrapPlaneBoundary extends BSimSolidPlaneBoundary implements BSimDrawable {
	// offset where the particle is placed in confront of the  corresponding point on the corresponding plane 
	private double[] wrapOffset = {0.0,0.0,0.0};
	
	boolean resetBacMem = false;
	
	//face id. We need this id to understand which is the face on the other side
	public int faceNum;
	
	private double length;
	private double width;
	private double depth;
	
	/**
	 * General constructor.
	 */
	public BSimWrapPlaneBoundary(double[] p1, double[] p2, double[] p3, double[] p4, double newLength, double newWidth, double newDepth, double[] newWrapOffset, int newFaceNum, double newResetBacMem) {

		super(p1, p2, p3, p4);
		
		wrapOffset[0] = newWrapOffset[0];
		wrapOffset[1] = newWrapOffset[1];
		wrapOffset[2] = newWrapOffset[2];
		
		length=newLength;
		width=newWidth;
		depth=newDepth;
		
		faceNum=newFaceNum;
		
		if(newResetBacMem == 1){
			resetBacMem = true;
		}
		else{
			resetBacMem = false;
		}
	}

	/**
	 * Method to deal with boundary collisions
	 */
	public void boundaryCollisions(Vector bacteria, Vector beads) {
		
		int n = bacteria.size();
		int m = beads.size();
		BSimBacterium bact;
		BSimBead bead;
		
		// Loop through each bacterium
		for (int i=0; i < n; i++) {
			// Get next bacterium
			bact = (BSimBacterium)bacteria.elementAt(i);
			
			// Check for collision with boundary; displace object if necessary 
			if (isColliding(bact)) {
				displace((BSimParticle)bact);
				// Reset their memory
				bact.setMemToReset(true);
			}
		}
		
		// Loop through all beads
		for (int i=0; i < m; i++) {
			// Get next bead
			bead  = (BSimBead)beads.elementAt(i);
			
			// Check for collision with boundary; displace object if necessary 
			if (isColliding(bead)) displace((BSimParticle)bead);
		}
	}
	
	/**
	 * Return a boolean to indicate whether or not an object is colliding with the boundary.  This is based on
	 * the minimum distance to the boundary which is always the perpendicular distance because we have a wrap box
	 */
	public boolean isColliding(BSimParticle x) {
		double[] point = x.getPosition();
		double radius = x.getRadius();
		//this method is the inherit method
		double[] info=calcInfoFromBoundary(point);
		//info[0] is the perpendicular distance
		double dist=info[0];
		if (dist - radius < 0) return true;
		else return false;
	}
	
	/**
	 * Move an object according to the wrapOffset vector
	 */
	public void displace(BSimParticle x) {
		
		double[] newCentrePos = {0.0,0.0,0.0};
		//point that fall into the plane
		double pp[] = {0.0,0.0,0.0};
		pp=perpendicularPointFallIntoPlane(x.getPosition());
		
		//the new centre position depends on the faceNum
		if(faceNum==0){			
			newCentrePos[0] = pp[0] - wrapOffset[0];
			newCentrePos[1] = pp[1] - wrapOffset[1];
			newCentrePos[2] = pp[2] + depth - wrapOffset[2];
		}
		else if(faceNum==1){		
			newCentrePos[0] = pp[0] - wrapOffset[0];
			newCentrePos[1] = pp[1] + width - wrapOffset[1];
			newCentrePos[2] = pp[2] - wrapOffset[2];
		}
		else if(faceNum==2){
			newCentrePos[0] = pp[0] + wrapOffset[0];
			newCentrePos[1] = pp[1] + wrapOffset[1];
			newCentrePos[2] = pp[2] - depth + wrapOffset[2];
		}
		else if(faceNum==3){
			newCentrePos[0] = pp[0] + wrapOffset[0];
			newCentrePos[1] = pp[1] - width + wrapOffset[1];
			newCentrePos[2] = pp[2] + wrapOffset[2];
		}
		else if(faceNum==4){
			newCentrePos[0] = pp[0] + length - wrapOffset[0];
			newCentrePos[1] = pp[1] - wrapOffset[1];
			newCentrePos[2] = pp[2] - wrapOffset[2];
		}
		else{
			newCentrePos[0] = pp[0] -length + wrapOffset[0];
			newCentrePos[1] = pp[1] + wrapOffset[1];
			newCentrePos[2] = pp[2] + wrapOffset[2];
		}
		
		x.setPosition(newCentrePos);
	}
	
	
	/**
	 * Draw the boundary.
	 */
	public void redraw(Graphics g) {
		g.setColor(Color.GRAY);
		Graphics2D g2d = (Graphics2D)g;
		// dash length, space length
		float[] dashValues = { 8.0f, 5.0f } ;
		Stroke stroke = new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashValues, 0 ) ;
 		g2d.setStroke( stroke ) ;
 		Line2D line = new Line2D.Double( (int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1] ) ;
 		g2d.draw( line ) ;
		g2d.setStroke( new BasicStroke() ) ;

	}
}
