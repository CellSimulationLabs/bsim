package BSimLacOperon;

import java.util.Random;
import java.util.Vector;

import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.ode.BSimOdeSolver;
import bsim.ode.BSimOdeSystem;
import bsim.particle.BSimBacterium;

/*********************************************************
 * A {@link BSimBacterium} with two dimensional ODE system representing lactose-permease interaction.
 * 
 * Deterministic continuous-time representation of bistable lactose operon behaviour,
 * with two variables (internal permease and inducer).
 * 
 * See {@link BSimLacBacteriumGRN} for details of the GRN.
 * 
 */
public class BSimLacBacterium extends BSimBacterium {
		
	protected static Random bacRng = new Random();
	
	// Internal Gene Regulatory Network (ODE system)
	protected BSimLacBacteriumGRN grn;
	
	// External chemical field representing external inducer (e.g., TMG)
	protected BSimChemicalField inducerField;
	
	// Local values of ODE variables
	protected double[] y, yNew;				
	
	// Sets whether a bacterium being in an 'uninduced' lactose state grows faster:
	// Modeling network dynamics: the lac operon, a case study 
	// Jose M.G. Vilar, Calin C. Guet, and Stanislas Leibler 
	// J Cell Biol 2003 161:471-476. Published May 12, 2003, doi:10.1083/jcb.200301125
	protected boolean INDUCTION_AFFECTS_GROWTH = false;
	
	// Stable population number in the region under investigation in the simulation
	protected double populationLimit = 100;
	public void setPopLimit(double newPopLimit){populationLimit = newPopLimit; }
	
	// Bacteria in the simulation
	protected Vector<BSimLacBacterium> bacteriaList;
	public void setBacteriaList(Vector<BSimLacBacterium> v) { bacteriaList = v; }
	
	// Bacteria to be removed
	protected Vector<BSimLacBacterium> removalList;
	public void setRemovalList(Vector<BSimLacBacterium> v) { removalList = v; }

	/*********************************************************
	 * Constructor for a {@link BSimLacBacterium}.
	 * @param sim The {@link BSim} that is running this bacterium.
	 * @param position Initial position of bacterium.
	 * @param externalInducerField External chemical field that contains some inducer.
	 */
	public BSimLacBacterium(BSim sim, Vector3d position, BSimChemicalField externalInducerField){
		super(sim, position);
		
		inducerField = externalInducerField;
		
		// Initialise the ODE system
		grn = new BSimLacBacteriumGRN();
		
		grn.RANDOM_ICS = true;
		grn.POPULATION_VARIANCE = true;
		
		INDUCTION_AFFECTS_GROWTH = false;
		
		y = grn.getICs();
	}
	
	/*********************************************************
	 * Action each time step.
	 */
	@Override
	public void action() {
		
		// Movement (and growth/replication if growth rate > 0)
		super.action();
		
		// Get the external chemical field level for the GRN ode system:
		grn.setIex(inducerField.getConc(position));
		
		// Solve the ode system
		// IMPORTANT: re-scale the time units correctly (GRN equations are in minutes, BSim works in seconds)
		yNew = BSimOdeSolver.rungeKutta45(grn, sim.getTime()/60, y, sim.getDt()/60);
		y = yNew;
		
		grow();
	}

	/*********************************************************
	 * Growth and replication.
	 */
	@Override
	public void grow() {
		
		double tGeneration = 3600; 
		double tRemoval = 3600;

		double P_generation = sim.getDt()/tGeneration;
		double P_removal = sim.getDt()/tRemoval;
		
		/*
		 * Currently assume that the population has stabilised and that therefore there
		 * is a limiting factor controlling the population growth and removal - the rates of change
		 * will act to bring the population to equilibrium, in this case the original number 
		 * of bacteria in the simulation.
		 */
        P_generation = (sim.getDt()/tGeneration)*(Math.pow(2, 1 - bacteriaList.size()/populationLimit));
        P_removal = (sim.getDt()/tRemoval)*(Math.pow(2, -(1 - bacteriaList.size()/populationLimit)));
//        P_generation = (sim.getDt()/tGeneration)*(2.5-(bacteriaList.size()/populationLimit));
//        P_removal = (sim.getDt()/tRemoval)*(bacteriaList.size()/populationLimit);

        if(INDUCTION_AFFECTS_GROWTH){
        	double maxFractionChange = 0.1; 

        	//double inductionStateScaling = 1 + Math.exp(-0.0025*y[1])*maxFractionChange;
			double inductionStateScaling = 1 + (1 - inducedState())*maxFractionChange;
        	P_generation *= inductionStateScaling;
        }
        
		// If the bacterium is removed then it can't replicate:
		if(Math.random() < P_removal){
			remove();
		}else if(Math.random() < P_generation){
			replicate();
		}
		
	}
		
	/**
	 * Cell removal - add to list for removal from the simulation
	 */
	public void remove(){
		removalList.add(this);
	}
	
	/**
	 * Replication method.
	 * (Complete override)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void replicate() {
		BSimLacBacterium child = new BSimLacBacterium(sim, new Vector3d(position), inducerField);
		
		child.setRadius();
		
		child.setChildList(childList);
		child.setRemovalList(removalList);
		child.setBacteriaList(bacteriaList);
		
		child.setPopLimit(populationLimit);

		child.y[0] = y[0];
		child.y[1] = y[1];
		
		childList.add(child);
	}
	
	
	/*********************************************************
	 * 'soft' measure of induced state: Istate(bac.y[1]).
	 * 
	 * Exponential function increasing from 0 to 1, uninduced to induced respectively,
	 * when bac.y[1] > 10000 the beastie is definitely induced.
	 */
	public double inducedState() {
		double inducedness = 0;
		
		if(y[1] <= 10000){
			inducedness = Math.exp(0.0025*(y[1] - 10000));
		} else {
			inducedness = 1;
		}
		
		return inducedness;
	}
	
	
	/*********************************************************
	 * Lac Operon GRN.
	 * 
	 * Chung JD, Stephanopoulos G. 
	 * On physiological multiplicity and population heterogeneity of biological systems. 
	 * Chemical engineering science. 1996;51(9):1509-1521. 
	 * Available at: http://linkinghub.elsevier.com/retrieve/pii/0009250995003061
	 * 
	 */
	protected class BSimLacBacteriumGRN implements BSimOdeSystem{
		
		// Number of equations in the system
		protected int numEq;
		
		// External level of inducer - can get this from the chemical field
		protected double Iex;				// [uM]
		
		// Initial population variance (induced/uninduced)
		protected boolean POPULATION_VARIANCE = false;
		
		// random initial conditions for the ODE system
		protected boolean RANDOM_ICS = false;		
		
		/** Set Iex. E.g., from external chemical field */
		public void setIex(double newIex) {
			Iex = newIex;
		}

		/*********************************************************
		 * Default constructor, set up the variables.
		 */
		public BSimLacBacteriumGRN(){
			Iex = 0;
			numEq = 2;
		}
		
		/*********************************************************
		 * System of ODEs and their parameters
		 */
		@Override
		public double[] derivativeSystem(double t, double[] y) {
			double[] dy = new double[numEq];
			
			// Parameters for the ODE system
			double k1 = 9;				// [min-1]
			double OT = 1e-3;          	// [uM]
			double RTK2 = 1e5;
			double K1 = 0.012;        	// [uM-2]
			double delta = 0.82;       	// [min-1]
			double beta = 500;         	// [uM]
			double k2 = 0.0055;        	// [min-1]
			double alpha = 60000;      	// [min-1]

			// lac permease generation rate
			double Rgen = k1*OT*(( 1 + K1*(Math.pow(y[1],2)) )/(1 + K1*Math.pow(y[1],2) + RTK2 ));

			// d[Y]/dt - internal permease conc. [uM]
			dy[0] = Rgen - k2*y[0];
			
			// d[I]/dt - internal inducer conc. [uM]
			dy[1] = (alpha*Iex*y[0])/(beta + Iex) + delta*(Iex - y[1]) - k2*y[1];
			
			return dy;
		}

		/**
		 * Get the number of equations in the system.
		 */
		@Override
		public int getNumEq() {
			return numEq;
		}
		
		/**
		 * Get system initial conditions.
		 */
		@Override
		public double[] getICs() {
			
			double[] ics = new double[numEq];
			
			if(RANDOM_ICS){
				if(POPULATION_VARIANCE){
					// equal no of induced and uninduced
					if(Math.random() < 0.5){
						// Induced
						ics[0] = 1 + 0.1*bacRng.nextGaussian();
						ics[1] = 10000 + 500*bacRng.nextGaussian();
					}else{
						// Uninduced
						ics[0] = 0.01 + 0.02*bacRng.nextGaussian();
						ics[1] = 50 + 10*bacRng.nextGaussian();
					}
				}else{
					// All uninduced
					ics[0] = 0.025 + 0.005*bacRng.nextGaussian();
					ics[1] = 2.5*bacRng.nextGaussian();
					}				
			}else{
				// All uninduced, uniform ICS
				ics[0] = 0.025;
				ics[1] = 0;
			}
			
			// Clamp to zero
			ics[0] = ics[0] < 0.0 ? 0 : ics[0];
			ics[1] = ics[1] < 0.0 ? 0 : ics[1];
			
			return ics;
		}
	}
}
