package dde;

import java.awt.*;
import org.opensourcephysics.frames.*;
import org.opensourcephysics.display3d.simple3d.*;

import javax.swing.*;

public class Box3DApp {
    public static void main( String [ ] args ) {
        // creates a drawing frame and a drawing panel
        Display3DFrame frame = new Display3DFrame("3D demo");
        frame.setPreferredMinMax(-10, 10, -10, 10, -10, 10);
        frame.setDecorationType(VisualizationHints.DECORATION_AXES); ;
        frame.setAllowQuickRedraw( false ); // use shading when rotating
        Element block = new ElementBox ( ) ;
        block.setXYZ(0, 0, 0);
        block.setSizeXYZ(6, 6, 3);
        block.getStyle().setFillColor(Color.RED);
        block.getStyle().setResolution(new Resolution(6, 6, 3)); // divides
        frame.addElement( block ) ;
        frame.setVisible( true );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } }