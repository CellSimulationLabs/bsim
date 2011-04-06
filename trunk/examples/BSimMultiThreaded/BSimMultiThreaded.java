package BSimMultiThreaded;

import java.awt.Color;
import java.util.ArrayList;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.*;
import bsim.draw.*;
import bsim.particle.*;


public class BSimMultiThreaded {
	static long oldTime;
	public static void main(String[] args) {

		/**
		 * Change this to compare speeds per time step.
		 */
		boolean useMultiThreading = true;
		
		/**
		 * Create the BSim environment.
		 */
		BSim sim = new BSim();		
		sim.setDt(0.01);
		sim.setSimulationTime(100);
		sim.setTimeFormat("0.00");
		sim.setBound(100,100,100);
		
		/**
		 * Create a default bacterial population.
		 */
		final ArrayList<BSimBacterium> bacteria = new ArrayList<BSimBacterium>();		
		while(bacteria.size() < 200000) {		
			BSimBacterium b = new BSimBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
			bacteria.add(b);		
		}
		
		if (useMultiThreading) {
			/**
			 * Create the worker to be used by the threaded ticker. This needs to figure out 
			 * which part of the simulation it needs to update based on the threadID it is
			 * given.
			 */
			class MyWorker extends BSimThreadedTickerWorker {
				public MyWorker (int threadID, int threads) {
					super(threadID, threads);
				}
				
				public void threadedTick(int threadID, int threads) {
					//System.out.println("Running the threaded ticker: Thread " + threadID + " of " + threads);
					
					// Find the block range to be processed by the thread
					int block = bacteria.size() / threads;
					int startIndex = block * threadID;
					int endIndex;
					if (threadID == threads-1) { endIndex = bacteria.size(); }
					else { endIndex = startIndex + block;}
					
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
				public void sequentialBefore() { 
					// System.out.println("Running sequentialBefore()"); 
				}
				public void sequentialAfter() { 
					// System.out.println("Running sequentialAfter()");
					long curTime = System.nanoTime();
					System.out.println("Running time for time step (BSimThreadedTicker): " + (curTime - oldTime));
					oldTime = curTime;
				}
				public BSimThreadedTickerWorker createWorker (int threadID, int threads) {
					MyWorker w = new MyWorker(threadID, threads);
					return (BSimThreadedTickerWorker)w;
				}
			}
			
			/**
			 * Create my new threaded ticker using 2 threads and attach to simulation
			 */
			MyTicker ticker = new MyTicker(2);
			sim.setTicker((BSimTicker)ticker);
		}
		else {
			
			/**
			 * Use the old style sequential ticker (for comparison)
			 */
			sim.setTicker(new BSimTicker() {
				@Override
				public void tick() {
					BSimBacterium b;
					int endIndex = bacteria.size();
					for (int i = 0; i < endIndex; i++) {
						b = bacteria.get(i);
						b.action();		
						b.updatePosition();	
					}
					long curTime = System.nanoTime();
					System.out.println("Running time for time step (BSimTicker): " + (curTime - oldTime));
					oldTime = curTime;
				}
			});
		}
		
		/**
		 * Draw the scene.
		 */
		sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {
				for(BSimBacterium b : bacteria) {
					draw(b,Color.GREEN);
				}
			}
		});	
		
		oldTime = System.nanoTime();
		sim.preview();
	}
}
