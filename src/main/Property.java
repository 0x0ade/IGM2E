package main;

/**
 * Property , made of string name and value . Used mainly in Params .
 * 
 * @see Params
 * @author AngelDE98
 */
public class Property {
	
	public String name;
	public String value;
	
	/**
	 * Creates a dummy property .
	 */
	public Property() {
		this("dummy");
	}
	
	/**
	 * Creates a property with empty value .
	 * @param name name
	 */
	public Property(String name) {
		this(name, "");
	}
	
	/**
	 * Creates a property with given name and value .
	 * @param name name
	 * @param value value
	 */
	public Property(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public String toString() {
		String s = "";
		
		s = s+name+";"+s+value+";";
		
		return s;
	}
	
	public static Property fromString(String s) {
		String[] sa = s.split(";");
		Property p = new Property(sa[0], sa[1]);
		return p;
	}
}
