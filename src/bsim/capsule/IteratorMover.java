package bsim.capsule;

import java.util.List;

/**
 */
public class IteratorMover implements Mover{

    int MAX_ITER = 2500;
    double FORCE_TOLERANCE = 0.5;

    double forceAccumulator = 1;

    List<BSimCapsuleBacterium> allBacteria;

    public IteratorMover(List<BSimCapsuleBacterium> _allBacteria) {
        this.allBacteria = _allBacteria;
    }

    public void move() {

        forceAccumulator = 2*FORCE_TOLERANCE;

        int bacteriaSize = allBacteria.size();

        int relax_iter;

        // TODO: FIX THIS ITERATOR OH MY GOD JUST USE THE DF/DT MEASURE CONSISTENTLY
        for(relax_iter = 0; forceAccumulator > FORCE_TOLERANCE && relax_iter < MAX_ITER; relax_iter++) {
            forceAccumulator = 0;

            for (BSimCapsuleBacterium b : allBacteria) {
                b.computeSelfForce();
                b.computeWallForce();
            }

            // Interaction
            int j = 1;
            for (BSimCapsuleBacterium b1 : allBacteria) {
                for (BSimCapsuleBacterium b2 : allBacteria.subList(j, bacteriaSize)) {
//                            System.out.println("Force between " + b1 + " and " + b2);
                    b1.computeNeighbourForce(b2);
                }
                j += 1;
            }

            for (BSimCapsuleBacterium b : allBacteria) {

                forceAccumulator += b.x1force.length();
                forceAccumulator += b.x2force.length();

                b.updatePosition();
            }
//                    System.out.format("Iteration %d. Total force: %f%n", relax_iter, forceAccumulator);
        }

        // Only need to apply flow one time (not iteratively)
        for (BSimCapsuleBacterium b : allBacteria) {
            b.computeFlowForce();
        }

//        System.out.format("Collision Iterations: %d %n", relax_iter);
    }
}