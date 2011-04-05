package BSimMultiThreaded;

import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.*;
import bsim.draw.*;
import bsim.particle.*;


public class BSimMultiThreaded {
	
	public static void main(String[] args) {

		/**
		 * Create the BSim environment.
		 */
		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setSimulationTime(10);
		sim.setTimeFormat("0.00");
		sim.setBound(100,100,100);
		
		/**
		 * Create a default bacterial population.
		 */
		final Vector<BSimBacterium> bacteria = new Vector<BSimBacterium>();		
		while(bacteria.size() < 10000) {		
			BSimBacterium b = new BSimBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
			if(!b.intersection(bacteria)) bacteria.add(b);		
		}
		
		/**
		 * Create the worker to be used by the threaded ticker. This needs to figure out 
		 * which part of the simulation it needs to update based on the threadID it is
		 * given.
		 */
		class MyWorker extends BSimThreadedTickerWorker {
			public MyWorker (int threadID, int threads, BSimNotifier notifier) {
				super(threadID, threads, notifier);
			}
			
			public void threadedTick(int threadID, int threads) {
				System.out.println("Running the threaded ticker: Thread " + threadID + " of " + threads);
				
				// Find the block range to be processed by the thread
				int block = bacteria.size() / threads;
				int startIndex = block * threadID;
				int endIndex;
				if (threadID < threads-1) { endIndex = startIndex + block; }
				else { endIndex = bacteria.size(); }
				
				// Process the block
				BSimBacterium b;
				for (int i = startIndex; i < endIndex; i++) {
					b = bacteria.get(i);
					b.action();		
					b.updatePosition();
				}
			}
		}
		
		/**
		 * Once the worker has been defined we can create the threaded ticker. This needs
		 * to include a createWorker function that links to the worker we defined previously.
		 */
		class MyTicker extends BSimThreadedTicker {
			public MyTicker (int totalThreads) { super(totalThreads); }
			
			/**
			 * These are the functions that are over written for the sequential operations
			 * and to create the parallel worker threads.
			 */
			public void sequentialBefore() { System.out.println("Running sequentialBefore()"); }
			public void sequentialAfter() { System.out.println("Running sequentialAfter()"); }
			public BSimThreadedTickerWorker createWorker (int threadID, int threads, BSimNotifier notifier) {
				MyWorker w = new MyWorker(threadID, threads, notifier);
				return (BSimThreadedTickerWorker)w;
			}
		}
		
		MyTicker ticker = new MyTicker(2);
		sim.setTicker((BSimTicker)ticker);
		
		/**
		 * Draw the scene.
		 */
		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {
				for(BSimBacterium b : bacteria) {
					draw(b,Color.YELLOW);
				}
			}
		});	
		
		sim.preview();
	}
}
