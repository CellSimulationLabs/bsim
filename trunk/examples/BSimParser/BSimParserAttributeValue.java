package BSimParser;

/**
 * This class embodies a single attribute value pair. It is immutable and so needs to be recreated if
 * any attribute name or the value changes.
 */

class BSimParserAttributeValue {
	
	String  attribute;
	String  valStr;
	
	BSimParserAttributeValue (String newAttribute, String newValue) {
		attribute = newAttribute;
		valStr = newValue;
	}
	
	public String getAttribute () { return attribute; }
	
	public int getIntVal () { return BSimParser.parseToInt(valStr); }
	
	public double getDoubleVal () { return BSimParser.parseToDouble(valStr); }
	
	public String getStringVal () { return valStr; }	
}
