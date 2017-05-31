package bsim.capsule;

import bsim.BSim;
import bsim.ode.BSimOdeSystem;

import javax.vecmath.Vector3d;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 */
public class BSimCapsuleBacterium {

    static final AtomicLong NEXT_ID = new AtomicLong(0);

    public final long id = NEXT_ID.getAndIncrement();

    protected Random rng = new Random();

    public Vector3d x1 = new Vector3d(0,0,0);
    public Vector3d x2 = new Vector3d(0,0,0);

    public Vector3d position = new Vector3d(0,0,0);

    public Vector3d x1force = new Vector3d(0,0,0);
    public Vector3d x2force = new Vector3d(0,0,0);

    public double radius = 1.0/2.0;

    public double L_initial = 2.25;
    public double L = L_initial;
    public double L_max = 2.5*L_initial;
    public double L_th = 2*L_initial;

//    public double k_growth = 0.002;
    public double k_growth = 0.2;

    // Springs
    public double k_int = 50.0;
    public double k_wall = 50.0;
    public double k_cell = 50.0;

    private double EPS = 1e-12;

    protected BSim sim;

    public void action(){
        Vector3d u = new Vector3d();
        u.sub(this.x2, this.x1);
        this.position.scaleAdd(0.5, u, this.x1);
    }

    public double brownianForceMagnitude;

    public void grow() {
        L = L + sim.getDt() * k_growth * L * (1 - (L / L_max));
    }

    public void initialise(double _L, Vector3d _x1, Vector3d _x2){
        this.L = _L;
        this.x1 = new Vector3d(_x1);
        this.x2 = new Vector3d(_x2);

        Vector3d u = new Vector3d();
        u.sub(this.x2, this.x1);
        this.position.scaleAdd(0.5, u, this.x1);
    }

    public BSimCapsuleBacterium divide() {
        /*
        this           this    child
        x1 ->u x2  ->  x1  x2  x1  x2
        o------o       o---o | o---o
        */

        // TODO: refactor u into the main class. (As a method? Could then be up-to-date each time it is required...)
        // Total length is actually L + 2*r

        Vector3d u = new Vector3d(); u.sub(this.x2, this.x1);

        // Uniform Distn; Change to Normal?
        double divPert = 0.1*L_max*(rng.nextDouble() - 0.5);

        double L_actual = u.length();

        double L1 = L_actual*0.5*(1 + divPert) - radius;
        double L2 = L_actual*0.5*(1 - divPert) - radius;

        /// TODO::: Check that these are computed correctly...!
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
        BSimCapsuleBacterium child = new BSimCapsuleBacterium(sim, x1_child, new Vector3d(this.x2));
        this.initialise(L1, this.x1, x2_new);
        ///
        child.L = L2;
        return child;
    }


    public void computeSelfForce() {
//        System.out.println("Internal Force");

        // Compute internal spring potential (growth)
        double internalPotential = 0;

//                from x1 to x2
        Vector3d seg = new Vector3d();
        seg.sub(x2, x1);

        double lengthDiff = seg.length() - L;

        seg.normalize();

        if(lengthDiff < 0) {
            internalPotential = 0.5 * k_int * Math.pow(lengthDiff, 2);
        }
        else {
            internalPotential = -0.5 * k_int * Math.pow(lengthDiff, 2);
        }

        this.x1force.scaleAdd(-internalPotential, seg, this.x1force);
        this.x2force.scaleAdd(internalPotential, seg, this.x2force);
    }

    public void wallBelow(double pointCoord, Vector3d theForce, Vector3d forceDir){
        if(radius - pointCoord > 0){
            theForce.scaleAdd(0.4*k_wall*Math.pow(radius - pointCoord, 2.5), forceDir, theForce);
        }
    }

    public void wallAbove(double pointCoord, Vector3d theForce, Vector3d forceDir, double simBound){
        if(radius + pointCoord - simBound > 0){
            theForce.scaleAdd(0.4*k_wall*Math.pow(radius + pointCoord - simBound, 2.5), forceDir, theForce);
        }
    }

    public boolean flowBelow(double pointCoord, Vector3d theForce, Vector3d forceDir, double simBound){
        if(radius - pointCoord > 0){
            theForce.scaleAdd(Math.abs(radius - pointCoord), forceDir, theForce);
            return true;
        }
        else return false;
    }

    public boolean flowAbove(double pointCoord, Vector3d theForce, Vector3d forceDir, double simBound){
        // Ignore the radius for now...
        if(radius + pointCoord - simBound > 0){
            theForce.scaleAdd(Math.abs(radius + pointCoord - simBound), forceDir, theForce);
            return true;
        }
        else return false;
    }

    public void computeFlowForce() {
        // TEST - apply velocity on BOTTOM
        flowAbove(x1.y, x1force, new Vector3d(0.5, 0, 0), sim.getBound().y);
        flowAbove(x2.y, x2force, new Vector3d(0.5, 0, 0), sim.getBound().y);

        // TEST - apply velocity on TOP
//        flowBelow(x1.y, x1force, new Vector3d(0.5, 0, 0), sim.getBound().y);
//        flowBelow(x2.y, x2force, new Vector3d(0.5, 0, 0), sim.getBound().y);
    }

    /*
    TODO - need proper wall contact location computation to implement flow past.
    TODO - for each boundary, apply a BoundaryCondition interface with BoundaryCondition.resolve() or something.
    - need a point of application so that force acts at both x1 and x2, or for a torque to act on the cell
     */
    public void computeWallForce(){
//        System.out.println("Wall Force");

        // TODO::: Ideally, there should also be a bounds check on the side NEXT to the one from which bacs can exit
        /**
         * i.e.,
         *
         * open, flow - - - - - - - ->
         *            |            |  should have a bounds check here @ top so that bacs being pushed by the 'flow'
         *  closed    |            |  are allowed to continue moving right, above the RHS wall, rather than being
         *            .            .  *stopped* by the RHS bound check!
         *
         */
        wallBelow(x1.x, x1force, new Vector3d(1,0,0));
        wallBelow(x1.y, x1force, new Vector3d(0,1,0)); // TOP //
        wallBelow(x1.z, x1force, new Vector3d(0,0,1));

        wallAbove(x1.x, x1force, new Vector3d(-1, 0, 0), sim.getBound().x);

//        wallAbove(x1.y, x1force, new Vector3d(0, -1, 0), sim.getBound().y); // BOTTOM //
        wallAbove(x1.z, x1force, new Vector3d(0, 0, -1), sim.getBound().z);

        wallBelow(x2.x, x2force, new Vector3d(1,0,0));
        wallBelow(x2.y, x2force, new Vector3d(0,1,0)); // TOP //
        wallBelow(x2.z, x2force, new Vector3d(0,0,1));

        wallAbove(x2.x, x2force, new Vector3d(-1,0,0), sim.getBound().x);

//        wallAbove(x2.y, x2force, new Vector3d(0, -1, 0), sim.getBound().y); // BOTTOM //
        wallAbove(x2.z, x2force, new Vector3d(0, 0, -1), sim.getBound().z);
    }

    public void computeNeighbourForce(BSimCapsuleBacterium neighbour_bac){
//        System.out.println("Neighbour Force");
        /*
        Vector returned (dP) is from the second bac, heading to the first

        Geometric Tools for Computer Graphics book,
        Also,
        http://geomalgorithms.com/a07-_distance.html#dist3D_Segment_to_Segment
        Thank you!
        */

        Vector3d dist = new Vector3d(); dist.sub(this.position, neighbour_bac.position);

        double rDist = (this.L + neighbour_bac.L)*0.5 + (this.radius + neighbour_bac.radius);

        if(dist.dot(dist) < rDist*rDist) {
            Vector3d u = new Vector3d();
            u.sub(this.x2, this.x1);
            Vector3d v = new Vector3d();
            v.sub(neighbour_bac.x2, neighbour_bac.x1);
            Vector3d w = new Vector3d();
            w.sub(this.x1, neighbour_bac.x1);
            double a = u.dot(u);         // always >= 0
            double b = u.dot(v);
            double c = v.dot(v);         // always >= 0
            double d = u.dot(w);
            double e = v.dot(w);
            double D = a * c - b * b;        // always >= 0
            double sc = D;
            double sN = D;
            double sD = D;       // sc = sN / sD, default sD = D >= 0
            double tc = D;
            double tN = D;
            double tD = D;       // tc = tN / tD, default tD = D >= 0

            // compute the line parameters of the two closest points
            if (D < EPS) { // the lines are almost parallel
                sN = 0.0;         // force using point P0 on segment S1
                sD = 1.0;         // to prevent possible division by 0.0 later
                tN = e;
                tD = c;
            } else {                 // get the closest points on the infinite lines
                sN = (b * e - c * d);
                tN = (a * e - b * d);
                if (sN < 0.0) {        // sc < 0 => the s=0 edge is visible
                    sN = 0.0;
                    tN = e;
                    tD = c;
                } else if (sN > sD) {  // sc > 1  => the s=1 edge is visible
                    sN = sD;
                    tN = e + b;
                    tD = c;
                }
            }

            if (tN < 0.0) {            // tc < 0 => the t=0 edge is visible
                tN = 0.0;
                // recompute sc for this edge
                if (-d < 0.0)
                    sN = 0.0;
                else if (-d > a)
                    sN = sD;
                else {
                    sN = -d;
                    sD = a;
                }
            } else if (tN > tD) {      // tc > 1  => the t=1 edge is visible
                tN = tD;
                // recompute sc for this edge
                if ((-d + b) < 0.0)
                    sN = 0;
                else if ((-d + b) > a)
                    sN = sD;
                else {
                    sN = (-d + b);
                    sD = a;
                }
            }

            // finally do the division to get sc and tc
            sc = (Math.abs(sN) < EPS ? 0.0 : sN / sD);
            tc = (Math.abs(tN) < EPS ? 0.0 : tN / tD);

            // get the difference of the two closest points
            Vector3d dP = new Vector3d(w);
            dP.scaleAdd(sc, u, dP);
            dP.scaleAdd(-tc, v, dP);

            double neighbourDist = dP.length();

            if (neighbourDist < 2 * radius) {
                // Try this; if necessary we can simplify to a linear force
                double repulsionStrength = 0.4 * k_cell * Math.pow(2 * radius - neighbourDist, 2.5);


/////////////////////////////////////////////////////////////////////////////////////
                    /*
                    OK, this section is not right.
                    We want the projection of dP (or 0.5*dP???)
                    onto the vector from intersection point to x1 (or x2)

                    ... Actually, not so sure. I think we could use the dP weighted by inverse distance
                    between the intersection point and x1/x2 as in Storck et al.

                    ... Or the single sphere approximation from Volfson.
                    Test + compare the alternatives if this doesn't work.
                    */
//                            Vector3d x1comp = new Vector3d(0,0,0);
//                            Vector3d x2comp = new Vector3d(0,0,0);

//                            x1comp.scale(0.5, dP);
//                            x2comp.scale(0.5, dP);
//
//                            x1comp.scaleAdd(-sc, u, x1comp);
//                            x2comp.scaleAdd(1-sc, u, x2comp);
/////////////////////////////////////////////////////////////////////////////////////

                // ***********
                // *********** NEW
                // ***********
//            Vector3d halfDp = new Vector3d(dP);
//            halfDp.scale(0.5);
//
//            Vector3d contactTox1 = new Vector3d();
//            contactTox1.scaleAdd(-sc, u, halfDp);
//
//            Vector3d contactTox2 = new Vector3d();
//            contactTox2.scaleAdd(1-sc, u, halfDp);
//
//            Vector3d contactToNeighbourx1 = new Vector3d(halfDp);
//            contactToNeighbourx1.negate();
//            contactToNeighbourx1.scaleAdd(-tc, v, contactToNeighbourx1);
//
//            Vector3d contactToNeighbourx2 = new Vector3d(halfDp);
//            contactToNeighbourx2.negate();
//            contactToNeighbourx2.scaleAdd(1-tc, v, contactToNeighbourx2);
//
////            double rsx1 = repulsionStrength/Math.pow(halfDp.length() + contactTox1.length(), 2);
////            double rsx2 = repulsionStrength/Math.pow(halfDp.length() + contactTox2.length(), 2);
////            double rsnx1 = repulsionStrength/Math.pow(halfDp.length() + contactToNeighbourx1.length(), 2);
////            double rsnx2 = repulsionStrength/Math.pow(halfDp.length() + contactToNeighbourx2.length(), 2);
//
//            double rsx1 = repulsionStrength*(u.length() + 2*radius - contactTox1.length())/(u.length() + 2*radius);
//            double rsx2 = repulsionStrength*(u.length() + 2*radius - contactTox2.length())/(u.length() + 2*radius);
//            double rsnx1 = repulsionStrength*(v.length() + 2*radius - contactToNeighbourx1.length())/(v.length() + 2*radius);
//            double rsnx2 = repulsionStrength*(v.length() + 2*radius - contactToNeighbourx2.length())/(v.length() + 2*radius);
//
////            System.out.format("%f, %f, %f, %f %n", rsx1, rsx2, rsnx1, rsnx2);
//            contactTox1.normalize();
//            contactTox2.normalize();
//            contactToNeighbourx1.normalize();
//            contactToNeighbourx2.normalize();
//
//            dP.normalize();
//
////            System.out.println(repulsionStrength/contactTox1.length() + "; " +
////                    repulsionStrength/contactTox2.length() + "; " +
////                    repulsionStrength/contactToNeighbourx1.length() + "; " +
////                    repulsionStrength/contactToNeighbourx2.length());
//
//            this.x1force.scaleAdd(rsx1, dP, this.x1force);
//            this.x2force.scaleAdd(rsx2, dP, this.x2force);
//
//            neighbour_bac.x1force.scaleAdd(-rsnx1, dP, neighbour_bac.x1force);
//            neighbour_bac.x2force.scaleAdd(-rsnx2, dP, neighbour_bac.x2force);
//
////            this.x1force.scaleAdd(rsx1, contactTox1, this.x1force);
////            this.x2force.scaleAdd(rsx2, contactTox2, this.x2force);
////
////            neighbour_bac.x1force.scaleAdd(rsnx1, contactToNeighbourx1, neighbour_bac.x1force);
////            neighbour_bac.x2force.scaleAdd(rsnx2, contactToNeighbourx2, neighbour_bac.x2force);
                // ***********
                // *********** /NEW
                // ***********

                // ***********
                // *********** OLD
                // ***********
                dP.normalize();

                this.x1force.scaleAdd((1.0 - sc) * repulsionStrength, dP, this.x1force);
                this.x2force.scaleAdd(sc * repulsionStrength, dP, this.x2force);

                neighbour_bac.x1force.scaleAdd(-(1.0 - tc) * repulsionStrength, dP, neighbour_bac.x1force);
                neighbour_bac.x2force.scaleAdd(-tc * repulsionStrength, dP, neighbour_bac.x2force);


//                this.x1force.scaleAdd((1.1 - sc) * repulsionStrength, dP, this.x1force);
//                this.x2force.scaleAdd((sc + 0.1) * repulsionStrength, dP, this.x2force);
//
//                neighbour_bac.x1force.scaleAdd(-(1.1 - tc) * repulsionStrength, dP, neighbour_bac.x1force);
//                neighbour_bac.x2force.scaleAdd(-(tc + 0.1) * repulsionStrength, dP, neighbour_bac.x2force);



                // ***********
                // *********** /OLD
                // ***********
            }
        }
    }

    public void applyForces(BSimCapsuleBacterium neighbour_bac){
        computeSelfForce();
        computeWallForce();
        computeNeighbourForce(neighbour_bac);
        System.out.println();
//        brownianForce(); // Will need to be changed; however include for now as a good symmetry breaker.
    }

    class ForceDisplacement implements BSimOdeSystem {
        private int numEq = 3;                // System of 7 equations

        private double[] force = new double[numEq];

        public void setForce(Vector3d appliedForce){
            force[0] = appliedForce.x;
            force[1] = appliedForce.y;
            force[2] = appliedForce.z;
        }

        // The equations
        public double[] derivativeSystem(double x, double[] y) {
            double[] dy = new double[numEq];

            dy[0] = force[0];
            dy[1] = force[1];
            dy[2] = force[2];

            return dy;
        }

        public int getNumEq() {
            return numEq;
        }

        // Initial conditions for the ODE system
        public double[] getICs() {
            double[] ics = {0,0,0};
            return ics;
        }
    }

//    private ForceDisplacement dfx1 = new ForceDisplacement();
//    private ForceDisplacement dfx2 = new ForceDisplacement();

    /**
     * Zero the forces applied on the points x1 and x2.
     */
    public void setAllForcesZero(){
        this.x1force.set(0,0,0);
        this.x2force.set(0,0,0);
    }

    /**
     * Euler position update, of points x1 and x2, based on applied force.
     */
    public void updatePosition(){
//        System.out.println("Updating Position");
//        // NEW: update using RK45
//        dfx1.setForce(x1force);
//        dfx2.setForce(x2force);
//
//        // Solve the ode system
//        double[] x1New = BSimOdeSolver.rungeKutta45(dfx1, sim.getTime(), new double[]{x1.x, x1.y, x1.z}, sim.getDt());
//        double[] x2New = BSimOdeSolver.rungeKutta45(dfx2, sim.getTime(), new double[]{x2.x, x2.y, x2.z}, sim.getDt());
//
//        // Apply the force to x1 and x2
//        x1 = new Vector3d(x1New[0], x1New[1], x1New[2]);
//        x2 = new Vector3d(x2New[0], x2New[1], x2New[2]);

        // OLD: Euler
        this.x1.scaleAdd(sim.getDt(), this.x1force, this.x1);
        this.x2.scaleAdd(sim.getDt(), this.x2force, this.x2);

        this.setAllForcesZero();
    }

    public BSimCapsuleBacterium(BSim _sim, Vector3d _x1, Vector3d _x2) {
        x1 = _x1;
        x2 = _x2;
        sim = _sim;
        setBrownianForceMagnitude();

        Vector3d u = new Vector3d();
        u.sub(this.x2, this.x1);
        this.position.scaleAdd(0.5, u, this.x1);
    }

    public void setBrownianForceMagnitude() {
        brownianForceMagnitude = Math.sqrt(2*stokesCoefficient()*BSim.BOLTZMANN*sim.getTemperature()/sim.getDt())*Math.pow(10,9);
    }

    public double stokesCoefficient() { return 6.0*Math.PI*radius*sim.getVisc(); } // micrometers*Pa sec

    /**
     * Applies a Brownian force to the particle. The applied force is a function of
     * radius, viscosity and temperature; if viscosity or temperature is changed externally,
     * you should call setBrownianForceMagnitude() again
     */
    public void brownianForce() {
        Vector3d f = new Vector3d(rng.nextGaussian(), rng.nextGaussian(), rng.nextGaussian());
        f.scale(brownianForceMagnitude);
        x1force.add(f);
        x2force.add(f);
    }
}
