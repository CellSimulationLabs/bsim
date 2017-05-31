package BSimChenOscillator;

import BSimChenOscillator.grn.ActivatorDDE;
import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.capsule.BSimCapsuleBacterium;
import org.opensourcephysics.numerics.ode_interpolation.StateHistory;
import org.opensourcephysics.numerics.ode_solvers.InterpolatorEventSolver;
import org.opensourcephysics.numerics.ode_solvers.rk.BogackiShampine23;

import javax.vecmath.Vector3d;

/**
 */
public class ActivatorBacterium extends BSimCapsuleBacterium {

    // TODO: parameters
    private double D_H = 3;
    private double D_I = 2.1;

    public ActivatorDDE grn;

    protected InterpolatorEventSolver solver;

    public StateHistory getStateHistory() {
        return solver.getStateHistory();
    }

    protected BSimChemicalField H_e_field;
    protected BSimChemicalField I_e_field;

    public double[] grn_state;

    public ActivatorBacterium(BSim sim, Vector3d px1, Vector3d px2, BSimChemicalField _H_e, BSimChemicalField _I_e, double[] _ICs){
        super(sim, px1, px2);

        D_H = ChenParameters.p.get("D_H");
        D_I = ChenParameters.p.get("D_I");

        // ---------
        // Set up the GRN

//        double[] ICs = {10, 1, 10, 10, 10, 10, 10, 0};

        ActivatorDDE new_grn = new ActivatorDDE(_ICs);
        this.setGrnAndSolver(new_grn);

//        int timeIndex = grn_state.length - 1;
        // ---------

        this.H_e_field = _H_e;
        this.I_e_field = _I_e;
    }

    /**
     * Set the grn, and initialise a solver for it.
     * @param _grn
     */
    public void setGrnAndSolver(ActivatorDDE _grn){
        this.grn = _grn;
        this.grn_state = this.grn.getState();

        // Solver and its parameters
        solver = new InterpolatorEventSolver(new BogackiShampine23(), _grn);

        double stepSize = sim.getDt()/100.0; // The initial step size (used by fixed step methods)
        double plotStepSize = sim.getDt()/60.0; // The step size for plotting the solution
        final double absTol = 1.0e-6, relTol = 1.0e-3; // The tolerance for adaptive methods

        // Initialize and customize the solver
        solver.initialize(stepSize);       // This step size affects the solver internal step size
        solver.setStepSize(plotStepSize);  // This step size is the reading step size
        solver.setTolerances(absTol, relTol);
        //    eventSolver.setDDEIterations(500);
//        solver.setHistoryLength(Double.POSITIVE_INFINITY); // Recall all past values
        solver.setHistoryLength(8.0); // Recall all past values
        solver.setEnableExceptions(false); // Do not throw exceptions when an error occurs
    }

    @Override
    public void action() {
        super.action();

        // Variables for chemical field response:
        double h_e;	// External Q1
        double i_e;	// External Q2

        double h_Delta;		// Change in Q1
        double i_Delta;		// Change in Q2

        // external chemical level at position of the bacterium:
        h_e = H_e_field.getConc(position);
        i_e = I_e_field.getConc(position);

//        System.out.println("HE: " + h_e + "; IE: " + i_e);

        // Get the external chemical field level for the GRN dde system later on:
        grn.setExternalFieldLevel(h_e, i_e);

        // Solve the dde system
        // TODO: re-scale the time units correctly (GRN equations are in minutes, BSim works in seconds)
        solver.step();
        if (solver.getErrorCode()!=InterpolatorEventSolver.ERROR.NO_ERROR) {
            System.err.println ("Error when advancing the solution from " + solver.getCurrentTime());
            System.err.println ("Delay solver failed in Activator bacterium " + id + ", ");
            System.err.println ("With error: " + solver.getErrorCode());
            System.exit(-42);
//            return;
        }

        // Adjust the external chemical field
        h_Delta = h_e - grn_state[5];
        i_Delta = i_e - grn_state[6];

        // TODO: re-scale time units.
        H_e_field.addQuantity(position, D_H*(-h_Delta)/60.0);
        I_e_field.addQuantity(position, D_I*(-i_Delta)/60.0);
    }

    @Override
    public ActivatorBacterium divide() {

        System.out.println("Activator " + this.id + " is dividing...");

        Vector3d u = new Vector3d(); u.sub(this.x2, this.x1);

        // Uniform Distn; Change to Normal?
        double divPert = 0.1*L_max*(rng.nextDouble() - 0.5);

        double L_actual = u.length();

        double L1 = L_actual*0.5*(1 + divPert) - radius;
        double L2 = L_actual*0.5*(1 - divPert) - radius;

        // Use for dividing the cell contents according to the length fraction of the mother and child.
//        double childProportion = L2/L_actual;
//        double thisProportion = 1 - childProportion;

        ///
        Vector3d x2_new = new Vector3d();
        x2_new.scaleAdd(L1/L_actual, u, this.x1);
        x2_new.add(new Vector3d(0.05*L_initial*(rng.nextDouble() - 0.5),
                0.05*L_initial*(rng.nextDouble() - 0.5),
                0.05*L_initial*(rng.nextDouble() - 0.5)));

        Vector3d x1_child = new Vector3d();
        x1_child.scaleAdd(-(L2/L_actual), u, this.x2);
        x1_child.add(new Vector3d(0.05*L_initial*(rng.nextDouble() - 0.5),
                0.05*L_initial*(rng.nextDouble() - 0.5),
                0.05*L_initial*(rng.nextDouble() - 0.5)));


        // Need to set a new mState and new history for this and the child.
        // These will be set automatically when the DDE constructor is called.
        // However this does mean that we need to initialise a ned DDE and solver for the mother.

        // ICs must be slightly perturbed for both the mother and the daughter.
        // the current state of the mother cell

        // The new states that will be slightly perturbed based on division
        double[] new_state = new double[this.grn_state.length];
        double[] child_state = new double[this.grn_state.length];

        // Store the current state of the DDE system:
        System.arraycopy(this.grn_state, 0, new_state, 0, this.grn_state.length);
        System.arraycopy(this.grn_state, 0, child_state, 0, this.grn_state.length);

        // Iterate to length-1 as we don't care about time
        for(int i = 0; i < grn_state.length - 1; i ++){
            // TODO: parametrise this based on the length or volume proportions.
            double pert_state = 0.1*rng.nextGaussian()*this.grn_state[i];

            new_state[i] = new_state[i] + pert_state;
            child_state[i] = child_state[i] - pert_state;
        }

        // Time since division inits to zero.
        // Can just disable this if it causes problems, but could be nice for tracking cell lifetime.
        new_state[new_state.length - 1] = 0;
        child_state[child_state.length - 1] = 0;

        // Set the new GRN for this cell.
        ActivatorDDE new_grn = new ActivatorDDE(new_state);
        this.setGrnAndSolver(new_grn);

        // Set the child cell.
        ////
        // TODO? Ideally initialise all four co-ordinates, otherwise this operation is order-dependent
        // (this.xi could be overwritten before being passed to child for ex.)
        ActivatorBacterium child = new ActivatorBacterium(sim, x1_child, new Vector3d(this.x2), H_e_field, I_e_field, child_state);
        this.initialise(L1, this.x1, x2_new);
        ///
        child.L = L2;

        System.out.println("Child ID id " + child.id);

        return child;
    }

}
