package bsim.example;

import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.BSimUtils;
import bsim.draw.BSimP3DDrawer;
import bsim.export.BSimLogger;
import bsim.export.BSimMovExporter;
import bsim.export.BSimPngExporter;
import bsim.particle.BSimBacterium;
import bsim.particle.BSimParticle;

public class BSimChemicalActivationExample {
	
		public static void main(String[] args) {

			BSim sim = new BSim();		
			sim.setSimulationTime(10);
			
			final double productionRate = 1;
			final double moleculeRadius = 1e-3;
													
			class MoleculeA extends BSimParticle {				
				public MoleculeA(BSim sim, Vector3d position, double radius) {
					super(sim, position, radius);
				}					
			}		
			final Vector<MoleculeA> aMoleculesToRemove = new Vector<MoleculeA>();
			class MoleculeB extends BSimParticle {				
				public MoleculeB(BSim sim, Vector3d position, double radius) {
					super(sim, position, radius);					
				}					
			}						
			final Vector<MoleculeB> bMoleculesToRemove = new Vector<MoleculeB>();
			
			class ProducerA extends BSimBacterium {
				private boolean activated = false;
				private Vector<MoleculeA> moleculeList;
				private double pMolecule = productionRate; 
				public void setMoleculeList(Vector<MoleculeA> l) { moleculeList = l; }

				public ProducerA(BSim sim, Vector3d position) {
					super(sim, position);		
				}
				
				@Override
				public void replicate() {
					setRadiusFromSurfaceArea(surfaceArea(replicationRadius)/2);
					ProducerA child = new ProducerA(sim, new Vector3d(position));
					child.setRadius(radius);
					child.setSurfaceAreaGrowthRate(surfaceAreaGrowthRate);
					child.setChildList(childList);
					child.setMoleculeList(moleculeList);
					childList.add(child);
				}
				
				public void interaction(ProducerA a) {
					if(outerDistance(a) < 0)
						logReaction(a, 1);
				}

				public void interaction(MoleculeB m) {
					if(outerDistance(m) < 0) {
						this.activated = true;
						bMoleculesToRemove.add(m);
					}
				}
			
				
				@Override
				public void action() {
					super.action();
					
					if(Math.random() < pMolecule*sim.getDt()) {
						moleculeList.add(new MoleculeA(sim, new Vector3d(position), moleculeRadius));
					}
				}
			}				
			
			class ProducerB extends BSimBacterium {
				private boolean activated = false;
				private Vector<MoleculeB> moleculeList;
				private double pMolecule = productionRate; 
				public void setMoleculeList(Vector<MoleculeB> l) { moleculeList = l; }

				public ProducerB(BSim sim, Vector3d position) {
					super(sim, position);		
				}
				
				@Override
				public void replicate() {
					setRadiusFromSurfaceArea(surfaceArea(replicationRadius)/2);
					ProducerB child = new ProducerB(sim, new Vector3d(position));
					child.setRadius(radius);
					child.setSurfaceAreaGrowthRate(surfaceAreaGrowthRate);
					child.setChildList(childList);
					child.setMoleculeList(moleculeList);
					childList.add(child);
				}

				public void interaction(MoleculeA m) {
					if(outerDistance(m) < 0) {
						this.activated = true;
						aMoleculesToRemove.add(m);
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
				
				@Override
				public void action() {
					super.action();
					
					if(Math.random() < pMolecule*sim.getDt()) {
						moleculeList.add(new MoleculeB(sim, new Vector3d(position), moleculeRadius));
					}
				}
			}
								
			final Vector<MoleculeA> aMolecules = new Vector<MoleculeA>();
			final Vector<ProducerA> aProducers = new Vector<ProducerA>();
			final Vector<ProducerA> aChildren = new Vector<ProducerA>();
			while(aProducers.size() < 50) {		
				ProducerA a = new ProducerA(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
				a.setRadius();
				a.setSurfaceAreaGrowthRate();
				a.setChildList(aChildren);				
				a.setMoleculeList(aMolecules);	
				aProducers.add(a);
			}
			final Vector<MoleculeB> bMolecules = new Vector<MoleculeB>();
			final Vector<ProducerB> bProducers = new Vector<ProducerB>();
			final Vector<ProducerB> bChildren = new Vector<ProducerB>();
			while(bProducers.size() < 50) {		
				ProducerB b = new ProducerB(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
				b.setRadius();
				b.setSurfaceAreaGrowthRate();
				b.setChildList(bChildren);				
				b.setMoleculeList(bMolecules);	
				bProducers.add(b);
			}

			
			sim.setTicker(new BSimTicker() {
				@Override
				public void tick() {
					for(ProducerA a : aProducers)
						for(MoleculeB m : bMolecules)
							a.interaction(m);
					
					bMolecules.removeAll(bMoleculesToRemove);
					bMoleculesToRemove.clear();
					
					for(ProducerB b : bProducers)
						for(MoleculeA m : aMolecules)
							b.interaction(m);
					
					aMolecules.removeAll(aMoleculesToRemove);
					aMoleculesToRemove.clear();
					
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
					
					for(MoleculeA m : aMolecules) {
						m.action();	
						m.updatePosition();		
					}
					for(MoleculeB m : bMolecules) {
						m.action();	
						m.updatePosition();		
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
					for(MoleculeA m : aMolecules)
						draw(m, Color.RED);
					for(MoleculeB m : bMolecules)
						draw(m, Color.GREEN);
				}
			};
			sim.setDrawer(drawer);
			
//			BSimLogger logger = new BSimLogger(sim, "chemicalActivation" + System.currentTimeMillis() + ".csv") {	
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
			
			
			BSimMovExporter movExporter = new BSimMovExporter(sim, drawer, "results/chemicalActivation.mov");
			movExporter.setDt(0.03);
			sim.addExporter(movExporter);

			
//			BSimPngExporter pngExporter = new BSimPngExporter(sim, drawer, "results");
//			pngExporter.setDt(10);
//			sim.addExporter(pngExporter);
			
			sim.export();
			

		}




	}

