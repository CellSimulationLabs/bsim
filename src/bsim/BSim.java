package bsim;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JFrame;
import javax.vecmath.Vector3d;

import bsim.draw.BSimDrawer;
import bsim.export.BSimExporter;

/**
 * Main simulation class.
 * Holds information related to a simulation including the bounds, types
 * of boundary, temperature, etc.
 */
public class BSim {
	
	/** Boltzmann constant. */
	public static double BOLTZMANN = 1.38e-23;

	private double dt = 0.01;
	private double simulationTime;
	private DecimalFormat timeFormat = new DecimalFormat("0.00");	
	private Vector3d bound = new Vector3d(100,100,100);
	 /* Wrapping boundaries (x, y, z) */
	private boolean[] solid = {false, false, false};
	/* Can chemicals can leak out of the boundaries (x_top, x_bottom, y_top, y_bottom, z_top, z_bottom) */
	private boolean[] leaky = {false, false, false, false, false, false};
	private double[]  leakyRate = {0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
	/* Conditions of 'Chemotaxis in Escherichia Coli', Berg at al. */
	private double visc = 2.7e-3; // Pa s
	private double temperature = 305; // K	
	private BSimTicker ticker;
	private BSimDrawer drawer;
	private Vector<BSimExporter> exporters = new Vector<BSimExporter>();	

	/** Set the timestep (secs). */
	public void setDt(double d) { dt = d; }
	/** Set the length of the simulation (secs). */
	public void setSimulationTime(double d) { simulationTime = d; }
	/** Set the time format. Used to display the time on movies. */
	public void setTimeFormat(String s) { timeFormat = new DecimalFormat(s); }
	/** Set the simulation bound (microns). */
	public void setBound(double x, double y, double z) { bound = new Vector3d(x,y,z); }
	/** Set whether the boundaries are solid (reflecting) or wrapping (periiodic). Solid = true, relecting = false. */
	public void setSolid(boolean x, boolean y, boolean z) { solid = new boolean[]{x,y,z}; }
	/** Set whether the boundaries are leaky. A leaky boundary allows for chemicals to escape at some defined rate. */
	public void setLeaky(boolean xTop, boolean xBottom, boolean yTop, boolean yBottom, boolean zTop, boolean zBottom) { leaky = new boolean[]{xTop,xBottom,yTop,yBottom,zTop,zBottom}; }
	/** Set the rate that chemicals can escape from the simulation (if the boundary is leaky). */
	public void setLeakyRate(double xTop, double xBottom, double yTop, double yBottom, double zTop, double zBottom) { leakyRate = new double[]{xTop,xBottom,yTop,yBottom,zTop,zBottom}; }
	/** Set the viscosity of the environment. */
	public void setVisc(double v) { visc = v; }
	/** Set the temperature of the environment. */
	public void setTemperature(double t) { temperature = t; }	
	/** Set the ticker to be used during simulation. */
	public void setTicker(BSimTicker bSimTicker) { ticker = bSimTicker;	}
	/** Set the drawer to be used during simulation. */
	public void setDrawer(BSimDrawer bSimDrawer) { drawer = bSimDrawer;	}
	/** Add an exporter to be called during simulation. */
	public void addExporter(BSimExporter e) { exporters.add(e); }	
	
	/** Return the timestep. */
	public double getDt() { return dt; }
	/** Return the length of the simulation. */
	public double getSimulationTime(){ return simulationTime; }
	/** Return the current timestep of the simulation. */
	public double getTimestep() { return timestep; }
	/** Return the current time of the simulation. */
	public double getTime() { return timestep*dt; }
	/** Return a formatted version of the current time of the simulation. */
	public String getFormattedTime() { return timeFormat.format(timestep*dt); }
	public String getFormattedTimeHours() { return timeFormat.format(timestep*dt/3600.0); }

	/** Return the simulation bounds (microns). */
	public Vector3d getBound() { return bound; }
	/** Return whether the boundaries are solid (reflecting) or wrapping (periiodic). Solid = true, relecting = false. */
	public boolean[] getSolid() { return solid; }
	/** Return whether the boundaries are leaky. */
	public boolean[] getLeaky() { return leaky; }
	/** Return the rate that chemicals can escape from the simulation (if the boundary is leaky). */
	public double[] getLeakyRate() { return leakyRate; }
	/** Return the viscosity of the environment. */
	public double getVisc() { return visc; }
	/** Return the temperature of the environment. */
	public double getTemperature() { return temperature; }
	
	private int timestep;

	/**
	 * Runs the simulation in a frame until the frame is closed, ignoring exporters. 
	 */
	public void preview() {
		@SuppressWarnings("serial")
		JFrame frame = new JFrame("BSim Preview") {

			@Override
			public void paint(Graphics g) {
				drawer.draw((Graphics2D)g);
			}
		};
		frame.setSize(drawer.getWidth(), drawer.getHeight());
		frame.setResizable(false);
		/* frame.addMouseListener(drawer) */
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
	 * Runs and exports the simulation.
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

		// Any clean-up that is required for the ticker (especially in threaded case)
		ticker.finish();
	}	
		
	/**
	 * Returns the number of complete timesteps in the duration d.
	 */
	public int timesteps(double d) {
		return (int)(d/dt);
	}	
	

}