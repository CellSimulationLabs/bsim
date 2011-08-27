package BSimParser;

/**
 * This class embodies a single attribute value pair. It is immutable and so needs to be recreated if
 * any attribute name or the value changes.
 */

class BSimParserAttributeValue {
	
	String  attribute;
	boolean valIsString;
	String  valStr;
	double  valNum;
	
	BSimParserAttributeValue (String newAttribute, String newValue, boolean isString) {
		attribute = newAttribute;
		valIsString = isString;
		if (valIsString) {
			valStr = newValue;
		}
		else {
			valNum = BSimParser.parseToDouble(newValue);
		}
	}
	
	public String getAttribute () { return attribute; }
	
	public int getIntVal () {
		if (valIsString) {
			System.err.println("Attempted to get numeric value when it was a string for attribute:" + attribute);
			return 0;
		}
		return (int)valNum;
	}
	
	public double getDoubleVal () {
		if (valIsString) {
			System.err.println("Attempted to get numeric value when it was a string for attribute:" + attribute);
			return 0;
		}
		return valNum;
	}
	
	public String getStringVal () {
		if (!valIsString) {
			System.err.println("Attempted to get string value when it was a number for attribute:" + attribute);
			return "";
		}
		return valStr;
	}	
}
