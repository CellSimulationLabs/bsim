package bsim;

import javax.vecmath.Vector3d;

public class BSimChemicalField {
	
	/* 1 molecule/(micron)^3 = 1.7 nM = 1.7 nanomol/L */
	/* 1 mM = 6e5 molecules/(micron)^3 */
	
	protected BSim sim;
	protected double diffusivity; // (microns)^2/s
	/** Fraction of chemical decaying per second, quantity(t+dt) = quantity(t)*(1-decayRate*dt) */
	protected double decayRate; // 1/seconds 
	/** The quantity of chemical in the box (i,j,k) */
	protected double[][][] quantity; // number of molecules
	/** sim.getBound() */
	protected Vector3d bound;
	/** Number of boxes in each dimension */
	protected int[] boxes = new int[3];
	/** Box size */
	protected double[] box = new double[3]; // microns
	protected double boxVolume;
	
	public BSimChemicalField(BSim sim, int[] boxes, double diffusivity, double decayRate) {
		this.sim = sim;
		this.bound = sim.getBound();
		this.boxes = boxes;
		box[0] = bound.x/boxes[0];
		box[1] = bound.y/boxes[1];
		box[2] = bound.z/boxes[2];
		boxVolume = box[0]*box[1]*box[2];
		this.quantity = new double[boxes[0]][boxes[1]][boxes[2]]; 
		this.diffusivity = diffusivity;
		this.decayRate = decayRate;
	}	
	
	public int[] getBoxes() { return boxes; }
	public double[] getBox() { return box; }	
	
	/**
	 * Creates a linear concentration gradient in the z direction
	 */
	public void linearZ(double startConc, double endConc) {
		double grad = (endConc - startConc)/boxes[2];		
		for(int i=0;i<boxes[0];i++)
			for(int j=0;j<boxes[1];j++)
				for(int k=0;k<boxes[2];k++)
					setConc(i,j,k, startConc + k*grad);
	}
	
	/** Adds a quantity of chemical to the box containing position v */
	public void addQuantity(Vector3d v, double q) {
		int[] b = boxCoords(v);
		addQuantity(b[0],b[1],b[2],q);
	}
	/** Adds a quantity of chemical to the box (i,j,k) */
	public void addQuantity(int i, int j, int k, double q) {
		quantity[i][j][k] += q;
		if(quantity[i][j][k] < 0) quantity[i][j][k] = 0;
	}
	
	/** Sets the concentration of the box containing position v */
	public void setConc(Vector3d v, double c) {
		int[] b = boxCoords(v);
		setConc(b[0],b[1],b[2],c);
	}
	/** Sets the concentration of the box (i,j,k) */
	public void setConc(int i, int j, int k, double c) {
		quantity[i][j][k] = c*boxVolume;
	}
	/** Sets the concentration of the field */
	public void setConc(double c) {		
		for(int i=0;i<boxes[0];i++)
			for(int j=0;j<boxes[1];j++)
				for(int k=0;k<boxes[2];k++) quantity[i][j][k] = c*boxVolume;
	}
	
	/** Gets the concentration of the field at the position v in molecules/(micron)^3 */
	public double getConc(Vector3d v) {
		int[] b = boxCoords(v);
		return getConc(b[0],b[1],b[2]);
	}	
	/** Gets the concentration of the field in the box (i,j,k) in molecules/(micron)^3 */
	public double getConc(int i, int j, int k) {
		return quantity[i][j][k]/boxVolume;
	}
	
	/** Returns the total quantity of chemical in the field */
	public double totalQuantity() {
		double t = 0;
		for(int i=0;i<boxes[0];i++)
			for(int j=0;j<boxes[1];j++)
				for(int k=0;k<boxes[2];k++) t += quantity[i][j][k];
		return t;
	}
	
	/** Returns the integer coordinates of the box containing the position v */ 
	public int[] boxCoords(Vector3d v) {
		return new int[] {(int)(v.x/box[0]), (int)(v.y/box[1]), (int)(v.z/box[2])};	
	}
	
	
	public void update() {
		diffuse();
		decay();
	}
	
	public void decay() {
		for(int i=0;i<boxes[0];i++)
			for(int j=0;j<boxes[1];j++)
				for(int k=0;k<boxes[2];k++) quantity[i][j][k] *= (1 - decayRate*sim.getDt());
	}	
	
	public void diffuse() {
		double[][][] before = quantity;
		/* Index of the box in positive (negative) .. direction, taking account of the boundary conditions */
		int xAbove, xBelow, yAbove, yBelow, zAbove, zBelow;
		/* Quantity of chemical leaving the box in the positive (negative) .. direction */
		double qxAbove, qxBelow, qyAbove, qyBelow, qzAbove, qzBelow;
		/*
		 * Flux of molecules crossing in the positive x-direction (Fick's law)
		 * 	J = -D(dC/dx) = -D*(C(x+dx)-C(x))/dx =  -D*(N(x+dx)-N(x))/((dx)^2*dy*dz)  molecules/(micron)^2/sec
		 * Number of molecules transferred in the positive x-direction over dt
		 * 	xAbove = J*(dy*dz)*dt = -((D*dt)/(dx)^2)*(N(x+dx)-N(x)) = -kX*(N(x+dx)-N(x))
		 * where kX = (D*dt)/(dx)^2 is a dimensionless constant
		 */
		double kX = (diffusivity*sim.getDt())/Math.pow(box[0],2);
		double kY = (diffusivity*sim.getDt())/Math.pow(box[1],2);
		double kZ = (diffusivity*sim.getDt())/Math.pow(box[2],2);
		for(int i=0;i<boxes[0];i++)
			for(int j=0;j<boxes[1];j++)
				for(int k=0;k<boxes[2];k++) {	
					/* Is this box the last box? 
					 * 	If so, is the boundary solid? If so, there is no box above, else, the box 'above' is the first box
					 * 	Else, the box above is the next box */
					xAbove = (i == boxes[0]-1 ? (sim.getSolid()[0] ? -1 : 0) : i+1);
					xBelow = (i == 0 ? (sim.getSolid()[0] ? -1 : boxes[0]-1) : i-1);
					yAbove = (j == boxes[1]-1 ? (sim.getSolid()[1] ? -1 : 0) : j+1);
					yBelow = (j == 0 ? (sim.getSolid()[1] ? -1 : boxes[1]-1) : j-1);
					zAbove = (k == boxes[2]-1 ? (sim.getSolid()[2] ? -1 : 0) : k+1);
					zBelow = (k == 0 ? (sim.getSolid()[2] ? -1 : boxes[2]-1) : k-1);
					
					/* If there is a box above */
					if(xAbove != -1) {
						/* Calculate the quantity of chemical leaving the box in this direction */
						qxAbove = -kX*(before[xAbove][j][k]-before[i][j][k]);
						/* Add that quantity to the box above */ 
						quantity[xAbove][j][k] += qxAbove;
						/* Remove it from this box */
						quantity[i][j][k] -= qxAbove;
					}
					if(xBelow != -1) {
						qxBelow = -kX*(before[xBelow][j][k]-before[i][j][k]);
						quantity[xBelow][j][k] += qxBelow;
						quantity[i][j][k] -= qxBelow;
					}
					if(yAbove != -1) {
						qyAbove = -kY*(before[i][yAbove][k]-before[i][j][k]);
						quantity[i][yAbove][k] += qyAbove;
						quantity[i][j][k] -= qyAbove;
					}
					if(yBelow != -1) {
						qyBelow = -kY*(before[i][yBelow][k]-before[i][j][k]);
						quantity[i][yBelow][k] += qyBelow;
						quantity[i][j][k] -= qyBelow;
					}
					if(zAbove != -1) {
						qzAbove = -kZ*(before[i][j][zAbove]-before[i][j][k]);
						quantity[i][j][zAbove] += qzAbove;
						quantity[i][j][k] -= qzAbove;
					}
					if(zBelow != -1) {
						qzBelow = -kZ*(before[i][j][zBelow]-before[i][j][k]);
						quantity[i][j][zBelow] += qzBelow;
						quantity[i][j][k] -= qzBelow;
					}
				}
		
	}
	
}
