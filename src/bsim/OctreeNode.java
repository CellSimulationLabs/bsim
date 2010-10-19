package bsim; 

import javax.vecmath.Vector3d;
import java.awt.Color;
import bsim.BSimOctreeChemicalField;

public class OctreeNode
{
	/**Parent of node, for root this is null*/
	 protected OctreeNode parent; 
	 
	 /**Color of the node, used when drawing*/
	 protected Color nodeColor = Color.blue; 
	
	 /**Location of centre of node in space*/
	 protected Vector3d centre; 
	
	 /**Dimension of the node*/
	 protected double length; 
	 
	 /**Not sure how to compute diffusion function, may use diffusion
	  * as a property of an individual node rather than a field
	  */
	// protected double diffusivity; 
	 
	 /**Volume of node (simply length^3*/
	 protected double volume; 
	
	 /**Depth in octree structure, root has depth 0*/
	 protected int depth = 0; 
	
	 /**subNodes of octree, these can have subnodes of their own*/
	 protected OctreeNode subNodes[] = new OctreeNode[8]; 
	 
	 /**For checking in division algorithm*/
	 public boolean processed = false; 
	 
	 /**stores the neighbors of an octree node 
	  * (necessary for the diffusion function), computed with setKids() function
	  */
	 private OctreeNode neighbors[] = new OctreeNode[6];
	 
	 /**number of molecules in the chemical field box*/
	 public double quantity; 
	
	
	//////////////////////////////////////////////////////////////////////////////
	//Constructors//
	 
	/**Basic Constructor*/
	public OctreeNode(){}
	
	/**Root Constructor - only used to make first octree*/
	public OctreeNode(Vector3d Centre, double Length) 
	{
		
		centre = Centre;
		length = Length;
		volume = Math.pow(length, 3);
					
		//Initialises the subtrees
		for (int i=0;  i<8; i++){ 				
			subNodes[i]=new OctreeNode();
		}
		
		//All of the root nodes neighbors are itself, to halt diffusion out of it
		for (int i=0; i<6; i++){
			this.neighbors[i]=this; 
		}
		
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//Getters//	
	
	/**Getter for depth*/
	public int getDepth(){
		return this.depth; 
	}
	
	/**Getter for centre*/
	public Vector3d getCentre(){
		return this.centre;
	}
	
	/**Getter for length*/
	public double getLength(){
		return this.length;
	}
	
	/**Getter for subNode, i is index of subnode*/
	public OctreeNode getsubNode(int i){
		return this.subNodes[i]; 
	}
	
	/**Getter for nodecolor*/
	public Color getnodeColor(){
		return this.nodeColor;
	}
	
	
	
	
	/**Setter to populate kids with appropriate centers, lengths*/
	public void setKids(BSimOctreeChemicalField Field){ 
			
		Vector3d plusX = new Vector3d((this.length)/4, 0, 0);
		Vector3d plusY = new Vector3d(0,(this.length)/4, 0);
		Vector3d plusZ = new Vector3d(0,0,(this.length)/4);

		//sets the length to be 1/2 that of previous generation		
		for (int i=0;  i<8; i++){
			this.subNodes[i]= new OctreeNode(); 
			this.subNodes[i].length=this.length/2;
			this.volume = Math.pow(this.length, 3);
			this.subNodes[i].parent=this;
			this.subNodes[i].depth = this.depth+1; 
		}

		//initializes centers to be same as parent		
		for (int i=0;  i<8; i++){
			
			Vector3d temp = new Vector3d(0,0,0);
			this.centre.get(temp);
			this.subNodes[i].centre= temp;
			
		}
		
		
		/**The following sets the centres for the subNodes of an octree.
		 * There is probably a more elegant way to do this (cyclic
		 * permutation function or something?). 
		 */
		
		this.subNodes[0].centre.sub(this.subNodes[0].centre,plusX);
		this.subNodes[0].centre.add(this.subNodes[0].centre,plusY);
		this.subNodes[0].centre.sub(this.subNodes[0].centre,plusZ);
		
		this.subNodes[1].centre.add(this.subNodes[1].centre,plusX);
		this.subNodes[1].centre.add(this.subNodes[1].centre,plusY);
		this.subNodes[1].centre.sub(this.subNodes[1].centre,plusZ);
		
		this.subNodes[2].centre.sub(this.subNodes[2].centre,plusX);
		this.subNodes[2].centre.sub(this.subNodes[2].centre,plusY);
		this.subNodes[2].centre.sub(this.subNodes[2].centre,plusZ);
		
		this.subNodes[3].centre.add(this.subNodes[3].centre,plusX);
		this.subNodes[3].centre.sub(this.subNodes[3].centre,plusY);
		this.subNodes[3].centre.sub(this.subNodes[3].centre,plusZ);
		
		this.subNodes[4].centre.sub(this.subNodes[4].centre,plusX);
		this.subNodes[4].centre.add(this.subNodes[4].centre,plusY);
		this.subNodes[4].centre.add(this.subNodes[4].centre,plusZ);
		
		this.subNodes[5].centre.add(this.subNodes[5].centre,plusX);
		this.subNodes[5].centre.add(this.subNodes[5].centre,plusY);
		this.subNodes[5].centre.add(this.subNodes[5].centre,plusZ);
		
		this.subNodes[6].centre.sub(this.subNodes[6].centre,plusX);
		this.subNodes[6].centre.sub(this.subNodes[6].centre,plusY);
		this.subNodes[6].centre.add(this.subNodes[6].centre,plusZ);
		
		this.subNodes[7].centre.add(this.subNodes[7].centre,plusX);
		this.subNodes[7].centre.sub(this.subNodes[7].centre,plusY);
		this.subNodes[7].centre.add(this.subNodes[7].centre,plusZ);
		
		
		//Set their entry in the space lookup field
		for (int i=0;  i<8; i++){
			this.subNodes[i].setSpaceLookup(Field);
		}
	
				
		/**Following sets the neighbors of the octree subnodes, again
		 * there is probably a more elegant way to do this, but it is easier
		 * to visualise. The indices correspond to 0th is +x dir, 1 is -x dir, 
		 * 2 is +y dir, 3 is -y dir, 4 is +z dir, 5 is -zdir. 
		 */
				this.subNodes[0].neighbors[0]=this.subNodes[1];
				this.subNodes[0].neighbors[1]=this.subNodes[0].parent;
				this.subNodes[0].neighbors[2]=this.subNodes[0].parent;
				this.subNodes[0].neighbors[3]=this.subNodes[2];
				this.subNodes[0].neighbors[4]=this.subNodes[4];
				this.subNodes[0].neighbors[5]=this.subNodes[0].parent;

				this.subNodes[1].neighbors[0]=this.subNodes[1].parent;
				this.subNodes[1].neighbors[1]=this.subNodes[0];
				this.subNodes[1].neighbors[2]=this.subNodes[1].parent;
				this.subNodes[1].neighbors[3]=this.subNodes[3];
				this.subNodes[1].neighbors[4]=this.subNodes[5];
				this.subNodes[1].neighbors[5]=this.subNodes[1].parent;
				
				this.subNodes[2].neighbors[0]=this.subNodes[3];
				this.subNodes[2].neighbors[1]=this.subNodes[2].parent;
				this.subNodes[2].neighbors[2]=this.subNodes[0];
				this.subNodes[2].neighbors[3]=this.subNodes[2].parent;
				this.subNodes[2].neighbors[4]=this.subNodes[6];
				this.subNodes[2].neighbors[5]=this.subNodes[2].parent;
				
				this.subNodes[3].neighbors[0]=this.subNodes[3].parent;
				this.subNodes[3].neighbors[1]=this.subNodes[2];
				this.subNodes[3].neighbors[2]=this.subNodes[1];
				this.subNodes[3].neighbors[3]=this.subNodes[3].parent;
				this.subNodes[3].neighbors[4]=this.subNodes[7];
				this.subNodes[3].neighbors[5]=this.subNodes[3].parent;
				
				this.subNodes[4].neighbors[0]=this.subNodes[5];
				this.subNodes[4].neighbors[1]=this.subNodes[4].parent;
				this.subNodes[4].neighbors[2]=this.subNodes[4].parent;
				this.subNodes[4].neighbors[3]=this.subNodes[6];
				this.subNodes[4].neighbors[4]=this.subNodes[4].parent;
				this.subNodes[4].neighbors[5]=this.subNodes[0];
				
				this.subNodes[5].neighbors[0]=this.subNodes[5].parent;
				this.subNodes[5].neighbors[1]=this.subNodes[4];
				this.subNodes[5].neighbors[2]=this.subNodes[5].parent;
				this.subNodes[5].neighbors[3]=this.subNodes[7];
				this.subNodes[5].neighbors[4]=this.subNodes[5].parent;
				this.subNodes[5].neighbors[5]=this.subNodes[1];
				
				this.subNodes[6].neighbors[0]=this.subNodes[7];
				this.subNodes[6].neighbors[1]=this.subNodes[6].parent;
				this.subNodes[6].neighbors[2]=this.subNodes[4];
				this.subNodes[6].neighbors[3]=this.subNodes[6].parent;
				this.subNodes[6].neighbors[4]=this.subNodes[6].parent;
				this.subNodes[6].neighbors[5]=this.subNodes[2];
				
				this.subNodes[7].neighbors[0]=this.subNodes[7].parent;
				this.subNodes[7].neighbors[1]=this.subNodes[6];
				this.subNodes[7].neighbors[2]=this.subNodes[5];
				this.subNodes[7].neighbors[3]=this.subNodes[7].parent;
				this.subNodes[7].neighbors[4]=this.subNodes[7].parent;
				this.subNodes[7].neighbors[5]=this.subNodes[3];
				
				//end of massive combinatorics exercise!
				
		
		
		
	}

	/**Populates the OctreeChemicalField SpaceLookup structure with the location of a new Octree
	 * BAD CODE FOLLOWS.....:( WILL IMPROVE*/
	public void setSpaceLookup(BSimOctreeChemicalField Field){
		
		//needs to take the center and the length, work out what bits of the 
		//spaceLookup structure that it is going to cover and then 
		//Maybe it should look at what it's parent was up to? That way you don't
		//have octrees that think they occupy the same border area
		
		if(parent==null){
			//condition where this is the first octree, so occupies all SpaceLookup
			for(int i=0; i<Field.Resolution; i++){
				for(int j=0; j<Field.Resolution; j++){
					for(int k=0; k<Field.Resolution; k++){
						
						Field.SpaceLookup[i][j][k]=this; 
						
						//sets color value to be centre
						colorFromCentre(this);
						
						
					
					}
				}
			}
			
		}
		
		//following for parent not equal null.....
		//Works out where to start and end.....
		int Xl=(int)(Field.Resolution*((centre.x-(length/2))/Field.bound.x));
		int Xh=(int)(Field.Resolution*((centre.x+(length/2))/Field.bound.x));
		int Yl=(int)(Field.Resolution*((centre.y-(length/2))/Field.bound.y));
		int Yh=(int)(Field.Resolution*((centre.y+(length/2))/Field.bound.y));
		int Zl=(int)(Field.Resolution*((centre.z-(length/2))/Field.bound.z));
		int Zh=(int)(Field.Resolution*((centre.z+(length/2))/Field.bound.z));
		
		//uses these to put record in appropriate spatial index boxes
		for(int i=Xl; i<Xh; i++){
			for(int j=Yl; j<Yh; j++){
				for(int k=Zl; k<Zh; k++){
					
			
					
					Field.SpaceLookup[i][j][k]=this; 
					
					//sets color value to be centre
					colorFromCentre(this);
				
				}
			}
		}
		
		
		
	}
	

	//////////////////////////////////////////////////////////////////////////////
	//Traversers - recursive functions that go through octree structure in different ways//
	
	/**Pre-Order full traverse*/
	public static void preOrderfull(OctreeNode t){
		
		if(t!=null){
			visit(t);	//visit tree root
			
			
			for (int i=0;  i<8; i++){
				preOrderfull(t.subNodes[i]);
			}
		}
		
	}
	
	/**In-Order full traverse*/ 
	public static void inOrderfull(OctreeNode t){	
		if(t!=null){
			
			inOrderfull(t.subNodes[0]);
			visit(t);
			inOrderfull(t.subNodes[1]);
			
			inOrderfull(t.subNodes[2]);
			
			inOrderfull(t.subNodes[3]);
			
			inOrderfull(t.subNodes[4]);
			
			inOrderfull(t.subNodes[5]);
			
			inOrderfull(t.subNodes[6]);
		
			inOrderfull(t.subNodes[7]);
	
		}
	}
	
	/**Post order traverse with visit function. This is the most logical
	 * traverse, outputs octrees in 'left to right, bottom to top' sense
	 * this is the one you'd use if outputting to screen*/
	public static void postOrderfull(OctreeNode t){
		if(t!=null){
			
			for (int i=0;  i<8; i++){
				postOrderfull(t.subNodes[i]);
			
			}
			
			visit(t);
		}
	}
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////
	//Miscellanious Functions//
	
	/**Troubleshooting visiting function, outputs useful stuff*/
	public static void visit(OctreeNode t){
		System.out.print(t.centre + " ");
		System.out.print(t.depth + " ");
	}
	
	
	/**Sets the nodeColor value as a function of the position of octree, 
	 * useful for troubleshooting
	 * @param t
	 */
	public void colorFromCentre(OctreeNode t){
		this.nodeColor = new Color((int)this.centre.x, (int)this.centre.y, (int)this.centre.z );
	}
	
	/**Sets nodeCololr value as a function of amount of chemical in box
	 * 
	 * @param t
	 */
	public void colorFromConc(){
		this.nodeColor = new Color((int)this.quantity , 0, 0);
	}
	
	
	/**Diffuses chemicals through whole octree chemi field*/
	public void diffuse(OctreeNode t, double diffusivity, double Dt){
		
		//System.out.print("Diffusing....\n");
		/*
		 * Flux of molecules crossing in the positive x-direction (Fick's law)
		 * 	J = -D(dC/dx) = -D*(C(x+dx)-C(x))/dx =  -D*(N(x+dx)-N(x))/((dx)^2*dy*dz)  molecules/(micron)^2/sec
		 * Number of molecules transferred in the positive x-direction over dt
		 * 	xAbove = J*(dy*dz)*dt = -((D*dt)/(dx)^2)*(N(x+dx)-N(x)) = -kX*(N(x+dx)-N(x))
		 * where kX = (D*dt)/(dx)^2 is a dimensionless constant
		 * 
		 * in this case tey are cubes, so it's always the same 
		 * 
		 * NB IN FINAL REVISION, USE NEIGHBORS DEPTH TO CALCULATE HOWEVER MANY OF 
		 * THESE AS ARE NECESSARY
		 * 
		 */
		//double k = (diffusivity*0.01)/Math.pow(length,2);
		
			//post order traverse of structure to do the diffusion
		
		if(t!=null){
			
			for (int i=0;  i<8; i++){
				diffuse(t.subNodes[i], diffusivity, Dt);
			
			}
		
		
			//INSERT ACTIONABLE CODE HERE
			for(int i=0; i<6; i++){
				
				double Before = 0; 
				Before= this.quantity;
				
				if(t.neighbors[i]!=null){
					
					double k = (diffusivity*Dt)/Math.pow(this.length,2);
					
					double q = k*(t.neighbors[i].quantity - Before);
					
					t.neighbors[i].quantity+=q;
					t.quantity-=q;
								/*	if (t.neighbors[i].quantity < t.quantity){
									t.neighbors[i].quantity += t.quantity/2;
									t.quantity = t.quantity/2;} */
				
				}
			}

		}
		
								
								
		
	}
	
	
	public void decay(OctreeNode t, double decayRate,double Dt){
		//post order traverse of structure to do the decay
		
		if(t!=null){
			
			for (int i=0;  i<8; i++){
				decay(t.subNodes[i], decayRate, Dt);
			
			}
		
		
			t.quantity *= (1-decayRate*Dt);
			
		}
	}
	
	
	
}


