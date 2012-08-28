package main;

import java.security.PrivilegedAction;

public class SetSystemPropAction implements PrivilegedAction {
	
	private String prop;
	private String value;
	
	public SetSystemPropAction(String prop, String value) {
		this.prop = prop;
		this.value = value;
	}

	@Override
	public Object run() {
		return System.setProperty(prop, value);
	}
	
}
