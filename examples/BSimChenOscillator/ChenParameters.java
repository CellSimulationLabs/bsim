package BSimChenOscillator;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class ChenParameters {

    /**
     * TODO. This (possibly?) should be replaced with a class/singleton.
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

    public static Map<String, Double> p;

    static {
        p = new HashMap<String, Double>();

        p.put("D_H", 3.0);
        p.put("D_I", 2.1);

        p.put("phi_H", 16.0);
        p.put("phi_I", 2.0);
    }

}
