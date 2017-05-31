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

import javax.vecmath.Vector3d;

import bsim.capsule.BSimCapsuleBacterium;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics3D;

import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.BSimOctreeField;
import bsim.geometry.BSimMesh;
import bsim.geometry.BSimTriangle;
import bsim.particle.BSimParticle;
import bsim.particle.BSimVesicle;

/**
 * Scene preview and visualisation renderer (extends BSimDrawer). Uses 
 * Processing3D libraries to render the scene. The resulting image is 
 * then drawn to a native Java 2D graphics context which can be used 
 * within a GUI.
 */
public abstract class BSimP3DDrawer extends BSimDrawer {

	/** Processing graphics context used for drawing. */
	protected PGraphics3D p3d;
	/** Font used when rendering text. */
	protected PFont font;
	/** Size of the simulation. */
	protected Vector3d bound;
	/** Centre of the simulation. Used for camera positioning. */
	protected Vector3d boundCentre;

	// With updated Processing library (1.5.1), looks like begindraw() will reset the camera
	// after 1st frame has been drawn, so this provides a workaround for now.
	protected boolean cameraIsInitialised = false;
	
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
		font = new PFont(PFont.findFont("Trebuchet MS").deriveFont((float)20), true, PFont.CHARSET);
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

		if(!cameraIsInitialised){
			p3d.camera(-(float)bound.x*0.7f, -(float)bound.y*0.3f, -(float)bound.z*0.5f, 
				(float)bound.x, (float)bound.y, (float)bound.z, 
				0, 1, 0);
			cameraIsInitialised = true;
		}
		
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
	 * Draw a mesh with a given colour (draws each triangle of the mesh individually). 
	 * Also draws face normals as a red line from the face.
	 * @param mesh	The mesh you want to draw...
	 * @param c Mesh face colour.
	 * @param normalScaleFactor	Scale factor for normal vector length. Set to zero to disable normal drawing.
	 */
	public void draw(BSimMesh mesh, Color c, double normalScaleFactor){
		p3d.fill(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha());
		
		int lineAlpha = c.getAlpha() + 50;
		if (lineAlpha > 255) lineAlpha = 255;
		p3d.stroke(c.getRed(),c.getGreen(),c.getBlue(),lineAlpha);
		
		p3d.beginShape(PConstants.TRIANGLES);
		for(BSimTriangle t:mesh.getFaces()){
			vertex(mesh.getVertCoordsOfTri(t,0));
			vertex(mesh.getVertCoordsOfTri(t,1));
			vertex(mesh.getVertCoordsOfTri(t,2));
		}
		p3d.endShape();
		
		if(normalScaleFactor != 0.0){
			for(BSimTriangle t:mesh.getFaces()){
				vector(mesh.getTCentre(t),t.getNormal(),normalScaleFactor,(new Color(255,0,0,150)));
			}
		}
	}
	
	/**
	 * Draw a mesh, default colour.
	 * @param mesh Mesh to draw.
	 * @param normalScaleFactor Scaling factor.
	 */
	public void draw(BSimMesh mesh, double normalScaleFactor){
		draw(mesh, new Color(128,128,255,50), normalScaleFactor);
	}
		
	/**
	 * Draw a 'vector' originating at a point, represented by a line.
	 * @param origin		Point from which the vector originates.
	 * @param theVector		The vector.
	 * @param scaleFactor	Scalar by which the vector is multiplied for drawing purposes.
	 */
	public void vector(Vector3d origin, Vector3d theVector, double scaleFactor, Color c){
		p3d.stroke(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha());
		p3d.line((float)origin.x, (float)origin.y, (float)origin.z,
				(float)(origin.x + scaleFactor*theVector.x), (float)(origin.y + scaleFactor*theVector.y), (float)(origin.z + scaleFactor*theVector.z));
	}
	
	/**
	 * Define a p3d vertex when constructing shapes, directly from a Point3d.
	 * @param newPoint The Point3d representing the vertex coordinates.
	 */
	public void vertex(Vector3d newPoint){
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
	 * Draw a capsule bacterium as a cylinder with hemispherical caps.
	 * @param bac	The BSimCapsuleBacterium that we would like to draw.
	 * @param c		The desired colour of the BSimCapsuleBacterium.
     */
	public void draw(BSimCapsuleBacterium bac, Color c) {
		p3d.fill(c.getRed(), c.getGreen(), c.getBlue());

		Vector3d worldY = new Vector3d(0, 1, 0);
		Vector3d bacDirVector = new Vector3d();

		bacDirVector.sub(bac.x2, bac.x1);

		Vector3d u = new Vector3d();
		u.scaleAdd(0.5, bacDirVector, bac.x1);

		Vector3d bacRotVector = new Vector3d();
		bacRotVector.cross(worldY, bacDirVector);

		bacDirVector.normalize();

		// TODO: This will fail in the rare case that bac is aligned with Y axis
		// (i.e., trying to normalize a zero vector, which results in [nan, nan, nan]).
		bacRotVector.normalize();

		p3d.pushMatrix();
		p3d.translate((float) u.x, (float) u.y, (float) u.z);
		//fix the rotation on the axis
		//pushMatrix();
		p3d.rotate((float) worldY.angle(bacDirVector), (float) bacRotVector.x, (float) bacRotVector.y, (float) bacRotVector.z);
		drawRodShape((float) bac.radius, (float) bac.L, 32);
		p3d.popMatrix();
		sphere(bac.x1, bac.radius, c, 255);
		sphere(bac.x2, bac.radius, c, 255);
	}


	/**
	 * A helper function for drawing the BSimCapsuleBacterium.
	 * Draws an uncapped cylinder or rod.
	 * the RodShape is drawn along the y axis.
	 * @param radius 	Desired cylinder radius.
	 * @param length 	Desired cylinder length.
	 * @param sides		Number of subdivisions around the outer surface (number of discretised sides in the final mesh).
     */
	public void drawRodShape(float radius, float length, int sides) {
		float angle = 0;
		float angleIncrement = p3d.TWO_PI / sides;
		// save a bunch of calculations:
		float lengthRatio = (length / 2.0f);
		p3d.beginShape(p3d.QUAD_STRIP);
		for (int i = 0; i < sides + 1; ++i) {
			p3d.vertex((float) (radius * Math.cos(angle)), 0 - lengthRatio, (float) (radius * Math.sin(angle)));
			p3d.vertex((float) (radius * Math.cos(angle)), 0 + lengthRatio, (float) (radius * Math.sin(angle)));
			angle += angleIncrement;
		}
		p3d.endShape();
	}

	/**
	 * Draws a chemical field structure based on its defined parameters, with custom transparency (alpha) parameters.
	 * @param field		The chemical field structure to be rendered.
	 * @param c			Desired colour of the chemical field.
	 * @param alphaGrad	Alpha per unit concentration of the field.
	 * @param alphaMax	Maximum alpha value (enables better viewing).
	 */
	public void draw(BSimChemicalField field, Color c, double alphaGrad, double alphaMax) {
		int[] boxes = field.getBoxes();
		double[] boxSize = field.getBox();
		double alpha = 0.0f;
		
		for(int i=0; i < boxes[0]; i++) {
			for(int j=0; j < boxes[1]; j++) {
				for(int k=0; k < boxes[2]; k++) {							
					p3d.pushMatrix();					
					p3d.translate((float)(boxSize[0]*i+boxSize[0]/2), (float)(boxSize[1]*j+boxSize[1]/2), (float)(boxSize[2]*k+boxSize[2]/2));
					
					alpha = alphaGrad*field.getConc(i,j,k);
					if (alpha > alphaMax) alpha = alphaMax;
					
					p3d.fill(c.getRed(), c.getGreen(), c.getBlue(),(float)alpha);
					p3d.box((float)boxSize[0], (float)boxSize[1], (float)boxSize[2]);
					p3d.popMatrix();
				}
			}
		}
	}
	
	/**
	 * Draw a chemical field structure based on its defined parameters (default alpha).
	 * @param field The chemical field to be drawn.
	 * @param c The desired colour.
	 * @param alphaGrad The alpha-per-unit-concentration.
	 */
	public void draw(BSimChemicalField field, Color c, float alphaGrad) {
		draw(field, c, alphaGrad, 255);
	}

	/**	
	 * 	Draw a BSimOctreeField in given colour. Post order hierarchy used for drawing.
	 *	@param t Octree to be drawn.
	 *	@param c Desired colour.
	 *	@param alphaGrad The alpha-per-unit-concentration. 
	 */
	public  void draw(BSimOctreeField t, Color c, float alphaGrad){
		if(t!=null){	
			for (int i=0;  i<8; i++){			
				draw(t.getsubNode(i), c,  alphaGrad);				
			}	
			p3d.pushMatrix();	
			p3d.translate((float)(t.getCentre().x), (float)(t.getCentre().y), (float)(t.getCentre().z));
			p3d.fill(c.getRed(),c.getGreen(),c.getBlue(),alphaGrad*(float)t.getQuantity());
			p3d.box((float)t.getLength(),(float)t.getLength(),(float)t.getLength());
			p3d.popMatrix();
		}
	}

	/**	
	 * 	Draw a BSimOctreeField. Post order hierarchy used for drawing.
	 *	@param t Octree to be drawn.
	 *	@param alphaGrad The alpha-per-unit-concentration. 
	 */
	public  void draw(BSimOctreeField t, float alphaGrad){
		if(t!=null){
			for (int i=0;  i<8; i++){			
				draw(t.getsubNode(i), alphaGrad);				
			}
			p3d.pushMatrix();	
			p3d.translate((float)(t.getCentre().x), (float)(t.getCentre().y), (float)(t.getCentre().z));	
			if(t.getnodeColor()==Color.cyan){
				p3d.fill(t.getnodeColor().getRed(), t.getnodeColor().getGreen(),t.getnodeColor().getBlue(), 100);
			} else {
				p3d.fill(t.getnodeColor().getRed(), t.getnodeColor().getGreen(),t.getnodeColor().getBlue(), alphaGrad);
			}
			p3d.box((float)t.getLength(),(float)t.getLength(),(float)t.getLength());
			p3d.popMatrix();
		}
	}
}
