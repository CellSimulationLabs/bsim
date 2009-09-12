package bsim;

import java.awt.Frame;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JFrame;

import bsim.export.BSimExporter;
import bsim.particle.BSimBacterium;

public class BSim {

	private double t;
	private double dt;
	private double simulationTime;	
	private BSimTicker ticker;
	private BSimDrawer drawer;
	private JFrame frame;
	private Vector<BSimExporter> exporters = new Vector<BSimExporter>();
	private Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>();	

	public double getTime() { return t; }
	public double getDt() { return dt; }
	public Vector<BSimBacterium> getBacteria() { return bacteria; }

	public void setDt(double d) { dt = d; }
	public void setSimulationTime(double d) { simulationTime = d; }
	public void setTicker(BSimTicker bSimTicker) { ticker = bSimTicker;	}
	public void setDrawer(BSimDrawer bSimDrawer) { drawer = bSimDrawer;	}
	public void setFrame(JFrame f) { frame = f;	}
	public void addBacterium(BSimBacterium b) { bacteria.add(b); }
	public void addExporter(BSimExporter e) { exporters.add(e); }

	public void run() {						
		frame.setSize(drawer.getWidth(), drawer.getHeight());
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		if(frame == null) {
			for(BSimExporter exporter : exporters)
				exporter.beginExport();
		} 

		for(t = 0; t <= simulationTime; t = t + dt) {
			ticker.tick();	
			if(frame == null) {
				for(BSimExporter exporter : exporters)
					exporter.exportFrame();
			} else {
				frame.repaint();
				try { Thread.sleep(100); } catch (InterruptedException e) {}
			}
		}		
		
		if(frame == null) {
			for(BSimExporter exporter : exporters)
				exporter.finishExport();	
		}
	}	

	public void draw(Graphics g) {
		drawer.draw(g);
	}

}