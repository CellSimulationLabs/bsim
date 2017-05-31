package BSimChenOscillator.grn;

import BSimChenOscillator.ChenParameters;
import org.opensourcephysics.numerics.ode_interpolation.StateHistory;
import org.opensourcephysics.numerics.ode_solvers.DelayDifferentialEquation;

/**
 */
public class RepressorDDE implements DelayDifferentialEquation {

    private double H_external = 10.0;
    private double I_external = 10.0;

    public RepressorDDE(double[] _ICs){
        D_H = ChenParameters.p.get("D_H");
        D_I = ChenParameters.p.get("D_I");

        phi_H = ChenParameters.p.get("phi_H");
        phi_I = ChenParameters.p.get("phi_I");

        this.initialConditions = _ICs;
        System.arraycopy(_ICs, 0, this.initialConditions, 0, _ICs.length);
        System.arraycopy(_ICs, 0, this.mState, 0, _ICs.length);
    }

    public void setExternalFieldLevel(double _H_external, double _I_external){
        this.H_external = _H_external;
        this.I_external = _I_external;
    }

    // ICs : in Matlab, (2) and (5) are set == 1; the rest == 10
    // Obviously time (state[16]) wasn't present in matlab and its IC is 0
    private double[] mState = new double[8];
    public double[] initialConditions = new double[8];

    private StateHistory mHistory;

    // Implementation of ODE

    public double[] getState() { return mState; }

    public void getRate(double[] state, double[] rate){
//        double R_a = state[0];
//        double L_a = state[1];
//        double A_a = state[2];

        double C_r = state[0];
        double L_r = state[1];
        double A_r = state[2];

//        double F_a = state[6];
//        double M_a = state[7];

        double Y_r = state[3];
        double M_r = state[4];

//        double H_a = state[10];
        double H_e = H_external;
        double H_r = state[5];

        double I_r = state[6];
        double I_e = I_external;
//        double I_a = state[15];

//        double H_a_tau = mHistory.interpolate(state[16] - lag, 10);
//        double I_a_tau = mHistory.interpolate(state[16] - lag, 15);

        double H_r_tau = mHistory.interpolate(state[7] - lag, 5);
        double I_r_tau = mHistory.interpolate(state[7] - lag, 6);

//        double L_a_tau = mHistory.interpolate(state[16] - lag, 1);
        double L_r_tau = mHistory.interpolate(state[7] - lag, 1);

//        double deg_a_pref = d_C/(K_C + R_a + A_a + L_a + F_a + M_a);
        double deg_r_pref = d_C/(K_C + C_r + A_r + L_r + Y_r + M_r);

        // DDEs
        // R_a
//        rate[0] = (eta_R0 + eta_R1*Math.pow((H_a_tau/K_H_s), n_H))/( 1 + Math.pow((H_a_tau/K_H_s), n_H) + Math.pow((L_a_tau/K_L), n_L) ) - deg_a_pref*R_a - d*R_a;
        // L_a
//        rate[1] = (eta_L0 + eta_L1*Math.pow((I_a_tau/K_I_w), n_I))/( 1 + Math.pow((I_a_tau/K_I_w), n_I) ) - deg_a_pref*L_a - d*L_a;
        // A_a
//        rate[2] = (eta_A0 + eta_A1*Math.pow((I_a_tau/K_I_m), n_I))/( 1 + Math.pow((I_a_tau/K_I_m), n_I) ) - deg_a_pref*A_a - d*A_a;

        // C_r
        rate[0] = (eta_C0 + eta_C1*Math.pow((H_r_tau/K_H_w), n_H))/( 1 + Math.pow((H_r_tau/K_H_w), n_H) + Math.pow((L_r_tau/K_L), n_L) ) - deg_r_pref*C_r - d*C_r;
        // L_r
        rate[1] = (eta_L0 + eta_L1*Math.pow((I_r_tau/K_I_w), n_I))/( 1 + Math.pow((I_r_tau/K_I_w), n_I) ) - deg_r_pref*L_r - d*L_r;
        // A_r
        rate[2] = (eta_A0 + eta_A1*Math.pow((I_r_tau/K_I_m), n_I))/( 1 + Math.pow((I_r_tau/K_I_m), n_I) ) - deg_r_pref*A_r - d*A_r;

        // F_a
//        rate[6] = (eta_F0 + eta_F1*Math.pow((H_a_tau/K_H_s), n_H))/( 1 + Math.pow((H_a_tau/K_H_s), n_H) + Math.pow((L_a_tau/K_L), n_L) ) - deg_a_pref*F_a - d*F_a - m*F_a;
        // M_a
//        rate[7] = m*F_a - deg_a_pref*M_a - d*M_a;

        // Y_r
        rate[3] = (eta_Y0 + eta_Y1*Math.pow((I_r_tau/K_I_w), n_I))/( 1 + Math.pow((I_r_tau/K_I_w), n_I) + Math.pow((L_r_tau/K_L), n_L) ) - deg_r_pref*Y_r - d*Y_r - m*Y_r;
        // M_r
        rate[4] = m*Y_r - deg_r_pref*M_r - d*M_r;

        // H_a
//        rate[10] = phi_H*R_a - D_H*(H_a - H_e) - (d_A*A_a*H_a)/( K_A + I_a + H_a ) - d*H_a;
        // H_e
//        rate[11] = (d_a/(1 - d_a - d_r))*D_H*(H_a - H_e) - (d_r/(1 - d_a - d_r))*D_H*(H_e - H_r) - mu_e*H_e;
        // H_r
        rate[5] = D_H*(H_e - H_r) - (d_A*A_r*H_r)/(K_A + I_r + H_r) - d*H_r;

        // I_r
        rate[6] = phi_I*C_r - D_I*(I_r - I_e) - (d_A*A_r*I_r)/( K_A + I_r + H_r ) - d*I_r;
        // I_e
//        rate[14] = (d_r/(1 - d_a - d_r))*D_I*(I_r - I_e) - (d_a/(1 - d_a - d_r))*D_I*(I_e - I_a) - mu_e*I_e;
        // I_a
//        rate[15] = D_I*(I_e - I_a) - (d_A*A_a*I_a)/(K_A + I_a + H_a) - d*I_a;

        rate[7] = 1; // time
    }

    // Implementation of DelayDifferentialEquation

    public void setStateHistory(StateHistory memory) { mHistory = memory; }

    public double getMaximumDelay() { return lag; }

    public double[] getDelays(double[] state) { return new double[] { lag }; }

    public void getInitialCondition(double time, double state[]){
//        state[0] = 10;
//        state[1] = 1;
//        state[2] = 10;
//        state[3] = 10;
//        state[4] = 10;
//        state[5] = 10;
//        state[6] = 10;
        System.arraycopy(initialConditions, 0, state, 0, initialConditions.length - 1);
        state[7] = time;
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

}
