package bsim;

import javax.vecmath.Vector3d;

/**
 * Standard chemical field (uniform division of space) .
 * Uses regular division of the three-dimensional space into boxes that
 * then allow for diffusion of chemical quantities between the boxes at
 * some rate (diffusivity).
 */
public class BSimChemicalField {
	
	/* 1 molecule/(micron)^3 = 1.7 nM = 1.7 nanomol/L */
	/* 1 mM = 6e5 molecules/(micron)^3 */
	
	/** Simulation the chemical field is associated with. */
	protected BSim sim;
	/** Diffusivity of the chemical field. */
	protected double diffusivity; // (microns)^2/s
	/** Fraction of chemical decaying per second, quantity(t+dt) = quantity(t)*(1-decayRate*dt). */
	protected double decayRate; // 1/seconds 
	/** The quantity of chemical in the box (i,j,k). */
	protected double[][][] quantity; // number of molecules
	/** sim.getBound(). */
	protected Vector3d bound;
	/** Number of boxes in each dimension. */
	protected int[] boxes = new int[3];
	/** Box size. */
	protected double[] box = new double[3]; // microns
	/** Volume of each box (microns^3). */
	protected double boxVolume;
	
	/**
	 * Constructor that creates a new chemical field with attached to a particular simulation and
	 * with a specified number of boxes, chemical diffusivity and decay rate.
	 * @param sim Associated simulation.
	 * @param boxes Number of boxes in the (x,y,z) directions.
	 * @param diffusivity Diffusivity of the chemical (microns)^2/s.
	 * @param decayRate Decay rate of the chemical (molecules/s).
	 */
	public BSimChemicalField(BSim sim, int[] boxes, double diffusivity, double decayRate) {
		this.sim = sim;
		this.bound = sim.getBound();
		this.boxes = boxes;
		box[0] = bound.x/(double)boxes[0];
		box[1] = bound.y/(double)boxes[1];
		box[2] = bound.z/(double)boxes[2];
		boxVolume = box[0]*box[1]*box[2];
		this.quantity = new double[boxes[0]][boxes[1]][boxes[2]]; 
		this.diffusivity = diffusivity;
		this.decayRate = decayRate;
	}	
	
	/** Return the number of boxes in (x,y,z) directions. */
	public int[] getBoxes() { return boxes; }
	/** Return the size of each box (x,y,z) in microns. */
	public double[] getBox() { return box; }	
	
	/**
	 * Creates a linear concentration gradient in the direction specified by 'axis' (x=0, y=1, z=2)
	 */
	public void linearGradient(int axis, double startConc, double endConc) {
		assert ((axis >= 0) && (axis <= 2)) :
			"Chemical field gradient - axis selection out of range [0,2]\n" +
			"Check axis is one of: x-axis = '0', y = '1', or z = '2'";
		
		
		int[] index = {0,0,0};
		
		double grad = (endConc - startConc)/(double)boxes[axis];
        
        for(index[0] = 0; index[0]<boxes[0]; index[0]++)
			for(index[1] = 0; index[1]<boxes[1]; index[1]++)
				for(index[2] = 0; index[2]<boxes[2]; index[2]++)
					setConc(index[0], index[1], index[2], startConc + index[axis]*grad);
	}
	
	/** Adds a quantity of chemical to the box containing position v. */
	public void addQuantity(Vector3d v, double q) {
		int[] b = boxCoords(v);
		addQuantity(b[0],b[1],b[2],q);
	}
	/** Adds a quantity of chemical to the box (x,y,z). */
	public void addQuantity(int x, int y, int z, double q) {
		quantity[x][y][z] += q;
		if(quantity[x][y][z] < 0) quantity[x][y][z] = 0;
	}
	
	/** Sets the concentration of the box containing position v. */
	public void setConc(Vector3d v, double c) {
		int[] b = boxCoords(v);
		setConc(b[0],b[1],b[2],c);
	}
	/** Sets the concentration of the box (x,y,z). */
	public void setConc(int x, int y, int z, double c) {
		quantity[x][y][z] = c*boxVolume;
	}
	/** Sets the concentration of the field */
	public void setConc(double c) {		
		for(int i=0;i<boxes[0];i++)
			for(int j=0;j<boxes[1];j++)
				for(int k=0;k<boxes[2];k++) quantity[i][j][k] = c*boxVolume;
	}
	
	/** Gets the concentration of the field at the position v in molecules/(micron)^3. */
	public double getConc(Vector3d v) {
		int[] b = boxCoords(v);
		return getConc(b[0],b[1],b[2]);
	}	
	/** Gets the concentration of the field in the box (x,y,z) in molecules/(micron)^3. */
	public double getConc(int i, int j, int k) {
		return quantity[i][j][k]/boxVolume;
	}
	
	/** Returns the total quantity of chemical in the field. */
	public double totalQuantity() {
		double t = 0;
		for(int i=0;i<boxes[0];i++)
			for(int j=0;j<boxes[1];j++)
				for(int k=0;k<boxes[2];k++) t += quantity[i][j][k];
		return t;
	}
	
	/** Returns the integer coordinates of the box containing the position v. */ 
	public int[] boxCoords(Vector3d v) {
		/* Check the bounds are valid */
		int x = (int)(v.x/box[0]);
		int y = (int)(v.y/box[1]);
		int z = (int)(v.z/box[2]);
		x = (x >= boxes[0] ? boxes[0]-1 : x);
		y = (y >= boxes[1] ? boxes[1]-1 : y);
		z = (z >= boxes[2] ? boxes[2]-1 : z);
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (z < 0) z = 0;
		return new int[] {x, y, z};	
	}
	
	/**
	 * Update the chemical field by diffusing and decaying the chemical present.
	 */
	public void update() {
		diffuse();
		decay();
	}
	
	/**
	 * Decay the chemical present in the field.
	 */
	public void decay() {
		for(int i=0;i<boxes[0];i++)
			for(int j=0;j<boxes[1];j++)
				for(int k=0;k<boxes[2];k++) quantity[i][j][k] *= (1 - decayRate*sim.getDt());
	}	
	
	/**
	 * Diffuse the chemical present in the field.
	 */
	public void diffuse() {
		double[][][] before = quantity;
		/* Index of the box in positive (negative) .. direction, taking account of the boundary conditions */
		int xAbove, xBelow, yAbove, yBelow, zAbove, zBelow;
		/* Quantity of chemical leaving the box in the positive (negative) .. direction */
		double qxAbove, qxBelow, qyAbove, qyBelow, qzAbove, qzBelow;
		/* Flags for leakiness at borders */
		boolean leaky[] = sim.getLeaky();
		double  leakyRate[] = sim.getLeakyRate();
		/*
		 * Flux of molecules crossing in the positive x-direction (Fick's law)
		 * 	J = -D(dC/dx) = -D*(C(x+dx)-C(x))/dx =  -D*(N(x+dx)-N(x))/((dx)^2*dy*dz)  molecules/(micron)^2/sec
		 * Number of molecules transferred in the positive x-direction over dt
		 * 	xAbove = J*(dy*dz)*dt = -((D*dt)/(dx)^2)*(N(x+dx)-N(x)) = -kX*(N(x+dx)-N(x))
		 * where kX = (D*dt)/(dx)^2 is a dimensionless constant
		 */
		double normX = sim.getDt()/Math.pow(box[0],2);
		double normY = sim.getDt()/Math.pow(box[1],2);
		double normZ = sim.getDt()/Math.pow(box[2],2);
		double kX = diffusivity*normX;
		double kY = diffusivity*normY;
		double kZ = diffusivity*normZ;
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
					
					if(xAbove != -1) {
						/* Calculate the quantity of chemical leaving the box in this direction */
						qxAbove = -kX*(before[xAbove][j][k]-before[i][j][k]);
						/* Add that quantity to the box above */ 
						quantity[xAbove][j][k] += qxAbove;
						/* Remove it from this box */
						quantity[i][j][k] -= qxAbove;
					} else {
						if (leaky[0]) {
							/* Calculate the quantity of chemical leaving the box in this direction (0 outside box if leaky)*/
							qxAbove = normX*leakyRate[0]*before[i][j][k];
							/* Remove it from this box */
							quantity[i][j][k] -= qxAbove;
						}
					}
					
					if(xBelow != -1) {
						qxBelow = -kX*(before[xBelow][j][k]-before[i][j][k]);
						quantity[xBelow][j][k] += qxBelow;
						quantity[i][j][k] -= qxBelow;
					} else {
						if (leaky[1]) {
							qxBelow = normX*leakyRate[1]*before[i][j][k];
							quantity[i][j][k] -= qxBelow;
						}
					}
					
					if(yAbove != -1) {
						qyAbove = -kY*(before[i][yAbove][k]-before[i][j][k]);
						quantity[i][yAbove][k] += qyAbove;
						quantity[i][j][k] -= qyAbove;
					} else {
						if (leaky[2]) {
							qyAbove = normY*leakyRate[2]*before[i][j][k];
							quantity[i][j][k] -= qyAbove;
						}
					}
					
					if(yBelow != -1) {
						qyBelow = -kY*(before[i][yBelow][k]-before[i][j][k]);
						quantity[i][yBelow][k] += qyBelow;
						quantity[i][j][k] -= qyBelow;
					} else {
						if (leaky[3]) {
							qyBelow = normY*leakyRate[3]*before[i][j][k];
							quantity[i][j][k] -= qyBelow;
						}
					}
					
					if(zAbove != -1) {
						qzAbove = -kZ*(before[i][j][zAbove]-before[i][j][k]);
						quantity[i][j][zAbove] += qzAbove;
						quantity[i][j][k] -= qzAbove;
					} else {
						if (leaky[4]) {
							qzAbove = normZ*leakyRate[4]*before[i][j][k];
							quantity[i][j][k] -= qzAbove;
						}
					}
					
					if(zBelow != -1) {
						qzBelow = -kZ*(before[i][j][zBelow]-before[i][j][k]);
						quantity[i][j][zBelow] += qzBelow;
						quantity[i][j][k] -= qzBelow;
					} else {
						if (leaky[3]) {
							qzBelow = normZ*leakyRate[5]*before[i][j][k];
							quantity[i][j][k] -= qzBelow;
						}
					}
					
				}
	}
	
}
