package org.newdawn.slick.command;

import java.util.HashMap;

/**
 * A control describing input provided from a controller. This allows controls to be
 * mapped to game pad inputs.
 * 
 * @author joverton
 */
abstract class ControllerControl implements Control {
    /** Indicates a button was pressed */
	protected static final int BUTTON_EVENT = 0;
    /** Indicates left was pressed */
    protected static final int LEFT_EVENT = 1;
    /** Indicates right was pressed */
    protected static final int RIGHT_EVENT = 2;
    /** Indicates up was pressed */
    protected static final int UP_EVENT = 3;
    /** Indicates down was pressed */
    protected static final int DOWN_EVENT = 4;
    
    /** The type of event we're looking for */
    private int event;
    /** The index of the button we're waiting for */
    private int button;
    /** The index of the controller we're waiting on */
    private int controllerNumber;
    
	protected int iid = 0;
	public static final HashMap<TriData, Integer> lastused = new HashMap<TriData, Integer>();
    
	/**
     * Create a new controller control
     * 
     * @param controllerNumber The index of the controller to react to
     * @param event The event to react to
     * @param button The button index to react to on a BUTTON event
     */
    protected ControllerControl(int controllerNumber, int event, int button) {
       this(controllerNumber, event, button, getLatestFor(controllerNumber, event, button));
    }
	
    /**
     * Create a new controller control
     * 
     * @param controllerNumber The index of the controller to react to
     * @param event The event to react to
     * @param button The button index to react to on a BUTTON event
     */
    protected ControllerControl(int controllerNumber, int event, int button, int iid) {
        this.event = event;
        this.button = button;
        this.controllerNumber = controllerNumber;
        this.iid = iid;
        addLatestFor(controllerNumber, event, button, iid);
    }
    
    public final static class TriData {
    	public int a;
    	public int b;
    	public int c;
    	public TriData(int a, int b, int c) {
    		this.a = a;
    		this.b = b;
    		this.c = c;
    	}
    	@Override
    	public boolean equals(Object o) {
    		if (o == null) return false;
    		if (!(o instanceof TriData)) return false;
    		TriData td = (TriData) o;
    		return td.a == a && td.b == b && td.c == c;
    	}
    	@Override
    	public int hashCode() {
    		return (a*b)+c*c;
    	}
    }
    
	protected final static int getLatestFor(int controllerNumber, int event, int button) {
		int iid = 0;
		if (!lastused.containsKey(new TriData(controllerNumber, event, button))) {
			return iid;
		}
		iid = lastused.get(new TriData(controllerNumber, event, button))+1;
		return iid;
	}
	
	protected final static void addLatestFor(int controllerNumber, int event, int button, int iid) {
		if (!lastused.containsKey(new TriData(controllerNumber, event, button))) {
			lastused.put(new TriData(controllerNumber, event, button), iid);
			return;
		}
		lastused.put(new TriData(controllerNumber, event, button), Math.max(lastused.get(new TriData(controllerNumber, event, button)), iid));
	}
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if(o == null)
            return false;
        if(!(o instanceof ControllerControl))
            return false;
        
        ControllerControl c = (ControllerControl)o;
        return c.controllerNumber == controllerNumber && c.event == event && c.button == button && c.iid == iid;
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return event + button + controllerNumber;
    }
}
