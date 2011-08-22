package BSimLacOperon;

import java.awt.Color;
import java.util.Calendar;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.BSimTicker;
import bsim.BSimUtils;
import bsim.draw.BSimDrawer;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimLogger;
import bsim.particle.BSimBacterium;

/*********************************************************
 * Simulation definition for bacteria with embedded ODE model of lac operon.
 * 
 * Population effects resulting from small local changes in the GRN,
 * investigation of the effects of induced state on growth rate.
 */
public class BSimLacOperon {

	/*********************************************************
	 * Simulation definition
	 * @param args Command line arguments for batch jobs
	 */
	public static void main(String[] args) {
		
		/*********************************************************
		 * Basic setup if we have command line arguments
		 */
		String timestamp;
		int simTimeSeconds;
		if(args.length != 0){
			/*
			 * What do we want to set from args?
			 * Timestamp - results directory
			 * 
			 */
			timestamp = args[0];
			simTimeSeconds = Integer.parseInt(args[1]);
		} else {
			timestamp = BSimUtils.timeStamp();
			simTimeSeconds = 54000;
		}
		
		double externalChem = 200;
		boolean CONSTANT_CHEM_FIELD = false;
		if(args.length >= 3){
			externalChem = Double.parseDouble(args[2]);
			CONSTANT_CHEM_FIELD = true;
		}
		
		/*********************************************************
		 * GLOBAL PARAMETERS ETC
		 */
		// Run simulation in export mode?
		boolean exportData = true;
			
		// Stable population number of bacteria
		int populationLimit = 500;
		
		// Path to results directory (for exported data)
		String exportPath = new String("./results/" + timestamp + "/");		
		
		/*********************************************************
		 * Create a new simulation object and set up simulation settings
		 */
		BSim sim = new BSim();		
		sim.setDt(0.5);
		sim.setTimeFormat("0.00");
		sim.setBound(100,100,100);
		sim.setSimulationTime(simTimeSeconds);
		
		/*********************************************************
		 * Set up the chemical field
		 */
		// External inducer (e.g., TMG)
		final BSimChemicalField externalInducerField = new BSimChemicalField(sim, new int[] {10,10,10}, 1, 0);

		// Constant level from args (if it's set)
		if(CONSTANT_CHEM_FIELD){
			externalInducerField.setConc(externalChem);
		} else {
			externalInducerField.setConc(0);
		}
		
		/*********************************************************
		 * Set up the bacteria
		 */		
		// All bacteria
		final Vector<BSimLacBacterium> bacteria = new Vector<BSimLacBacterium>();
		
		// Temporary children vector for when bacteria replicate
		final Vector<BSimLacBacterium> childBacteria = new Vector<BSimLacBacterium>();
		
		// Temporary vector of removed bacteria
		final Vector<BSimLacBacterium> removedBacteria = new Vector<BSimLacBacterium>();
		
		// Add randomly positioned bacteria to the main vector
		while(bacteria.size() < populationLimit) {		
			BSimLacBacterium b = new BSimLacBacterium(sim, 
										  new Vector3d(Math.random()*sim.getBound().x,
													   Math.random()*sim.getBound().y,
													   Math.random()*sim.getBound().z),
													   externalInducerField);
			b.setRadius();
			
			b.setChildList(childBacteria);
			b.setRemovalList(removedBacteria);
			b.setBacteriaList(bacteria);
			
			b.setPopLimit(populationLimit);			
						
			if(!b.intersection(bacteria)) bacteria.add(b);
		}
				
		/*********************************************************
		 * Custom BSimTicker definition and creation.
		 * The reason is to allow access to parameters of the main BSim simulation 
		 * (specifically time, for time-dependent functionality)
		 */
		class BSimTickerPlus extends BSimTicker{
			
			protected BSim theSim;
			private double chemicalInputState;
			private boolean enableConstantChemField;
			
			public BSimTickerPlus(BSim newSim, boolean simEnableConstChemField) {
				theSim	= newSim;
				chemicalInputState = 0;
				enableConstantChemField = simEnableConstChemField;
			}				

			// A time-varying external chemical field function
			private double chemicalInjector(double oldChemState){

				double newChemState = oldChemState;
				
				/*
				 * Sets up the chemical input (over time) as follows:
				 * 
				 * [chem input]
				 * 
				 *  ^
				 *  |
				 *  |            
				 *  |       _________   
				 *  |      /         \
				 *  |     /           \
				 *  0____/             \______
				 *  |---0----------------------------> [t]
				 */

				// Section durations
				int t_ramp_up_1 = 3600;
				int t_plateau = 42000;
//				int t_ramp_down = 14400;
				
				// Max chemical levels
				double chemical_1 = 125;
				
				if(theSim.getTime() < t_ramp_up_1){
					newChemState += theSim.getDt()*chemical_1/t_ramp_up_1;
				} 
				else if(theSim.getTime() < (t_ramp_up_1 + t_plateau)){
					return newChemState;
				} 
//				else if(theSim.getTime() < t_ramp_up_1 + t_plateau + t_ramp_down){
//					newChemState -= theSim.getDt()*(chemical_1)/t_ramp_down;
//				} 
				else {
					newChemState = 0.0;
				}
								
				return newChemState;
			}
			
			/*********************************************************
			 * Actions to perform at each time-step.
			 */		
			@Override
			public void tick() {
				// Update all our bacteria
				for(BSimBacterium b : bacteria) {
					b.action();		
					b.updatePosition();					
				}
				
				// Remove all removable bacteria
				bacteria.removeAll(removedBacteria);
				removedBacteria.clear();
				
				// Add freshly bred bacteria
				bacteria.addAll(childBacteria);
				childBacteria.clear();
				
				// Trim the vector of bacteria
				// bacteria.trimToSize();
				
				// Chemical field update (if it's time-varying)
				if(!enableConstantChemField){
					// Set the external chemical field forcing
					chemicalInputState = chemicalInjector(chemicalInputState);
					for(int i = 0; i < externalInducerField.getBoxes()[0]; i++){
						for(int j = 0; j < externalInducerField.getBoxes()[1]; j++){
							externalInducerField.setConc(i,j,0, chemicalInputState);
						}
					}
					// Update our chemical fields
					externalInducerField.update();
				}
				
			}
		}

		// Add the ticker to the simulation
		BSimTickerPlus theTicker = new BSimTickerPlus(sim, CONSTANT_CHEM_FIELD);
		sim.setTicker(theTicker);
		
		
		/*********************************************************
		 * Implement draw(Graphics) on a BSimDrawer and add the 
		 * drawer to the simulation.
		 */
		BSimDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {
				for(BSimLacBacterium b : bacteria) {
					Color bacCol;
					
					// Yellow if induced, red if not
					bacCol = new Color(255, (int)(255*b.inducedState()), 0);
					
					draw(b, bacCol);
				}
								
				draw(externalInducerField, new Color(200,200,255), 0.2f);
			}
		}; 
		sim.setDrawer(drawer);	

		/*********************************************************
		 * Set up the exporters and run the simulation
		 */
		// If we are running in export mode:
		if (exportData) {
			// Check export path exists
			BSimUtils.generateDirectoryPath(exportPath);
			
			/*
			 * Implement before(), during() and after() on BSimExporters
			 * and add them to the simulation
			 */
			// MOVIES
//			BSimMovExporter movieExporter = new BSimMovExporter(sim, drawer, exportPath + "lacOperon.mov");
//			movieExporter.setSpeed(10);
//			movieExporter.setDt(0.25);
//			sim.addExporter(movieExporter);	

			// IMAGES
//			BSimPngExporter imageExporter = new BSimPngExporter(sim, drawer, exportPath);
//			imageExporter.setDt(600);
//			sim.addExporter(imageExporter);	

			/*********************************************************
			 * Simulation statistics logger
			 */
			BSimLogger statsLogger = new BSimLogger(sim, exportPath + "Settings.csv") {
				long tStart = 0;
				long tEnd = 0;
				@Override
				public void before() {
					super.before();
					tStart = Calendar.getInstance().getTimeInMillis();
					// Write parameters of the simulation
					write("Dt," + sim.getDt()); 
					write("Time (sec)," + sim.getSimulationTime());
				}
				
				@Override
				public final void during() {
					// Ignore this...
				}
				
				public void after(){
					// Elapsed time (real time)
					tEnd = Calendar.getInstance().getTimeInMillis();
					write("Elapsed time (sec)," + ((tEnd - tStart)/1000.0));
					super.after();
				}
			};
			sim.addExporter(statsLogger);

			/*********************************************************
			 * Internal lac logger
			 */
			BSimLogger lacLogger = new BSimLogger(sim, exportPath + "internalLactose.csv") {
				@Override
				public void before() {
					super.before();
				}
				
				@Override
				public final void during() {
					
					String buffer = new String();
					
					for (BSimLacBacterium b : bacteria) {
						buffer += b.y[1] + ",";
					}
										
					write(buffer);
				}
				
			};
			lacLogger.setDt(60);			// Set export time step
			sim.addExporter(lacLogger);
			
			/*********************************************************
			 * Internal permease logger
			 */
			BSimLogger permLogger = new BSimLogger(sim, exportPath + "internalPermease.csv") {
				@Override
				public void before() {
					super.before();
				}
				
				@Override
				public final void during() {
					
					String buffer = new String();
					
					for (BSimLacBacterium b : bacteria) {
						buffer += b.y[0] + ",";
					}
										
					write(buffer);
				}
				
			};
			permLogger.setDt(60);			// Set export time step
			sim.addExporter(permLogger);
			
			/*********************************************************
			 * Population statistics logger
			 */
			BSimLogger popLogger = new BSimLogger(sim, exportPath + "Hysteresis.csv") {
				@Override
				public void before() {
					super.before();
					write("time,Yex,Yavg,Ymin,Ymax,inducedproportion,population");
				}
				
				@Override
				public final void during() {
					// Average external inducer field
					double extInducerAvg = 0;
					for(int i = 0; i < externalInducerField.getBoxes()[0]; i++){
						for(int j = 0; j < externalInducerField.getBoxes()[1]; j++){
							for(int k = 0; k < externalInducerField.getBoxes()[2]; k++){
								extInducerAvg += externalInducerField.getConc(i, j, k);
							}
						}
					}
					// divide by total no. of boxes
					extInducerAvg = extInducerAvg/
										(externalInducerField.getBoxes()[0]*
										externalInducerField.getBoxes()[1]*
										externalInducerField.getBoxes()[2]);
					
					// Population average inducer
					double bacInducerAvg = 0;
					for(BSimLacBacterium b: bacteria){
						bacInducerAvg += b.y[0];
					}
					bacInducerAvg = bacInducerAvg/bacteria.size();
										
					// Population Minimum and maximum inducer level
					// (Oh the brute-force inefficiency of it all :D)
					double bacInducerMin = Double.MAX_VALUE;
					double bacInducerMax = 0.0;
					
					for(BSimLacBacterium b:bacteria){
						if(b.y[0] < bacInducerMin){
							bacInducerMin = b.y[0];
						} else if(b.y[0] > bacInducerMax){
							bacInducerMax = b.y[0];
						}
					}
					
					// Compute the proportion of the bacteria that are 'induced' 
					double inducedProportion = 0;
					for(BSimLacBacterium b : bacteria){
						inducedProportion += b.inducedState();
					}
					inducedProportion = inducedProportion/bacteria.size();
					
					// Fill up a mini buffer for this line and add it to the buffered writer
					String buffer = sim.getFormattedTime() + ","
									+ extInducerAvg + ","
									+ bacInducerAvg + ","
									+ bacInducerMin + ","
									+ bacInducerMax + ","
									+ inducedProportion + ","
									+ bacteria.size();
					
					write(buffer);
				}
				
			};
			popLogger.setDt(60);			// Set export time step
			sim.addExporter(popLogger);

			// Run in export mode:
			sim.export();
		} 
		else 
		{
			// Running in preview mode:
			sim.preview();
		}
	}
	
}
