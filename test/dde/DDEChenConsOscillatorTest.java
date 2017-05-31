package dde;

import org.opensourcephysics.display.Dataset;
import org.opensourcephysics.display.DrawingFrame;
import org.opensourcephysics.display.PlottingPanel;
import org.opensourcephysics.numerics.ODE;
import org.opensourcephysics.numerics.ode_interpolation.StateHistory;
import org.opensourcephysics.numerics.ode_solvers.DelayDifferentialEquation;
import org.opensourcephysics.numerics.ode_solvers.InterpolatorEventSolver;
import org.opensourcephysics.numerics.ode_solvers.rk.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.SynchronousQueue;

/**
 * Chen et al., consortium oscillator
 */
public class DDEChenConsOscillatorTest {

    static public void main (String[] args) {

        Random rng = new Random();

        // ODE
        ODE ode = new DDEChenConsOscillator();
        final double initTime = 0, maxTime = 1000.0;

        // Solver and its parameters
        InterpolatorEventSolver solver = new InterpolatorEventSolver(new BogackiShampine23(), ode);

        double stepSize = 0.1; // The initial step size (used by fixed step methods)
        double plotStepSize = 0.1; // The step size for plotting the solution
        double outputStepSize = 0.1; // The step size for dense output from the solver
        final double absTol = 1.0e-6, relTol = 1.0e-3; // The tolerance for adaptive methods

        // Initialize and customize the solver
        solver.initialize(stepSize);       // This step size affects the solver internal step size
        solver.setStepSize(outputStepSize);  // This step size is the reading step size
        solver.setTolerances(absTol,relTol);
        //    eventSolver.setDDEIterations(500);
        solver.setHistoryLength(Double.POSITIVE_INFINITY); // Recall all past values
        solver.setEnableExceptions(false); // Do not throw exceptions when an error occurs

        // main loop for solving the ODE
        double[] state = ode.getState();
        int timeIndex = state.length-1;

        System.out.println("Starting simulation...");
        long tStart = System.nanoTime();

        // Do we want the 'cell' to divide?
        boolean divisionPerturb = false;

        // If the cell divides, how much do we perturb chemical quantities?
        double divisionPerturbAmount = 0.1;

        int[] internalQuantities = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 15};

        double divisionRate = 1.0/30.0;
        double pDivision = divisionRate*outputStepSize;

        // Solve for the whole [initTime,maxTime] interval at once
        while (solver.getCurrentTime() < maxTime) {
//            System.out.println(ode.getState()[9]);

            // We perturb all internal chemical quantities by an amount upon cell division
            if (divisionPerturb && (rng.nextDouble() < pDivision)) {
                System.out.println("Perturbing at t = " + solver.getCurrentTime());
                System.out.println(state[0]);
                for (int i = 0; i < internalQuantities.length; i++) {
                    state[i] = state[i] + rng.nextGaussian()*divisionPerturbAmount*state[i];
                }
                System.out.println(state[0]);
            }

            solver.step();
            if (solver.getErrorCode()!=InterpolatorEventSolver.ERROR.NO_ERROR) {
                System.err.println ("Error when advancing the solution from "+solver.getCurrentTime()); //$NON-NLS-1$
                return;
            }
        }
        long tEnd = System.nanoTime();
        System.out.println("Time elapsed : " + (tEnd - tStart)/1e9 + " seconds.");

//        System.out.println(solver.getStateHistory().toString());

        // Compute max error at each plot point
        StateHistory history = solver.getStateHistory();

//        { // print the error
//            double maxError = 0;
//            double maxRelError = 0;
//            double[] interpolated = new double[ode.getState().length];
//            double time = initTime;
//            while (time<=maxTime) {
//                history.interpolate(time, interpolated);
//                double error = 0, relError = 0;
//                for (int k=0; k<timeIndex; k++) {
//                    if (Math.abs(interpolated[k])>InterpolatorEventSolver.EPSILON) relError = Math.max(relError, error/Math.abs(interpolated[k]));
//                }
//                System.out.println("Time = " + time + ", value = "+ interpolated[0]+ ", Error(t) = " + error + " Relative error(t) = "+relError);
//                maxError = Math.max(maxError, error);
//                maxRelError = Math.max(maxRelError, relError);
//                time += plotStepSize;
//            }
//            System.out.println("Max error = " + maxError + " Max relative error = "+maxRelError);
//        }

        System.out.println(System.getProperty("user.dir"));
        ArrayList<double[]> solution = readDataFromFileLines ("./test/dde/chen_matlab.csv");

        {  // plot the graphs
            // Prepare the graphics
            Dataset stripChart0 = new Dataset(Color.BLUE, Color.BLUE, true);
            stripChart0.setMarkerShape(Dataset.NO_MARKER);
            Dataset stripChart1 = new Dataset(Color.GREEN, Color.GREEN, true);
            stripChart1.setMarkerShape(Dataset.NO_MARKER);

            Dataset stripChartSol0 = new Dataset(Color.BLUE, Color.BLUE, false);
            stripChartSol0.setMarkerShape(Dataset.CIRCLE);
            Dataset stripChartSol1 = new Dataset(Color.GREEN, Color.GREEN, false);
            stripChartSol1.setMarkerShape(Dataset.CIRCLE);

            // Matlab vs. Java error
            Dataset errorChart0 = new Dataset(Color.RED, Color.BLUE, true);
            errorChart0.setMarkerShape(Dataset.CIRCLE);
            Dataset errorChart1 = new Dataset(Color.RED, Color.GREEN, true);
            errorChart1.setMarkerShape(Dataset.CIRCLE);


//            double time = initTime;
            double[] interpolated = new double[ode.getState().length];

            for (int i=0, n=solution.size(); i < n; i++) {
                double[] point = solution.get(i);
                double time = point[0];

                history.interpolate(time, interpolated);

                stripChart0.append(time, interpolated[7]);
                stripChart1.append(time, interpolated[9]);

                stripChartSol0.append(time, point[1]);
                stripChartSol1.append(time, point[2]);

                errorChart0.append(time, Math.abs(1 - interpolated[7]/point[1]));
                errorChart1.append(time, Math.abs(1 - interpolated[9]/point[2]));

                time += plotStepSize;
            }

//            double time = initTime;
//            double[] interpolated = new double[ode.getState().length];
//            while (time<=maxTime) {
//                history.interpolate(time, interpolated);
//                stripChart0.append(time, interpolated[7]);
//                time += plotStepSize;
//            }

            PlottingPanel plottingPanel = new PlottingPanel("time", "state", "Chen Oscillator"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            DrawingFrame plottingFrame = new DrawingFrame("Chen Oscillator", plottingPanel); //$NON-NLS-1$
            plottingFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

            // Draw the Java solutions
            plottingPanel.addDrawable(stripChart0);
            plottingPanel.addDrawable(stripChart1);

            // Draw the (Matlab) solutions on top
            plottingPanel.addDrawable(stripChartSol0);
            plottingPanel.addDrawable(stripChartSol1);

            plottingPanel.render();
            plottingFrame.setLocation(0, 0);
            plottingFrame.setSize(700,700);
            plottingFrame.setVisible(true);


            // Plot matlab vs. Java error
            PlottingPanel errorPanel = new PlottingPanel("time", "error", "Relative error Java vs Matlab"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            DrawingFrame errorFrame = new DrawingFrame("Relative error Java vs Matlab", errorPanel); //$NON-NLS-1$
            errorFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

            errorPanel.addDrawable(errorChart0);
            errorPanel.addDrawable(errorChart1);

            errorPanel.render();
            errorFrame.setLocation(700, 0);
            errorFrame.setSize(700, 400);
            errorFrame.setVisible(true);
        }
    }

    static private ArrayList<double[]> readDataFromFileLines (String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            ArrayList<double[]> list = new ArrayList<double[]>();
            String line;
            while((line=br.readLine())!=null) {
                double[] input = new double[3];
                StringTokenizer tkn = new StringTokenizer(line,",");
                input[0]=Double.parseDouble(tkn.nextToken());
                input[1]=Double.parseDouble(tkn.nextToken());
                input[2]=Double.parseDouble(tkn.nextToken());
                list.add(input);
//        System.out.println("["+input[0]+", "+input[1]+", "+input[2]+", "+input[3]+"],");
            }
            br.close();
            return list;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    static class DDEChenConsOscillator implements DelayDifferentialEquation {

        // ICs : in Matlab, (2) and (5) are set == 1; the rest == 10
        // Obviously time (state[16]) wasn't present in matlab and its IC is 0
        private double[] mState = {10, 1, 10, 10, 1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 0};
        private StateHistory mHistory;

        // Implementation of ODE

        public double[] getState() { return mState; }

        public void getRate(double[] state, double[] rate){
            double R_a = state[0];
            double L_a = state[1];
            double A_a = state[2];

            double C_r = state[3];
            double L_r = state[4];
            double A_r = state[5];

            double F_a = state[6];
            double M_a = state[7];

            double Y_r = state[8];
            double M_r = state[9];

            double H_a = state[10];
            double H_e = state[11];
            double H_r = state[12];

            double I_r = state[13];
            double I_e = state[14];
            double I_a = state[15];

            double H_a_tau = mHistory.interpolate(state[16] - lag, 10);
            double I_a_tau = mHistory.interpolate(state[16] - lag, 15);

            double H_r_tau = mHistory.interpolate(state[16] - lag, 12);
            double I_r_tau = mHistory.interpolate(state[16] - lag, 13);

            double L_a_tau = mHistory.interpolate(state[16] - lag, 1);
            double L_r_tau = mHistory.interpolate(state[16] - lag, 4);

            double deg_a_pref = d_C/(K_C + R_a + A_a + L_a + F_a + M_a);
            double deg_r_pref = d_C/(K_C + C_r + A_r + L_r + Y_r + M_r);

            // DDEs
            // R_a
            rate[0] = (eta_R0 + eta_R1*Math.pow((H_a_tau/K_H_s), n_H))/( 1 + Math.pow((H_a_tau/K_H_s), n_H) + Math.pow((L_a_tau/K_L), n_L) ) - deg_a_pref*R_a - d*R_a;
            // L_a
            rate[1] = (eta_L0 + eta_L1*Math.pow((I_a_tau/K_I_w), n_I))/( 1 + Math.pow((I_a_tau/K_I_w), n_I) ) - deg_a_pref*L_a - d*L_a;
            // A_a
            rate[2] = (eta_A0 + eta_A1*Math.pow((I_a_tau/K_I_m), n_I))/( 1 + Math.pow((I_a_tau/K_I_m), n_I) ) - deg_a_pref*A_a - d*A_a;

            // C_r
            rate[3] = (eta_C0 + eta_C1*Math.pow((H_r_tau/K_H_w), n_H))/( 1 + Math.pow((H_r_tau/K_H_w), n_H) + Math.pow((L_r_tau/K_L), n_L) ) - deg_r_pref*C_r - d*C_r;
            // L_r
            rate[4] = (eta_L0 + eta_L1*Math.pow((I_r_tau/K_I_w), n_I))/( 1 + Math.pow((I_r_tau/K_I_w), n_I) ) - deg_r_pref*L_r - d*L_r;
            // A_r
            rate[5] = (eta_A0 + eta_A1*Math.pow((I_r_tau/K_I_m), n_I))/( 1 + Math.pow((I_r_tau/K_I_m), n_I) ) - deg_r_pref*A_r - d*A_r;

            // F_a
            rate[6] = (eta_F0 + eta_F1*Math.pow((H_a_tau/K_H_s), n_H))/( 1 + Math.pow((H_a_tau/K_H_s), n_H) + Math.pow((L_a_tau/K_L), n_L) ) - deg_a_pref*F_a - d*F_a - m*F_a;
            // M_a
            rate[7] = m*F_a - deg_a_pref*M_a - d*M_a;

            // Y_r
            rate[8] = (eta_Y0 + eta_Y1*Math.pow((I_r_tau/K_I_w), n_I))/( 1 + Math.pow((I_r_tau/K_I_w), n_I) + Math.pow((L_r_tau/K_L), n_L) ) - deg_r_pref*Y_r - d*Y_r - m*Y_r;
            // M_r
            rate[9] = m*Y_r - deg_r_pref*M_r - d*M_r;

            // H_a
            rate[10] = phi_H*R_a - D_H*(H_a - H_e) - (d_A*A_a*H_a)/( K_A + I_a + H_a ) - d*H_a;
            // H_e
            rate[11] = (d_a/(1 - d_a - d_r))*D_H*(H_a - H_e) - (d_r/(1 - d_a - d_r))*D_H*(H_e - H_r) - mu_e*H_e;
            // H_r
            rate[12] = D_H*(H_e - H_r) - (d_A*A_r*H_r)/(K_A + I_r + H_r) - d*H_r;

            // I_r
            rate[13] = phi_I*C_r - D_I*(I_r - I_e) - (d_A*A_r*I_r)/( K_A + I_r + H_r ) - d*I_r;
            // I_e
            rate[14] = (d_r/(1 - d_a - d_r))*D_I*(I_r - I_e) - (d_a/(1 - d_a - d_r))*D_I*(I_e - I_a) - mu_e*I_e;
            // I_a
            rate[15] = D_I*(I_e - I_a) - (d_A*A_a*I_a)/(K_A + I_a + H_a) - d*I_a;

            rate[16] = 1; // time
        }

        // Implementation of DelayDifferentialEquation

        public void setStateHistory(StateHistory memory) { mHistory = memory; }

        public double getMaximumDelay() { return lag; }

        public double[] getDelays(double[] state) { return new double[] { lag }; }

        public void getInitialCondition(double time, double state[]){
            state[0] = 10;
            state[1] = 1;
            state[2] = 10;
            state[3] = 10;
            state[4] = 1;
            state[5] = 10;
            state[6] = 10;
            state[7] = 10;
            state[8] = 10;
            state[9] = 10;
            state[10] = 10;
            state[11] = 10;
            state[12] = 10;
            state[13] = 10;
            state[14] = 10;
            state[15] = 10;
            state[16] = time;

        }

        public double[] getInitialConditionDiscontinuities(){
            return null;
        }

        // Delay
        private double lag = 7.5;

        /**
         * PARAMETERS
         */
        private double S_R = 10.423;
        private double S_C = 16.437;
        private double S_L = 8.36;
        private double S_A = 408.37; //15.108;
        private double S_F = 5.031;
        private double S_Y = 7.723;
        private double ClpXP = 683.89;

        private double d_a = 0.4;
        private double d_r = 0.4;

        private double eta_R0 = 20.*S_R;
        private double eta_R1 = 367.*S_R;
        private double eta_C0 = 1.*S_C;
        private double eta_C1 = 624.44*S_C;
        private double eta_F0 = 20.*S_F;
        private double eta_F1 = 367.*S_F;
        private double eta_Y0 = 1.*S_Y;
        private double eta_Y1 = 1713.*S_Y;
        private double eta_L0 = 1.*S_L;
        private double eta_L1 = 1735.47*S_L;
        private double eta_A0 = 1.*S_A; //27.03.*S_A;
        private double eta_A1 = 5.23*S_A; //141.61.*S_A;

        private double K_H_w = 16599.38;
        private double K_H_m = 10333.46;
        private double K_H_s = 5936.86;

        private double K_L = 47.7;
        private double K_L_t = 85.38;

        private double K_I_w = 2357.3;
        private double K_I_m = 594.23;

        private double n_H = 4;
        private double n_L = 2;
        private double n_I = 4;

        private double d_C = 1.8*ClpXP;
        private double K_C = 1300;

        private double d_A = 2257;
        private double K_A = 5110000;

        private double d = Math.log(2)/25.0;

        private double mu_e = 0.1;
        private double D_H = 3;
        private double D_I = 2.1;
        private double phi_H = 16;
        private double phi_I = 2;

        private double m = Math.log(2)/3.0;

    } // End of DelayDifferentialEquation

}
