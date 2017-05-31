package dde;

import org.opensourcephysics.display.Dataset;
import org.opensourcephysics.display.DrawingFrame;
import org.opensourcephysics.display.PlottingPanel;
import org.opensourcephysics.numerics.ODE;
import org.opensourcephysics.numerics.ode_interpolation.StateHistory;
import org.opensourcephysics.numerics.ode_solvers.DelayDifferentialEquation;
import org.opensourcephysics.numerics.ode_solvers.InterpolatorEventSolver;
import org.opensourcephysics.numerics.ode_solvers.rk.CashKarp45;

import java.awt.*;
import java.util.ArrayList;

/**
 * Example of direct use of an InterpolatorEventSolver
 *
 * @author Francisco Esquembre
 * @author Rafael Chicón
 * @version 1.0 December 2013
 */
public class DDEExponentialCopyContinue {

    static public void main (String[] args) {

        // ODE
        ODE ode = new DDEExponential(new double[] {0.06, 0.0});
        double initTime = 0, maxTime = 8;

        // Solver and its parameters
        InterpolatorEventSolver solver = new InterpolatorEventSolver(new CashKarp45(), ode);

        double stepSize = 0.01; // The initial step size (used by fixed step methods)
        double plotStepSize = 0.01; // The step size for plotting the solution
        double absTol = 1.0e-6, relTol = 1.0e-3; // The tolerance for adaptive methods

        // Initialize and customize the solver
        solver.initialize(stepSize);       // This step size affects the solver internal step size
        solver.setStepSize(plotStepSize);  // This step size is the reading step size
        solver.setTolerances(absTol,relTol);
        //    eventSolver.setDDEIterations(500);
        solver.setHistoryLength(Double.POSITIVE_INFINITY); // Recall all past values

        // main loop for solving the ODE
        double[] state = ode.getState();
        int timeIndex = state.length-1;

        // Solve for the whole [initTime,maxTime] interval at once

        boolean duplicated = false;

        ArrayList<InterpolatorEventSolver> solvers = new ArrayList<>();
        ArrayList<InterpolatorEventSolver> solversToAdd = new ArrayList<>();
        solvers.add(solver);

        double tSplit = 1.5;

        while (solver.getCurrentTime() < maxTime) {
            int nSolvers = solvers.size();

            for(int i_sol = 0; i_sol < nSolvers; i_sol++) {
                InterpolatorEventSolver s = solvers.get(i_sol);

                try {
                    System.out.println("Solver " + i_sol + ": time = " + s.getCurrentTime());
                    s.step();

                    if (s.getErrorCode() != InterpolatorEventSolver.ERROR.NO_ERROR) {
                        System.err.println("Error when advancing the solution from " + solver.getCurrentTime()); //$NON-NLS-1$
                        break;
                    }

                    if(!duplicated && solver.getCurrentTime() >= tSplit){
                        duplicated = true;

                        // ODE
                        ODE ode_new = new DDEExponential(solver.getODE().getState()){
//              @Override
//              public void getInitialCondition(double time, double[] state){
//                state[0] = solver.getODE().getState()[0];
//                state[1] = time;
//              }
                        };

                        System.out.println("State before: " + ode_new.getState()[0]);
                        System.arraycopy(solver.getODE().getState(), 0, ode_new.getState(), 0, state.length);
                        System.out.println("State after: " + ode_new.getState()[0]);

                        // Solver and its parameters
                        InterpolatorEventSolver solver_new = new InterpolatorEventSolver(new CashKarp45(), ode_new);

                        // Initialize and customize the solver
                        solver_new.initialize(stepSize);       // This step size affects the solver internal step size
                        solver_new.setStepSize(plotStepSize);  // This step size is the reading step size
                        solver_new.setTolerances(absTol,relTol);
                        //    eventSolver.setDDEIterations(500);
                        solver_new.setHistoryLength(Double.POSITIVE_INFINITY); // Recall all past values

                        solversToAdd.add(solver_new);
                    }

                } catch (Exception exc) {
                    exc.printStackTrace();
                    break;
                }
            }
            solvers.addAll(solversToAdd);
            solversToAdd.clear();
        }

        // Compute max error at each plot point
        StateHistory history = solver.getStateHistory();

//    { // plot the error
//      double maxError = 0;
//      double maxRelError = 0;
//      double[] interpolated = new double[ode.getState().length];
//      double time = initTime;
//      while (time <= maxTime) {
//        history.interpolate(time, interpolated);
//        double error = 0, relError = 0;
//        for (int k=0; k<timeIndex; k++) {
//          double errorK = Math.abs(solution(interpolated[timeIndex]) - interpolated[k]);
//          error = Math.max(error,errorK);
//          if (Math.abs(interpolated[k])>InterpolatorEventSolver.EPSILON) relError = Math.max(relError, error/Math.abs(interpolated[k]));
//        }
//        System.out.println("Time = " + time + ", value = "+interpolated[0]+ ", Error(t) = " + error + " Relative error(t) = "+relError);
//        maxError = Math.max(maxError, error);
//        maxRelError = Math.max(maxRelError, relError);
//        time += plotStepSize;
//      }
//      System.out.println("Max error = " + maxError + " Max relative error = "+maxRelError);
//    }

        InterpolatorEventSolver solver2 = solvers.get(1);
        StateHistory history2 = solver2.getStateHistory();

        {  // Plot the graphs
            // Prepare the graphics
            Dataset stripChart = new Dataset(Color.BLUE, Color.BLUE, true);
            stripChart.setMarkerShape(Dataset.NO_MARKER);

            Dataset solutionStripChart = new Dataset(Color.RED, Color.RED, true);
            solutionStripChart.setMarkerShape(Dataset.NO_MARKER);

            Dataset stripChartContinue = new Dataset(Color.GREEN, Color.GREEN, true);
            stripChartContinue.setMarkerShape(Dataset.NO_MARKER);

            double time = initTime;
            double[] interpolated = new double[ode.getState().length];
            while (time<=maxTime) {
                history.interpolate(time, interpolated);
                stripChart.append(time, interpolated[0]);

                solutionStripChart.append(time, solution(time));

                if(time > tSplit) {
                    history2.interpolate(time, interpolated);
                    stripChartContinue.append(time, interpolated[0]);
                }

                time += plotStepSize;
            }

            PlottingPanel plottingPanel = new PlottingPanel("time", "state", "ODE Test"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            DrawingFrame plottingFrame = new DrawingFrame("ODE Test", plottingPanel); //$NON-NLS-1$
            plottingFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

            plottingPanel.addDrawable(solutionStripChart); // This one is drawn first

            plottingPanel.addDrawable(stripChart);

            plottingPanel.addDrawable(stripChartContinue);

            plottingPanel.render();
            plottingFrame.setLocation(0, 0);
            plottingFrame.setSize(700,700);
            plottingFrame.setVisible(true);
        }

    }

    static private double solution(double time) {
        return Math.sin(time)*Math.exp(-time);
    }

    static class DDEExponential implements DelayDifferentialEquation {
        static private double sAlpha = Math.exp(-Math.PI/2);

        private double constInit;

        private double[] mState = new double[2];
        private StateHistory mHistory;

        // Implementation of ODE

        public DDEExponential(double[] ICs){
            constInit = ICs[0];
            mState[0] = ICs[0];
            mState[1] = ICs[1];
        }

        public double[] getState() { return mState; }

        public void getRate(double[] state, double[] rate){
            rate[0] = - state[0] - sAlpha*mHistory.interpolate(state[1]-Math.PI/2, 0);
            rate[1] = 1; // time
        }

        // Implementation of DelayDifferentialEquation

        public void setStateHistory(StateHistory memory) { mHistory = memory; }

        public double getMaximumDelay() { return Math.PI/2; }

        public double[] getDelays(double[] state) { return new double[] { Math.PI/2 }; }

        public void getInitialCondition(double time, double state[]){
            state[0] = constInit; //Math.sin(time)*Math.exp(-time);
            state[1] = time;
        }

        public double[] getInitialConditionDiscontinuities(){
            return null;
        }

    } // End of DelayDifferentialEquation

}
