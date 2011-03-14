package bsim; 

import java.awt.Color;
import bsim.geometry.BSimMesh;
import bsim.geometry.BSimTriangle;

import javax.vecmath.Vector3d;

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
	/**Setter to populate kids with appropriate centers, lengths WITHOUT FIELD*/
	public void setKids(){ 
			
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
	
	/**method to set nodes to mesh without chemial field. Field can now be discarded
	 * all methods are performed on the level of the octree*/
	public void setNodestoMesh(BSimMesh theMesh, OctreeNode t){
		
			if(t!=null){
			
			for (int i=0;  i<8; i++){
				setNodestoMesh(theMesh, t.subNodes[i]);
			
			}
			
			if (t.processed == false){
				
				//now it has been processed
				t.processed = true; 
				
				
				//WARNING: FOLLOWING IS A DISGUSTING BRUTE FORCE METHOD: 
				//key to points in 19-09-10 page of notebook. Key as in
				//conversion from octree index to actual geometry - will
				//include this in documentation
					
				
					//array of vectors to store corners
					Vector3d p[] = new Vector3d[8];
					
					for (int l=0; l<8; l++){
						Vector3d temp = new Vector3d(t.getCentre());
						p[l]=temp;
						
						
					}
									
				
					Vector3d plusX = new Vector3d((t.getLength())/2, 0, 0);
					Vector3d plusY = new Vector3d(0,(t.getLength())/2, 0);
					Vector3d plusZ = new Vector3d(0,0,(t.getLength())/2);
				
					
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
			for(BSimTriangle tri:theMesh.getFaces()){
				
				
			
				//checks against 12 lines
				Boolean intersect[] = new Boolean[13];
				
				//checks for intersection between each triangle and each of the 12
				//lines that makes up octree node
				intersect[0] = intersectVectorTriangle(p[0],p[1],tri);
				 intersect[1] = intersectVectorTriangle(p[0],p[2],tri);
				 intersect[2] = intersectVectorTriangle(p[0],p[4],tri);
				 intersect[3] = intersectVectorTriangle(p[1],p[3],tri);
				 intersect[4] = intersectVectorTriangle(p[1],p[5],tri);
				 intersect[5] = intersectVectorTriangle(p[2],p[3],tri);
				 intersect[6] = intersectVectorTriangle(p[2],p[6],tri);
				 intersect[7] = intersectVectorTriangle(p[3],p[7],tri);
				 intersect[8] = intersectVectorTriangle(p[4],p[5],tri);
				 intersect[9] = intersectVectorTriangle(p[4],p[6],tri);
				 intersect[10] = intersectVectorTriangle(p[5],p[7],tri);
				 intersect[11] = intersectVectorTriangle(p[6],p[7],tri);
				 intersect[12]=false; //hackish way to check entire vector
				 
				 for(int u=0; u<11; u++){
					 if(intersect[u]==true){
						 intersect[12]=true; //final value used in test
					 }
					 
				 }
				 
				 /**if there has been a collision*/
				if(intersect[12]==true){
					//troubleshooting output:
					t.visit(t);
					//System.out.print("\n");
				
				
				t.processed=true; //wont cover this again
				
				t.setKids();
				
				if(t.depth==6){
					if(Math.random()<0.1){
						t.quantity+=1e10;
					}
				}
				
				break; //breaks is to stop dividing once you've detected your first intersection
				//WHAT'S TO SAY THE FIRST INTERSECTION IS THE MOST IMPORTANT ? 
				}
				
				
				
			}
			
			
			} //end of if processed == false bracket
		}
		
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
			t.visit(t);	//visit tree root
			
			
			for (int i=0;  i<8; i++){
				preOrderfull(t.subNodes[i]);
			}
		}
		
	}
	
	/**In-Order full traverse*/ 
	public static void inOrderfull(OctreeNode t){	
		if(t!=null){
			
			inOrderfull(t.subNodes[0]);
			t.visit(t);
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
			
			t.visit(t);
		}
	}
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////
	//Miscellanious Functions//
	
	/**Gets a sample octree from lowest depth*/
	
	public OctreeNode NodeFinder(OctreeNode t, int depth){
		OctreeNode temp= null;
		if(t!=null){
			
			for (int i=0;  i<8; i++){
				NodeFinder(t.subNodes[i],depth);
			
			}
			
			if(t.getDepth()==depth){
			temp = t; }
			
		}
		
		return temp;
		
	}
	
	/**Troubleshooting visiting function, outputs useful stuff*/
	public void visit(OctreeNode t){
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
		int tempQ = (int)this.quantity;
		tempQ = tempQ > 255 ? 255 : tempQ;
		tempQ = tempQ < 0 ? 0 : tempQ;

		this.nodeColor = new Color(tempQ , 0, 0);
	}
	
	
	/**Diffuses chemicals through whole octree chemi field*/
	public void diffuse(OctreeNode t, double diffusivity, double Dt, int depth){
		
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
				diffuse(t.subNodes[i], diffusivity, Dt,depth);
			
			}
		
		
			//INSERT ACTIONABLE CODE HERE
			
			//checks that the depth is the right depth
			if(t.getDepth()==depth){
			
			for(int i=0; i<6; i++){
				
				
				
			//	if(t.neighbors[i].getDepth()==1){t.neighbors[i].quantity = 1e8;}
			//	if(t.neighbors[i].getDepth()==2){t.neighbors[i].quantity=1e9;}
			//	if(t.neighbors[i].getDepth()==3){t.neighbors[i].quantity=1e10;}

				
							double Before = 0; 
									Before= this.quantity;
									
									//checks for non-null neighbors AND neighbors of the correct depth
									
									if(t.neighbors[i] != null && t.neighbors[i].getDepth() == depth){ //DIFFUSES INTO ITSELF! STUPID!
										
										
										double k = (diffusivity*Dt)/Math.pow(this.length,2);
										
										double q = k*(t.neighbors[i].quantity - Before);
										
										t.neighbors[i].quantity+=q;
										t.quantity-=q;
												
									
											} 
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
	
	
	public static boolean intersectVectorTriangle(Vector3d startPos, Vector3d endPos, BSimTriangle tri){
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
		
	//	coll.set(t, qp, startPos);
		
		return true;
	
	}
	
	
}


