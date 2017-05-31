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
public class CapsulePerformance {

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
    public List<Double> simDimensions = new ArrayList<>(Arrays.asList(new Double[] {20., 20., 1.}));

    @Parameter(names = "-pop", arity = 2, description = "Initial seed population (n_blue, n_red).")
    public List<Integer> initialPopulation = new ArrayList<>(Arrays.asList(new Integer[] {5, 5}));

    // TODO: parameterise the (x?) spatial proportion which a given population occupies.

    public static void main(String[] args) {
        CapsulePerformance cg = new CapsulePerformance();

        System.out.println(cg.simDimensions);

        new JCommander(cg, args);

        System.out.println(cg.simDimensions);

        cg.run();
    }

    public void run() {

        double simX = simDimensions.get(0);
        double simY = simDimensions.get(1);
        double simZ = simDimensions.get(2);

        int nBlueStart = initialPopulation.get(0);
        int nRedStart = initialPopulation.get(1);

        long simulationStartTime = System.nanoTime();

        // create the simulation object
        BSim sim = new BSim();
        sim.setDt(0.1);				// Simulation Timestep
        sim.setSimulationTime(21600); // 21600 = 6 hours
        sim.setTimeFormat("0.00");		// Time Format for display
        sim.setBound(simX, simY, simZ);		// Simulation Boundaries
        sim.setSolid(true, false, true);

//        System.out.println(sim.getBound());
//        System.exit(0);


        /*********************************************************
         * Create the bacteria
         */

        final List<BSimCapsuleBacterium> blueBacteria = Collections.synchronizedList(new ArrayList());
        final List<BSimCapsuleBacterium> redBacteria = Collections.synchronizedList(new ArrayList());

        Random bacRng = new Random();

//        for(int i = 0; i < 10; i++){
//            for(int j = 0; j < 10; j++){
//                blueBacteria.add(new BSimCapsuleBacterium(sim,
//                        new Vector3d(1.0 + ((double)i*48.5)/10, 1.0 + ((double)j*38)/10, 0.4),
//                        new Vector3d(3.0 + ((double)i*48.5)/10, 1.0 + ((double)j*38)/10, 0.4)
//                        ));
//
//                redBacteria.add(new BSimCapsuleBacterium(sim,
//                        new Vector3d(2.5 + ((double)i*50)/10, 2.5 + ((double)j*40)/10, 0.4),
//                        new Vector3d(4.5 + ((double)i*50)/10, 2.5 + ((double)j*40)/10, 0.4)
//                        ));
//            }
//        }


        final List<BSimCapsuleBacterium> allBacteria = Collections.synchronizedList(new ArrayList());

        generator:
        while(blueBacteria.size() < nBlueStart) {
            double bL = 1. + 1.*0.2*(bacRng.nextDouble() - 0.5);
            Vector3d pos = new Vector3d(3 + bacRng.nextDouble()*(sim.getBound().x - 6), 3 + bacRng.nextDouble()*(sim.getBound().y - 6), bacRng.nextDouble()*0.1*(simZ - 0.1)/2.0);
            // Test intersection

            Vector3d distance = new Vector3d(0,0,0);

            for(BSimCapsuleBacterium otherBac : allBacteria){
                distance.sub(otherBac.position, pos);
                if(distance.lengthSquared() < 7.0){
                    continue generator;
                }
            }

            double angle = bacRng.nextDouble()*2*Math.PI;
            BSimCapsuleBacterium bc = new BSimCapsuleBacterium(sim,
                    pos, new Vector3d(bL*Math.sin(angle) + pos.x, bL*Math.cos(angle) + pos.y, pos.z));
            bc.L = bL;
            blueBacteria.add(bc);
            allBacteria.add(bc);
        }

        generator:
        while(redBacteria.size() < nRedStart) {
            double bL = 1. + 1.*0.2*(bacRng.nextDouble() - 0.5);
            Vector3d pos = new Vector3d(3 + bacRng.nextDouble()*(sim.getBound().x - 6), 3 + bacRng.nextDouble()*(sim.getBound().y - 6), simZ/2.0);
            // Test intersection

            Vector3d distance = new Vector3d(0,0,0);

            for(BSimCapsuleBacterium otherBac : allBacteria){
                distance.sub(otherBac.position, pos);
                if(distance.lengthSquared() < 7.0){
                    continue generator;
                }
            }

            double angle = bacRng.nextDouble()*2*Math.PI;
            BSimCapsuleBacterium bc = new BSimCapsuleBacterium(sim,
                    pos, new Vector3d(bL*Math.sin(angle) + pos.x, bL*Math.cos(angle) + pos.y, pos.z));
            bc.L = bL;
            redBacteria.add(bc);
            allBacteria.add(bc);
        }


        /*********************************************************
         * Set up the ticker
         */

        final List<BSimCapsuleBacterium> blue_born = Collections.synchronizedList(new ArrayList());
        final List<BSimCapsuleBacterium> blue_dead = Collections.synchronizedList(new ArrayList());

        final List<BSimCapsuleBacterium> red_born = Collections.synchronizedList(new ArrayList());
        final List<BSimCapsuleBacterium> red_dead = Collections.synchronizedList(new ArrayList());

        final IteratorMover mover = new IteratorMover(allBacteria);

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

                for(BSimCapsuleBacterium b : blueBacteria) {
                    b.grow();

                    // Divide if grown past threshold
                    if(b.L > b.L_th){
                        blue_born.add(b.divide());
                    }
                }
                blueBacteria.addAll(blue_born);
                allBacteria.addAll(blue_born);
                blue_born.clear();

                for(BSimCapsuleBacterium b : redBacteria) {
                    b.grow();

                    // Divide if grown past threshold
                    if(b.L > b.L_th){
                        red_born.add(b.divide());
                    }
                }
                redBacteria.addAll(red_born);
                allBacteria.addAll(red_born);
                red_born.clear();

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
                for(BSimCapsuleBacterium b : blueBacteria){
                    // Kick out if past the boundary
                    if((b.x1.y < 0) && (b.x2.y < 0)){
                        blue_dead.add(b);
                    }
                    if((b.x1.y > sim.getBound().y) && (b.x2.y > sim.getBound().y)){
                        blue_dead.add(b);
                    }
                }
                blueBacteria.removeAll(blue_dead);
                allBacteria.removeAll(blue_dead);
                blue_dead.clear();

                // Removal
                for(BSimCapsuleBacterium b : redBacteria){
                    // Kick out if past the boundary
                    if((b.x1.y < 0) && (b.x2.y < 0)){
                        red_dead.add(b);
                    }
                    if((b.x1.y > sim.getBound().y) && (b.x2.y > sim.getBound().y)){
                        red_dead.add(b);
                    }
                }
                redBacteria.removeAll(red_dead);
                allBacteria.removeAll(red_dead);
                red_dead.clear();

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
                p3d.directionalLight(128, 128, 128, 1, 1, -1);

                for(BSimCapsuleBacterium b : blueBacteria){
                    draw(b, new Color(55,126,184));
                }

                for(BSimCapsuleBacterium b : redBacteria){
                    draw(b, new Color(228,26,28));
                }
            }

            public void draw(BSimCapsuleBacterium bac, Color c) {
                p3d.fill(c.getRed(), c.getGreen(), c.getBlue());

                Vector3d worldY = new Vector3d(0, 1, 0);
                Vector3d bacDirVector = new Vector3d();

                bacDirVector.sub(bac.x2, bac.x1);

                Vector3d u = new Vector3d();
                u.scaleAdd(0.5, bacDirVector, bac.x1);

                Vector3d bacRotVector = new Vector3d();
                bacRotVector.cross(worldY, bacDirVector);

                bacDirVector.normalize();
                bacRotVector.normalize();

                p3d.pushMatrix();
                p3d.translate((float) u.x, (float) u.y, (float) u.z);
                //fix the rotation on the axis
                //pushMatrix();
                p3d.rotate((float) worldY.angle(bacDirVector), (float) bacRotVector.x, (float) bacRotVector.y, (float) bacRotVector.z);
                drawRodShape((float) bac.radius, (float) bac.L, 32);
                p3d.popMatrix();
                sphere(bac.x1, bac.radius, c, 255);
                sphere(bac.x2, bac.radius, c, 255);
            }

            //the RodShape is drawn along the y axis
            public void drawRodShape(float radius, float diameter, int sides) {
                float angle = 0;
                float angleIncrement = p3d.TWO_PI / sides;
                // save a bunch of calculations:
                float diameterRatio = (diameter / 2.0f);
                p3d.beginShape(p3d.QUAD_STRIP);
                for (int i = 0; i < sides + 1; ++i) {
                    p3d.vertex((float) (radius * Math.cos(angle)), 0 - diameterRatio, (float) (radius * Math.sin(angle)));
                    p3d.vertex((float) (radius * Math.cos(angle)), 0 + diameterRatio, (float) (radius * Math.sin(angle)));
                    angle += angleIncrement;
                }
                p3d.endShape();
            }
        };
        sim.setDrawer(drawer);

        /*********************************************************
         * Create a new directory for the simulation results
         */
        if(export) {
            // TODO: generate export path based on input parameters.
//            String filePath = BSimUtils.generateDirectoryPath("/Users/antmatyjajo/Desktop/tmp-results/" + BSimUtils.timeStamp() + "__dim_" + simX + "_" + simY + "_" + simZ + "/");
            String filePath = BSimUtils.generateDirectoryPath("/home/am6465/tmp-results/" + BSimUtils.timeStamp() + "__dim_" + simX + "_" + simY + "_" + simZ + "__b_r_" + nBlueStart + "_" + nRedStart + "/");

            /**
             * Log all.
             * - per timestep.
             */
            BSimLogger popLogger = new BSimLogger(sim, filePath + "population.csv") {
                DecimalFormat formatter = new DecimalFormat("###.##", DecimalFormatSymbols.getInstance( Locale.ENGLISH ));

                @Override
                public void before() {
                    super.before();
                    write("per time, per blue; per red, id, p1x, p1y, p1z, p2x, p2y, p2z");
                }

                @Override
                public void during() {
                    String buffer = new String();

                    buffer += sim.getFormattedTime() + "\n";
                    write(buffer);

                    write("blues");

                    buffer = "";
                    for(BSimCapsuleBacterium b : blueBacteria) {
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
                    for(BSimCapsuleBacterium b : redBacteria) {
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
