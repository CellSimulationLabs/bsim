package BSimParser;

import javax.vecmath.Vector3d;

import bsim.BSim;
import bsim.BSimChemicalField;
import bsim.particle.BSimBacterium;

class BSimFromFileBacterium extends BSimBacterium {
	
	// Names of chemical fields that this bacterium interacts with
	private String chemotaxisGoalField = "";
	private String inputChemicalField = "";
	private String outputChemicalField = "";
	
	// Chemical fields that this bacterium interacts with
	private BSimChemicalField inputField = null;
	private BSimChemicalField outputField = null;
	
	// Chemical transfer rates
	private double chemicalInRate = 0;
	private double chemicalOutRate = 0;
	
	public BSimFromFileBacterium(BSim sim, Vector3d position) {
		super(sim, position);
	}
	
	@Override
	public void action(){
		super.action();
		
		inputChemical();
		outputChemical();
	}
	
	/** Input chemical quantity from the external field */
	public void inputChemical(){
		inputField.addQuantity(position, -chemicalInRate);
	}
	
	/** Output chemical quantity to the external field */
	public void outputChemical(){
		outputField.addQuantity(position, chemicalOutRate);
	}
			
	/** Set this bacterium's input chemical field */
	public void setInput(BSimChemicalField field) { inputField = field; }
	
	/** Set this bacterium's output chemical field */
	public void setOutput(BSimChemicalField field) { outputField = field; }

	/** Set this bacterium's inward chemical transfer rate */
	public void setInputRate(double rate) { chemicalInRate = rate; }

	/** Set this bacterium's outward chemical transfer rate */
	public void setOutputRate(double rate) { chemicalOutRate = rate; }
	
	// Set the names of the chemical fields with which this bacterium interacts
	public void setChemotaxisGoalFieldName (String name) { chemotaxisGoalField = name; }
	public void setChemicalInputName (String name) { inputChemicalField = name; }
	public void setChemicalOutputName (String name) { outputChemicalField = name; }
	
	public String getChemotaxisGoalFieldName () { return chemotaxisGoalField; }
	public String getChemicalInputName () {return inputChemicalField; }
	public String getChemicalOutputName () {return outputChemicalField; }
}