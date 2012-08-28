package net.message;

import java.io.Serializable;

/**
 *	Message that can be sent over a network of NetClients and one or multipile NetHosts .
 *	
 */
public abstract class Message implements Serializable {
	public String message;
	
	/**
	 * Serialization constructor
	 */
	public Message() {
	}
}
