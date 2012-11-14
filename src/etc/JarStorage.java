package etc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import main.IGM2E;

/**
 *	Stores all the files in this .jar as StoredFiles.
 */
public final class JarStorage {
	
	public final static ArrayList<StoredFile> storage = new ArrayList<StoredFile>();
	
	static {
		try {
			storeAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void storeAll() throws IOException {
		String decodedPath = ResourceUtil.getJarPath();
		System.out.println(decodedPath);
		System.out.println();
		File dcheckf = new File(decodedPath);
		if (dcheckf.isDirectory()) {
			//Folders are handled another way...
			return;
		}
		JarFile jarFile = new JarFile(decodedPath);
	
		Enumeration<JarEntry> entries = jarFile.entries();
		
		while (entries.hasMoreElements()) {
		    JarEntry jarEntry = (JarEntry) entries.nextElement();
	
		    if (jarEntry.getName().endsWith(".jar")) {
		        JarInputStream jarIS = new JarInputStream(jarFile
		                .getInputStream(jarEntry));
	
		        // iterate the entries, copying the contents of each nested file 
		        // to the OutputStream
		        JarEntry innerEntry = jarIS.getNextJarEntry();
		        
		        while (innerEntry != null) {
		        	ArrayList<Byte> dataal = new ArrayList<Byte>();
		        	for (int i = 0; jarIS.available() > 0; i++) {
		        		dataal.add((byte) jarIS.read());
		        	}
		        	Byte[] databb = dataal.toArray(new Byte[0]);
		        	byte[] data = new byte[databb.length];
		        	for (int i = 0; i < databb.length; i++) {
		        		data[i] = databb[i];
		        	}
		            StoredFile sf = new StoredFile(innerEntry.getName(), data);
		            storage.add(sf);
		            innerEntry = jarIS.getNextJarEntry();
		        }
		        jarIS.close();
		    }
	    }
	}
	
}
