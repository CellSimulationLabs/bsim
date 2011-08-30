package BSimParser;

import java.awt.Color;

import bsim.BSim;
import bsim.BSimChemicalField;

public class BSimFromFileChemicalField extends BSimChemicalField{
	
	private Color fieldColor;
	private double alphaPerUnit;
	private double alphaMax;
	
	
	
	public BSimFromFileChemicalField(BSim sim, int[] boxes, double diffusivity, double decayRate, Color newColor, double newAlphaPerUnit, double newAlphaMax) {
		super(sim, boxes, diffusivity, decayRate);
		setColor(newColor);
		setAlphaMax(newAlphaMax);
		setAlphaPerUnit(newAlphaPerUnit);
	}
	
	// Drawing parameters...
	public Color getColor() {
		return fieldColor; 
	}
	public void setColor(Color newColor) { 
		fieldColor = newColor; 
	}
	
	public double getAlphaPerUnit() { 
		return alphaPerUnit; 
	}
	public void setAlphaPerUnit(double newAlphaPerUnit) { 
		alphaPerUnit = newAlphaPerUnit; 
	}
	
	public double getAlphaMax() { 
		return alphaMax; 
	}
	public void setAlphaMax(double newAlphaMax) { 
		alphaMax = newAlphaMax; 
	}
}
