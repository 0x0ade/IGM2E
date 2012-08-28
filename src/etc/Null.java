package etc;

import access.AccessUtil;

/**
 * Almost empty class representing null .
 *
 */
public final class Null {
	
    /**
     * The Class object representing the pseudo-type corresponding to
     * the keyword null.
     */
    public static final Class<Null> TYPE = type();
    
    private static final Class<Null> type() {
    	Class clazz = null;
    	AccessUtil au = new AccessUtil(Class.class);
    	clazz = (Class) au.invokeMethod(true, "getPrimitiveClass", "null");
    	return clazz;
    }
	
}
