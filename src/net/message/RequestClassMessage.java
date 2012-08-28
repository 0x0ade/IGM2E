package net.message;

import java.nio.BufferOverflowException;

/**
 * This message type has 2 operations : <p>
 * 
 *	- A message containing a string containing a name of a class to be requested and that is available on the other side <p>
 *	
 *	- A message containing a string containing the name and a byte array that were requested and wasn't available on the other side <p>
 *	
 *	Used indirectly with {@link net.NetClassLoader} .
 */
public class RequestClassMessage {
	
	public String clazz = "";
	public byte[] data = new byte[0];
	/**
	 * Used for avoiding buffer overflows while sending .
	 */
	public int offset = 0;
	/**
	 * Used for avoiding buffer overflows while sending .
	 */
	public int length = 0;
	/**
	 * Used for avoiding buffer overflows while sending .
	 */
	public static final int maxlength = 1024;
	
	/**
	 * Serialization constructor
	 */
	public RequestClassMessage() {
		this("");
	}
	
	public RequestClassMessage(String clazz) {
		this(clazz, new byte[0]);
	}
	
	public RequestClassMessage(String clazz, byte[] data) {
		if (data.length > maxlength) {
			throw new IllegalArgumentException("When sending the data with that lenght the connection may close ! Limit it to "+maxlength+" !", new BufferOverflowException());
		}
		this.clazz = clazz;
		this.data = data;
	}
	
}
