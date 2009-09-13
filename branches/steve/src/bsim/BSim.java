package bsim;

import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JFrame;
import javax.vecmath.Vector3d;

public class BSim {

	private int timestep;
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
	 * Runs the simulation in a frame until the frame is closed, ignoring exporters. 
	 */
	public void preview() {
		JFrame frame = new JFrame("BSim Preview") {
			public void paint(Graphics g) {
				draw(g);
			}
		};
		frame.setSize(getWidth(), getHeight());
		frame.setResizable(false);
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
			System.out.println(getTime());
			for(BSimExporter exporter : exporters)
				if(timestep % timesteps(exporter.getDt()) == 0) exporter.during();
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
	 * Returns the number of timesteps in the duration d
	 */
	private int timesteps(double d) {
		return (int)(d/dt);
	}	
	
	/**
	 * Returns the simulation time formatted according to timeFormat
	 */
	public String getTime() {
	    DecimalFormat df = new DecimalFormat(timeFormat);
	    return df.format(timestep*dt);
	}

}