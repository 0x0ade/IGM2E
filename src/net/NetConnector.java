package net;

import java.util.ArrayList;

/**
 *	A NetConnector is something like a NetClient or NetHost ... this interface just has some important functions for registering classes .
 *	
 */
public interface NetConnector {
	
	/**
	 * Get all registered classes .
	 * @return ArrayList with registered classes
	 */
	public ArrayList<Class> getRegisteredClasses();
	
	/**
	 * Registers class clazz and adds it into the list of registered classes .
	 * @param clazz Class to register
	 */
	public void register(Class clazz);
	
	/**
	 * Registers class clazz and adds it into the list of registered classes .
	 * @param clazz Class to register
	 */
	public void register(String clazz);
	
	/**
	 * Registers class clazz , adds it into the list of registered classes and sends an registering message to all connections .
	 * @param clazz Class to register
	 * @param everyone true if everyone needs to register this class , false otherwise ( only checked by NetHost )
	 */
	public void registerAndSend(String clazz, boolean everyone);
	
	/**
	 * Registers class clazz , adds it into the list of registered classes and sends an registering message to all connections excepting that one with the given id .
	 * @param clazz Class to register
	 * @param id Connection ID to ignore
	 */
	public void registerBy(String clazz, int id);
	
	/**
	 * Sends a new RequestClassMessage and waits until it got respond .
	 * @param name Name of class to find
	 * @return Array of bytes that equals the class data
	 */
	public byte[] askForClassData(String name);
	
}
