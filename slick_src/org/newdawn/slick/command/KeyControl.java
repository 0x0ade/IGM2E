package org.newdawn.slick.command;

import java.util.HashMap;

/**
 * A control relating to a command indicate that it should be fired when a specific key is pressed
 * or released.
 * 
 * @author joverton
 */
public class KeyControl implements Control {
	/** The key code that needs to be pressed */
    protected int keycode;
    
	protected int iid = 0;
	public static final HashMap<Integer, Integer> lastused = new HashMap<Integer, Integer>();
    
    /**
     * Create a new control that caused an command to be fired on a key pressed/released
     * 
     * @param keycode The code of the key that causes the command
     */
	public KeyControl(int keycode) {
		this(keycode, getLatestFor(keycode));
	}
	
	public KeyControl(int keycode, int iid) {
		this.keycode = keycode;
		this.iid = iid;
		addLatestFor(keycode, iid);
	}
	
	protected final static int getLatestFor(int keycode) {
		int iid = 0;
		if (!lastused.containsKey(keycode)) {
			return iid;
		}
		iid = lastused.get(keycode)+1;
		return iid;
	}
	
	protected final static void addLatestFor(int keycode, int iid) {
		if (!lastused.containsKey(keycode)) {
			lastused.put(keycode, iid);
			return;
		}
		lastused.put(keycode, Math.max(lastused.get(keycode), iid));
	}

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (o instanceof KeyControl) {
        	return ((KeyControl)o).keycode == keycode && ((KeyControl)o).iid == iid;
        }
        
        return false;
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return keycode;
    }
}
