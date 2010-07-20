package bsim;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JFrame;
import javax.vecmath.Vector3d;

import bsim.draw.BSimDrawer;
import bsim.export.BSimExporter;

public class BSim {
	
	public static double BOLTZMANN = 1.38e-23;

	private double dt = 0.01;
	private double simulationTime;
	private DecimalFormat timeFormat = new DecimalFormat("0.00");	
	private Vector3d bound = new Vector3d(100,100,100);
	private boolean[] solid = {false, false, false};
	/* Conditions of 'Chemotaxis in Escherichia Coli', Berg at al. */
	private double visc = 2.7e-3; // Pa s
	private double temperature = 305; // K	
	private BSimTicker ticker;
	private BSimDrawer drawer;
	private Vector<BSimExporter> exporters = new Vector<BSimExporter>();	

	public void setDt(double d) { dt = d; }	
	public void setSimulationTime(double d) { simulationTime = d; }
	public void setTimeFormat(String s) { timeFormat = new DecimalFormat(s); }
	public void setBound(double x, double y, double z) { bound = new Vector3d(x,y,z);	}
	public void setSolid(boolean x, boolean y, boolean z) { solid = new boolean[]{x,y,z}; }
	public void setVisc(double v) { visc = v; }
	public void setTemperature(double t) { temperature = t; }	
	public void setTicker(BSimTicker bSimTicker) { ticker = bSimTicker;	}
	public void setDrawer(BSimDrawer bSimDrawer) { drawer = bSimDrawer;	}
	public void addExporter(BSimExporter e) { exporters.add(e); }	
	
	public double getDt() { return dt; }
	public double getSimulationTime(){ return simulationTime; }
	public double getTimestep() { return timestep; }
	public double getTime() { return timestep*dt; }
	public String getFormattedTime() { return timeFormat.format(timestep*dt); }
	public Vector3d getBound() { return bound; }
	public boolean[] getSolid() { return solid; }
	public double getVisc() { return visc; }
	public double getTemperature() { return temperature; }
	
	private int timestep;

	/**
	 * Runs the simulation in a frame until the frame is closed, ignoring exporters. 
	 */
	public void preview() {
		JFrame frame = new JFrame("BSim Preview") {
			@Override
			public void paint(Graphics g) {
				drawer.draw((Graphics2D)g);
			}
		};
		frame.setSize(drawer.getWidth(), drawer.getHeight());
		frame.setResizable(false);
		/* TODO frame.addMouseListener(drawer) */
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		timestep = 0;
		while(true) {
			ticker.tick();	
			timestep++;
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

		// Increment integer timesteps than adding to double time to avoid rouding issues
		for(timestep = 0; timestep <= timesteps(simulationTime); timestep++) {			
			ticker.tick();	
			System.out.println(getFormattedTime());
			for(BSimExporter exporter : exporters)
				if(timestep % timesteps(exporter.getDt()) == 0) exporter.during();
		}		

		for(BSimExporter exporter : exporters) exporter.after();			
	}	
		
	/**
	 * Returns the number of timesteps in the duration d
	 */
	public int timesteps(double d) {
		return (int)(d/dt);
	}	
	

}