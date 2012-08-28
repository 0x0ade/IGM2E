package net.message;

import java.util.ArrayList;

/**
 *	A list of names of connections ready to be sendt .
 *	
 */
public class NameList extends Message {
	public ArrayList<String> names = new ArrayList<String>();
	
	/**
	 * Serialization constructor
	 */
	public NameList() {
	}
	
	public NameList(ArrayList<String> names) {
		this.names = names;
	}
}
