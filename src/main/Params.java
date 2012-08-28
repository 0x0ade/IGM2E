package main;

import java.util.ArrayList;

/**
 * Custom parameter container class . Contains multipile properties . Mainly used for caching .
 * 
 * @see Property
 * @author AngelDE98
 */
public class Params {
	
	public String name;
	
	private ArrayList<Property> props = new ArrayList<Property>();
	
	/**
	 * Creates a dummy parameter / property list .
	 */
	public Params() {
		this("dummy");
	}
	
	/**
	 * Creates a parameter / property list .
	 * @param name List name
	 */
	public Params(String name) {
		this.name = name;
	}
	
	/**
	 * Adds Property p .
	 * @param p Property to handle with .
	 */
	public void add(Property p) {
		props.add(p);
	}
	
	/**
	 * Adds Property p at index index .
	 * @param p Property to handle with .
	 * @param index Index to bind it into .
	 */
	public void add(Property p, int index) {
		props.add(index, p);
	}
	
	/**
	 * Removes Property p .
	 * @param p Property to handle with .
	 */
	public void remove(Property p) {
		props.remove(p);
	}
	
	/**
	 * Removes Property at index index .
	 * @param index The index of the property to remove .
	 */
	public void remove(int index) {
		props.remove(index);
	}
	
	/**
	 * Gets Property at index index .
	 * @param index index
	 * @return Property at index index .
	 */
	public Property get(int index) {
		return props.get(index);
	}
	
	/**
	 * Gets the full Property list .
	 * @return Property list 
	 */
	public ArrayList<Property> getAll() {
		return props;
	}
	
	/**
	 * Sets the full Property list .
	 * @param props List to replace with .
	 */
	public void setAll(ArrayList<Property> props) {
		this.props = props;
	}
	
	/**
	 * Replaces Property p at given index .
	 * @param p Property to replace with .
	 * @param index Index of the Property to be replaced .
	 */
	public void set(Property p, int index) {
		props.set(index, p);
	}
	
	@Override
	public String toString() {
		String s = "";
		
		String ls = "";
		
		for (Property p : props) {
			ls = ls+p.toString()+",,";
		}
		
		s = s+"<"+ls+">"+name+";";
		
		return s;
	}
	
	public static Params fromString(String s) {
		String sps = s.substring(0, s.indexOf(">"));
		String[] saps = sps.split(",,");
		
		String name = s.substring(s.indexOf(">"));
		Params params = new Params(name);
		
		for (String ps : saps) {
			Property p = Property.fromString(ps);
		}
		
		return params;
	}
	
}
