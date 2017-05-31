package bsim.capsule;

import bsim.BSim;
import bsim.BSimTicker;
import bsim.BSimUtils;
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
 */
public class MoverTest {

//    @Parameter
//    private List<String> parameters = new ArrayList<>();
//
//    @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity")
//    private Integer verbose = 1;
//
//    @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
//    private String groups;

    @Parameter(names = "-export", description = "Enable export mode.")
    private boolean export = false;

    @Parameter(names = "-dim", arity = 3, description = "The dimensions (x, y, z) of simulation environment (um).")
    public List<Double> simDimensions = new ArrayList<>(Arrays.asList(new Double[] {50., 42., 1.}));

    @Parameter(names = "-pop", arity = 1, description = "Initial seed population.")
    public int initialPopulation = 100;

    // TODO: parameterise the (x?) spatial proportion which a given population occupies.

    public static void main(String[] args) {
        MoverTest mt = new MoverTest();

        new JCommander(mt, args);

        mt.run();
    }

    public void run() {

        double simX = simDimensions.get(0);
        double simY = simDimensions.get(1);
        double simZ = simDimensions.get(2);

        int nBacStart = initialPopulation;

        long simulationStartTime = System.nanoTime();

        // create the simulation object
        BSim sim = new BSim();
        sim.setDt(0.1);				// Simulation Timestep
        sim.setSimulationTime(21600); // 21600 = 6 hours
        sim.setTimeFormat("0.00");		// Time Format for display
        sim.setBound(simX, simY, simZ);		// Simulation Boundaries

        // TODO: Set up the boundary conditions for the bacs to be removed from the simulation here.
        sim.setSolid(true, false, true);

//        System.out.println(sim.getBound());
//        System.exit(0);


        /*********************************************************
         * Create the bacteria
         */
        Random bacRng = new Random();

        final List<BSimCapsuleBacterium> allBacteria = Collections.synchronizedList(new ArrayList());

        generator:
        while(allBacteria.size() < nBacStart) {
            double bL = 1. + 1.*0.2*(bacRng.nextDouble() - 0.5);

            Vector3d pos = new Vector3d(3 + bacRng.nextDouble()*(sim.getBound().x - 6), 3 + bacRng.nextDouble()*(sim.getBound().y - 6), bacRng.nextDouble()*0.1*(simZ - 0.1)/2.0);

            // Test intersection
            Vector3d distance = new Vector3d(0,0,0);

            for(BSimCapsuleBacterium otherBac : allBacteria){
                distance.sub(otherBac.position, pos);
                if(distance.lengthSquared() < 6.0){
                    continue generator;
                }
            }

            double angle = bacRng.nextDouble()*2*Math.PI;
            BSimCapsuleBacterium bc = new BSimCapsuleBacterium(sim,
                    pos, new Vector3d(bL*Math.sin(angle) + pos.x, bL*Math.cos(angle) + pos.y, pos.z));
            bc.L = bL;
            allBacteria.add(bc);
        }

//        for (int i = 0; i < nBacStart; i++) {
//            double bL = 1. + 0.1 * (bacRng.nextDouble() - 0.5);
//            double angle = bacRng.nextDouble() * 2 * Math.PI;
//
//            Vector3d pos = new Vector3d(1 + 2 * Math.floor((2 * i) / simY), 1 + ((2 * i) % simY), simZ / 2.0);
//            Vector3d p1 = new Vector3d(pos.x - 0.5 * bL * Math.sin(angle), pos.y - 0.5 * bL * Math.cos(angle), pos.z);
//            Vector3d p2 = new Vector3d(0.5 * bL * Math.sin(angle) + pos.x, 0.5 * bL * Math.cos(angle) + pos.y, pos.z);
//
//            BSimCapsuleBacterium bac = new BSimCapsuleBacterium(sim, p1, p2);
//            bac.initialise(bL, p1, p2);
//
//            allBacteria.add(bac);
//        }


        /*********************************************************
         * Set up the ticker
         */

        final List<BSimCapsuleBacterium> bacs_born = Collections.synchronizedList(new ArrayList());
        final List<BSimCapsuleBacterium> bacs_dead = Collections.synchronizedList(new ArrayList());

        final RelaxationMoverGrid mover = new RelaxationMoverGrid(allBacteria, sim);
//        final RelaxationMover mover = new RelaxationMover(allBacteria);


        int LOG_INTERVAL = 100;

        sim.setTicker(new BSimTicker() {
            @Override
            public void tick() {

                // ********************************************** Action
                long startTimeAction = System.nanoTime();

                for(BSimCapsuleBacterium b : allBacteria) {
                    b.action();
                }

                long endTimeAction = System.nanoTime();
                if((sim.getTimestep() % LOG_INTERVAL) == 0) {
                    System.out.println("Action update for " + allBacteria.size() + " bacteria took " + (endTimeAction - startTimeAction)/1e6 + " ms.");
                }

                // ********************************************** Growth and division
                startTimeAction = System.nanoTime();

                for(BSimCapsuleBacterium b : allBacteria) {
                    b.grow();

                    // Divide if grown past threshold
                    if(b.L > b.L_th){
                        bacs_born.add(b.divide());
                    }
                }
                allBacteria.addAll(bacs_born);
                bacs_born.clear();

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

                // ********************************************** Boundaries/removal
                startTimeAction = System.nanoTime();
                // Removal
                for(BSimCapsuleBacterium b : allBacteria){
                    // Kick out if past the boundary
                    if((b.x1.y < 0) && (b.x2.y < 0)){
                        bacs_dead.add(b);
                    }
                    if((b.x1.y > sim.getBound().y) && (b.x2.y > sim.getBound().y)){
                        bacs_dead.add(b);
                    }
                }
                allBacteria.removeAll(bacs_dead);
                bacs_dead.clear();

                endTimeAction = System.nanoTime();
                if((sim.getTimestep() % LOG_INTERVAL) == 0) {
                    System.out.println("Death and removal took " + (endTimeAction - startTimeAction)/1e6 + " ms.");
                }
            }
        });


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
                p3d.directionalLight(128, 128, 128, 0, 0, -1);

                for (BSimCapsuleBacterium b : allBacteria) {
                    draw(b, new Color(55, 126, 184));
                }

//                p3d.noFill();
//                p3d.stroke(255, 128, 128);
//                double boxHalfWidth = mover.gridCellWidth/2.0;
//                double boxCenterX, boxCenterY;
//                for(int gi = 0; gi < mover.nX; gi++) {
//                    for(int gj = 0; gj < mover.nY; gj++){
//                        boxCenterX = gi*mover.gridCellWidth - boxHalfWidth;
//                        boxCenterY = gj*mover.gridCellWidth - boxHalfWidth;
//                        p3d.pushMatrix();
//                        p3d.translate((float)boxCenterX,(float)boxCenterY,(float)boundCentre.z);
//                        p3d.box((float)mover.gridCellWidth, (float)mover.gridCellWidth, (float)bound.z);
//                        p3d.popMatrix();
//                    }
//                }
//                p3d.noStroke();
            }
        };
        sim.setDrawer(drawer);

        /*********************************************************
         * Create a new directory for the simulation results
         */
        if(export) {
            // TODO: generate export path based on input parameters.
            String filePath = BSimUtils.generateDirectoryPath("/Users/antmatyjajo/Desktop/tmp-results/" + BSimUtils.timeStamp() + "__dim_" + simX + "_" + simY + "_" + simZ + "__pop_" + nBacStart + "/");
//            String filePath = BSimUtils.generateDirectoryPath("/home/am6465/tmp-results/" + BSimUtils.timeStamp() + "__dim_" + simX + "_" + simY + "_" + simZ + "__pop_" + nBacStart + "/");

            /**
             * Log all.
             * - per timestep.
             */
            BSimLogger popLogger = new BSimLogger(sim, filePath + "population.csv") {
                DecimalFormat formatter = new DecimalFormat("###.##", DecimalFormatSymbols.getInstance( Locale.ENGLISH ));

                @Override
                public void before() {
                    super.before();
                    write("per time, per cells; id, p1x, p1y, p1z, p2x, p2y, p2z");
                }

                @Override
                public void during() {
                    String buffer = new String();

                    buffer += sim.getFormattedTime() + "\n";
                    write(buffer);

                    write("cells");

                    buffer = "";
                    for(BSimCapsuleBacterium b : allBacteria) {
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
            popLogger.setDt(30);			// Set export time step
            sim.addExporter(popLogger);

            BSimPngExporter imageExporter = new BSimPngExporter(sim, drawer, filePath);
            imageExporter.setDt(30);
            sim.addExporter(imageExporter);

            sim.export();
        } else {
            ///////////////////////////////////
            // Run the simulation
            sim.preview();
        }

        long simulationEndTime = System.nanoTime();
        System.out.println("TOTAL TIME ELAPSED: " + (simulationEndTime - simulationStartTime)/1e6 + " ms.");
    }
}
