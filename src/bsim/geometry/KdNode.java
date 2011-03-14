package bsim.geometry;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

/**
 * See, for example, http://en.wikipedia.org/wiki/Kd-tree
 * 
 * Effectively an axis-aligned BSP tree which alternates the splitting plane axis at each level of branching.
 * 
 * Median point finding via quick-select algorithm.
 * 
 */
public class KdNode {
	
	/** Index of the vertex on which the splitting plane is located */
	public int location;
	/** Axis normal to the splitting plane (0 = x, 1 = y, 2 = z) */
	public int nodeAxis;
	
	/** Left child: coordinate of interest < splitting plane */
	public KdNode leftChild;
	/** Right child: coordinate of interest > splitting plane */
	public KdNode rightChild;
	
	/** Triangles that are classified as being to the left of the node. */
	public ArrayList<Integer> leftTris;
	
	/** Triangles that are classified as being to the right of this node. */
	public ArrayList<Integer> rightTris;
	
	public BSimMesh parentMesh;
	
	// Not currently used - TODO: will be implemented in the median choosing 
	// method again once bugs are squashed
//	private static Random rng = new Random();	
	
	/**
	 * Default constructor.
	 */
	public KdNode(){
	}
	
	public boolean hasLeftChild(){
		return this.leftChild != null;
	}
	
	public boolean hasRightChild(){
		return this.rightChild != null;
	}
	
	/**
	 * ...GHH
	 */
	public static double vecGetCoord(Vector3d v, int i){
		if(i == 0){
			return v.x;
		}else if(i == 1){
			return v.y;
		}else if(i == 2){
			return v.z;
		}else{
			return Double.NaN;
		}
	}
		
	/**
	 * Intersect a direction vector segment with KdNode (or hierarchy)
	 * @param p1
	 */
	public static ArrayList<ArrayList<Integer>> intersectVectorKdNode(Vector3d p1, Vector3d p2, KdNode theKdNode){
		
		ArrayList<ArrayList<Integer>> masterList = new ArrayList<ArrayList<Integer>>();
		
		// Compare the two points 
		Vector3d location = new Vector3d(theKdNode.parentMesh.getVertCoords(theKdNode.location));

		double nodeAxial = vecGetCoord(location, theKdNode.nodeAxis);
		double p1Axial = vecGetCoord(p1, theKdNode.nodeAxis);
		double p2Axial = vecGetCoord(p2, theKdNode.nodeAxis);

		boolean p1OnTheRight = p1Axial > nodeAxial;

		// If the points are both on the same side
		if(p1OnTheRight == (p2Axial > nodeAxial)){
			// If they're on the right, check the right region
			if(p1OnTheRight){
				// Recurse right
				if(theKdNode.hasRightChild()){
					masterList.addAll(intersectVectorKdNode(p1, p2, theKdNode.rightChild));
				} else {
					masterList.add(theKdNode.rightTris);
				}
			}else{ 
				// Recurse Left
				if(theKdNode.hasLeftChild()){
					masterList.addAll(intersectVectorKdNode(p1, p2, theKdNode.leftChild));
				} else {
					masterList.add(theKdNode.leftTris);
				}
			}
		}else{
			// If they cross the cutting plane
			Vector3d direction = new Vector3d(p2);
			direction.sub(p1);

			double[] n = {0, 0, 0};
			n[theKdNode.nodeAxis] = 1;

			Vector3d planeNormal = new Vector3d(n);
			// plane d value: DOT(normal, location)
			double d = planeNormal.dot(new Vector3d(location));

			// point where vector intersects cutting plane
			BSimCollision col = BSimMeshUtils.intersectVectorPlane(p1, direction, planeNormal, d);

			// The vector crosses. This means we must check both sides.
			// now can have 2 vectors: p1 -> col.pos and col.pos -> p2

			// Which side to check?
			// (Check and return in order of closest to most distant)
			if(p1OnTheRight){
				// Recurse right
				if(theKdNode.hasRightChild()){
					masterList.addAll(intersectVectorKdNode(p1, col.getLocation(), theKdNode.rightChild));
				} else {
					masterList.add(theKdNode.rightTris);
				}
				// Recurse Left
				if(theKdNode.hasLeftChild()){
					masterList.addAll(intersectVectorKdNode(col.getLocation(), p2, theKdNode.leftChild));
				} else {
					masterList.add(theKdNode.leftTris);
				}
			}else{
				// Recurse Left
				if(theKdNode.hasLeftChild()){
					masterList.addAll(intersectVectorKdNode(p1, col.getLocation(), theKdNode.leftChild));
				} else {
					masterList.add(theKdNode.leftTris);
				}
				// Recurse right
				if(theKdNode.hasRightChild()){
					masterList.addAll(intersectVectorKdNode(col.getLocation(), p2, theKdNode.rightChild));
				} else {
					masterList.add(theKdNode.rightTris);
				}
			}
		}
		
		return masterList;
	}
		
	
	public static void main(String[] args){
		//test();
		
		Vector3d spCentre = new Vector3d(0,0,0);
		double spRadius = 10;
		
		BSimSphereMesh theMesh = new BSimSphereMesh(spCentre, spRadius, 0);
		
		KdNode testKdTree = new KdNode();
		
//		TestMesh theMesh = testKdTree.new TestMesh();
		theMesh.printStats();
		
		testKdTree = testKdTree.kdTreeFromMesh(theMesh);
		
//		System.out.println("First level median " + theMesh.getVertices().get(testKdTree.location).location);
//		
//		System.out.println("Second level left median " + theMesh.getVertices().get(testKdTree.leftChild.location).location);
//		System.out.println("Second level right median " + theMesh.getVertices().get(testKdTree.rightChild.location).location);
//
//		System.out.println("Third level left left median " + theMesh.getVertices().get(testKdTree.leftChild.leftChild.location).location);
//		System.out.println("Third level left right median " + theMesh.getVertices().get(testKdTree.leftChild.rightChild.location).location);
//		System.out.println("Third level right left median " + theMesh.getVertices().get(testKdTree.rightChild.leftChild.location).location);
//		System.out.println("Third level right right median " + theMesh.getVertices().get(testKdTree.rightChild.rightChild.location).location);

	}
	
	
	/*
	 */
	public void findMedian(Indexed3d[] dList, int axis, int firstIndex, int lastIndex, int medianPos){
		if(lastIndex > firstIndex){
			int pivotIndex = 0;//rng.nextInt(lastIndex - firstIndex) + firstIndex;
			
			int pivotNewIndex = partition(dList, axis, firstIndex, lastIndex, pivotIndex);
			
			if(pivotNewIndex > medianPos){
				findMedian(dList, axis, firstIndex, pivotNewIndex - 1, medianPos);
			}
			if(pivotNewIndex < medianPos){
				findMedian(dList, axis, pivotNewIndex + 1, lastIndex, medianPos);
			}
		}
	}
	
	/*
	 */
	public int partition(Indexed3d[] dList, int axis, int firstIndex, int lastIndex, int pivotIndex){
		
		Indexed3d pVal = new Indexed3d(dList[pivotIndex]);
		
		dList[pivotIndex].set(dList[lastIndex]);
		dList[lastIndex].set(pVal);
		
	    int storeIndex = firstIndex;
	    Indexed3d storeVal;
		
		for(int i = firstIndex; i < lastIndex; i++){
			// Sort on current axis
			if(dList[i].val[axis] < pVal.val[axis]){
				storeVal = new Indexed3d(dList[storeIndex]);
				dList[storeIndex].set(dList[i]);
				dList[i].set(storeVal);
				storeIndex = storeIndex + 1;
			}
		}
		
		storeVal = new Indexed3d(dList[storeIndex]);
		dList[storeIndex].set(dList[lastIndex]);
		dList[lastIndex].set(storeVal);
		
		return storeIndex;
	}
	
	public KdNode kdTreeMeshTest(){
		KdNode kn = new KdNode();
		
		TestMesh tm = new TestMesh();
		tm.scale(10);
		tm.translateAbsolute(new Vector3d(50,50,50));
				
		kn = kdTreeFromMesh(tm);
		
		return kn;
	}
	
	/*
	 */
	public KdNode kdTreeFromMesh(BSimMesh theMesh){
		int listSize = theMesh.getVertices().size();
		
		// XXX: If this needs to be an ArrayList (or etc) can use Collection.swap() to swap elements
		Indexed3d[] coordList = new Indexed3d[listSize];
		
		double[] t = new double[3];
		
		for(int i = 0; i < listSize; i++){
			theMesh.getVertices().get(i).getLocation().get(t);
			coordList[i] = new Indexed3d(t, i);
		}
		
//		for(int i = 0; i < coordList.length; i++){
//			System.out.print(coordList[i].val[0] + " ");
//		}
//		System.out.println();
//		for(int i = 0; i < coordList.length; i++){
//			System.out.print(coordList[i].val[1] + " ");
//		}
//		System.out.println();
//		for(int i = 0; i < coordList.length; i++){
//			System.out.print(coordList[i].val[2] + " ");
//		}
//		System.out.println();
		
		KdNode theKdTree = makeTree(theMesh, coordList, 0);
		theKdTree.parentMesh = theMesh;
		
		// Parent mesh has been assigned, so assign triangles to each node.
		// 	(This could be done during the construction stage by, for example,
		//	testing the triangles assigned to each vertex, however due to the need
		//  to split triangles (at least virtually) etc. this approach would 
		//  appear to result in a lot of repetition, hence we assign triangles here.
		assignTrianglesFromParentMesh(theKdTree);

		return theKdTree;
	}
	

	/**
	 * Assigns triangles from the parent mesh of the k-d tree to each (leaf) node.
	 */
	public void assignTrianglesFromParentMesh(KdNode kn){	
		// For each triangle in the mesh, add that triangle to the leaves which define the area which it occupies
		for(int i = 0; i < kn.parentMesh.getFaces().size(); i++){
			BSimTriangle t = kn.parentMesh.getFaces().get(i);
			
			for(int j = 0; j < 3; j++){
				// for each edge of the triangle
				Vector3d p0 = t.getVertCoords(j);
				Vector3d p1 = t.getVertCoords((j+1)%3);
				
				// Find the triangle lists belonging to nodes which contain this edge
				ArrayList<ArrayList<Integer>> triLists = intersectVectorKdNode(p0, p1, kn);
				
				// If a list doesn't already contain this triangle then add it.
				for(ArrayList<Integer> al: triLists){
					if(!al.contains(i)){
						al.add(i);
					}
				}
				
			}
		}
	}

	
	
	/*
	 */
	public KdNode makeTree(BSimMesh theMesh, Indexed3d[] points, int depth){
		if(points == null || points.length == 0){
			return null;
		} else {
			int theSize = points.length;
			int medianPos = (new Double(Math.floor(theSize/2))).intValue();
			int axis = depth % 3;
			
			// Quickselect to get the median and partially sorted points list
			findMedian(points, axis, 0, theSize - 1, medianPos);
			
			KdNode node = new KdNode();

			node.location = points[medianPos].originalIndex;
			node.nodeAxis = axis;
			node.parentMesh = theMesh;
			
			node.leftChild = makeTree(theMesh, getSubSet(points, 0, medianPos - 1),depth + 1);
			node.rightChild = makeTree(theMesh, getSubSet(points, medianPos + 1, theSize - 1), depth + 1);
			
			if(!node.hasLeftChild()){
				node.leftTris = new ArrayList<Integer>(1);
			}
			if(!node.hasRightChild()){
				node.rightTris = new ArrayList<Integer>(1);
			}
			
			return node;
		}
	}
		
	
	public Indexed3d[] getSubSet(Indexed3d[] fullSet, int start, int end)
	{		
		if(fullSet.length <= 1){
			return null;
		}else{
			Indexed3d[] subSet = new Indexed3d[end - start + 1];
			System.arraycopy(fullSet, start, subSet, 0, subSet.length);
			
//			for(int i = 0; i < subSet.length;i++){
//				System.out.print(subSet[i].originalIndex + " ");
//			}
//			System.out.println();
			
			
			return subSet;
		}
	}
	
	@SuppressWarnings("unused")
	private static void test(){
		KdNode tree = new KdNode();
		
		double[] testlistX = {9,8,7,3,1,2,5,4,6};		
		double[] testlistY = {1,3,5,7,9,11,13,15,17};		
		double[] testlistZ = {23,21,19,17,15,13,11,9,7};	
		
		int testAxis = 2;
		
		int theSize = testlistX.length;
		int mindex = (new Double(Math.floor((theSize - 1)/2))).intValue();

		Indexed3d[] coordList = new Indexed3d[theSize];
		
		for(int i = 0; i < theSize; i++){
			double[] tlist = {testlistX[i],testlistY[i],testlistZ[i]};
			coordList[i] = tree.new Indexed3d(tlist, i);
		}

		////////////////////////////////////////
		System.out.println();
		for(int i = 0; i < theSize; i++){
			System.out.print(coordList[i].originalIndex + " ");
		}
		
		System.out.println();
		for(int i = 0; i < theSize; i++){
			System.out.print(coordList[i].val[0] + " ");
		}
		System.out.println();
		for(int i = 0; i < theSize; i++){
			System.out.print(coordList[i].val[1] + " ");
		}
		System.out.println();
		for(int i = 0; i < theSize; i++){
			System.out.print(coordList[i].val[2] + " ");
		}
		System.out.println();
		////////////////////////////////////////
		
		tree.findMedian(coordList, testAxis, 0, theSize - 1, mindex);
		
		////////////////////////////////////////
		System.out.println();
		for(int i = 0; i < theSize; i++){
			System.out.print(coordList[i].originalIndex + " ");
		}
		
		System.out.println();
		for(int i = 0; i < theSize; i++){
			System.out.print(coordList[i].val[0] + " ");
		}
		System.out.println();
		for(int i = 0; i < theSize; i++){
			System.out.print(coordList[i].val[1] + " ");
		}
		System.out.println();
		for(int i = 0; i < theSize; i++){
			System.out.print(coordList[i].val[2] + " ");
		}
		System.out.println();
		////////////////////////////////////////

		System.out.println("Median is " + coordList[mindex].val[testAxis] + " at position " + coordList[mindex].originalIndex);
	}
	
	class TestMesh extends BSimMesh{

		public TestMesh() {
			super();
			createMesh();
		}
		
		@Override
		protected void createMesh() {
			addVertex(new Vector3d(0,0,0)); // median 1
			
			addVertex(new Vector3d(2,-1,-1));
			addVertex(new Vector3d(2,-1,0)); // m3 rl
			addVertex(new Vector3d(2,-1,1));

			addVertex(new Vector3d(2,0,0)); // m2 r
			
			addVertex(new Vector3d(2,1,-1));
			addVertex(new Vector3d(2,1,0)); // m3 rr
			addVertex(new Vector3d(2,1,1));


			addVertex(new Vector3d(-2,-1,-1));
			addVertex(new Vector3d(-2,-1,0)); // m3 ll
			addVertex(new Vector3d(-2,-1,1));

			addVertex(new Vector3d(-2,0,0));// m2 l

			addVertex(new Vector3d(-2,1,-1));
			addVertex(new Vector3d(-2,1,0)); // m3 lr
			addVertex(new Vector3d(-2,1,1));
		}
	}
		
	
	/**
	 * Class that holds a triplet of doubles and its original index.
	 * Thus we can sort by value then refer back to the original object, 
	 * without rearranging order of original data.
	 */
    class Indexed3d{
    	double[] val;
    	int originalIndex;
    	
    	public Indexed3d(){
    		val = new double[3];
    		originalIndex = -1;
    	}
    	
    	public Indexed3d(double[] newVal, int newIndex) {
    		this();
    		val = newVal.clone();
    		originalIndex = newIndex;
		}
    	
    	public Indexed3d(Indexed3d id) {
    		this(id.val, id.originalIndex);
		}
    	
    	public void set(Indexed3d id){
    		set(id.val, id.originalIndex);
    	}
    	
    	public void set(double[] newVal, int newIndex){
    		val = newVal.clone();
    		originalIndex = newIndex;
    	}
    }
}
