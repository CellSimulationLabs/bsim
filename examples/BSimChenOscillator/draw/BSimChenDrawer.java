package BSimChenOscillator.draw;

import bsim.BSim;
import bsim.draw.BSimP3DDrawer;
import processing.core.PConstants;

import java.awt.*;

/**
 * Created by antmatyjajo on 11/08/2016.
 */
public abstract class BSimChenDrawer extends BSimP3DDrawer {

    public BSimChenDrawer(BSim _sim, int _w, int _h) {
        super(_sim, _w, _h);
    }

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
                    bound.x > bound.y ? (float)bound.x : (float)bound.y,
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
//        p3d.text(sim.getFormattedTime(), 50, 50);
    }
}
