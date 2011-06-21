package BSimVesicleActivation;

import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.BSimUtils;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimMovExporter;
import bsim.particle.BSimBacterium;
import bsim.particle.BSimVesicle;

public class BSimVesicleActivation {
	
		public static void main(String[] args) {

			BSim sim = new BSim();		
			sim.setSimulationTime(10);
			
			final double productionRate = 0.1;		
													
			class VesicleA extends BSimVesicle {				
				public VesicleA(BSim sim, Vector3d position, double radius) {
					super(sim, position, radius);
				}					
			}			
			final Vector<VesicleA> aVesiclesToRemove = new Vector<VesicleA>();
			class VesicleB extends BSimVesicle {				
				public VesicleB(BSim sim, Vector3d position, double radius) {
					super(sim, position, radius);					
				}					
			}						
			final Vector<VesicleB> bVesiclesToRemove = new Vector<VesicleB>();
			
			class ProducerA extends BSimBacterium {
				private boolean activated = false;

				public ProducerA(BSim sim, Vector3d position) {
					super(sim, position);		
				}
				
				@SuppressWarnings("unchecked")
				@Override
				public void replicate() {
					setRadiusFromSurfaceArea(surfaceArea(replicationRadius)/2);
					ProducerA child = new ProducerA(sim, new Vector3d(position));
					child.setRadius(radius);
					child.setSurfaceAreaGrowthRate(surfaceAreaGrowthRate);
					child.setChildList(childList);
					child.pVesicle(pVesicle);
					child.setVesicleList(vesicleList);
					childList.add(child);
				}
				
				public void interaction(ProducerA a) {
					if(outerDistance(a) < 0)
						logReaction(a, 1);
				}

				public void interaction(VesicleB v) {
					if(outerDistance(v) < 0) {
						this.activated = true;
						bVesiclesToRemove.add(v);
					}
				}
				
				@SuppressWarnings("unchecked")
				@Override
				public void vesiculate() {
					double r = vesicleRadius();
					vesicleList.add(new VesicleA(sim, new Vector3d(position), r));
					setRadiusFromSurfaceArea(getSurfaceArea()-surfaceArea(r));
				}
			}	
			
			
			class ProducerB extends BSimBacterium {
				private boolean activated = false;

				public ProducerB(BSim sim, Vector3d position) {
					super(sim, position); // default radius is 1 micron			
				}
				
				@SuppressWarnings("unchecked")
				@Override
				public void replicate() {
					setRadiusFromSurfaceArea(surfaceArea(replicationRadius)/2);
					ProducerB child = new ProducerB(sim, new Vector3d(position));
					child.setRadius(radius);
					child.setSurfaceAreaGrowthRate(surfaceAreaGrowthRate);
					child.setChildList(childList);
					child.pVesicle(pVesicle);
					child.setVesicleList(vesicleList);
					childList.add(child);
				}

				public void interaction(VesicleA v) {
					if(outerDistance(v) < 0) {
						this.activated = true;
						aVesiclesToRemove.add(v);
					}
				}
				
				public void interaction(ProducerA a) {
					if(outerDistance(a) < 0)
						logReaction(a, 1);
				}
				
				public void interaction(ProducerB b) {
					if(outerDistance(b) < 0)
						logReaction(b, 1);
				}
				
				@SuppressWarnings("unchecked")
				@Override
				public void vesiculate() {
					double r = vesicleRadius();
					vesicleList.add(new VesicleB(sim, new Vector3d(position), r));
					setRadiusFromSurfaceArea(getSurfaceArea()-surfaceArea(r));
				}
				
			}
								
			final Vector<VesicleA> aVesicles = new Vector<VesicleA>();
			final Vector<ProducerA> aProducers = new Vector<ProducerA>();
			final Vector<ProducerA> aChildren = new Vector<ProducerA>();
			while(aProducers.size() < 50) {		
				ProducerA a = new ProducerA(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
				a.setRadius();
				a.setSurfaceAreaGrowthRate();
				a.setChildList(aChildren);
				a.pVesicle(productionRate);
				a.setVesicleList(aVesicles);	
				aProducers.add(a);
			}
			final Vector<VesicleB> bVesicles = new Vector<VesicleB>();
			final Vector<ProducerB> bProducers = new Vector<ProducerB>();
			final Vector<ProducerB> bChildren = new Vector<ProducerB>();
			while(bProducers.size() < 50) {		
				ProducerB b = new ProducerB(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
				b.setRadius();
				b.setSurfaceAreaGrowthRate();
				b.setChildList(bChildren);
				b.pVesicle(productionRate);
				b.setVesicleList(bVesicles);	
				bProducers.add(b);
			}

			
			sim.setTicker(new BSimTicker() {
				@Override
				public void tick() {
					for(ProducerA a : aProducers)
						for(VesicleB v : bVesicles)
							a.interaction(v);
					
					bVesicles.removeAll(bVesiclesToRemove);
					bVesiclesToRemove.clear();
					
					for(ProducerB b : bProducers)
						for(VesicleA v : aVesicles)
							b.interaction(v);
					
					aVesicles.removeAll(aVesiclesToRemove);
					aVesiclesToRemove.clear();
					
					for(ProducerB b : bProducers)
						for(ProducerA a : aProducers)
							b.interaction(a);
					
					for(int i = 1; i < aProducers.size(); i++)
						for(int j = i+1; j < aProducers.size(); j++)
							aProducers.get(i).interaction(aProducers.get(j));
					
					for(int i = 1; i < bProducers.size(); i++)
						for(int j = i+1; j < bProducers.size(); j++)
							bProducers.get(i).interaction(bProducers.get(j));

					for(ProducerA a : aProducers) {
						a.action();		
						a.updatePosition();
					}
					aProducers.addAll(aChildren);
					aChildren.clear();
					
					for(ProducerB b : bProducers) {
						b.action();		
						b.updatePosition();
					}
					bProducers.addAll(bChildren);
					bChildren.clear();
					
					for(VesicleA v : aVesicles) {
						v.action();	
						v.updatePosition();		
					}

					for(VesicleB v : bVesicles) {
						v.action();	
						v.updatePosition();		
					}
					
				}		
			});

	
			BSimP3DDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
				@Override
				public void scene(PGraphics3D p3d) {						
					for(ProducerA a : aProducers)
						draw(a, a.activated ? Color.RED : new Color(150, 0, 0));
					for(ProducerB b : bProducers)
						draw(b, b.activated ? Color.GREEN : new Color(0, 150, 0));
					for(VesicleA v : aVesicles)
						draw(v, Color.RED);					
					for(VesicleB v : bVesicles)
						draw(v, Color.GREEN);}								
			};
			sim.setDrawer(drawer);
			
			/*
			 * Create a new directory for the simulation results
			 */
			String resultsDir = BSimUtils.generateDirectoryPath("./results/");			

			
//			BSimLogger logger = new BSimLogger(sim, resultsDir + "vesicleActivation" + System.currentTimeMillis() + ".csv") {
//				@Override
//				public void during() {
//					int activations = 0;
//					for (ProducerA a : aProducers)
//						if(a.activated) activations++;
//					for (ProducerB b : bProducers)
//						if(b.activated) activations++;
//					write(sim.getFormattedTime()+","+activations);
//				}
//			};
//			sim.addExporter(logger);
			
			BSimMovExporter movExporter = new BSimMovExporter(sim, drawer, resultsDir+ "vesicleActivation.mov");
			movExporter.setDt(0.03);
			sim.addExporter(movExporter);
						
//			BSimPngExporter pngExporter = new BSimPngExporter(sim, drawer, resultsDir);
//			pngExporter.setDt(10);
//			sim.addExporter(pngExporter);
			
			//sim.export();
			sim.preview();

		}




	}

