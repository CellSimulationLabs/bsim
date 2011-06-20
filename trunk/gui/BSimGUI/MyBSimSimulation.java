
import java.awt.Color;
import java.util.Random;
import java.util.Vector;
import javax.vecmath.Vector3d;
import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.ode.BSimOdeSolver;
import bsim.ode.BSimOdeSystem;
import bsim.particle.BSimBacterium;
import bsim.export.BSimMovExporter;
import bsim.export.BSimLogger;
import bsim.export.BSimPngExporter;

public class MyBSimSimulation {
	


static int numEq1 = 0;
static Color simpleColour1 = new Color(255,0,0);
static Color thresSignallingColour1 = new Color(255,0,0);
static Color collisionColour1 = new Color(255,0,0);
static  int populationSize1 = 1;
	public static void main(String[] args) {

		BSim sim = new BSim();	

sim.setSolid(false,false,false);
final BSimChemicalField field1 = new BSimChemicalField(sim, new int[]{20,20,20},890,0.9);
	class CustomBacterium1 extends BSimBacterium {
		public boolean activated = false;  //threshold1 signalling bacterium1
			
		protected ODEHandler repGRN;    //ODE signalling bacterium1 
		protected double[] y, yNew;        //ODE signalling bacteria1
		
		private boolean collision = false;	//collision bacteria1
		private boolean ODEsignalling = false;  //ODE signalling bacteria1 
			
		public CustomBacterium1(BSim sim, Vector3d position) {
			super(sim, position);


			repGRN = new ODEHandler();
			y = repGRN.getICs();    
		}
 
			
		public void interaction(CustomBacterium1 p) {   //collision bacteria1 !instead of action method!!!CAN STAY HERE ALL THE TIME!
			if(outerDistance(p) < 0) {
				collision = true;
				p.collision = true;
			}
		}  
			
		@Override
		public void action() {
								
			super.action();




		}
			
		//  ODE signalling bacteria1
		 // Representation of the repressilator ODE system with quorum coupling
		 //TODO: quorum coupling when we have fields
		 //TODO: Simple implementation using averages as in the paper
		 //
		class ODEHandler implements BSimOdeSystem{

			private Random r = new Random();
				
			public double[] derivativeSystem(double x, double[] y) {
				double[] dy = new double[numEq1];

					



	
		
dy[0] = -1*y[0] + 1*Math.pow(y[1],2);
dy[1] = -1*y[1] + 1*y[0];				return dy;
			}

				
			//
			 // Create the initial conditions of the ODE system
			 //
			public double[] getICs() {
				// Start synchronised
double[] ics = {1,0};				
				return ics;
			}
				
		
			public int getNumEq() {
				return numEq1;
			}
		}	 
			
	}

		
	final Vector<CustomBacterium1> bacteria1 = new Vector<CustomBacterium1>();
	// Add bacteria1 to the vector
	while(bacteria1.size() < populationSize1) {		
		CustomBacterium1 bacterium1 = new CustomBacterium1(sim, 
									  new Vector3d(Math.random()*sim.getBound().x,
												   Math.random()*sim.getBound().y,
												   Math.random()*sim.getBound().z));
		if(!bacterium1.intersection(bacteria1)) bacteria1.add(bacterium1);
	}

//----------------------
//TICKER DEFINITION
//----------------------
			
	sim.setTicker(new BSimTicker() {
		@Override
		public void tick() {

field1.update();

				
			for (CustomBacterium1 bacterium1 : bacteria1) {
				bacterium1.action();


}
		}
	});

	BSimP3DDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
		@Override
		public void scene(PGraphics3D p3d) {	
			sim.getBound();	

p3d.camera(-(float)bound.x*0.7f,-(float)bound.y*0.3f,-(float)bound.z*0.5f,(float)bound.x,(float)bound.y,(float)bound.z,0,1,0);
draw(field1, Color.RED, (float)(255/12.0e4));
			for(CustomBacterium1 bacterium1 : bacteria1) {
					
				if (bacterium1.activated == true)   //threshold signalling 
					draw(bacterium1, thresSignallingColour1);
				else if (bacterium1.collision == true)   //collision bacteria1
					draw(bacterium1, collisionColour1);
				else if (bacterium1.ODEsignalling == true)     

	

draw(bacterium1,new Color(4*(int)bacterium1.y[2],255 - 4*(int)bacterium1.y[2],0));
				else 
					draw(bacterium1, simpleColour1);
					
			}


		}
	};

	sim.setDrawer(drawer); 

		sim.preview();
	}

}
