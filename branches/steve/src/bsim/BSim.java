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
	private Vector<BSimExporter> exporters = new Vector<BSimExporter>();
	private Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>();	

	public double getTime() { return t; }
	public double getDt() { return dt; }
	public Vector<BSimBacterium> getBacteria() { return bacteria; }

	public void setDt(double d) { dt = d; }
	public void setSimulationTime(double d) { simulationTime = d; }
	public void setTicker(BSimTicker bSimTicker) { ticker = bSimTicker;	}
	public void setDrawer(BSimDrawer bSimDrawer) { drawer = bSimDrawer;	}	
	public void addExporter(BSimExporter e) { exporters.add(e); }
	public void addBacterium(BSimBacterium b) { bacteria.add(b); }

	public void preview() {
		JFrame frame = new JFrame() {
			public void paint(Graphics g) {
				draw(g);
			}
		};
		frame.setSize(getWidth(), getHeight());
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		while(true) {
			ticker.tick();	
			frame.repaint();
			// http://www.ryerson.ca/~dgrimsha/courses/cps840/repaint.html
			try { Thread.sleep(100); } catch (InterruptedException e) {}
		}
	}
	
	public void export() {						
		for(BSimExporter exporter : exporters) exporter.before();		

		for(t = 0; t <= simulationTime; t = t + dt) {
			ticker.tick();	
			for(BSimExporter exporter : exporters) exporter.during();
		}		

		for(BSimExporter exporter : exporters) exporter.after();			
	}	

	public void draw(Graphics g) {
		drawer.draw(g);
	}
	
	public int getWidth() {
		return drawer.getWidth();
	}
	
	public int getHeight() {
		return drawer.getHeight();	
	}

}