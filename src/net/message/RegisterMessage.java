package net.message;

/**
 *	A message containing a string containing a name of a class to be registered and that is avaible on the other side . <p>
 *	Use {@link net.NetClassLoader} if you need to load a class over the network .
 */
public class RegisterMessage {
	
	public String clazz;
	public boolean everyone;
	
	/**
	 * Serialization constructor
	 */
	public RegisterMessage() {
		this("", false);
	}
	
	public RegisterMessage(String clazz, boolean everyone) {
		this.clazz = clazz;
		this.everyone = everyone;
	}
	
}
