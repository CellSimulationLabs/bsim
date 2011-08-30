package BSimParser;

import javax.vecmath.Vector3d;
import java.awt.Color;

import bsim.BSim;
import bsim.particle.BSimParticle;

public class BSimFromFileParticle extends BSimParticle {

	private Color partCol;
	
	public BSimFromFileParticle(BSim sim, Vector3d position, double radius, Color newColor) {
		super(sim, position, radius);
		setColor(newColor);
	}
	
	public Color getColor() {
		return partCol;
	}
	
	public void setColor(Color newColor) {
		partCol = newColor;
	}
}
