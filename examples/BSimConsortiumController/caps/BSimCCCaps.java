package BSimConsortiumController.caps;

import bsim.capsule.BSimCapsuleBacterium;
import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.BSimTicker;
import bsim.BSimUtils;
import bsim.capsule.IteratorMover;
import bsim.draw.BSimDrawer;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimLogger;
import bsim.export.BSimPngExporter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import processing.core.PConstants;
import processing.core.PGraphics3D;

import javax.vecmath.Vector3d;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;

/**
 * Consortium control system.
 */
public class BSimCCCaps {

    @Parameter(names = "-export", description = "Enable export mode.")
    private boolean export = false;

    @Parameter(names = "-dim", arity = 3, description = "The dimensions (x, y, z) of simulation environment (um).")
    public List<Double> simDimensions = new ArrayList<>(Arrays.asList(new Double[] {25., 20., 1.1}));

    @Parameter(names = "-pop", arity = 1, description = "Initial seed population (ratio, n_total).")
    public int initialPopulation = 12;

    @Parameter(names = "-ratio", arity = 1, description = "Ratio of initial populations (proportion of controllers).")
    public double populationRatio = 0.5; //0.418;

    @Parameter(names = "-pert_c", description = "Perturb controllers.")
    private boolean perturbControllers = false;

    @Parameter(names = "-pert_t", description = "Perturb targets.")
    private boolean perturbTargets = false;

    @Parameter(names = "-pert_amount", arity = 1, description = "Fraction of original parameter value by which to scale population distribution")
    public double perturbationFraction = 0.1;


    public static void main(String[] args) {
        BSimCCCaps cc = new BSimCCCaps();

        new JCommander(cc, args);

        cc.run();
    }

    long moveTimeSum = 0;
    int moveTimeSteps = 0;

    public void run() {
        /*********************************************************
         * Initialise parameters from command line
         */

        double simX = simDimensions.get(0);
        double simY = simDimensions.get(1);
        double simZ = simDimensions.get(2);

        int nControllerStart = (int)Math.round(populationRatio*initialPopulation);
        int nTargetStart = (int)Math.round((1 - populationRatio)*initialPopulation);

        System.out.println(nControllerStart + nTargetStart);

        long simulationStartTime = System.nanoTime();

        // create the simulation object
        BSim sim = new BSim();
        sim.setDt(0.01);				    // Simulation Timestep
        sim.setSimulationTime(96000);       // 21600 = 6 hours
        sim.setTimeFormat("0.00");		    // Time Format for display
        sim.setBound(simX, simY, simZ);		// Simulation Boundaries

        /*
        NOTE - solid=false sets a periodic boundary condition. This overrides leakiness!
         */
//        sim.setSolid(true, false, true);    // Periodic bounds y+ and y-
        sim.setLeaky(false, false, true, true, false, false);

        System.out.println(BSimConsortiumController.CCParameters.defaultParameters.get("K_r"));


        /*********************************************************
         * Set up the chemical fields
         */
        double external_diffusivity = 800.0/60.0;

        double external_decay = BSimConsortiumController.CCParameters.defaultParameters.get("gamma_e_Q_2")/60.0;

        BSimChemicalField q1e_field = new BSimChemicalField(sim, new int[] {(int) simX, (int)simY, 1}, external_diffusivity, external_decay);
        BSimChemicalField q2e_field = new BSimChemicalField(sim, new int[] {(int) simX, (int)simY, 1}, external_diffusivity, external_decay);

        // Leaky on the right hand side
        // TODO::: Check this.
        sim.setLeaky(true, false, false, false, false, false);

        /*********************************************************
         * Create the bacteria
         */

        // Separate lists of bacteria in case we want to manipulate the species individually
        final ArrayList<CCControllerCap> bacteriaControllers = new ArrayList();
        final ArrayList<CCTargetCap> bacteriaTargets = new ArrayList();

        // Track all of the bacteria in the simulation, for use of common methods etc
        final ArrayList<BSimCapsuleBacterium> bacteriaAll = new ArrayList();

        Random bacRng = new Random();

        /*
        **********************************************************
        Generating two blocks of bacteria, split across the
        centre of the domain with uniform distribution.
        **********************************************************
         */
        for (int i = 0; i < nControllerStart; i++) {

            double bL = 1. + 0.1*(bacRng.nextDouble() - 0.5);
            double angle = bacRng.nextDouble()*2*Math.PI;

            Vector3d pos = new Vector3d(1 + 2*Math.floor((2*i) / simY), 1 + ((2*i) % simY), simZ/2.0);

            CCControllerCap bac = new CCControllerCap(sim,
                    new Vector3d(pos.x - 0.5*bL*Math.sin(angle), pos.y - 0.5*bL*Math.cos(angle), pos.z),
                    new Vector3d(0.5*bL*Math.sin(angle) + pos.x, 0.5*bL*Math.cos(angle) + pos.y, pos.z),
                    q1e_field, q2e_field, perturbControllers, perturbationFraction);

            bac.L = bL;

            bacteriaControllers.add(bac);
            bacteriaAll.add(bac);
        }

        for (int i = 0; i < nTargetStart; i++) {

            double bL = 1. + 0.1*(bacRng.nextDouble() - 0.5);
            double angle = bacRng.nextDouble()*2*Math.PI;

//            Vector3d pos = new Vector3d(simX*(populationRatio) + 2 + 2*Math.floor((2*i) / simY), 1 + ((2*i) % simY), simZ/2.0);
            Vector3d pos = new Vector3d(simX - 1 - 2*Math.floor((2*i) / simY), simY - 1 - ((2*i) % simY), simZ/2.0);

            CCTargetCap bac = new CCTargetCap(sim,
                    new Vector3d(pos.x - 0.5*bL*Math.sin(angle), pos.y - 0.5*bL*Math.cos(angle), pos.z),
                    new Vector3d(0.5*bL*Math.sin(angle) + pos.x, 0.5*bL*Math.cos(angle) + pos.y, pos.z),
                    q1e_field, q2e_field, perturbTargets, perturbationFraction);

            bac.L = bL;

            bacteriaTargets.add(bac);
            bacteriaAll.add(bac);
        }

        final ArrayList<CCControllerCap> con_born = new ArrayList();
        final ArrayList<CCControllerCap> con_dead = new ArrayList();

        final ArrayList<CCTargetCap> tar_born = new ArrayList();
        final ArrayList<CCTargetCap> tar_dead = new ArrayList();

        final IteratorMover mover = new IteratorMover(bacteriaAll);

        int LOG_INTERVAL = 100;

        BSimTicker ticker = new BSimTicker() {
            @Override
            public void tick() {
                // ********************************************** Action
                long startTimeAction = System.nanoTime();

                for(BSimCapsuleBacterium b : bacteriaAll) {
                    b.action();
                }

                long endTimeAction = System.nanoTime();
                if((sim.getTimestep() % LOG_INTERVAL) == 0) {
                    System.out.println("Action update for " + bacteriaAll.size() + " bacteria took " + (endTimeAction - startTimeAction)/1e6 + " ms.");
                }

                // ********************************************** Chemical fields
                startTimeAction = System.nanoTime();

                q1e_field.diffuse();
                q2e_field.diffuse();

                q1e_field.decay();
                q2e_field.decay();

                // ********

                endTimeAction = System.nanoTime();
                if((sim.getTimestep() % LOG_INTERVAL) == 0) {
                    System.out.println("Chemical field update took " + (endTimeAction - startTimeAction)/1e6 + " ms.");
                }


                // ********************************************** Growth and division
                startTimeAction = System.nanoTime();

                for(CCControllerCap b : bacteriaControllers) {
                    b.grow();

                    // Divide if grown past threshold
                    if(b.L > b.L_th){
                        con_born.add(b.divide());
                    }
                }
                bacteriaControllers.addAll(con_born);
                bacteriaAll.addAll(con_born);
                con_born.clear();

                for(CCTargetCap b : bacteriaTargets) {
                    b.grow();

                    // Divide if grown past threshold
                    if(b.L > b.L_th){
                        tar_born.add(b.divide());
                    }
                }
                bacteriaTargets.addAll(tar_born);
                bacteriaAll.addAll(tar_born);
                tar_born.clear();

                endTimeAction = System.nanoTime();
                if((sim.getTimestep() % LOG_INTERVAL) == 0) {
                    System.out.println("Growth and division took " + (endTimeAction - startTimeAction)/1e6 + " ms.");
                }

                // ********************************************** Neighbour interactions
                startTimeAction = System.nanoTime();

                mover.move();

                endTimeAction = System.nanoTime();
                if((sim.getTimestep() % LOG_INTERVAL) == 0) {
                    System.out.println("Wall and neighbour interactions took " + (endTimeAction - startTimeAction)/1e6 + " ms.");
                }

                // For the timing logger
                moveTimeSum += (endTimeAction - startTimeAction);
                moveTimeSteps += 1;

//                System.out.format("Time: %s; Iterations: %d %n", sim.getFormattedTime(), relax_iter);

                // ********************************************** Boundaries/removal
                startTimeAction = System.nanoTime();
                // Removal
                for(CCControllerCap b : bacteriaControllers){
                    // Kick out if past the boundary
                    if((b.x1.y < 0) && (b.x2.y < 0)){
                        con_dead.add(b);
                    }
                    if((b.x1.y > sim.getBound().y) && (b.x2.y > sim.getBound().y)){
                        con_dead.add(b);
                    }
                }
                bacteriaControllers.removeAll(con_dead);
                bacteriaAll.removeAll(con_dead);
                con_dead.clear();

                // Removal
                for(CCTargetCap b : bacteriaTargets){
                    // Kick out if past the boundary
                    if((b.x1.y < 0) && (b.x2.y < 0)){
                        tar_dead.add(b);
                    }
                    if((b.x1.y > sim.getBound().y) && (b.x2.y > sim.getBound().y)){
                        tar_dead.add(b);
                    }
                }
                bacteriaTargets.removeAll(tar_dead);
                bacteriaAll.removeAll(tar_dead);
                tar_dead.clear();

                endTimeAction = System.nanoTime();
                if((sim.getTimestep() % LOG_INTERVAL) == 0) {
                    System.out.println("Death and removal took " + (endTimeAction - startTimeAction)/1e6 + " ms.");
                }

            }
        };
        sim.setTicker(ticker);

        /*********************************************************
         * Set up the drawer
         */
        BSimDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
            /**
             * Draw the default cuboid boundary of the simulation as a partially transparent box
             * with a wireframe outline surrounding it.
             */
            @Override
            public void boundaries() {
                p3d.noFill();
                p3d.stroke(128, 128, 255);
                p3d.pushMatrix();
                p3d.translate((float)boundCentre.x,(float)boundCentre.y,(float)boundCentre.z);
                p3d.box((float)bound.x, (float)bound.y, (float)bound.z);
                p3d.popMatrix();
                p3d.noStroke();
            }

            @Override
            public void draw(Graphics2D g) {
                p3d.beginDraw();

                if(!cameraIsInitialised){
                    p3d.camera((float)bound.x*0.5f, (float)bound.y*0.5f,
                            // Set the Z offset to the largest of X/Y dimensions for a reasonable zoom-out distance:
                            simX > simY ? (float)simX : (float)simY,
                            (float)bound.x*0.5f, (float)bound.y*0.5f, 0,
                            0,1,0);
                    cameraIsInitialised = true;
                }

                p3d.textFont(font);
                p3d.textMode(PConstants.SCREEN);

                p3d.sphereDetail(10);
                p3d.noStroke();
                p3d.background(255, 255,255);

                scene(p3d);
                boundaries();
                time();

                p3d.endDraw();
                g.drawImage(p3d.image, 0,0, null);
            }

            /**
             * Draw the formatted simulation time to screen.
             */
            @Override
            public void time() {
                p3d.fill(0);
                p3d.text(sim.getFormattedTimeHours(), 50, 50);
            }

            @Override
            public void scene(PGraphics3D p3d) {
                p3d.ambientLight(128, 128, 128);
                p3d.directionalLight(128, 128, 128, 1, 1, -1);

                for(CCTargetCap b_tar : bacteriaTargets){
                    // Oscillate from pale yellow to yellow.
                    int R = 50 + (int)(205*b_tar.y[1]/1.25),
                            G = 25,
                            B = 25;
                    // Clamp these to [0, 255] to avoid errors
                    if(R < 0) R = 0; if(R > 255) R = 255;
                    if(G < 0) G = 0; if(G > 255) G = 255;
                    if(B < 0) B = 0; if(B > 255) B = 255;

                    draw(b_tar, new Color(R, G, B));
                }

                for(CCControllerCap b_con : bacteriaControllers){
                    // Oscillate from pale yellow to yellow.
                    int R = 25,
                            G = 25,
                            B = 50 + (int)(205*b_con.y[1]/0.5);
                    // Clamp these to [0, 255] to avoid errors
                    if(R < 0) R = 0; if(R > 255) R = 255;
                    if(G < 0) G = 0; if(G > 255) G = 255;
                    if(B < 0) B = 0; if(B > 255) B = 255;

                    draw(b_con, new Color(R, G, B));
                }
            }
        };
        sim.setDrawer(drawer);


        /*********************************************************
         * Finally, run the simulation.
         *
         * If export is enabled, then log everything.
         * Otherwise, run in preview mode.
         */
        if(export) {
            String simParameters = "" + BSimUtils.timeStamp() + "__ip_" + initialPopulation + "__pr_" + populationRatio;

            simParameters += "__dim_" + simDimensions.get(0) + "_" + simDimensions.get(1) + "_" + simDimensions.get(2);

            if(perturbTargets || perturbControllers) {
                simParameters += "__pert";

                if (perturbControllers) {
                    simParameters += "_c";
                }

                if (perturbTargets) {
                    simParameters += "_t";
                }

                simParameters += "_" + perturbationFraction;
            }

//            String filePath = BSimUtils.generateDirectoryPath("/Users/antmatyjajo/Desktop/tmp-results/" + simParameters + "/");
            String filePath = BSimUtils.generateDirectoryPath("/home/am6465/tmp-results/" + simParameters + "/");

            BSimLogger grnLogger_cons = new BSimLogger(sim, filePath + "grn-con.csv") {
                @Override
                public void before() {
                    super.before();

                    String buffer = "time";

                    buffer += ",reference";

                    if(bacteriaControllers.size() > 0) {
                        for (int j = 0; j < bacteriaControllers.get(0).grn.numEq; j++) {
                            buffer += ",con_grn(" + j + ")";
                        }
                    }

                    write(buffer);
                }

                @Override
                public void during() {
                    String buffer = "" + sim.getFormattedTime();

                    if(bacteriaControllers.size() > 0) {
                        buffer += "," + bacteriaControllers.get(0).grn.ref_in(sim.getTime() / 60.0);
                    } else {
                        buffer += ",NaN";
                    }

                    for(CCControllerCap b : bacteriaControllers) {
                        for(int j = 0; j < b.grn.numEq; j++) {
                            buffer += "," + b.y[j];
                        }
                    }

                    write(buffer);
                }
            };
            grnLogger_cons.setDt(30);
            sim.addExporter(grnLogger_cons);

            BSimLogger grnLogger_tars = new BSimLogger(sim, filePath + "grn-tar.csv") {
                @Override
                public void before() {
                    super.before();

                    String buffer = "time";

                    if(bacteriaTargets.size() > 0) {
                        for (int j = 0; j < bacteriaTargets.get(0).grn.numEq; j++) {
                            buffer += ",tar_grn(" + j + ")";
                        }
                    }

                    write(buffer);
                }

                @Override
                public void during() {
                    String buffer = "" + sim.getFormattedTime();

                    for(CCTargetCap b : bacteriaTargets) {
                        for(int j = 0; j < b.grn.numEq; j++) {
                            buffer += "," + b.y[j];
                        }
                    }

                    write(buffer);
                }
            };
            grnLogger_tars.setDt(30);
            sim.addExporter(grnLogger_tars);

            BSimLogger popLogger = new BSimLogger(sim, filePath + "population.csv") {
                @Override
                public void before() {
                    super.before();
                    write("time(seconds),controllers,targets");
                }

                @Override
                public void during() {
                    String buffer = sim.getFormattedTime();

                    buffer += "," + bacteriaControllers.size() + "," + bacteriaTargets.size();

                    write(buffer);
                }
            };
            popLogger.setDt(30);			// Set export time step
            sim.addExporter(popLogger);

            BSimLogger timeLogger = new BSimLogger(sim, filePath + "timing_for_dims.csv") {
                @Override
                public void before() {
                    super.before();
                    write("dimensions::" + sim.getBound() );
                    write("time(seconds), total_pop, action_time(ns), n_steps");
                }

                @Override
                public void during() {
                    String buffer = sim.getFormattedTime();

                    buffer += "," + (bacteriaControllers.size() + bacteriaTargets.size()) + "," + moveTimeSum + "," + moveTimeSteps;

                    write(buffer);

                    moveTimeSteps = 0;
                    moveTimeSum = 0;
                }
            };
            timeLogger.setDt(30);			// Set export time step
            sim.addExporter(timeLogger);


            BSimLogger posLogger = new BSimLogger(sim, filePath + "position.csv") {
                DecimalFormat formatter = new DecimalFormat("###.##", DecimalFormatSymbols.getInstance( Locale.ENGLISH ));

                @Override
                public void before() {
                    super.before();
                    write("per time, per con; per tar, id, p1x, p1y, p1z, p2x, p2y, p2z");
                }

                @Override
                public void during() {
                    String buffer = new String();

                    buffer += sim.getFormattedTime() + "\n";
                    write(buffer);

                    write("blues");

                    buffer = "";
                    for(BSimCapsuleBacterium b : bacteriaControllers) {
                        buffer += b.id + "," + formatter.format(b.x1.x)
                                + "," + formatter.format(b.x1.y)
                                + "," + formatter.format(b.x1.z)
                                + "," + formatter.format(b.x2.x)
                                + "," + formatter.format(b.x2.y)
                                + "," + formatter.format(b.x2.z)
                                + "\n";
                    }

                    write(buffer);

                    write("reds");

                    buffer = "";
                    for(BSimCapsuleBacterium b : bacteriaTargets) {
                        buffer += b.id + "," + formatter.format(b.x1.x)
                                + "," + formatter.format(b.x1.y)
                                + "," + formatter.format(b.x1.z)
                                + "," + formatter.format(b.x2.x)
                                + "," + formatter.format(b.x2.y)
                                + "," + formatter.format(b.x2.z)
                                + "\n";
                    }

                    write(buffer);

                }
            };
            posLogger.setDt(30);			// Set export time step
            sim.addExporter(posLogger);

            BSimPngExporter imageExporter = new BSimPngExporter(sim, drawer, filePath);
            imageExporter.setDt(30);
            sim.addExporter(imageExporter);

            sim.export();
        } else {
            /**
             * Preview the simulation
             */
            sim.preview();
        }

        long simulationEndTime = System.nanoTime();
        System.out.println("TOTAL TIME ELAPSED: " + (simulationEndTime - simulationStartTime)/1e6 + " ms.");
    }


}
