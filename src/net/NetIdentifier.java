package net;

import java.io.Serializable;

/**
 *	Simple identifying class used over the net implementation .
 *	
 */
public class NetIdentifier implements Serializable {
	
	public int id;
	public String name;
	
	public NetIdentifier() {
		id = -1;
		name = "";
	}
	
	public NetIdentifier(int id, String name) {
		if (id < 0) {
			throw new IllegalArgumentException("id < 0 !");
		}
		if (name.trim().isEmpty()) {
			throw new IllegalArgumentException("name is empty !");
		}
		this.id = id;
		this.name = name;
	}
	
}
