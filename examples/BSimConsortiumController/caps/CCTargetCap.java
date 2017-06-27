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
 * Target bacterium, capsule edition.
 *
 */
public class CCTargetCap extends BSimCapsuleBacterium {
    protected TargetGRN grn;
    protected double[] y, yNew;

    protected BSimChemicalField q1e_field;
    protected BSimChemicalField q2e_field;

    protected double eta = BSimConsortiumController.CCParameters.defaultParameters.get("eta");;

    private final double perturbation;
    private final boolean applyPerturbation;

    /**
     *
     */
    public CCTargetCap(BSim sim, Vector3d pos_x1, Vector3d pos_x2, BSimChemicalField _q1e, BSimChemicalField _q2e, boolean _applyPerturbation, double _perturbation) {
        super(sim, pos_x1, pos_x2);

        this.applyPerturbation = _applyPerturbation;
        this.perturbation = _perturbation;

        grn = new TargetGRN(_applyPerturbation, _perturbation);
        y = grn.getICs();

        this.q1e_field = _q1e;
        this.q2e_field = _q2e;
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

    @Override
    public CCTargetCap divide() {

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
        CCTargetCap child = new CCTargetCap(sim, x1_child, new Vector3d(this.x2), q1e_field, q2e_field, applyPerturbation, perturbation);
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

    class TargetGRN implements BSimOdeSystem {
        private Random r = new Random();

        int numEq = 4;

        double q1e = 0,
                q2e = 0;

        double chi_c_0,
                chi_c,
                K_c,
                n_c,
                gamma_C,
                chi_d_0,
                chi_d,
                K_d,
                n_d,
                gamma_D,
                eta_1,
                eta_2,
                gamma_t_Q_1,
                gamma_t_Q_2,
                K_Q_2;

        public TargetGRN(boolean applyPerturbation, double perturbation) {
            chi_c_0 = BSimConsortiumController.CCParameters.defaultParameters.get("chi_c_0");
            chi_c = BSimConsortiumController.CCParameters.defaultParameters.get("chi_c");
            K_c = BSimConsortiumController.CCParameters.defaultParameters.get("K_c");
            n_c = BSimConsortiumController.CCParameters.defaultParameters.get("n_c");
            gamma_C = BSimConsortiumController.CCParameters.defaultParameters.get("gamma_C");
            chi_d_0 = BSimConsortiumController.CCParameters.defaultParameters.get("chi_d_0");
            chi_d = BSimConsortiumController.CCParameters.defaultParameters.get("chi_d");
            K_d = BSimConsortiumController.CCParameters.defaultParameters.get("K_d");
            n_d = BSimConsortiumController.CCParameters.defaultParameters.get("n_d");
            gamma_D = BSimConsortiumController.CCParameters.defaultParameters.get("gamma_D");

            eta_1 = BSimConsortiumController.CCParameters.defaultParameters.get("eta");
            eta_2 = BSimConsortiumController.CCParameters.defaultParameters.get("eta");

            K_Q_2 = BSimConsortiumController.CCParameters.defaultParameters.get("K_Q_2");
            gamma_t_Q_1 = BSimConsortiumController.CCParameters.defaultParameters.get("gamma_t_Q_1");
            gamma_t_Q_2 = BSimConsortiumController.CCParameters.defaultParameters.get("gamma_t_Q_2");

            if(applyPerturbation) {
                chi_c_0 = chi_c_0 + chi_c_0*perturbation*r.nextGaussian();
                chi_c = chi_c + chi_c*perturbation*r.nextGaussian();
                K_c = K_c + K_c*perturbation*r.nextGaussian();
                n_c = n_c + n_c*perturbation*r.nextGaussian();
                gamma_C = gamma_C + gamma_C*perturbation*r.nextGaussian();
                chi_d_0 = chi_d_0 + chi_d_0*perturbation*r.nextGaussian();
                chi_d = chi_d + chi_d*perturbation*r.nextGaussian();
                K_d = K_d + K_d*perturbation*r.nextGaussian();
                n_d = n_d + n_d*perturbation*r.nextGaussian();
                gamma_D = gamma_D + gamma_D*perturbation*r.nextGaussian();

                eta_1 = eta_1 + eta_1*perturbation*r.nextGaussian();
                eta_2 = eta_2 + eta_2*perturbation*r.nextGaussian();

                K_Q_2 = K_Q_2 + K_Q_2*perturbation*r.nextGaussian();
                gamma_t_Q_1 = gamma_t_Q_1 + gamma_t_Q_1*perturbation*r.nextGaussian();
                gamma_t_Q_2 = gamma_t_Q_2 + gamma_t_Q_2*perturbation*r.nextGaussian();
            }
        }

        public double[] derivativeSystem(double t, double[] y) {
            double[] dy = new double[numEq];

            double C = y[0];
            double D = y[1];
            double Q_1_t = y[2];
            double Q_2_t = y[3];

            dy[0] = chi_c_0 + chi_c * (Math.pow(Q_1_t, n_c) / ( Math.pow(K_c, n_c) + Math.pow(Q_1_t, n_c) )) - gamma_C * C;
            dy[1] = chi_d_0 + chi_d * (Math.pow(K_d, n_d) / ( Math.pow(K_d, n_d) + Math.pow(C, n_d) )) - gamma_D * D;

            dy[2] = eta_1*(q1e - Q_1_t) - gamma_t_Q_1 * Q_1_t;
            dy[3] = K_Q_2 * D + eta_2*(q2e - Q_2_t) - gamma_t_Q_2 * Q_2_t;

            return dy;
        }

        public int getNumEq() {
            return numEq;
        }

        public double[] getICs() {
            double ics[] = {0.0, 0.0, 0.0, 0.0};
            return ics;
        }

        // Set up what the external chemical level is:
        public void setExternalFieldLevel(double _q1e, double _q2e){
            this.q1e = _q1e;
            this.q2e = _q2e;
        }

    }

}
