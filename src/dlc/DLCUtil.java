package dlc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import main.IGM2E;

/**
 * DLCUtil , Utility for loading external classes ( Mods , Plugins , Addons ) . 
 *
 */
public class DLCUtil {
	
	private static ArrayList<IDLCBase> dlcs = new ArrayList<IDLCBase>();
	
	public static ArrayList<IDLCBase> getExClasses() {
		return dlcs;
	}
	

	public static void injectDLCs(File dir) {
		for (File f : dir.listFiles()) {
			String name = f.getName();
			if (name.toLowerCase().endsWith(".dlc")) {
				injectDLC(f);
			}
		}
	}
	
	public static void injectDLC(File f) {
		try {
			System.out.println("Processing zip: " + f.getName());
			
			String name = "";
	    	FileInputStream fis = new FileInputStream(f);
	    	ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	    	ZipEntry entry;
	    	while ((entry = zis.getNextEntry()) != null && name.equals("")) {
	    		if (entry.getName().contains("exc_") && !entry.getName().contains("$") && !entry.isDirectory()) name = entry.getName();
	    	}
	    	String className = name.replaceAll("/", ".").replaceAll("\\\\", ".").replaceAll(".class", "");
			
			final URLClassLoader loader = URLClassLoader.newInstance(new URL[] {f.toURI().toURL()});
			System.out.println("Starting injecting class "+className+" in "+f);
			Object oc = loader.loadClass(className);
			Class cl = (Class) oc;
			Object i = cl.newInstance();
			Class c = i.getClass();
			if (!(i instanceof IDLCBase)) {
				System.err.println("Class must extend IDLCBase !");
				zis.close();
				return;
			}
			IDLCBase iexc = (IDLCBase) i;
			name = iexc.name();
			String version = iexc.version();
			String canrun = iexc.canRun(IGM2E.name, IGM2E.build, dlcs);
			System.out.println(name+" "+version+"\nCan this DLC run ?");
			System.out.println(canrun);
			if (canrun.startsWith("No")) {
				zis.close();
				return;
			}
			inject(iexc);
			zis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void inject(IDLCBase c) {
		dlcs.add(c);
		c.init();
	}
	
	public static void tick() {
		for (IDLCBase c : dlcs) {
			c.tick();
		}
	}
	
	public static void stopAll() {
		for (IDLCBase c : dlcs) {
			c.stop();
			dlcs.remove(c);
		}
	}
	
	public static void eject(int i) {
		dlcs.get(i).stop();
		dlcs.remove(i);
	}
	
	public static void eject(IDLCBase c) {
		c.stop();
		dlcs.remove(c);
	}
	
}
