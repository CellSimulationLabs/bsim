package bsim.example;

import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3d;

import processing.core.PGraphics3D;
import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.particle.BSimBacterium;
import bsim.particle.BSimParticle;
import bsim.particle.BSimVesicle;

public class BSimLactamExample {
	
	public static void main(String[] args) {

		BSim sim = new BSim();		
		
		class Lactam extends BSimParticle {
			public Lactam(BSim sim, Vector3d position, double radius) {
				super(sim, position, radius);
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
			public void vesiculate() {
				if (vesiculating) {
					double r = vesicleRadius();
					vesicleList.add(new LactamaseVesicle(sim, new Vector3d(position), r));
					setRadiusFromSurfaceArea(getSurfaceArea()-surfaceArea(r));
				}
			}
			
			@Override
			public void replicate() {
				setRadiusFromSurfaceArea(surfaceArea(replicationRadius)/2);
				LactamaseBacterium child = new LactamaseBacterium(sim, new Vector3d(position));	
				child.setRadius(radius);
				child.setRadiusGrowthRate(radiusGrowthRate);
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
			
			public boolean vesiculating() { return vesiculating; }
		}
		
		class ThreatenedBacterium extends BSimBacterium {
			private boolean dead = false;
			
			public ThreatenedBacterium(BSim sim, Vector3d position) {
				super(sim, position);
			}
			@Override
			public void action() {
				if(dead()) brownianForce();
				else super.action();
			}
			
			@Override
			public void replicate() {
				setRadiusFromSurfaceArea(surfaceArea(replicationRadius)/2);
				ThreatenedBacterium child = new ThreatenedBacterium(sim, new Vector3d(position));
				child.setRadius(radius);
				child.setRadiusGrowthRate(radiusGrowthRate);
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
			
			public boolean dead() { return dead; }
		}
		
		
		final Vector<Lactam> lactams = new Vector<Lactam>();
		final Vector<LactamaseVesicle> lactamaseVesicles = new Vector<LactamaseVesicle>();
		final Vector<LactamaseBacterium> lactamaseBacteria = new Vector<LactamaseBacterium>();
		final Vector<LactamaseBacterium> lactamaseChildren = new Vector<LactamaseBacterium>();
		final Vector<ThreatenedBacterium> threatenedBacteria = new Vector<ThreatenedBacterium>();
		final Vector<ThreatenedBacterium> threatenedChildren = new Vector<ThreatenedBacterium>();
		while(lactamaseBacteria.size() < 100) {		
			LactamaseBacterium b = new LactamaseBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
			b.setRadius();
			b.setRadiusGrowthRate();
			b.setChildList(lactamaseChildren);
			b.pVesicle(0.05);
			b.setVesicleList(lactamaseVesicles);
			lactamaseBacteria.add(b);		
		}			
		while(threatenedBacteria.size() < 100) {		
			ThreatenedBacterium b = new ThreatenedBacterium(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z));
			b.setRadius();
			b.setRadiusGrowthRate();
			b.setChildList(threatenedChildren);			
			threatenedBacteria.add(b);		
		}
		while(lactams.size() < 1000) {
			Lactam l = new Lactam(sim, new Vector3d(Math.random()*sim.getBound().x, Math.random()*sim.getBound().y, Math.random()*sim.getBound().z), 1e-3);
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
				for(LactamaseBacterium b : lactamaseBacteria) draw(b, b.vesiculating() ? Color.PINK : Color.YELLOW );
				for(ThreatenedBacterium b : threatenedBacteria) draw(b, b.dead() ? Color.RED : Color.GREEN);
				for(LactamaseVesicle v : lactamaseVesicles) draw(v, Color.RED);
				for(Lactam l : lactams) draw(l, Color.RED);
			}
		};
		sim.setDrawer(drawer);	
		
		sim.preview();
	}

}
