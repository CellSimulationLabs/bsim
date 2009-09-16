package bsim;

import javax.vecmath.Vector3d;

public class BSimChemicalField {
	
	/* 1 molecule/(micron)^3 = 1.6 nM = 1.6 nanomol/L */ 
	
	protected BSim sim;
	/** The quantity of chemical in the box (i,j,k) */
	protected double[][][] quantity; // number of molecules
	/** sim.getBound() */
	protected Vector3d bound;
	/** Number of partitions in each dimension */
	protected int[] partition = new int[3];
	/** Box size */
	protected double[] box = new double[3]; // microns
	protected double boxVolume;
	
	public BSimChemicalField(BSim sim, int[] partition) {
		bound = sim.getBound();
		this.partition = partition;
		box[0] = bound.x/partition[0];
		box[1] = bound.x/partition[1];
		box[2] = bound.x/partition[2];
		boxVolume = box[0]*box[1]*box[2];
		quantity = new double[partition[0]][partition[1]][partition[2]]; 
	}	
	
	/**
	 * Creates a linear concentration gradient in the z direction
	 */
	public void linearZ(double startConc, double endConc) {
		double startQuantity = startConc*boxVolume;
		double endQuantity = endConc*boxVolume;
		double grad = (endQuantity - startQuantity)/partition[2];
		
		for(int i=0;i<partition[0];i++)
			for(int j=0;j<partition[0];j++)
				for(int k=0;k<partition[0];k++)
					quantity[i][j][k] = startQuantity + k*grad;
	}
	
	/** Adds a number of molecules to the box containing position v */
	public void addQuantity(Vector3d v, double q) {
		int[] b = boxCoords(v);
		addQuantity(b[0],b[1],b[2],q);
	}
	/** Adds a number of molecules to the box (i,j,k) */
	public void addQuantity(int i, int j, int k, double q) {
		quantity[i][j][k] += q;
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
	
	/** Returns the coordinates of the box containing the position v */ 
	public int[] boxCoords(Vector3d v) {
		return new int[] {(int)(v.x/box[0]), (int)(v.y/box[1]), (int)(v.z/box[2])};
	}
	public int[] getPartition() { return partition; }
	public double[] getBox() { return box; }	
	
}
