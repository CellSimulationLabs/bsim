package dde_osp_ejs;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import org.opensourcephysics.display.Dataset;
import org.opensourcephysics.display.DrawingFrame;
import org.opensourcephysics.display.PlottingPanel;
import org.opensourcephysics.numerics.ODE;
import org.opensourcephysics.numerics.ode_interpolation.StateHistory;
import org.opensourcephysics.numerics.ode_solvers.DelayDifferentialEquation;
import org.opensourcephysics.numerics.ode_solvers.InterpolatorEventSolver;
import org.opensourcephysics.numerics.ode_solvers.rk.*;

/**
 * Example of direct use of an InterpolatorEventSolver
 * 
 * @author Francisco Esquembre
 * @author María José Cano
 * @version 1.0 November 2013
 */
public class DDEInfectiousDiseaseApp {

  static public void main (String[] args) {

    // ODE
    ODE ode = new DDEInfectiousDesease();
    double maxTime = 40.09;

    // Solver and its parameters
    InterpolatorEventSolver solver = new InterpolatorEventSolver(new CashKarp45(), ode);

    double stepSize = 1; // The initial step size (used by fixed step methods)
    double plotStepSize = 1; // The step size for plotting the solution
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
    while (solver.getCurrentTime()<maxTime) {
      try{
        solver.step();
      } catch(Exception exc) {
        System.err.println ("Error when advancing the solution from "+solver.getCurrentTime()); //$NON-NLS-1$
        exc.printStackTrace();
        return;
      }
    }

    // Compute max error at each plot point
    StateHistory history = solver.getStateHistory();
    ArrayList<double[]> solution = readDataFromFile ("test/dde_osp_ejs/salidaEx2_3Matlab.dat");

    if (solution!=null) { // print the error
      double maxError = 0;
      double maxRelError = 0;
      double[] interpolated = new double[ode.getState().length]; 
      for (int i=0,n=solution.size(); i<n; i++) {
        double[] point = solution.get(i);
        double time = point[timeIndex];
        history.interpolate(time, interpolated);   
        double error = 0, relError = 0;   
        for (int k=0; k<timeIndex; k++) {
          double errorK = Math.abs(point[k] - interpolated[k]);
          error = Math.max(error,errorK);
          if (Math.abs(interpolated[k])>InterpolatorEventSolver.EPSILON) relError = Math.max(relError, error/Math.abs(interpolated[k]));
        }
        System.out.println("Time = " + time + ", Error(t) = " + error + " Relative error(t) = "+relError);
        maxError = Math.max(maxError, error);
        maxRelError = Math.max(maxRelError, relError);
        time += plotStepSize;
      } 
      System.out.println("Max error = " + maxError + " Max relative error = "+maxRelError);
    }


    if (solution!=null) {  // plot the graphs
      // Prepare the graphics
      Dataset stripChart0 = new Dataset(Color.BLUE, Color.BLUE, true);
      stripChart0.setMarkerShape(Dataset.NO_MARKER);
      Dataset stripChart1 = new Dataset(Color.BLUE, Color.BLUE, true);
      stripChart1.setMarkerShape(Dataset.NO_MARKER);
      Dataset stripChart2 = new Dataset(Color.BLUE, Color.BLUE, true);
      stripChart2.setMarkerShape(Dataset.NO_MARKER);
      Dataset stripChartSol0 = new Dataset(Color.RED, Color.RED, true);
      stripChartSol0.setMarkerShape(Dataset.CIRCLE);
      Dataset stripChartSol1 = new Dataset(Color.RED, Color.RED, true);
      stripChartSol1.setMarkerShape(Dataset.CIRCLE);
      Dataset stripChartSol2 = new Dataset(Color.RED, Color.RED, true);
      stripChartSol2.setMarkerShape(Dataset.CIRCLE);

      double[] interpolated = new double[ode.getState().length]; 
      for (int i=0,n=solution.size(); i<n; i++) {
        double[] point = solution.get(i);
        double time = point[timeIndex];
        history.interpolate(time, interpolated); 
        stripChart0.append(time, interpolated[0]);
        stripChart1.append(time, interpolated[1]);
        stripChart2.append(time, interpolated[2]);
        stripChartSol0.append(time, point[0]);
        stripChartSol1.append(time, point[1]);
        stripChartSol2.append(time, point[2]);
        time += plotStepSize;
      }

      PlottingPanel plottingPanel = new PlottingPanel("time", "state", "ODE Test"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      DrawingFrame plottingFrame = new DrawingFrame("ODE Test", plottingPanel); //$NON-NLS-1$
      plottingFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
      plottingPanel.addDrawable(stripChartSol0); // This one is drawn first
      plottingPanel.addDrawable(stripChart0);
      plottingPanel.addDrawable(stripChartSol1); // This one is drawn first
      plottingPanel.addDrawable(stripChart1);
      plottingPanel.addDrawable(stripChartSol2); // This one is drawn first
      plottingPanel.addDrawable(stripChart2);

      plottingPanel.render();
      plottingFrame.setLocation(0, 0);
      plottingFrame.setSize(700,700);
      plottingFrame.setVisible(true);
    }

  }

  static private ArrayList<double[]> readDataFromFile (String filename) {
    try {
      BufferedReader br = new BufferedReader(new FileReader (filename));
      ArrayList<double[]> list = new ArrayList<double[]>();
      String line;
      while((line=br.readLine())!=null) {
        double[] input = new double[4];
        input[0]=Double.parseDouble(line);
        input[1]=Double.parseDouble(br.readLine());
        input[2]=Double.parseDouble(br.readLine());
        input[3]=Double.parseDouble(br.readLine());
        list.add(input);
      }  
      br.close();
      return list;
    }
    catch(Exception e){
      e.printStackTrace();
      return null;
    }
  }


  static class DDEInfectiousDesease implements DelayDifferentialEquation {

    private double[] mState = {5.,0.1,1.,0.};
    private StateHistory mHistory;

    // Implementation of ODE

    public double[] getState() { return mState; }

    public void getRate(double[] state, double[] rate){    
      rate[0] = -state[0]*mHistory.interpolate(state[3]-1, 1)+mHistory.interpolate(state[3]-10, 1);
      rate[1] =  state[0]*mHistory.interpolate(state[3]-1, 1) - state[1];
      rate[2] =  state[1]-mHistory.interpolate(state[3]-10, 1);
      rate[3] = 1; // time
    }

    // Implementation of DelayDifferentialEquation

    public void setStateHistory(StateHistory memory) { mHistory = memory; }

    public double getMaximumDelay() { return 10; }

    public double[] getDelays(double[] state) { return new double[] { 1, 10 }; }

    public void getInitialCondition(double time, double state[]){
      state[0] = 5.0;
      state[1] = 0.1;
      state[2] = 1.0;
      state[3] = time;
    }

    public double[] getInitialConditionDiscontinuities(){
      return null;
    }

  } // End of DelayDifferentialEquation

}
