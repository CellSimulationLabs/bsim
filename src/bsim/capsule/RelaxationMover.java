package bsim.capsule;

import org.opensourcephysics.numerics.ODE;
import org.opensourcephysics.numerics.ode_solvers.InterpolatorEventSolver;
import org.opensourcephysics.numerics.ode_solvers.SolverEngineDiscreteTime;
import org.opensourcephysics.numerics.ode_solvers.rk.*;
import org.opensourcephysics.numerics.ode_solvers.symplectic.VelocityVerlet;

import java.util.List;

/**
 */
public class RelaxationMover implements Mover {

    List<BSimCapsuleBacterium> allBacteria;

    public RelaxationMover(List<BSimCapsuleBacterium> _allBacteria){
        this.allBacteria = _allBacteria;
    }

    public int getSystemDimension(){
        return this.allBacteria.size();
    }

    public void move() {
        /**
         * -1. Compute the velocities and integrate these...
         *
         * 1. Take the list of current co-ordinates ('unpack' from the bacteria)
         * 2. Compute intersections. Generate the list of intersecting pairs.
         * 3. Generate the 'ODE' system
         * 4. Integrate forces -> positions
         * 5. Pack the new positions back into the bacteria list.
         *
         *
         * Storck:
         * Constructs relaxation ODE system
         * Assigns y from balls
         * Solves y for y
         * Assigns new y back into balls
         *
         * compute derivatives::
         * assign in y, to balls' pos, vel; initialise force to zero
         * for each cell,
         *      and then for other cells
         * do a cheap (prelim) distance test.
         * then
         * do a rod-rod collision (l 69)
         * compute the overlap (l 78)
         * use the overlap to compute the force on the cells (l 79-90) *** MAYBE IMPORTANT ***
         * assign the force to the balls -> this is as ours, only they use sc and 1-sc etc while we have a small fudge factor.
         * (then they calculate some other forces; including VELOCITY DAMPING - may need this)
         * then they apply the INTERNAL SPRING FORCE
         * finally they return the yDot
         *
         * This means that posdot = vel, veldot = force
         * full F = ma
         *
         * THEN
         * the ODE integrator repeats this whole thing a number of times... (5ish, depends on pars).
         *
         * AND this gets assigned back into the balls
         *
         * And then apparently this whole process gets repeated a bunch of times...!
         */

        // At some point (before solving the position constraint) we need to compute other velocities
        // (flow et al.) and apply these to the cells.

        int OUTER_ITER = 3;
        SolverEngineDiscreteTime SOLVER = new RK4();
        double T_RELAX = 5.0;
        double DT_RELAX = 0.1;

        for(int iter = 0; iter < OUTER_ITER; iter++) {
            // Construct ODEs for solving all contact constraints
            ODE moverEquations = new MoverODESystem();

            // Solve the contact constraint system
            final double maxTime = T_RELAX;

            // Solver and its parameters
            // Fehlberg family seem to provide a nice solution
            // Some others (Dopri?) are perhaps over-correcting;
            // They are not necessarily stable. Cash-carp is always unstable (practically); Dopri seem to become unstable
            // under certain conditions; Some of the adaptive ones are 'transiently' unstable i.e., they may exhibit some
            // 'jumping', of small amplitude, between time-steps, but will overall remain stable (no bacsplosions).
            /**
             * SolverEngineDiscreteTime: (not adaptive... I think)
             * Euler; EulerRichardson; Fehlberg8; RK4
             *
             * SolverEngineDiscreteTimeAdaptive extends SolverEngineDiscreteTime:
             * BogackiShampine23; CashCarp45; Dopri5; Dopri853; Fehlberg78
             */
            InterpolatorEventSolver solver = new InterpolatorEventSolver(SOLVER, moverEquations);

            double stepSize = DT_RELAX; // The initial step size (used by fixed step methods)
            double plotStepSize = 0.1; // The step size for plotting the solution
            final double absTol = 1.0e-6, relTol = 1.0e-3; // The tolerance for adaptive methods

            // Initialize and customize the solver
            solver.initialize(stepSize);       // This step size affects the solver internal step size
            solver.setStepSize(plotStepSize);  // This step size is the reading step size
            solver.setTolerances(absTol, relTol);
            solver.setHistoryLength(5); // Don't recall all past values
            solver.setEnableExceptions(false); // Do not throw exceptions when an error occurs

            // Solve for the whole [initTime,maxTime] interval at once
            // TODO: solve based on a threshold (i.e., delta force or delta positions)
            while (solver.getCurrentTime() < maxTime) {
//            System.out.println("Hello - stepping solver");
//            System.out.println ("Advancing the solution from " + solver.getCurrentTime());

                solver.step();
                if (solver.getErrorCode() != InterpolatorEventSolver.ERROR.NO_ERROR) {
                    System.err.println("Error when advancing the solution from " + solver.getCurrentTime());
                    return;
                }
            }

            // Pipe the solution back into the bacteria
            // Temporary storage for new (velocities and) positions at each iteration of the integration
            double y_new[] = moverEquations.getState();
            assert y_new.length == getSystemDimension() * 6 + 1;
            int bac_i = 0;
            for (BSimCapsuleBacterium b : allBacteria) {
                b.x1.x = y_new[bac_i++];
                b.x1.y = y_new[bac_i++];
                b.x1.z = y_new[bac_i++];
                b.x2.x = y_new[bac_i++];
                b.x2.y = y_new[bac_i++];
                b.x2.z = y_new[bac_i++];
            }
        }

    }

    class MoverODESystem implements ODE {
        private double[] mState;

        /**
         * Build the vector of initial state variables.
         */
        MoverODESystem() {
            // Temporary storage for new (velocities and) positions at each iteration of the integration
            this.mState = new double[getSystemDimension()*6 + 1];

            // Assign into y the initial conditions (current state of capsular bacteria)
            int bac_i = 0;
            for(BSimCapsuleBacterium b : allBacteria){
                mState[bac_i++] = b.x1.x;
                mState[bac_i++] = b.x1.y;
                mState[bac_i++] = b.x1.z;
                mState[bac_i++] = b.x2.x;
                mState[bac_i++] = b.x2.y;
                mState[bac_i++] = b.x2.z;
            }

            // Remember to include time as the final state variable...
            mState[bac_i] = 0;
        }

        /**
         * Initial conditions should be the original value of all positions.
         */
        @Override
        public double[] getState() {
            return mState;
        }

        /**
         * d pos / dy = force
         *
         * Probably we should integrate the force/velocity for other motile bits before applying position constraints.
         *
         * This equation only considers the position constraints.
         */
        @Override
        public void getRate(double[] y, double[] dy) {

            // Positions for all cells are updated for this integration iteration.
            // We will be accessing these later on to compute collision/intersection.
            int bac_i = 0;
            for(BSimCapsuleBacterium b : allBacteria){
                // Pretty dirty syntax...
                b.x1.x = y[bac_i++];
                b.x1.y = y[bac_i++];
                b.x1.z = y[bac_i++];
                b.x2.x = y[bac_i++];
                b.x2.y = y[bac_i++];
                b.x2.z = y[bac_i++];

                b.x1force.set(0.0, 0.0, 0.0);
                b.x2force.set(0.0, 0.0, 0.0);
            }

//                forceAccumulator = 0;

            for (BSimCapsuleBacterium b : allBacteria) {
                b.computeSelfForce();
                b.computeWallForce();
            }

            // Interaction
            int bacteriaSize = getSystemDimension();
            int j = 1;

            // Dirty somewhat >> O(n^2) factorial loop...
            // TODO: Replace this with a gridded method.
            for (BSimCapsuleBacterium b1 : allBacteria) {

                List<BSimCapsuleBacterium> remainingBacteria = allBacteria.subList(j, bacteriaSize);

                for (BSimCapsuleBacterium b2 : remainingBacteria) {
                    b1.computeNeighbourForce(b2);
                }
                j += 1;
            }

//                for (BSimCapsuleBacterium b : allBacteria) {
//
//                    forceAccumulator += b.x1force.length();
//                    forceAccumulator += b.x2force.length();
//
//                    b.updatePosition();
//                }

            // dpos/dt = force... We are concerned with a viscous regime...
            bac_i = 0;
            for(BSimCapsuleBacterium b : allBacteria){
                dy[bac_i++] = b.x1force.x;
                dy[bac_i++] = b.x1force.y;
                dy[bac_i++] = b.x1force.z;
                dy[bac_i++] = b.x2force.x;
                dy[bac_i++] = b.x2force.y;
                dy[bac_i++] = b.x2force.z;
            }
            // And finally, remember to advance time by the appropriate rate...:
            dy[bac_i] = 1.0;
        }
    }
}
