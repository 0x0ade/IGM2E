package access;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Class for gaining access to hidden fields , constructors or methods . Also for using these . <p>
 * 
 * Before returning any <code>Field</code> , <code>Method</code> or <code>Constructor</code> , <code>.setAccessible(true)</code> is being applicated on these .
 */
public final class AccessUtil {
	public Class clazz;
	public Object instance;
	
	public AccessUtil(Class clazz) {
		this(clazz, null);
	}
	
	public AccessUtil(Object instance) {
		this(instance.getClass(), instance);
	}
	
	public AccessUtil(Class clazz, Object instance) {
		this.clazz = clazz;
		this.instance = instance;
	}
	
	/**
	 * Get all methods avaible in <code>Class clazz</code> .
	 * @return List of methods
	 * @see Class#getDeclaredMethods
	 */
	public Method[] getAllMethods() {
		Method[] methods = clazz.getDeclaredMethods();
		Method.setAccessible(methods, true);
		return methods;
	}
	
	/**
	 * Get the method matching name and parameter types given .
	 * @param name Method name 
	 * @param parameterTypes Method parameter types 
	 * @return Method , or null if any problem appeared .
	 * @see Class#getDeclaredMethod(String, Class...)
	 */
	public Method getMethod(String name, Class... parameterTypes) {
		try {
			Method method = clazz.getDeclaredMethod(name, parameterTypes);
			method.setAccessible(true);
			return method;
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (NullPointerException e) {
		}
		return null;
	}
	
	/**
	 * Invokes the method matching name and parameters given .
	 * @param asstatic Invoke as static or as instance given in <code>Object instance</code>
	 * @param name Method name 
	 * @param parameters Method parameters
	 * @return The returned object from the invoked method - or null if void .
	 * @see #getMethod(String, Class...)
	 * @see Method#invoke(Object, Object...)
	 */
	public Object invokeMethod(boolean asstatic, String name, Object... args) {
		try {
			Class[] types = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				types[i] = args[i].getClass();
			}
			Method method = getMethod(name, types);
			return method.invoke(asstatic?null:instance, args);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (NullPointerException e) {
		} catch (ExceptionInInitializerError e) {
		}
		return null;
	}
	
	/**
	 * Get all fields avaible in <code>Class clazz</code> .
	 * @return List of fields
	 * @see Class#getDeclaredFields
	 */
	public Field[] getAllFields() {
		Field[] fields = clazz.getDeclaredFields();
		Field.setAccessible(fields, true);
		return fields;
	}
	
	/**
	 * Get the field matching name .
	 * @param name Field name 
	 * @return Field , or null if any problem appeared .
	 * @see Class#getDeclaredField(String)
	 */
	public Field getField(String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		} catch (NullPointerException e) {
		}
		return null;
	}
	
	/**
	 * Gets the field content of the field matching name given .
	 * @param asstatic Get value as static or as instance given in <code>Object instance</code>
	 * @param name Field name 
	 * @return The field content
	 * @see #getField(String, Class...)
	 * @see Field#get(Object)
	 */
	public Object getFieldContent(boolean asstatic, String name) {
		Field field = getField(name);
		try {
			return field.get(asstatic?null:instance);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (NullPointerException e) {
		} catch (ExceptionInInitializerError e) {
		}
		return null;
	}
	
	/**
	 * Sets the field content of the field matching name given .
	 * @param asstatic Set value as static or as instance given in <code>Object instance</code>
	 * @param name Field name 
	 * @see #getField(String, Class...)
	 * @see Field#set(Object, Object)
	 */
	public void setFieldContent(boolean asstatic, String name, Object value) {
		Field field = getField(name);
		try {
			field.set(asstatic?null:instance, value);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (NullPointerException e) {
		} catch (ExceptionInInitializerError e) {
		}
	}
	
	/**
	 * Get all constructors avaible in <code>Class clazz</code> .
	 * @return List of constructors
	 * @see Class#getDeclaredConstructors
	 */
	public Constructor[] getAllConstructors() {
		Constructor[] constructors = clazz.getDeclaredConstructors();
		Constructor.setAccessible(constructors, true);
		return constructors;
	}
	
	/**
	 * Get the constructor matching parameter types given .
	 * @param name Constructor name 
	 * @param parameterTypes Constructor parameter types 
	 * @return Constructor , or null if any problem appeared .
	 * @see Class#getDeclaredConstructor(Class...)
	 */
	public Constructor getConstructor(Class... parameterTypes) {
		try {
			Constructor constructor = clazz.getDeclaredConstructor(parameterTypes);
			constructor.setAccessible(true);
			return constructor;
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (NullPointerException e) {
		}
		return null;
	}
	
}
