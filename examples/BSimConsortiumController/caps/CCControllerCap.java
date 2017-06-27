package BSimConsortiumController.caps;

import bsim.capsule.BSimCapsuleBacterium;
import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.ode.BSimOdeSolver;
import bsim.ode.BSimOdeSystem;

import javax.vecmath.Vector3d;
import java.util.Random;

/**
 * Consortium feedback controller.
 *
 * Controller bacterium, capsule edition.
 *
 */
public class CCControllerCap extends BSimCapsuleBacterium {
    protected ControllerGRN grn;
    protected double[] y, yNew;

    protected BSimChemicalField q1e_field;
    protected BSimChemicalField q2e_field;

    protected double eta = BSimConsortiumController.CCParameters.defaultParameters.get("eta");

    protected boolean applyPerturbation = false;
    protected double perturbation = 0.1;

    /**
     *
     */
    public CCControllerCap(BSim sim, Vector3d pos_x1, Vector3d pos_x2, BSimChemicalField _q1e, BSimChemicalField _q2e, boolean _applyPerturbation, double _perturbation) {
        super(sim, pos_x1, pos_x2);

        grn = new ControllerGRN(_applyPerturbation, _perturbation);
        y = grn.getICs();

        this.q1e_field = _q1e;
        this.q2e_field = _q2e;

        this.applyPerturbation = _applyPerturbation;
        this.perturbation = _perturbation;
    }

    @Override
    public void action() {

        // Movement etc:
        super.action();

        // Variables for chemical field response:
        double q1e;	// External Q1
        double q2e;	// External Q2

        double q1Delta;		// Change in Q1
        double q2Delta;		// Change in Q2

        // external chemical level at position of the bacterium:
        q1e = q1e_field.getConc(position);
        q2e = q2e_field.getConc(position);

        // Get the external chemical field level for the GRN ode system later on:
        grn.setExternalFieldLevel(q1e, q2e);

        // Solve the ode system
        // IMPORTANT: re-scale the time units correctly (GRN equations are in minutes, BSim works in seconds)
        yNew = BSimOdeSolver.rungeKutta45(grn, sim.getTime()/60.0, y, sim.getDt()/60.0);
        y = yNew;

        // Adjust the external chemical field
        q1Delta = q1e - y[2];
        q2Delta = q2e - y[3];

        q1e_field.addQuantity(position, eta*(-q1Delta)/60.0);
        q2e_field.addQuantity(position, eta*(-q2Delta)/60.0);
    }

    /**
     * NOTE/TODO: could potentially refactor this into a superclass by the looks of it.
     * Might need a generic way of containing a grn and then dividing its contents...
     * @return
     */
    @Override
    public CCControllerCap divide() {

        Vector3d u = new Vector3d(); u.sub(this.x2, this.x1);

        // Uniform Distn; Change to Normal?
        double divPert = 0.1*L_max*(rng.nextDouble() - 0.5);

        double L_actual = u.length();

        double L1 = L_actual*0.5*(1 + divPert) - radius;
        double L2 = L_actual*0.5*(1 - divPert) - radius;

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

        /*
        This is dangerous.
        Ideally initialise all four co-ordinates, otherwise this operation is order-dependent
        (this.xi will be overwritten before being passed to child for ex.)
         */
        CCControllerCap child = new CCControllerCap(sim, x1_child, new Vector3d(this.x2), q1e_field, q2e_field, applyPerturbation, perturbation);
        this.initialise(L1, this.x1, x2_new);
        ///
        child.L = L2;

        double childProportion = child.L/L_actual;
        double thisProportion = 1 - childProportion;

        for(int i = 0; i < grn.getNumEq(); i ++){
            // order dependent!
            child.y[i] = this.y[i] * childProportion;
            this.y[i] = this.y[i] * thisProportion;
        }

        return child;
    }

    class ControllerGRN implements BSimOdeSystem {
        private Random r = new Random();
        int numEq = 4;

        double q1e = 0,
                q2e = 0;

        double chi_aq_r_0,
                chi_aq_r,
                K_r,
                n_r,
                chi_aq_a_0,
                chi_aq_a,
                K_q,
                n_q,
                gamma_AQ,
                chi_b_0,
                chi_b,
                K_b,
                n_b,
                gamma_B,
                K_Q_1,
                eta_1,
                eta_2,
                gamma_c_Q_1,
                gamma_c_Q_2;

        public ControllerGRN(boolean applyPerturbation, double perturbation){
            chi_aq_r_0 = BSimConsortiumController.CCParameters.defaultParameters.get("chi_aq_r_0");
            chi_aq_r = BSimConsortiumController.CCParameters.defaultParameters.get("chi_aq_r");
            K_r = BSimConsortiumController.CCParameters.defaultParameters.get("K_r");
            n_r = BSimConsortiumController.CCParameters.defaultParameters.get("n_r");
            chi_aq_a_0 = BSimConsortiumController.CCParameters.defaultParameters.get("chi_aq_a_0");
            chi_aq_a = BSimConsortiumController.CCParameters.defaultParameters.get("chi_aq_a");
            K_q = BSimConsortiumController.CCParameters.defaultParameters.get("K_q");
            n_q = BSimConsortiumController.CCParameters.defaultParameters.get("n_q");
            gamma_AQ = BSimConsortiumController.CCParameters.defaultParameters.get("gamma_AQ");
            chi_b_0 = BSimConsortiumController.CCParameters.defaultParameters.get("chi_b_0");
            chi_b = BSimConsortiumController.CCParameters.defaultParameters.get("chi_b");
            K_b = BSimConsortiumController.CCParameters.defaultParameters.get("K_b");
            n_b = BSimConsortiumController.CCParameters.defaultParameters.get("n_b");
            gamma_B = BSimConsortiumController.CCParameters.defaultParameters.get("gamma_B");
            K_Q_1 = BSimConsortiumController.CCParameters.defaultParameters.get("K_Q_1");

            eta_1 = BSimConsortiumController.CCParameters.defaultParameters.get("eta");
            eta_2 = BSimConsortiumController.CCParameters.defaultParameters.get("eta");

            gamma_c_Q_1 = BSimConsortiumController.CCParameters.defaultParameters.get("gamma_c_Q_1");
            gamma_c_Q_2 = BSimConsortiumController.CCParameters.defaultParameters.get("gamma_c_Q_2");

            if (applyPerturbation){
                chi_aq_r_0 = chi_aq_r_0 + chi_aq_r_0*perturbation*r.nextGaussian();
                chi_aq_r = chi_aq_r + chi_aq_r*perturbation*r.nextGaussian();
                K_r = K_r + K_r*perturbation*r.nextGaussian();
                n_r = n_r + n_r*perturbation*r.nextGaussian();
                chi_aq_a_0 = chi_aq_a_0 + chi_aq_a_0*perturbation*r.nextGaussian();
                chi_aq_a = chi_aq_a + chi_aq_a*perturbation*r.nextGaussian();
                K_q = K_q + K_q*perturbation*r.nextGaussian();
                n_q = n_q + n_q*perturbation*r.nextGaussian();
                gamma_AQ = gamma_AQ + gamma_AQ*perturbation*r.nextGaussian();
                chi_b_0 = chi_b_0 + chi_b_0*perturbation*r.nextGaussian();
                chi_b = chi_b + chi_b*perturbation*r.nextGaussian();
                K_b = K_b + K_b*perturbation*r.nextGaussian();
                n_b = n_b + n_b*perturbation*r.nextGaussian();
                gamma_B = gamma_B + gamma_B*perturbation*r.nextGaussian();
                K_Q_1 = K_Q_1 + K_Q_1*perturbation*r.nextGaussian();

                eta_1 = eta_1 + eta_1*perturbation*r.nextGaussian();
                eta_2 = eta_2 + eta_2*perturbation*r.nextGaussian();

                gamma_c_Q_1 = gamma_c_Q_1 + gamma_c_Q_1*perturbation*r.nextGaussian();
                gamma_c_Q_2 = gamma_c_Q_2 + gamma_c_Q_2*perturbation*r.nextGaussian();
            }
        }

        public double ref_in(double t){
            return ref_in_ramp(t);
        }

        public double ref_in_sin(double t) {
            if (t < 400){
                return 0.0;
            } else {//if ( t >= 400 ) {
                return 1 + Math.sin((2*Math.PI/400.0)*(t - 500.0));
            }
        }

        public double ref_in_ramp(double t) {
            double out = 0.0;

            if (t < 200) {
                return out;
            }

            if (t >= 200) {
                out += (3.0/400.0)*(t - 200);
            }

            if (t >= 600) {
                out -= (3.0 / 400.0) * (t - 600);
            }

            if (t >= 1000) {
                out -= (3.0/400.0)*(t - 1000);
            }

            if (t >= 1400) {
                out = 0.0;
            }

            return out;
        }

        public double ref_in_multi(double t) {
            if (t >= 400 && t < 800) {
                return 1.0;
            } else if (t >= 800 && t < 1200) {
                return 3.0;
            } else if (t >= 1200) {
                return 2.0;
            } else {
                return 0.0;
            }
        }

        public double ref_in_step(double t){
            if(t >= 250){
                return 3.0;
            } else {
                return 0.0;
            }
        }

        public double[] derivativeSystem(double t, double[] y) {
            double[] dy = new double[numEq];

            double AQ = y[0];
            double B  = y[1];
            double Q_1_c = y[2];
            double Q_2_c = y[3];

            dy[0] = (chi_aq_r_0 + chi_aq_r*(Math.pow(K_r, n_r) / (Math.pow(K_r, n_r ) + Math.pow(ref_in(t), n_r )))) * (chi_aq_a_0 + chi_aq_a*(Math.pow(Q_2_c, n_q) / (Math.pow(K_q, n_q ) + Math.pow(Q_2_c, n_q )))) - gamma_AQ * AQ;
            dy[1] = chi_b_0 + chi_b * (Math.pow(AQ, n_b) / ( Math.pow(K_b, n_b) + Math.pow(AQ, n_b) )) - gamma_B * B;

            dy[2] = K_Q_1 * B + eta_1*(q1e - Q_1_c) - gamma_c_Q_1 * Q_1_c;
            dy[3] = eta_2 * (q2e - Q_2_c) - gamma_c_Q_2 * Q_2_c;

            return dy;
        }

        public int getNumEq() {
            return numEq;
        }

        public double[] getICs() {
            double[] ics = {0.0, 0.0, 0.0, 0.0};
            return ics;
        }

        // Set up what the external chemical level is:
        public void setExternalFieldLevel(double _q1e, double _q2e){
            this.q1e = _q1e;
            this.q2e = _q2e;
        }

    }

}
