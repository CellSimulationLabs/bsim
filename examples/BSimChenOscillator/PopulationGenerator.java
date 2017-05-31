package BSimChenOscillator;

import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.capsule.BSimCapsuleBacterium;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Random;

/**
 */
public class PopulationGenerator {

    private Random bacRng;

    // Separate lists of bacteria in case we want to manipulate the species individually
    private ArrayList<ActivatorBacterium> bacteriaActivators;
    private ArrayList<RepressorBacterium> bacteriaRepressors;

    // Track all of the bacteria in the simulation, for use of common methods etc
    private ArrayList<BSimCapsuleBacterium> bacteriaAll;

    // Reference the parent simulation
    private BSim sim;

    // Simulation dimensions
    double simX;
    double simY;
    double simZ;

    // Bacs need to know about the chemical fields.
    private BSimChemicalField h_e_field;
    private BSimChemicalField i_e_field;

    /**
     * Initialise the generator for a given simulation
     */
    public PopulationGenerator(BSim _sim, ArrayList _bacteriaAll, ArrayList _bacteriaActivators, ArrayList _bacteriaRepressors,
                               BSimChemicalField _h_e_field, BSimChemicalField _i_e_field){
        this.sim = _sim;

        this.bacRng = new Random();

        this.bacteriaAll = _bacteriaAll;
        this.bacteriaActivators = _bacteriaActivators;
        this.bacteriaRepressors = _bacteriaRepressors;

        Vector3d bounds = this.sim.getBound();
        this.simX = bounds.x;
        this.simY = bounds.y;
        this.simZ = bounds.z;

        this.h_e_field = _h_e_field;
        this.i_e_field = _i_e_field;
    }

    /**
     * Generate an evenly spaced grid of points throughout the domain.
     * These can be used as a seeding for subsequent generation of bac agent positions.
     */
    public void generateGrid(){



    }

    /**
     * Set up the population as evenly mixed, loosely spread throughout the space.
     * Average distance between all neighbours, and the edges of the domain, should be
     * kept constant with respect to a grid (LRUD, not diagonal neighbours).
     */
    public void mixedAsBlock(int nActivatorStart, int nRepressorStart) {
        /*
        **********************************************************
        Generating two blocks of bacteria, split across the
        centre of the domain with uniform distribution.
        **********************************************************
         */
        for (int i = 0; i < nActivatorStart; i++) {

            double bL = 1. + 0.1 * (bacRng.nextDouble() - 0.5);
            double angle = bacRng.nextDouble() * 2 * Math.PI;

            Vector3d pos = new Vector3d(1 + 2 * Math.floor((4 * i) / simY), 1 + ((4 * i) % simY), simZ / 2.0);
            Vector3d p1 = new Vector3d(pos.x - 0.5 * bL * Math.sin(angle), pos.y - 0.5 * bL * Math.cos(angle), pos.z);
            Vector3d p2 = new Vector3d(0.5 * bL * Math.sin(angle) + pos.x, 0.5 * bL * Math.cos(angle) + pos.y, pos.z);

            double[] ICs = {10, 1, 10, 10, 10, 10, 10, 0};

            ActivatorBacterium bac = new ActivatorBacterium(sim, p1, p2,
                    h_e_field, i_e_field, ICs);

            bac.initialise(bL, p1, p2);

            bacteriaActivators.add(bac);
            bacteriaAll.add(bac);
        }

        for (int i = 0; i < nRepressorStart; i++) {

            double bL = 1. + 0.1 * (bacRng.nextDouble() - 0.5);
            double angle = bacRng.nextDouble() * 2 * Math.PI;

//            Vector3d pos = new Vector3d(simX*(populationRatio) + 2 + 2*Math.floor((2*i) / simY), 1 + ((2*i) % simY), simZ/2.0);
            Vector3d pos = new Vector3d(simX - 1 - 2 * Math.floor((4 * i) / simY), simY - 1 - ((4 * i) % simY), simZ / 2.0);
            Vector3d p1 = new Vector3d(pos.x - 0.5 * bL * Math.sin(angle), pos.y - 0.5 * bL * Math.cos(angle), pos.z);
            Vector3d p2 = new Vector3d(0.5 * bL * Math.sin(angle) + pos.x, 0.5 * bL * Math.cos(angle) + pos.y, pos.z);

            double[] ICs = {10, 1, 10, 10, 10, 10, 10, 0};

            RepressorBacterium bac = new RepressorBacterium(sim, p1, p2,
                    h_e_field, i_e_field, ICs);

            bac.initialise(bL, p1, p2);

            bacteriaRepressors.add(bac);
            bacteriaAll.add(bac);
        }
    }
    /**
     * Set up the population as loose blocks
     * (act fill from UL, rep from LR; the aim is to have them evenly distributed across the space,
     * in contrast to denseBlocks() where they are clustered closely together)
     */

    /**
     * Set up the distribution of bacteria as two dense blocks:
     * Act fill from UL
     * Rep fill from LR
     */
    public void denseBlocks(int nActivatorStart, int nRepressorStart) {
        /*
        **********************************************************
        Generating two blocks of bacteria, split across the
        centre of the domain with uniform distribution.
        **********************************************************
         */
        for (int i = 0; i < nActivatorStart; i++) {

            double bL = 1. + 0.1 * (bacRng.nextDouble() - 0.5);
            double angle = bacRng.nextDouble() * 2 * Math.PI;

            Vector3d pos = new Vector3d(1 + 2 * Math.floor((2 * i) / simY), 1 + ((2 * i) % simY), simZ / 2.0);
            Vector3d p1 = new Vector3d(pos.x - 0.5 * bL * Math.sin(angle), pos.y - 0.5 * bL * Math.cos(angle), pos.z);
            Vector3d p2 = new Vector3d(0.5 * bL * Math.sin(angle) + pos.x, 0.5 * bL * Math.cos(angle) + pos.y, pos.z);

            double[] ICs = {10, 1, 10, 10, 10, 10, 10, 0};

            ActivatorBacterium bac = new ActivatorBacterium(sim, p1, p2,
                    h_e_field, i_e_field, ICs);

            bac.initialise(bL, p1, p2);

            bacteriaActivators.add(bac);
            bacteriaAll.add(bac);
        }

        for (int i = 0; i < nRepressorStart; i++) {

            double bL = 1. + 0.1 * (bacRng.nextDouble() - 0.5);
            double angle = bacRng.nextDouble() * 2 * Math.PI;

//            Vector3d pos = new Vector3d(simX*(populationRatio) + 2 + 2*Math.floor((2*i) / simY), 1 + ((2*i) % simY), simZ/2.0);
            Vector3d pos = new Vector3d(simX - 1 - 2 * Math.floor((2 * i) / simY), simY - 1 - ((2 * i) % simY), simZ / 2.0);
            Vector3d p1 = new Vector3d(pos.x - 0.5 * bL * Math.sin(angle), pos.y - 0.5 * bL * Math.cos(angle), pos.z);
            Vector3d p2 = new Vector3d(0.5 * bL * Math.sin(angle) + pos.x, 0.5 * bL * Math.cos(angle) + pos.y, pos.z);

            double[] ICs = {10, 1, 10, 10, 10, 10, 10, 0};

            RepressorBacterium bac = new RepressorBacterium(sim, p1, p2,
                    h_e_field, i_e_field, ICs);

            bac.initialise(bL, p1, p2);

            bacteriaRepressors.add(bac);
            bacteriaAll.add(bac);
        }
    }
}
