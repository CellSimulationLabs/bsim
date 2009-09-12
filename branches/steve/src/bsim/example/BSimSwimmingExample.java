package bsim.example;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.vecmath.Vector3d;

import processing.core.PGraphics;
import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimDrawer;
import bsim.BSimTicker;
import bsim.export.BSimExporter;
import bsim.export.BSimLogger;
import bsim.particle.BSimBacterium;

public class BSimSwimmingExample {
	
	public static void main(String[] args) {
		
		final BSim sim = new BSim();		
		sim.setDt(0.1);
		sim.setSimulationTime(50);
		
		BSimBacterium bacterium = new BSimBacterium(sim, new Vector3d(50,50,50), 1, new Vector3d(1,1,1));
		sim.addBacterium(bacterium);	
				
		sim.setTicker(new BSimTicker() {
			public void tick() {
				for(BSimBacterium b : sim.getBacteria()) {
					b.action();		
					b.updatePosition();
				}
			}		
		});
		
		sim.setDrawer(new BSimDrawer(400,500) {
			public void draw(Graphics g) {
				PGraphics g3 = new PGraphics3D();
				g3.setPrimary(true); 
				g3.setSize(width, height);
				g3.beginDraw();

				g3.sphereDetail(10);
				g3.noStroke();		
				g3.background(0, 0, 0);

				for(BSimBacterium b : sim.getBacteria()) {
					g3.translate((float) (100*Math.random()),200,0);
					g3.fill((float) (255*Math.random()), (float) (255*Math.random()), 0);		
					g3.sphere((float)100);
				}

				g3.endDraw();		
				g.drawImage(g3.image, 0,0, null);
			}
		});
				
		sim.addExporter(new BSimLogger("BSim.log") {
			public void beginExport() {
				write("Let's go!"); 
			}
			public void exportFrame() {
				String o = "";
				for (BSimBacterium b : sim.getBacteria())
					o += sim.getTime() + " " + b.getPosition() + " " + b.getMotionState();
				write(o);
			}
		});
				
		sim.addExporter(new BSimExporter() {			
			public void exportFrame() {
				
				BufferedImage img = new BufferedImage(400,400, BufferedImage.TYPE_INT_RGB);
				Graphics g = img.createGraphics();
				
				sim.draw(g);
				
				try {
				    File outputfile = new File(sim.getTime() + ".png");
				    ImageIO.write(img, "png", outputfile);
				} catch (IOException e) {}

			}	
		});		
		
		sim.setFrame(new JFrame() {
			public void paint(Graphics g) {
				sim.draw(g);
			}
		});
		
		sim.run();	
		
	}
}
