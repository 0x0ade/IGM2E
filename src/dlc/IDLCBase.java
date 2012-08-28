package dlc;

import java.util.ArrayList;

/**
 * Interface for external / extending classes ( Plugins , Mods , Features ) .
 *
 */
public interface IDLCBase {
	
	/**
	 * DLC Name
	 * @return DLC Name
	 */
	public String name();
	
	/**
	 * DLC Version
	 * @return DLC Version
	 */
	public String version();
	
	/**
	 * Can this DLC run in this IGM2E environment ?
	 * @param name Game Name
	 * @param build Game Build Number
	 * @param buildEngine IGM2E Build Number
	 * @param loadedlist List of already loaded ExClasses
	 * @return "Yes" if can run , "Yes, ..." if needs notification and "No" or "No, ..." if can not run .
	 */
	public String canRun(String name, String build, String buildEngine, ArrayList<IDLCBase> loadedlist);
	
	/**
	 * Inits the DLC , should not start any tick-like threads because 
	 * DLCUtil gets ticked from Main and ExClassUtil ticks every ExClass from it's list .
	 */
	public void init();
	
	/**
	 * Ticks the DLC ( other than Tickable )
	 */
	public void tick();
	
	/**
	 * Stops this DLC internally and interrupts threads for safe shutdown , 
	 * externally it gets removed from ExClassUtil's list when ejected 
	 */
	public void stop();
	
}
