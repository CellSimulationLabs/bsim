package bsim; 

import javax.vecmath.Vector3d;
import java.lang.Math;
import bsim.OctreeNode;
import bsim.geometry.BSimMesh;
import bsim.geometry.BSimTriangle;

/**Chemical field that covers the whole Simulation area*/
public class BSimOctreeChemicalField
{	
	protected BSim sim;	
	
	/**Maximum depth of octree structure*/
	@SuppressWarnings("unused")
	private int MaxDepth;
	
	/** sim.getBound() */
	protected Vector3d bound;
	
	/**Number of boxes in any direction*/
	protected int Resolution; 

	/**Truely hackish way to store where Octree's are, this will be changed to
	 * an appropriate traversal function rather than a straight lookup. As it is
	 * the memory saving use of octree's is lost because there is an array of the
	 * maximum resolution. Should not be hard to replace with a traversal function, 
	 * case of deciding which is most efficient and running some tests
	 */
	protected OctreeNode[][][] SpaceLookup; 
	
	/**Global diffusivity for chemical field, again, not sure if best way to do this
	 * is locally at level of nodes or globally at level of fields. Applies to
	 * decay rate and diffusivity
	 */
	protected double diffusivity; // (microns)^2/s
	/** Fraction of chemical decaying per second, quantity(t+dt) = quantity(t)*(1-decayRate*dt) */
	protected double decayRate; // 1/seconds 

	
	
	
	//is doing diffusion globally the best way to do chemical fields? different fields in different
	//media with different diffusivity may be easier to do by manipulating these factors
	//in the OctreeNode object, rather than the field object, that way a segementation algorithm
	//that divides things into 'outside' or 'inside' a mesh can also set the diffusivity. 
	
	//for now,as a proof of concept it should not be too hard
	
	//ALSO TO BE CONSIDERED SHOULD BE WHERE THE OCTREE DIVISION ALGORITHM OCCURS, OBVIOUSLY IT NEEDS
	//TO BE PUT INTO THE CHEMICAL FIELD CLASS, BUT MAYBE ANOTHER WYA TO DO DO IT THAT TAKES CARE OF
	//THE NEED FOR SEPARATE FIELDS??
	
	
	
	//constructors
	
	/**Basic Constructor*/
	public BSimOctreeChemicalField(BSim sim, int MaxDepth, double decayRate){
		
		this.sim=sim; 
		this.bound=sim.getBound();
		this.MaxDepth=MaxDepth; 
		/**The maximum number of nodes in any direction (x,y,z)*/
		this.Resolution = (int) Math.pow(2,MaxDepth);
		this.SpaceLookup = new OctreeNode[Resolution][Resolution][Resolution];
		this.decayRate = decayRate; 
		
		//int z=0;
		
		 
	}
	
	/**Getter for entire SpaceLookup array*/
	public OctreeNode[][][] getSpaceLookup(){ 
		
		return SpaceLookup; 
		
	}
	
	/**Getter for an octree from a given location*/
	public OctreeNode getOctree(Vector3d Location){
		
		/**Finds location in terms of box from the input vector
		 * NB: how the double to int cast casts is important, floor ceil etc 
		 * will give different results for the boundaries 
		 */
		
		int x =  (int) (Resolution*(Location.x)/(this.bound.x));
		int y =  (int) (Resolution*(Location.y)/(this.bound.y));
		int z =  (int) (Resolution*(Location.z)/(this.bound.z));
		
		return this.SpaceLookup[x][y][z];
		
		
		}
	
	/**Getter for octree from spaceLookup index (shoudn't need to use this)*/
	public OctreeNode getOctree(int x, int y, int z){
		return this.SpaceLookup[x][y][z];		
		}

	
	/**Adds a quantity of chemical to octree node containing position v*/
	public void addQuantity(Vector3d v, double q){
		
		getOctree(v).quantity+=q;
		//getOctree(v).colorFromConc();
		
	}

	/**Sets the concentration of chemical (quantity/volume) to octree node containing position v*/
	public void setConc(Vector3d v, double c){
		OctreeNode temp = getOctree(v);
		temp.quantity=c;//*(getOctree(v).volume); 
		getOctree(v).quantity=c;//*(getOctree(v).volume); 
	}
	
	/**Sets the concentration (quantity/volume) for the entire field*/
	public void setConc(double c){
		for(int i=0; i<this.Resolution; i++){
			for(int j=0; j<this.Resolution; j++){
				for(int k=0; k<this.Resolution; k++){
					this.SpaceLookup[i][j][k].quantity=c*(SpaceLookup[i][j][k].volume); 
				}
			}
		}
	}
	
	/**Gets the concentration (quantitity/volume) of the field at point v*/
	public double getConc(Vector3d v){
		return (getOctree(v).quantity)/(getOctree(v).volume);
	}
	
	/**Returns the total quantity of chemical in teh field*/
	public double totalQuantity(){
		double t=0; 
		for(int i=0; i<this.Resolution; i++){
			for(int j=0; j<this.Resolution; j++){
				for(int k=0; k<this.Resolution; k++){
					t+= this.SpaceLookup[i][j][k].quantity; 
				}
			}
		}
		
		return t; 
		
	}
	
	/**bunches together the two functions that update chemical field*/
	public void update(){
		//diffuse();
		decay();
	}
	
	
	//Decay and diffuse implemented on node level
	
	/**Decays each box as per it's decay rate, currently decay rate is global to FIELD*/
	public void decay(){
	}
	
	/**Diffuse is a bit more tricky............ Implemented diffuse as a function on the node level
	 * that way it is easier to know what is going on*/
	public void diffuse(){	
	}

	
	
	/**Intensely hacky function that subdivides an octree into the shape of a mesh*/
	
	public void FitOctreeFieldToMesh(BSimMesh theMesh, int maxdepth){
		
		/**Goes through entire spaceLookup (in final version, traverses entire tree*/
		for (int i=0; i<(Math.pow(2,maxdepth)); i++){
			for (int j=0; j<(Math.pow(2,maxdepth)); j++){
				for (int k=0; k<(Math.pow(2,maxdepth)); k++){
					
					/**work with TestNode rather than accessor*/
					OctreeNode TestNode = this.getOctree(i,j,k);
					
					/**If not has not already been checked against triangels for intersection*/
					if (TestNode.processed == false){
						
						//now it has been processed
						this.getOctree(i,j,k).processed = true; 
						
						
						//WARNING: FOLLOWING IS A DISGUSTING BRUTE FORCE METHOD: 
						//key to points in 19-09-10 page of notebook. Key as in
						//conversion from octree index to actual geometry - will
						//include this in documentation
							
						
							//array of vectors to store corners
							Vector3d p[] = new Vector3d[8];
							
							for (int l=0; l<8; l++){
								Vector3d temp = new Vector3d(TestNode.getCentre());
								p[l]=temp;
								
								
							}
											
						
							Vector3d plusX = new Vector3d((TestNode.getLength())/2, 0, 0);
							Vector3d plusY = new Vector3d(0,(TestNode.getLength())/2, 0);
							Vector3d plusZ = new Vector3d(0,0,(TestNode.getLength())/2);
						
							
							/**Computes location of corners of the node*/
							p[0].sub(p[0],plusX);
							p[0].add(p[0],plusY);
							p[0].sub(p[0],plusZ);
							
							p[1].add(p[1],plusX);
							p[1].add(p[1],plusY);
							p[1].sub(p[1],plusZ);
							
							p[2].sub(p[2],plusX);
							p[2].sub(p[2],plusY);
							p[2].sub(p[2],plusZ);
							
							p[3].add(p[3],plusX);
							p[3].sub(p[3],plusY);
							p[3].sub(p[3],plusZ);
							
							p[4].sub(p[4],plusX);
							p[4].add(p[4],plusY);
							p[4].add(p[4],plusZ);
							
							p[5].add(p[5],plusX);
							p[5].add(p[5],plusY);
							p[5].add(p[5],plusZ);
							
							p[6].sub(p[6],plusX);
							p[6].sub(p[6],plusY);
							p[6].add(p[6],plusZ);
							
							p[7].add(p[7],plusX);
							p[7].sub(p[7],plusY);
							p[7].add(p[7],plusZ);
						
					
						
						
				
					/**for each triangle looks for collisions, it is unnecessary to
					 * do each one  but was useful for troubleshooting */		
					for(BSimTriangle t:theMesh.getFaces()){
						
						
					
						//checks against 12 lines
						Boolean intersect[] = new Boolean[13];
						
						//checks for intersection between each triangle and each of the 12
						//lines that makes up octree node
						 intersect[0] = intersectVectorTriangle(p[0],p[1],t);
						 intersect[1] = intersectVectorTriangle(p[0],p[2],t);
						 intersect[2] = intersectVectorTriangle(p[0],p[4],t);
						 intersect[3] = intersectVectorTriangle(p[1],p[3],t);
						 intersect[4] = intersectVectorTriangle(p[1],p[5],t);
						 intersect[5] = intersectVectorTriangle(p[2],p[3],t);
						 intersect[6] = intersectVectorTriangle(p[2],p[6],t);
						 intersect[7] = intersectVectorTriangle(p[3],p[7],t);
						 intersect[8] = intersectVectorTriangle(p[4],p[5],t);
						 intersect[9] = intersectVectorTriangle(p[4],p[6],t);
						 intersect[10] = intersectVectorTriangle(p[5],p[7],t);
						 intersect[11] = intersectVectorTriangle(p[6],p[7],t);
						 intersect[12]=false; //hackish way to check entire vector
						 
						 for(int u=0; u<11; u++){
							 if(intersect[u]==true){
								 intersect[12]=true; //final value used in test
							 }
							 
						 }
						 
						 /**if there has been a collision*/
						if(intersect[12]==true){
							//troubleshooting output:
							this.getOctree(i,j,k).visit(this.getOctree(i,j,k));
							System.out.print("\n");
						
						
						this.getOctree(i,j,k).processed=true; //wont cover this again
						
						this.getOctree(i,j,k).setKids(this);
						
						break; //breaks is to stop dividing once you've detected your first intersection
						//WHAT'S TO SAY THE FIRST INTERSECTION IS THE MOST IMPORTANT ? 
						}
						
						
						
					}
					
					
					} //end of if processed == false bracket
					
					
					
				}
			}
			
		}
	}
	
	
	

/**Intersection methods stolen from Antos!!*/
		public static boolean intersectVectorTriangle(Vector3d startPos, Vector3d endPos, BSimTriangle tri/*, BSimCollision coll*/){
			Vector3d ab = new Vector3d();
			Vector3d ac = new Vector3d();
			Vector3d qp = new Vector3d();
			
			ab.sub(tri.getVertCoords(1), tri.getVertCoords( 0));
			ac.sub(tri.getVertCoords( 2), tri.getVertCoords( 0));
			qp.sub(startPos, endPos); //value of vec3d is difference between startpos and endpos
			
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
			ap.sub(startPos, tri.getVertCoords( 0));
			
			double oodenom = 1.0/denom;
			
			double t = ap.dot(normal) * oodenom;
			if (Math.abs(t) > Math.abs(denom)) return false; 
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
			
			//COMMENTED THIS OUT TO MAKES IT WORKS
			//coll.set(t, qp, startPos);
			
			return true;
		
		}
	
	
	
}



