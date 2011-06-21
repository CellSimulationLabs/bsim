package BSimLogic;

/**
 * BSimLogic.java
 * 
 * Simulation that implements bacteria to reproduce the results from:
 *    Tamsir et al. 'Robust multicellular computing using genetically encoded NOR gates and chemical wires'
 *    http://www.nature.com/nature/journal/v469/n7329/abs/nature09565.html
 */

import java.awt.Color;
import java.io.File;
import java.util.*;
import javax.vecmath.Vector3d;
import processing.core.PGraphics3D;

import bsim.*;
import bsim.draw.*;
import bsim.export.*;

public class BSimLogic {
	
	// Global variables
	static Random rng = new Random();
	static boolean AraOn = false;
	static boolean aTcOn = false;
	static boolean exportData = false;
	static double p1x, p1y, p2x, p2y, p3x, p3y, p4x, p4y;
	
	public static void main(String[] args) {
		
		/**
		 * Create a new simulation object
		 */
		BSim sim = new BSim();		
		sim.setDt(0.5);
		sim.setTimeFormat("0.00");
		sim.setBound(150,150,12);
		sim.setSolid(true, true, true);
		
		/**
		 * Flag to switch between preview and export (also time to simulate for)
		 */
		exportData = true;
		sim.setSimulationTime(10000); // Export data for 10 hours by default
		String filePath = new String();
		// Update this path to the location that results should be stored
		filePath = "/Users/enteg/Desktop/results/EQUAL_FT/" + BSimUtils.timeStamp() +"/";
		
		/**
		 * Input chemical fields general level across environment (low resolution)
		 */
		final BSimChemicalField fAra = new BSimChemicalField(sim, new int[]{1,1,1}, 0, 0);
		final BSimChemicalField faTc = new BSimChemicalField(sim, new int[]{1,1,1}, 0, 0);
		
		/**
		 * Chemical fields used for input/output from logic gate populations (high resolution)
		 * 3OC12-HSL: From Tamsir et al. Nature (2011)
		 * LasI: diff = 6e-3 cm^2/hr, prod = 1.6e-8 nanomoles/hr cell, decay = 0.012 per hr
		 * RhII: use the same parameters
		 */
		final BSimChemicalField fRhll = new BSimChemicalField(sim, new int[]{150,150,2}, 0.016, 0.012/3600.0);
		final BSimChemicalField fLasI = new BSimChemicalField(sim, new int[]{150,150,1}, 0.016, 0.012/3600.0);

		/**
		 * Set the initial conditions of the input chemical fields (fAra and faTc)
		 */
		AraOn = false;
		aTcOn = true;
		
		// Update boolean flags above not the fields directly
		if (AraOn) fAra.setConc(50000.0); // This is arbitary, as long as it exceeds threshold
		else fAra.setConc(0.0);
		if (aTcOn) faTc.setConc(50000.0); // This is arbitary, as long as it exceeds threshold
		else faTc.setConc(0.0);
		
		/**
		 * Create the vectors for all groups of bacteria we might want to use
		 */
		final Vector<BSimLogicBacterium> bacteria1 = new Vector<BSimLogicBacterium>();
		final Vector<BSimLogicBacterium> bacteria2 = new Vector<BSimLogicBacterium>();
		final Vector<BSimLogicBacterium> bacteria3 = new Vector<BSimLogicBacterium>();
		final Vector<BSimLogicBacterium> bacteria4 = new Vector<BSimLogicBacterium>();
			
		/**
		 * Here we define the bacterial populations. By changing the populations that are created
		 * and the fields that each population uses it is possible to rewire them and implement
		 * all configurations presented in the Tamsir et al. Nature (2011) paper. The bacteria 
		 * populations are placed as follows:
		 *
		 *     -----------------------------------------
		 *    |                                         |
		 *    |              Population 2               |
		 *    |                                         |
		 *    |   Population 1          Population 4    |
		 *    |                                         |
		 *    |              Population 3               |
		 *    |                                         |
		 *     -----------------------------------------
		 *
		 * There are 4 types of bacterium that we implement:
		 *
		 *  - BSimNORBacterium:    NOR logic gate (2 inputs, 1 output)
		 *  - BSimORBacterium:     OR logic gate (2 inputs, 1 output)
		 *  - BSimNOTBacterium:    NOT logic gate (1 input, 0 outputs other than reporter)
		 *  - BSimBufferBacterium: Buffer (1 input, 0 outputs other than reporter)
		 *
		 */
		double prodDly =       5.0  * 60.0; // Approximately 5 mins
		double prodDlySpread = 4.0  * 60.0; // Approximately 4 mins
		double repDly =        20.0 * 60.0; // Approximately 20 mins
		double repDlySpread =  8.0  * 60.0; // Approximately 8 mins
		int    popSize =       20000;
		double popSpread =     3.0;         // This gives the right density of bacterial (r = 6.7 microns)
		
		// Locations of the populations
		p4x = 45;
		p4y = 75;
		double pDiffx = 17.96 + (6.7*2);
		double pDiffy = 15.07 + (6.7*2);

		// Automatically calculate other positions from this
		p2x = p4x + pDiffx;
		p2y = p4y + pDiffy;
		p3x = p4x + pDiffx;
		p3y = p4y - pDiffy;
		p1x = p2x + pDiffx;
		p1y = p4y;
		
		while(bacteria1.size() < popSize) {		
			BSimNORBacterium p = new BSimNORBacterium(sim,
																	getNextBacteriumPos(sim, p1x, p1y, 5.0, popSpread, popSpread),
																	fAra, 30115, // Input chemical field and threshold 1
																	faTc, 30115, // Input chemical field and threshold 2
																	fLasI, 2675, // Output chemical field, rate and delay
																	getDecayTime(prodDly, prodDlySpread), // Output delay
																	getDecayTime(repDly, repDlySpread));  // Reporter off delay
			bacteria1.add(p);
		}
		while(bacteria2.size() < popSize) {		
			BSimNORBacterium p = new BSimNORBacterium(sim,
																	getNextBacteriumPos(sim, p2x, p2y, 5.0, popSpread, popSpread),
																	fAra,  30115, 
																	fLasI, 30115, 
																	fRhll, 2675,  
																	getDecayTime(prodDly, prodDlySpread),
																	getDecayTime(repDly, repDlySpread));
			bacteria2.add(p);
		}
		while(bacteria3.size() < popSize) {		
			BSimNORBacterium p = new BSimNORBacterium(sim,
																	getNextBacteriumPos(sim, p3x, p3y, 5.0, popSpread, popSpread),
																	fLasI, 30115, 
																	faTc,  30115, 
																	fRhll, 2675,  
																	getDecayTime(prodDly, prodDlySpread),
																	getDecayTime(repDly, repDlySpread));
			bacteria3.add(p);
		}
		while(bacteria4.size() < popSize) {		
			BSimNOTBacterium p = new BSimNOTBacterium(sim,
																			getNextBacteriumPos(sim, p4x, p4y, 5.0, popSpread, popSpread),
																			fRhll, 30115, 
																			getDecayTime(prodDly, prodDlySpread));
			bacteria4.add(p);
		}
		
		/** 
		 * Implement tick() on a BSimTicker and add the ticker to the simulation. Because this
		 * uses the parent BSimLogicBacterium this does not need to be updated for new configurations.	  
		 */
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				// Update the actions for all the bacterial populations
				for(BSimLogicBacterium p : bacteria1) p.action();
				for(BSimLogicBacterium p : bacteria2) p.action();
				for(BSimLogicBacterium p : bacteria3) p.action();
				for(BSimLogicBacterium p : bacteria4) p.action();
				// Update the chemical fields, fAra and faTc do not update as they are fixed in medium
				fRhll.update();
				fLasI.update();
			}		
		});	
		
		/**
		 * Create location to export data to (if required).
		 */
		if (exportData) {
			// Check that path is valid and that it exists, if it doesn't then create it
			File testPath = new File(filePath);
			if(!testPath.exists()){
				// Create directories as required
				testPath.mkdirs();
			}
		}
		
		/**
		 * Create exporters to record the state and reporter status of each population over time.
		 */
		BSimLogger dataLoggerPop1 = new BSimLogger(sim, filePath + "Population1.csv") {
			int nActON, nActOFF, nRepON, nRepOFF;
			@Override
			public void before() {
				super.before();
				String buffer = new String();
				buffer = "time(seconds),nActON,nActOFF,nRepON,nRepOFF";
				write(buffer); 
			}
			@Override
			public void during() {
				String o = sim.getFormattedTime();
				nActON  = 0; nActOFF = 0; nRepON  = 0; nRepOFF = 0;
				for(BSimLogicBacterium b : bacteria1) {
					if (b.activated == true) { nActON++; }
					else { nActOFF++; }
					if (b.reporter == true) { nRepON++; }
					else { nRepOFF++; }
				}
				write(o + "," + nActON + "," + nActOFF + "," + nRepON + "," + nRepOFF);
			}
		};
		dataLoggerPop1.setDt(30);
		sim.addExporter(dataLoggerPop1);
		
		BSimLogger dataLoggerPop2 = new BSimLogger(sim, filePath + "Population2.csv") {
			int nActON, nActOFF, nRepON, nRepOFF;
			@Override
			public void before() {
				super.before();
				String buffer = new String();
				buffer = "time(seconds),nActON,nActOFF,nRepON,nRepOFF";
				write(buffer); 
			}
			@Override
			public void during() {
				String o = sim.getFormattedTime();
				nActON  = 0; nActOFF = 0; nRepON  = 0; nRepOFF = 0;
				for(BSimLogicBacterium b : bacteria2) {
					if (b.activated == true) { nActON++; }
					else { nActOFF++; }
					if (b.reporter == true) { nRepON++; }
					else { nRepOFF++; }
				}
				write(o + "," + nActON + "," + nActOFF + "," + nRepON + "," + nRepOFF);
			}
		};
		dataLoggerPop2.setDt(30);
		sim.addExporter(dataLoggerPop2);
		
		BSimLogger dataLoggerPop3 = new BSimLogger(sim, filePath + "Population3.csv") {
			int nActON, nActOFF, nRepON, nRepOFF;
			@Override
			public void before() {
				super.before();
				String buffer = new String();
				buffer = "time(seconds),nActON,nActOFF,nRepON,nRepOFF";
				write(buffer); 
			}
			@Override
			public void during() {
				String o = sim.getFormattedTime();
				nActON  = 0; nActOFF = 0; nRepON  = 0; nRepOFF = 0;
				for(BSimLogicBacterium b : bacteria3) {
					if (b.activated == true) { nActON++; }
					else { nActOFF++; }
					if (b.reporter == true) { nRepON++; }
					else { nRepOFF++; }
				}
				write(o + "," + nActON + "," + nActOFF + "," + nRepON + "," + nRepOFF);
			}
		};
		dataLoggerPop3.setDt(30);
		sim.addExporter(dataLoggerPop3);
		
		BSimLogger dataLoggerPop4 = new BSimLogger(sim, filePath + "Population4.csv") {
			int nActON, nActOFF, nRepON, nRepOFF;
			@Override
			public void before() {
				super.before();
				String buffer = new String();
				buffer = "time(seconds),nActON,nActOFF,nRepON,nRepOFF";
				write(buffer); 
			}
			@Override
			public void during() {
				String o = sim.getFormattedTime();
				nActON  = 0; nActOFF = 0; nRepON  = 0; nRepOFF = 0;
				for(BSimLogicBacterium b : bacteria4) {
					if (b.activated == true) { nActON++; }
					else { nActOFF++; }
					if (b.reporter == true) { nRepON++; }
					else { nRepOFF++; }
				}
				write(o + "," + nActON + "," + nActOFF + "," + nRepON + "," + nRepOFF);
			}		};
		dataLoggerPop4.setDt(30);
		sim.addExporter(dataLoggerPop4);
		
		BSimLogger dataLoggerConc = new BSimLogger(sim, filePath + "Concentrations.csv") {
			@Override
			public void before() {
				super.before();
				String buffer = new String();
				buffer = "time(seconds),ConcAra1,ConcaTc1,ConcLasI1,ConcRhll1,ConcAra2,ConcaTc2,ConcLasI2,ConcRhll2,ConcAra3,ConcaTc3,ConcLasI3,ConcRhll3,ConcAra4,ConcaTc4,ConcLasI4,ConcRhll4";
				write(buffer); 
			}
			@Override
			public void during() {
				String o = sim.getFormattedTime();
				String buffer = new String();
				// Position 1
				buffer = buffer + "," + fAra.getConc(new Vector3d(p1x, p1y, 5.0));
				buffer = buffer + "," + faTc.getConc(new Vector3d(p1x, p1y, 5.0));
				buffer = buffer + "," + fLasI.getConc(new Vector3d(p1x, p1y, 5.0));
				buffer = buffer + "," + fRhll.getConc(new Vector3d(p1x, p1y, 5.0));
				// Position 2
				buffer = buffer + "," + fAra.getConc(new Vector3d(p2x, p2y, 5.0));
				buffer = buffer + "," + faTc.getConc(new Vector3d(p2x, p2y, 5.0));
				buffer = buffer + "," + fLasI.getConc(new Vector3d(p2x, p2y, 5.0));
				buffer = buffer + "," + fRhll.getConc(new Vector3d(p2x, p2y, 5.0));
				// Position 3
				buffer = buffer + "," + fAra.getConc(new Vector3d(p3x, p3y, 5.0));
				buffer = buffer + "," + faTc.getConc(new Vector3d(p3x, p3y, 5.0));
				buffer = buffer + "," + fLasI.getConc(new Vector3d(p3x, p3y, 5.0));
				buffer = buffer + "," + fRhll.getConc(new Vector3d(p3x, p3y, 5.0));
				// Position 4
				buffer = buffer + "," + fAra.getConc(new Vector3d(p4x, p4y, 5.0));
				buffer = buffer + "," + faTc.getConc(new Vector3d(p4x, p4y, 5.0));
				buffer = buffer + "," + fLasI.getConc(new Vector3d(p4x, p4y, 5.0));
				buffer = buffer + "," + fRhll.getConc(new Vector3d(p4x, p4y, 5.0));	
				// Write to file
				write(o + buffer);
			}
		};
		dataLoggerConc.setDt(30);
		sim.addExporter(dataLoggerConc);
		
		
		/**
		 * Create the drawer for the scene because we use the generalised BSimLogicBacterium
		 * we do not need to update this for alternative configurations
		 */
		BSimDrawer drawer = new BSimP3DDrawer(sim, 800,600) {	
			// Colours for active and inactive bacteria
			Color bInactive  = new Color(205,197,191);
			Color b1ColAct   = new Color(255,0,27);
			Color b2ColAct   = new Color(255,126,0);
			Color b3ColAct   = new Color(0,174,255);
			Color b4ColAct   = new Color(162,255,0);
			// Chemical field colours (including alpha)
			Color fRhllCol = new Color(100,149,237);
			Color fLasICol = new Color(255,48,48); 
			@Override
			public void scene(PGraphics3D p3d) {
				// Print the input state to the screen
				p3d.fill(255);
				if (AraOn) p3d.text("Ara: ON", 50, 75);
				else p3d.text("Ara: OFF", 50, 75);
				if (aTcOn) p3d.text("aTc: ON", 50, 100);
				else p3d.text("aTc: OFF", 50, 100);
				// Place camera in correct position
				p3d.translate(0, 25, 0);
				p3d.scale(0.70f);
				p3d.rotateZ(9.0f * (float)(Math.PI/0.915)); 
				p3d.rotateY(9.0f * (float)(Math.PI/1.05)); 
				p3d.rotateX(9.0f * (float)(Math.PI/2.0)); 
				p3d.translate(-75, -75, -6);
				// Draw the chemical fields
				draw(fRhll, fRhllCol, 1);
				draw(fLasI, fLasICol, 1);
				// Draw the groups of bacteria
				for(BSimLogicBacterium p : bacteria1) draw(p, p.reporter ? b1ColAct : bInactive);
				for(BSimLogicBacterium p : bacteria2) draw(p, p.reporter ? b2ColAct : bInactive);
				for(BSimLogicBacterium p : bacteria3) draw(p, p.reporter ? b3ColAct : bInactive);
				for(BSimLogicBacterium p : bacteria4) draw(p, p.reporter ? b4ColAct : bInactive);
			}
		}; 
		sim.setDrawer(drawer);	

		/**
		 * Set up the exporter for video
		 */
		BSimMovExporter movExporter = new BSimMovExporter(sim, drawer, filePath + "BSimLogicMovie.mov");
		movExporter.setSpeed(50);
		movExporter.setDt(1);
		// Don't export movie by default
		// sim.addExporter(movExporter);
		
		/**
		 * Call sim.preview() to preview the scene or sim.export() to set exporters working 
		 */
		if (exportData) { sim.export(); }
		else { sim.preview(); }
	}
	
	/**
	 * Randomly (with Guassian distribution) place a bacterium
	 */
	public static Vector3d getNextBacteriumPos(BSim sim, double x, double y, double z, double xWid, double yWid) {
		double newX, newY;
		newX = rng.nextGaussian()*xWid + x;
		// Ensures that the x-corod is in the environment
		if (newX >= sim.getBound().x) newX = sim.getBound().x - 20.0;
		if (newX <= 0.0) newX = 20.0;
		newY = rng.nextGaussian()*yWid + y;
		// Ensures that the y-corod is in the environment
		if (newY >= sim.getBound().y) newY = sim.getBound().y - 20.0;
		if (newY <= 0.0) newY = 20.0;
		return new Vector3d(newX, newY, z);
	}
	
	/**
	 * Randomly generate a +ve decay time (with Guassian distribution)
	 */
	public static double getDecayTime(double mean, double spread) {
		double out;
		out = rng.nextGaussian()*spread + mean;
		if (out <= 0.0) out = 0.0001; // Ensure small positive value
		return out;
	}
}
