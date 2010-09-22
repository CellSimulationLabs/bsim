/**
 * BSimP3DDrawer.java
 * Processing3D based renderer for scene preview and visualisation.
 * 
 * Uses components of the Processing libraries:
 * 		www.processing.org
 */

package bsim.draw;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.OctreeNode;
import bsim.geometry.BSimMesh;
import bsim.geometry.BSimTriangle;
import bsim.particle.BSimParticle;
import bsim.particle.BSimVesicle;


/**
 * Scene preview and visualisation renderer (extends BSimDrawer). Uses 
 * Processing3D libraries to render the scene. The resulting image is 
 * then drawn to a native Java 2D graphics context which can be used 
 * within a GUI etc.
 */
public abstract class BSimP3DDrawer extends BSimDrawer {

	protected PGraphics3D p3d;
	protected PFont font;
	protected Vector3d bound;
	protected Vector3d boundCentre;

	/**
	 * Default constructor for initialising a Processing3D rendering context.
	 * @param sim	The simulation we wish to render.
	 * @param width	The desired horizontal resolution (pixels).
	 * @param height	The desired vertical resolution (pixels).
	 */
	public BSimP3DDrawer(BSim sim, int width, int height) {
		super(sim, width, height);		
		bound = sim.getBound();
		boundCentre = new Vector3d();
		boundCentre.scale(0.5, bound);		
		/* See 'Subclassing and initializing PGraphics objects'
		 * http://dev.processing.org/reference/core/ */
		p3d = new PGraphics3D();
		p3d.setPrimary(true); 
		p3d.setSize(width, height);				
		p3d.camera(-(float)bound.x*0.7f, -(float)bound.y*0.3f, -(float)bound.z*0.5f, (float)bound.x, (float)bound.y, (float)bound.z, 0, 1, 0);
		font = new PFont(PFont.findFont("Trebuchet MS").deriveFont((float)20), true, PFont.DEFAULT_CHARSET);
	}
	
	/**
	 * Render all simulation and scene elements to the Processing3D graphics
	 * context 'p3d' (effectively the render buffer), then draw the rendered 
	 * contents to the native Java graphics context.
	 * @param g The native Java graphics context to which we wish to draw our
	 * 			rendered scene.
	 */
	@Override
	public void draw(Graphics2D g) {			
		p3d.beginDraw();

		p3d.textFont(font);
		p3d.textMode(PConstants.SCREEN);

		p3d.sphereDetail(10);
		p3d.noStroke();		
		p3d.background(0, 0, 0);	

		scene(p3d);
		boundaries();
		time();

		p3d.endDraw();
		g.drawImage(p3d.image, 0,0, null);
	}
			
	/**
	 * Draws remaining scene objects to the PGraphics3D object.
	 * Abstract method that should be overridden in a BSim_X_Example to render objects
	 * that are specific to a simulation.
	 */
	public abstract void scene(PGraphics3D p3d);
	
	/**
	 * Draw the default cuboid boundary of the simulation as a partially transparent box
	 * with a wireframe outline surrounding it.
	 */
	public void boundaries() {
		p3d.fill(128, 128, 255, 50);
		p3d.stroke(128, 128, 255);
		p3d.pushMatrix();
		p3d.translate((float)boundCentre.x,(float)boundCentre.y,(float)boundCentre.z);
		p3d.box((float)bound.x, (float)bound.y, (float)bound.z);
		p3d.popMatrix();
		p3d.noStroke();
	}
	
	/**
	 * Draw the default cuboid boundary of the simulation as a wireframe outline.
	 */
	public void boundaryOutline() {
		p3d.noFill();
		p3d.stroke(128, 128, 255);
		p3d.pushMatrix();
		p3d.translate((float)boundCentre.x,(float)boundCentre.y,(float)boundCentre.z);
		p3d.box((float)bound.x, (float)bound.y, (float)bound.z);
		p3d.popMatrix();
		p3d.noStroke();
	}
	
	/**
	 * Draw the formatted simulation time to screen.
	 */
	public void time() {
		p3d.fill(255);
		p3d.text(sim.getFormattedTime(), 50, 50);
	}

	/**
	 * Draw a BSimParticle as a point if it is very small (radius < 1), or a sphere otherwise.
	 * @param p	The BSimParticle to be rendered.
	 * @param c	The desired colour of the particle.
	 */
	public void draw(BSimParticle p, Color c) {
		if (p.getRadius() < 1) point(p.getPosition(), c);
		else sphere(p.getPosition(), p.getRadius(), c, 255);
	}
		
	/**
	 * Draw a vesicle as a pixel surrounded by a 'halo' to make it easier to spot.
	 * @param v	The BSimVesicle to be rendered.
	 * @param c	The desired vesicle colour.
	 */
	public void draw(BSimVesicle v, Color c) {	
		sphere(v.getPosition(), 100*v.getRadius(), c, 50);
		point(v.getPosition(),c);
	}

	/**
	 * Mesh draw (draws each triangle of the mesh individually). 
	 * Also draws face normals as a red line from the face.
	 * @param mesh	The mesh you want to draw...
	 * @param normalScaleFactor	Scale factor for normal vector length. Set to zero to disable normal drawing.
	 */
	public void draw(BSimMesh mesh, double normalScaleFactor){
		p3d.fill(128,128,255,50);
		p3d.stroke(128,128,255,100);
		p3d.beginShape(PConstants.TRIANGLES);
		for(BSimTriangle t:mesh.getFaces()){
			vertex(mesh.getTCoords(t,0));
			vertex(mesh.getTCoords(t,1));
			vertex(mesh.getTCoords(t,2));
		}
		p3d.endShape();
		if(normalScaleFactor != 0){
			for(BSimTriangle t:mesh.getFaces()){
				vector(mesh.getTCentre(t),t.getNormal(),normalScaleFactor,(new Color(255,0,0,150)));
			}
		}
	}
	
	/**
	 * Draw a 'vector' originating at a point, represented by a line.
	 * @param origin		Point from which the vector originates.
	 * @param theVector		The vector.
	 * @param scaleFactor	Scalar by which the vector is multiplied for drawing purposes.
	 */
	public void vector(Point3d origin, Vector3d theVector, double scaleFactor, Color c){
		p3d.stroke(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha());
		p3d.line((float)origin.x, (float)origin.y, (float)origin.z,
				(float)(origin.x + scaleFactor*theVector.x), (float)(origin.y + scaleFactor*theVector.y), (float)(origin.z + scaleFactor*theVector.z));
	}
	
	/**
	 * Define a p3d vertex when constructing shapes, directly from a Point3d.
	 * @param newPoint The Point3d representing the vertex coordinates.
	 */
	public void vertex(Point3d newPoint){
		p3d.vertex((float)newPoint.x, (float)newPoint.y, (float)newPoint.z);
	}
	
	/**
	 * Draw sphere; helper function which draws a parametrised sphere.
	 * @param position	The Cartesian coordinates of the sphere's centre in 3-D space.
	 * @param radius	Sphere radius.
	 * @param c			Colour of the sphere.
	 * @param alpha		Sphere transparency.
	 */
	public void sphere(Vector3d position, double radius, Color c, int alpha) {
		p3d.pushMatrix();
		p3d.translate((float) position.x, (float) position.y, (float) position.z);
		p3d.fill(c.getRed(), c.getGreen(), c.getBlue(), alpha);
		p3d.sphere((float) radius);
		p3d.popMatrix();
	}
	
	/**
	 * Draw a point (pixel); parametrised helper function.
	 * @param position	The Cartesian coordinates of the point in 3-D space.
	 * @param c 		The colour of the point.
	 */
	public void point(Vector3d position, Color c) {
		p3d.stroke(c.getRed(), c.getGreen(), c.getBlue());
		p3d.point((float)position.x, (float)position.y,(float)position.z);
		p3d.noStroke();
	}

	/**
	 * Draws a chemical field structure based on its defined parameters. with  alphaGrad
	 * @param field		The chemical field structure to be rendered.
	 * @param c			Desired colour of the chemical field.
	 * @param alphaGrad	Alpha per unit concentration of the field.
	 */
	public void draw(BSimChemicalField field, Color c, float alphaGrad) {
		int[] boxes = field.getBoxes();
		double[] boxSize = field.getBox();				
		for(int i=0; i < boxes[0]; i++)
			for(int j=0; j < boxes[1]; j++)
				for(int k=0; k < boxes[2]; k++) {							
					p3d.pushMatrix();					
					p3d.translate((float)(boxSize[0]*i+boxSize[0]/2), (float)(boxSize[1]*j+boxSize[1]/2), (float)(boxSize[2]*k+boxSize[2]/2));
					p3d.fill(c.getRed(),c.getGreen(),c.getBlue(),alphaGrad*(float)field.getConc(i,j,k));
					p3d.box((float)boxSize[0],(float)boxSize[1],(float)boxSize[2]);
					p3d.popMatrix();
				}
	}
	
	
	
//////////////////////////////////////////////////////////////////////////////////
	
	//Drawing functions for octrees
	
	/**Post order hierarchy display drawing function for Octree
	 * takes color as a parameter*/
	public  void draw(OctreeNode t, Color c, float alphaGrad){
		if(t!=null){
			
			for (int i=0;  i<8; i++){			
				draw(t.getsubNode(i), c,  alphaGrad);				
			}
			
			
				p3d.pushMatrix();	
				p3d.translate((float)(t.getCentre().x), (float)(t.getCentre().y), (float)(t.getCentre().z));
				p3d.fill(c.getRed(),c.getGreen(),c.getBlue(), alphaGrad);
				p3d.box((float)t.getLength(),(float)t.getLength(),(float)t.getLength());
				p3d.popMatrix();
			
			
			
		}
	}
	
	
	/**Post order hierarchy display drawing - uses internal color value for individual nodes
	 * Not the fastest while rendering....
	 * */
	public  void draw(OctreeNode t, float alphaGrad){
		if(t!=null){
			
			for (int i=0;  i<8; i++){			
				draw(t.getsubNode(i), alphaGrad);				
			}
			
				p3d.pushMatrix();	
				p3d.translate((float)(t.getCentre().x), (float)(t.getCentre().y), (float)(t.getCentre().z));
				if(t.getnodeColor()==Color.cyan){p3d.fill(t.getnodeColor().getRed(), t.getnodeColor().getGreen(),t.getnodeColor().getBlue(), 100);}
				else{
				p3d.fill(t.getnodeColor().getRed(), t.getnodeColor().getGreen(),t.getnodeColor().getBlue(), alphaGrad);}
				p3d.box((float)t.getLength(),(float)t.getLength(),(float)t.getLength());
				p3d.popMatrix();
			
			
			
		}
	}
	
	


}
