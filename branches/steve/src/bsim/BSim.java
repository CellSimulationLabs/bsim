package bsim;

import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JFrame;
import javax.vecmath.Vector3d;

import bsim.particle.BSimBacterium;
import bsim.particle.BSimParticle;

public class BSim {

	private int ticks;
	private double dt;
	private double simulationTime;
	private String timeFormat;
	private Vector3d bound;
	private double visc = 1e-3; // Pa s
	private BSimTicker ticker;
	private BSimDrawer drawer;
	private Vector<BSimExporter> exporters = new Vector<BSimExporter>();


	public void setDt(double d) { dt = d; }	
	public void setSimulationTime(double d) { simulationTime = d; }
	public void setTimeFormat(String s) { timeFormat = s; }
	public void setBound(Vector3d b) { bound = b;	}
	public void setVisc(double v) { visc = v; }
	public void setTicker(BSimTicker bSimTicker) { ticker = bSimTicker;	}
	public void setDrawer(BSimDrawer bSimDrawer) { drawer = bSimDrawer;	}
	public void addExporter(BSimExporter e) { exporters.add(e); }
	
	public double getDt() { return dt; }
	public Vector3d getBound() { return bound; }
	public double getVisc() { return visc; }

	/**
	 * Runs the simulation in a frame until the frame is closed. Ignores exporters. 
	 */
	public void preview() {
		JFrame frame = new JFrame() {
			public void paint(Graphics g) {
				draw(g);
			}
		};
		frame.setSize(getWidth(), getHeight());
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ticks = 0;
		while(true) {
			ticker.tick();	
			ticks++;
			frame.repaint();
			// http://www.ryerson.ca/~dgrimsha/courses/cps840/repaint.html
			try { Thread.sleep((long) (1000*dt)); } catch (InterruptedException e) {}
		}
	}
	
	/**
	 * Runs and exports the simulation
	 */
	public void export() {						
		for(BSimExporter exporter : exporters) exporter.before();		

		// Use integer ticks than double time to avoid rouding issues
		for(ticks = 0; ticks <= ticksIn(simulationTime); ticks++) {			
			ticker.tick();	
			System.out.println(getTime());
			for(BSimExporter exporter : exporters)
				if(ticks % ticksIn(exporter.getDt()) == 0) exporter.during();
		}		

		for(BSimExporter exporter : exporters) exporter.after();			
	}	

	/**
	 * Uses the drawer to draw to the graphics object g
	 */
	public void draw(Graphics g) {
		drawer.draw(g);
	}
	
	public int getWidth() {
		return drawer.getWidth();
	}
	
	public int getHeight() {
		return drawer.getHeight();	
	}
		
	/**
	 * Returns the number of ticks in the duration d
	 */
	private int ticksIn(double d) {
		return (int)(d/dt);
	}	
	
	/**
	 * Returns the simulation time formatted according to timeFormat
	 */
	public String getTime() {
	    DecimalFormat df = new DecimalFormat(timeFormat);
	    return df.format(ticks*dt);
	}

}