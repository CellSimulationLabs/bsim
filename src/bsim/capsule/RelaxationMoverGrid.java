package bsim.capsule;

import bsim.BSim;
import org.opensourcephysics.numerics.ODE;
import org.opensourcephysics.numerics.ode_solvers.InterpolatorEventSolver;
import org.opensourcephysics.numerics.ode_solvers.SolverEngineDiscreteTime;
import org.opensourcephysics.numerics.ode_solvers.rk.*;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Finally, we are integrating the positions for all bacteria, so if the integrator is slow that will be a limiting factor.
 * Might not be too bad since we still need to apply flow, growth, etc., to all bacteria anyway.
 *
 */
public class RelaxationMoverGrid implements Mover {

    class Box {
        List<BSimCapsuleBacterium> bacsInside;

        public Box(){
            this.bacsInside = new ArrayList<BSimCapsuleBacterium>();
        }
    }

    List<BSimCapsuleBacterium> allBacteria;
    BSim sim;
    Vector3d simDims;
    Box[][] grid;
    int nX;
    int nY;
    double gridCellWidth;
    double originX;
    double originY;

    public RelaxationMoverGrid(List<BSimCapsuleBacterium> _allBacteria, BSim _sim){
        this.sim = _sim;
        this.allBacteria = _allBacteria;
        this.simDims = sim.getBound();

        // Generate the grid
        // all we need to do each timestep is update where cells are in the grid.
        double simX = simDims.x;
        double simY = simDims.y;

        // An initial approximate (desired) grid cell width.
        // Care; if this is too small, it is actually possible for cells to
        //  a) not be able to collide with a cell that is outside of its grid box's neighbours, even though they're slightly overlapping
        //  b) end up outside the 'bounds' of the domain enclosed by the grid, as a result of a cell division
        this.gridCellWidth = 6.5;

        // We want the cell size to be >= minimum width
        this.nX = (int)Math.floor(simX/gridCellWidth);
        this.nY = (int)Math.floor(simY/gridCellWidth);

        // Check validity (e.g., if bad aspect ratio domain etc.)
        assert(nX > 0);
        assert(nY > 0);

        // TODO: max(simX/nX, simY/nX)? in case of narrow domain for example?
        this.gridCellWidth = simX/(float)nX;

        // And, we want there to be grid cells on the outside of the domain in case there's an open edge.
        this.nX += 2;
        this.nY += 2;

        simX += gridCellWidth;
        simY += gridCellWidth;
        this.originX = -gridCellWidth;
        this.originY = -gridCellWidth;

        // initialise the grid
        this.grid = new Box[nX][nY];
        for(int iX = 0; iX < nX; iX++){
            for(int iY = 0; iY < nY; iY++){
                grid[iX][iY] = new Box();
            }
        }
    }

    public int getSystemDimension(){
        return this.allBacteria.size();
    }

    public void move() {
        // Before solving the position constraint, we need to compute other velocities
        // (flow etc.) and apply these to the cells.
        for(BSimCapsuleBacterium b : allBacteria){
            b.setAllForcesZero();
            b.computeFlowForce();
            b.updatePosition();
        }

        int OUTER_ITER = 1;
        SolverEngineDiscreteTime SOLVER = new BogackiShampine23();
        double T_RELAX = 2.0;
        double DT_RELAX = 0.01;

        for(int iter = 0; iter < OUTER_ITER; iter++) {
            // For a first approximation.
            // The bacteria should not move a 'significant' relative distance in the grid.
            // Therefore we can generate the grid once per timestep, for now.
            // Updating the grid could be optimised to make use of temporal coherence between timesteps.
            for(int iX = 0; iX < nX; iX++){
                for(int iY = 0; iY < nY; iY++){
                    grid[iX][iY].bacsInside.clear();
                }
            }

            for(BSimCapsuleBacterium b : allBacteria){
                int bInX = (int)Math.floor(b.position.x/gridCellWidth) + 1;
                int bInY = (int)Math.floor(b.position.y/gridCellWidth) + 1;
//                if(bInX >= nX || bInY >= nY){
//                    System.out.println();
//                }
                grid[bInX][bInY].bacsInside.add(b);
            }


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
                    System.err.println ("Error when advancing the solution from " + solver.getCurrentTime());
                    System.err.println ("Relaxation ODE solver failed for position constraints,");
                    System.err.println ("With error: " + solver.getErrorCode());
                    System.exit(-42);
//                    return;
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

            // Interaction on a grid...

            List<BSimCapsuleBacterium> bacsThisGridCell = new ArrayList<BSimCapsuleBacterium>();
            List<BSimCapsuleBacterium> bacsNeighbourGridCell = new ArrayList<BSimCapsuleBacterium>();
            int prevX, prevY, nextX, nextY;

            // Loop over every grid cell...
            for(int iX = 0; iX < nX; iX++){
//                System.out.println("iX: " + iX);

                prevX = iX - 1;
                nextX = iX + 2;
                if(prevX < 0) prevX = 0;
                if(nextX > nX) nextX = nX;

//                System.out.println("pX, nX: " + prevX + ", " + nextX);

                for(int iY = 0; iY < nY; iY++){

                    prevY = iY - 1;
                    nextY = iY + 2;
                    if(prevY < 0) prevY = 0;
                    if(nextY > nY) nextY = nY;


                    bacsThisGridCell = grid[iX][iY].bacsInside;

//                    System.out.println("This X,Y: " + iX + ", " + iY);
//                    System.out.println("Prev X,Y: " + prevX + ", " + prevY);
//                    System.out.println("Next X,Y: " + nextX + ", " + nextY);

                    // Loop over neighbouring grid elements
                    for(int neighbourX = prevX; neighbourX < nextX; neighbourX++){
                        for(int neighbourY = prevY; neighbourY < nextY; neighbourY++) {
//                            System.out.println("neighbour X,Y: " + neighbourX + ", " + neighbourY);
                            bacsNeighbourGridCell = grid[neighbourX][neighbourY].bacsInside;

                            // Iterate over all possible pairs of bacteria
                            for (BSimCapsuleBacterium b1 : bacsThisGridCell) {
                                for (BSimCapsuleBacterium b2 : bacsNeighbourGridCell) {
                                    // In the case that we are testing against bacs in the same box...
                                    // We need to check that we are not doing a self-intersection, which is impossible
                                    if (b1 != b2) {
                                        b1.computeNeighbourForce(b2);
                                    }
                                }
                            }

                        }
                    }
                }
            }

//                for (BSimCapsuleBacterium b : allBacteria) {
//
//                    forceAccumulator += b.x1force.length();
//                    forceAccumulator += b.x2force.length();
//
//                    b.updatePosition();
//                }

            // dpos/dt = force... We are in a viscous regime...
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
