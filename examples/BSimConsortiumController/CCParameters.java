package BSimConsortiumController;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class CCParameters {
    /**
     * TODO. This probably (possibly?) should be replaced with a class.
     * Or an interface.
     *
     * ''' It is a bad idea to have 'magic strings' in the code '''
     * Defining parameters as e.g., fields in a class, will help with compile-time checking and validation. However,
     * how will it affect our ability to loop over parameters e.g., when perturbing them by an amount etc.
     *
     * Or enum:
     * stackoverflow.com/questions/2503489/design-pattern-for-one-time-loaded-configuration-properties
     *
     */

    public static Map<String, Double> defaultParameters;

    static {
        defaultParameters = new HashMap<String, Double>();
        defaultParameters.put("chi_aq_r_0", 1e-1);
        defaultParameters.put("chi_aq_r", 2.0);

        defaultParameters.put("chi_aq_a_0", 1e-1);
        defaultParameters.put("chi_aq_a", 2.0);

        defaultParameters.put("K_r", 1.0);
        defaultParameters.put("n_r", 2.0);
        defaultParameters.put("K_q", 0.1);
        defaultParameters.put("n_q", 2.0);
        defaultParameters.put("gamma_AQ", 1.4);

        defaultParameters.put("chi_b_0", 1e-1);
        defaultParameters.put("chi_b", 2.0);
        defaultParameters.put("K_b", 0.5);
        defaultParameters.put("n_b", 2.0);
        defaultParameters.put("gamma_B", 1.4);

        defaultParameters.put("chi_c_0", 1e-1);
        defaultParameters.put("chi_c", 2.0);
        defaultParameters.put("K_c", 0.015);
        defaultParameters.put("n_c", 2.0);
        defaultParameters.put("gamma_C", 1.4);

        defaultParameters.put("chi_d_0", 1e-1);
        defaultParameters.put("chi_d", 2.0);
        defaultParameters.put("K_d", 0.5);
        defaultParameters.put("n_d", 2.0);
        defaultParameters.put("gamma_D", 1.4);

        defaultParameters.put("eta", 2.0);

        defaultParameters.put("K_Q_1", 0.05);
        defaultParameters.put("eta_1", 2.0);
//        defaultParameters.put("gamma_c_Q_1", 0.0018);
//        defaultParameters.put("gamma_t_Q_1", 0.0018);
        defaultParameters.put("gamma_c_Q_1", 0.4);
        defaultParameters.put("gamma_t_Q_1", 0.4);
        defaultParameters.put("gamma_e_Q_1", 0.2);

        defaultParameters.put("K_Q_2", 0.05);
        defaultParameters.put("eta_2", 2.0);
//        defaultParameters.put("gamma_c_Q_2", 0.0018);
//        defaultParameters.put("gamma_t_Q_2", 0.0018);
        defaultParameters.put("gamma_c_Q_2", 0.4);
        defaultParameters.put("gamma_t_Q_2", 0.4);
        defaultParameters.put("gamma_e_Q_2", 0.2);
    }
}
