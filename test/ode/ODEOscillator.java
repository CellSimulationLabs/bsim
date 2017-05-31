package ode;

import org.opensourcephysics.display.Dataset;
import org.opensourcephysics.display.DrawingFrame;
import org.opensourcephysics.display.PlottingPanel;
import org.opensourcephysics.numerics.ODE;
import org.opensourcephysics.numerics.ode_interpolation.StateHistory;
import org.opensourcephysics.numerics.ode_solvers.InterpolatorEventSolver;
import org.opensourcephysics.numerics.ode_solvers.rk.BogackiShampine23;

import java.awt.*;

/**
 * Created by antmatyjajo on 22/11/2016.
 */
public class ODEOscillator {

    static public void main(String[] args) {

        ODEOscillator os = new ODEOscillator();

        // Construct ODEs for solving all contact constraints
        ODE odes = os.new RepressilatorODESystem();

        // Solve the contact constraint system
        final double initTime = 0.0, maxTime = 10;

        // Solver and its parameters
        InterpolatorEventSolver solver = new InterpolatorEventSolver(new BogackiShampine23(), odes);

        double stepSize = 0.01; // The initial step size (used by fixed step methods)
        double plotStepSize = 0.1; // The step size for plotting the solution
        final double absTol = 1.0e-6, relTol = 1.0e-3; // The tolerance for adaptive methods

        // Initialize and customize the solver
        solver.initialize(stepSize);       // This step size affects the solver internal step size
        solver.setStepSize(plotStepSize);  // This step size is the reading step size
        solver.setTolerances(absTol, relTol);
        solver.setHistoryLength(Double.POSITIVE_INFINITY); // Don't recall all past values
        solver.setEnableExceptions(false); // Do not throw exceptions when an error occurs

        // Solve for the whole [initTime,maxTime] interval at once
        while (solver.getCurrentTime() < maxTime) {
//            System.out.println("Hello - stepping solver");
            System.out.println("Advancing the solution from " + solver.getCurrentTime());

            solver.step();
            if (solver.getErrorCode() != InterpolatorEventSolver.ERROR.NO_ERROR) {
                System.err.println("Error when advancing the solution from " + solver.getCurrentTime());
                return;
            }
        }

        // Compute max error at each plot point
        StateHistory history = solver.getStateHistory();

        {  // plot the graphs
            // Prepare the graphics
            Dataset stripChart = new Dataset(Color.BLUE, Color.BLUE, true);
            stripChart.setMarkerShape(Dataset.NO_MARKER);

            double time = initTime;
            double[] interpolated = new double[odes.getState().length];
            while (time <= maxTime) {
                history.interpolate(time, interpolated);
                stripChart.append(time, interpolated[0]);
                time += plotStepSize;
            }

            PlottingPanel plottingPanel = new PlottingPanel("time", "state", "ODE Test"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            DrawingFrame plottingFrame = new DrawingFrame("ODE Test", plottingPanel); //$NON-NLS-1$
            plottingFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            plottingPanel.addDrawable(stripChart);

            plottingPanel.render();
            plottingFrame.setLocation(0, 0);
            plottingFrame.setSize(700, 700);
            plottingFrame.setVisible(true);
        }
    }

    class RepressilatorODESystem implements ODE {

        // Everything gets upset without mState
        private double[] mState;

        // Let's try to redefine mState in the constructor
        // (useful in case we want some complicated ICs...)
        public RepressilatorODESystem(){
            this.mState = new double[]{1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        }

        /**
         * Initial conditions
         */
        @Override
        public double[] getState() {
            return mState;
        }

        /**
         * Classic Garcia-Ojalvo repressilator ODEs
         */
        @Override
        public void getRate(double[] y, double[] dy) {
            double a = 10;
            double a0 = 0;
            double n = 2;
            double b = 5;

            dy[0] = -y[0] + a/(1 + Math.pow(y[5],n)) + a0;
            dy[1] = -y[1] + a/(1 + Math.pow(y[3],n)) + a0;
            dy[2] = -y[2] + a/(1 + Math.pow(y[4],n)) + a0;
            dy[3] = b*(y[0] - y[3]);
            dy[4] = b*(y[1] - y[4]);
            dy[5] = b*(y[2] - y[5]);

            dy[6] = 1.0;
        }
    }

}
