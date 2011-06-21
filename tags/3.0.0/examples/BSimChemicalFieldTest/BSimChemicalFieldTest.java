package BSimChemicalFieldTest;

import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.particle.BSimBacterium;

/**
 * Tests out the functionality of the {@link BSimChemicalField}.</br>
 * A number of {@link BSimBacterium} bacteria are set up to swim around in a chemical field.
 * The bacteria deposit chemical randomly in space.
 * This acts as an attractor for other bacteria in the vicinity (chemotaxis).
 */
public class BSimChemicalFieldTest {

	/*********************************************************
	 * Simulation Definition
	 *********************************************************/
	public static void main(String[] args) {

		/*********************************************************
		 * Create a new simulation object and set up simulation settings
		 */
		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setTimeFormat("0.00");
		sim.setSolid(true,true,true);
		sim.setBound(100,100,100);
			
		/*********************************************************
		 * Set up the chemical field
		 */
		final double c = 12e5; // molecules
		final double decayRate = 0.9;
		final double diffusivity = 890; // (microns)^2/sec
		final BSimChemicalField field = new BSimChemicalField(sim, new int[]{10,10,10}, diffusivity, decayRate);
//		field.linearZ(0,c);
			
		/*********************************************************
		 * Set up the bacteria
		 */
		final Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>();		
		while(bacteria.size() < 30) {	
			BSimBacterium p = new BSimBacterium(sim, 
					new Vector3d(Math.random()*sim.getBound().x, 
							Math.random()*sim.getBound().y, 
							Math.random()*sim.getBound().z)) {
				// Bacteria move etc. and also add chemical to the global field.
				public void action() {
					super.action();
					if (Math.random() < sim.getDt())
						field.addQuantity(position, 1e9);					
				}
			};
			// Chemotaxis according to chemical field strength
			p.setGoal(field);
			
			if(!p.intersection(bacteria)) bacteria.add(p);		
		}
		
		/*********************************************************
		 * Set the ticker, define what happens each time step
		 */
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				for(BSimBacterium b : bacteria) {
					b.action();		
					b.updatePosition();
				}
				field.update(); 
			}		
		});

		/*********************************************************
		 * Set the drawer for the simulation
		 */
		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {	
				draw(field, Color.BLUE, (float)(255/c));						
				for(BSimBacterium p : bacteria) draw(p, Color.GREEN);		
			}
		});				

		// Run the simulation
		sim.preview();

	}
}
