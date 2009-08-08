/**
 * BSimPlaneBoundaryCreate.java
 *
 * Class to hold static methods to generate solid and wrapping boundaries planes.
 *
 * Authors: Mattia Fazzini
 * Created: 08/08/2009
 * Updated: 08/08/2009
 */
package bsim.drawable.boundary;


public class BSimPlaneBoundaryCreate {
	
	
	/**
	 * Create a solid boundary from parameter arguments
	 */
	public static BSimSolidPlaneBoundary createSolidPlaneBoundary( double[] args, int j){
		
		// Create points required to create the solid boundary
		double[] p1 = new double[3];
		double[] p2 = new double[3];
		double[] p3 = new double[3];
		double[] p4 = new double[3];
		
		
		if(j==0){
			p1[0] = args[0];
			p1[1] = args[1];
			p1[2] = args[2];
			p2[0] = args[0]+args[3];
			p2[1] = args[1];
			p2[2] = args[2];
			p3[0] = args[0]+args[3];
			p3[1] = args[1]+args[4];
			p3[2] = args[2];
			p4[0] = args[0];
			p4[1] = args[1]+args[4];
			p4[2] = args[2];	
		}
		else if (j==1){
			p1[0] = args[0];
			p1[1] = args[1];
			p1[2] = args[2]+args[5];
			p2[0] = args[0]+args[3];
			p2[1] = args[1];
			p2[2] = args[2]+args[5];
			p3[0] = args[0]+args[3];
			p3[1] = args[1];
			p3[2] = args[2];
			p4[0] = args[0];
			p4[1] = args[1];
			p4[2] = args[2];
			
		}
		else if (j==2){
			p1[0] = args[0];
			p1[1] = args[1];
			p1[2] = args[2]+args[5];
			p2[0] = args[0]+args[3];
			p2[1] = args[1];
			p2[2] = args[2]+args[5];
			p3[0] = args[0]+args[3];
			p3[1] = args[1]+args[4];
			p3[2] = args[2]+args[5];
			p4[0] = args[0];
			p4[1] = args[1]+args[4];
			p4[2] = args[2]+args[5];
		}
		else if (j==3){
			p1[0] = args[0];
			p1[1] = args[1]+args[4];
			p1[2] = args[2]+args[5];
			p2[0] = args[0]+args[3];
			p2[1] = args[1]+args[4];
			p2[2] = args[2]+args[5];
			p3[0] = args[0]+args[3];
			p3[1] = args[1]+args[4];
			p3[2] = args[2];
			p4[0] = args[0];
			p4[1] = args[1]+args[4];
			p4[2] = args[2];
			
		}
		else if (j==4){
			p1[0] = args[0];
			p1[1] = args[1];
			p1[2] = args[2]+args[5];
			p2[0] = args[0];
			p2[1] = args[1]+args[4];
			p2[2] = args[2]+args[5];
			p3[0] = args[0];
			p3[1] = args[1]+args[4];
			p3[2] = args[2];
			p4[0] = args[0];
			p4[1] = args[1];
			p4[2] = args[2];
		}
		else{
			p1[0] = args[0]+args[3];
			p1[1] = args[1];
			p1[2] = args[2]+args[5];
			p2[0] = args[0]+args[3];
			p2[1] = args[1]+args[4];
			p2[2] = args[2]+args[5];
			p3[0] = args[0]+args[3];
			p3[1] = args[1]+args[4];
			p3[2] = args[2];
			p4[0] = args[0]+args[3];
			p4[1] = args[1];
			p4[2] = args[2];
		}
		
		// return the new boundary
		return new BSimSolidPlaneBoundary(p1, p2, p3, p4);
	}
	

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Create a wrapping boundary from parameter arguments
	 */
	public static BSimWrapBoundary createWrapBoundary( double[] args ){
		
		// Create points required to create the solid boundary
		double[] p1 = new double[2];
		double[] p2 = new double[2];
		double[] offset = new double[2];
		
		p1[0] = args[0];
		p1[1] = args[1];
		p2[0] = args[2];
		p2[1] = args[3];
		offset[0] = args[4];
		offset[1] = args[5];
		
		// return the new boundary
		return new BSimWrapBoundary(p1, p2, offset, args[6]);
	}
}

