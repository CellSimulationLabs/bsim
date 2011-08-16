package BSimLactam;

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
import bsim.particle.BSimBacterium;
import bsim.particle.BSimParticle;
import bsim.particle.BSimVesicle;

public class BSimLactam {
	
	public static void main(String[] args) {

		BSim sim = new BSim();
		sim.setBound(100,100,100);
		sim.setSimulationTime(30);
		
		final double propLactamaseBacteria = 0.1;
				
		final int nLactams = 1000;
		final int nBacteria = 100;
		final int nThreatenedBacteria = (int) (nBacteria*(1-propLactamaseBacteria));
		final int nLactamaseBacteria = (int) (nBacteria*propLactamaseBacteria);
		final double pVesicle = 0.05;
		final double lactamRadius = 1e-3;
		
		class Lactam extends BSimParticle {
			public Lactam(BSim sim, Vector3d position) {
				super(sim, position, lactamRadius);
			}				
		}				
		final Vector<Lactam> lactamsToRemove = new Vector<Lactam>();
		
		class LactamaseVesicle extends BSimVesicle {			
			public LactamaseVesicle(BSim sim, Vector3d position, double radius) {
				super(sim, position, radius);
			}	
			
			public void interaction(Lactam l) {
				if(outerDistance(l) < 0)
					lactamsToRemove.add(l);			
			}
		}
		
		class LactamaseBacterium extends BSimBacterium {
			private boolean vesiculating = false;
			
			public LactamaseBacterium(BSim sim, Vector3d position) {
				super(sim, position);
			}
			@SuppressWarnings("unchecked")
			public void vesiculate() {
				if (vesiculating) {
					double r = vesicleRadius();
					vesicleList.add(new LactamaseVesicle(sim, new Vector3d(position), r));
					setRadiusFromSurfaceArea(getSurfaceArea()-surfaceArea(r));
				}
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void replicate() {
				setRadiusFromSurfaceArea(surfaceArea(replicationRadius)/2);
				LactamaseBacterium child = new LactamaseBacterium(sim, new Vector3d(position));	
				child.setRadius(radius);
				child.setSurfaceAreaGrowthRate(surfaceAreaGrowthRate);
				child.setChildList(childList);
				child.pVesicle(pVesicle);
				child.setVesicleList(vesicleList);
				childList.add(child);
			}
			
			public void interaction(Lactam l) {
				if(outerDistance(l) < 0) {
					vesiculating = true;
					lactamsToRemove.add(l);				
				}
			}
			
			public void interaction(LactamaseBacterium b) {
				if(outerDistance(b) < 0)
					logReaction(b, 1);
			}
			
		}
		
		class ThreatenedBacterium extends BSimBacterium {
			private boolean dead = false;
			
			public ThreatenedBacterium(BSim sim, Vector3d position) {
				super(sim, position);
			}
			@Override
			public void action() {
				if(dead) brownianForce();
				else super.action();
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void replicate() {
				setRadiusFromSurfaceArea(surfaceArea(replicationRadius)/2);
				ThreatenedBacterium child = new ThreatenedBacterium(sim, new Vector3d(position));
				child.setRadius(radius);
				child.setSurfaceAreaGrowthRate(surfaceAreaGrowthRate);
				child.setChildList(childList);
				childList.add(child);
			}
			
			public void interaction(Lactam l) {
				if(outerDistance(l) < 0) {
					dead = true; 
					lactamsToRemove.add(l);
				}
			}
			
			public void interaction(ThreatenedBacterium b) {
				if(outerDistance(b) < 0)
					logReaction(b, 1);
			}
			
			public void interaction(LactamaseBacterium b) {
				if(outerDistance(b) < 0)
					logReaction(b, 1);
			}
			
		}
		
		
		final Vector<Lactam> lactams = new Vector<Lactam>();
		final Vector<LactamaseVesicle> lactamaseVesicles = new Vector<LactamaseVesicle>();
		final Vector<LactamaseBacterium> lactamaseBacteria = new Vector<LactamaseBacterium>();
		final Vector<LactamaseBacterium> lactamaseChildren = new Vector<LactamaseBacterium>();
		final Vector<ThreatenedBacterium> threatenedBacteria = new Vector<ThreatenedBacterium>();
		final Vector<ThreatenedBacterium> threatenedChildren = new Vector<ThreatenedBacterium>();
		while(lactamaseBacteria.size() < nLactamaseBacteria) {		
			LactamaseBacterium b = new LactamaseBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
			b.setRadius();
			b.setSurfaceAreaGrowthRate();
			b.setChildList(lactamaseChildren);
			b.pVesicle(pVesicle);
			b.setVesicleList(lactamaseVesicles);
			lactamaseBacteria.add(b);		
		}			
		while(threatenedBacteria.size() < nThreatenedBacteria) {		
			ThreatenedBacterium b = new ThreatenedBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
			b.setRadius();
			b.setSurfaceAreaGrowthRate();
			b.setChildList(threatenedChildren);			
			threatenedBacteria.add(b);		
		}
		while(lactams.size() < nLactams) {
			Lactam l = new Lactam(sim, new Vector3d(sim.getBound().x/2,sim.getBound().y/2,sim.getBound().z/2));
			lactams.add(l);
		}
		
		sim.setTicker(new BSimTicker() {
			@Override
			public void tick() {
				
				for(LactamaseVesicle v : lactamaseVesicles)
					for(Lactam l : lactams)
						v.interaction(l);
				
				lactams.removeAll(lactamsToRemove);
				lactamsToRemove.clear();
				
				for(ThreatenedBacterium b : threatenedBacteria)
					for(Lactam l : lactams)					
						b.interaction(l);
								
				lactams.removeAll(lactamsToRemove);
				lactamsToRemove.clear();
				
				for(LactamaseBacterium b : lactamaseBacteria)
					for(Lactam l : lactams)					
						b.interaction(l);
								
				lactams.removeAll(lactamsToRemove);
				lactamsToRemove.clear();
	
				for(ThreatenedBacterium b : threatenedBacteria)
					for(LactamaseBacterium a : lactamaseBacteria)
						b.interaction(a);
				
				for(int i = 1; i < threatenedBacteria.size(); i++)
					for(int j = i+1; j < threatenedBacteria.size(); j++)
						threatenedBacteria.get(i).interaction(threatenedBacteria.get(j));
				
				for(int i = 1; i < lactamaseBacteria.size(); i++)
					for(int j = i+1; j < lactamaseBacteria.size(); j++)
						lactamaseBacteria.get(i).interaction(lactamaseBacteria.get(j));
				
				for(Lactam l : lactams) {
					l.action();		
					l.updatePosition();
				}
				
				for(LactamaseBacterium b : lactamaseBacteria) {
					b.action();		
					b.updatePosition();
				}				
				lactamaseBacteria.addAll(lactamaseChildren);
				lactamaseChildren.clear();
				
				for(ThreatenedBacterium b : threatenedBacteria) {
					b.action();		
					b.updatePosition();
				}
				threatenedBacteria.addAll(threatenedChildren);
				threatenedChildren.clear();
				
				for(LactamaseVesicle v : lactamaseVesicles) {
					v.action();		
					v.updatePosition();
				}
								
			}
		});
		
		BSimP3DDrawer drawer = new BSimP3DDrawer(sim, 800,600) {
			@Override
			public void scene(PGraphics3D p3d) {
				for(LactamaseBacterium b : lactamaseBacteria) draw(b, b.vesiculating ? new Color(255,0,255) : new Color(150,0,150));
				for(ThreatenedBacterium b : threatenedBacteria) draw(b, b.dead ? new Color(0,100,0) : Color.GREEN);
				for(LactamaseVesicle v : lactamaseVesicles) draw(v, new Color(255,0,255));
				for(Lactam l : lactams) draw(l, Color.ORANGE);
			}
		};
		sim.setDrawer(drawer);	
		
		/*
		 * Create a new directory for the simulation results
		 */
		String resultsDir = BSimUtils.generateDirectoryPath("./results/");			
		
		@SuppressWarnings("unused")
		BSimLogger logger = new BSimLogger(sim, resultsDir + "l_" + 10*propLactamaseBacteria + "_" + System.currentTimeMillis() + ".csv") {
			@Override
			public void during() {
				double alive = threatenedBacteria.size();
				for (ThreatenedBacterium b : threatenedBacteria)
					if(b.dead) alive--;
				alive = alive/(nThreatenedBacteria);
				write(sim.getFormattedTime()+","+alive);
			}
		};
//		sim.addExporter(logger);
		
		BSimMovExporter movExporter = new BSimMovExporter(sim, drawer, resultsDir + "lactam.mov");	
		movExporter.setDt(0.03);
		sim.addExporter(movExporter);
		
		sim.preview();
	}

}
