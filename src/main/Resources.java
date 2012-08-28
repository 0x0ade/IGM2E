package main;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.newdawn.slick.util.ResourceLocation;

import main.Options.EnumOS;

public final class Resources {
	
	public static class Fonts {
		
		public static String externalPath = getExternalPath(Fonts.class);
		
		public static Font ubuntul = utilizeFont("/res/ui/ubuntufontfamily/Ubuntu-L.ttf", false);
		public static Font ubuntub = utilizeFont("/res/ui/ubuntufontfamily/Ubuntu-B.ttf", false);
		
		public static Font utilizeFontExternally(String sarg) {
			Font alternate = new Font("Tahoma", 0, 12);
			String sa = sarg;
			try {
				String s = "";
				int type = -1;
				if (sa.toLowerCase().endsWith(".ttf")) type = Font.TRUETYPE_FONT;
				s = sa;
				Font f = Font.createFont(type, new File(s));
				f = f.deriveFont(12f);
				return f;
			} catch (Throwable e) {
				return alternate;
			}
		}
		
		public static Font utilizeFont(String sarg, boolean absolute) {
			if (!absolute) return utilizeFontExternallyInit(sarg);
			else return utilizeFontExternally(sarg);
		}
		
		public static Font utilizeFontExternallyInit(String sarg) {
			Font alternate = new Font("Tahoma", 0, 12);
			String sa = sarg;
			try {
				if (RES.class == null) {
					System.err.println("FUU - No res class !");
					return alternate;
				}
				URL u = RES.class.getResource(sa);
				if (u == null) {
					System.err.println("FUUUU - No resource !");
					System.err.println(sa);
					return alternate;
				}
				String path = externalPath;
				String fullpath = new File(externalPath, sarg).getPath();
				File pathf = new File(path);
				if (pathf.exists() && !pathf.isDirectory()) {
					pathf.delete();
				}
				if (!pathf.exists()) {
					pathf.mkdirs();
				}
				boolean fail = !extractFromJar();
				if (extracted) fail = false;
				if (fail) return alternate;
				return utilizeFont(fullpath, true);
			} catch (Throwable e) {
				e.printStackTrace();
				return alternate;
			}
		}
		
	}
	
	private static boolean extracted = false;
	private static ArrayList<String> externallyLoadingClasses = new ArrayList<String>();
	
	
	public static String getExternalPath(Class<?> c) {
		String s = "";
		s = Options.getTempDir().getAbsolutePath();
		if (filesInJar() && !(c.isInstance(Resources.class))) externallyLoadingClasses.add(c.getSimpleName());
		return s;
	}
	
	public static boolean filesInJar() {
		return (IGM2E.class.getResource("/res/ui/cursor.png").toString()).contains(".jar!");
	}
	
	public static String getJarPath() {
		String path = RES.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath = "";
		try {
			decodedPath = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decodedPath;
	}
	
	public static String getJarFolderPath() {
		return getJarPath().substring(0, getJarPath().lastIndexOf(File.separator) + 1);
	}
	
	/**
	 * Extracts files from this jar , if no jar then just copy .
	 * @return Failed or Completed , Completed when completed already 
	 */
	public static boolean extractFromJar() {
		if (extracted) return true;
		
		String jar = getJarPath();
		String expath = getExternalPath(Resources.class);
		String md5 = "";
		
		File jarf = new File(jar);
		File expathf = new File(expath);
		File md5f = new File(expathf.getParentFile(), "md5res.txt");
		if (expathf.exists() && !expathf.isDirectory()) {
			expathf.delete();
		}
		if (!expathf.exists()) {
			expathf.mkdirs();
		}
		jarf.setReadable(true);
		expathf.setReadable(true);
		expathf.setWritable(true);
		boolean state = false;
		if (!jarf.isDirectory() || filesInJar()) {
			try {
				Zipper.unzipFolder(jarf, expathf);
				state = true;
			} catch (Throwable t) {
				state = false;
			}
		} else {
			state = copyContentsOfFolder(jarf, expathf);
		}
		extracted = state;
		if (state = true) {
			try {
			    md5 = getMD5FolderChecksum(expathf, true);
			} catch (Exception e) {
			}
			saveTextFile(md5, md5f);
		}
		return state;
	}
	
	public static void copyLibs() {
		if (!extracted) extractFromJar();
		File binDir = new File(Options.getDir(), "bin");
		File natDir = new File(binDir, "natives");
		File tmpDir = new File(getExternalPath(Resources.class));
		File tlibsDir = new File(tmpDir, "lib");
		File tnatmDir = new File(tmpDir, "nat");
		File tnatDir = tnatmDir;
		EnumOS os = Options.getOs();
		if (os.equals(EnumOS.windows)) tnatDir = new File(tnatmDir, "win");
		if (os.equals(EnumOS.macos)) tnatDir = new File(tnatmDir, "mac");
		if (os.equals(EnumOS.linux)) tnatDir = new File(tnatmDir, "lin");
		if (os.equals(EnumOS.solaris)) tnatDir = new File(tnatmDir, "sol");
		
		if (binDir.exists() && !binDir.isDirectory()) binDir.delete();
		binDir.mkdirs();
		if (natDir.exists() && !natDir.isDirectory()) natDir.delete();
		natDir.mkdirs();
		
		copyContentsOfFolder(tlibsDir, binDir);
		copyContentsOfFolder(tnatDir, natDir);
	}
	
	public static boolean copyContentsOfFolder(File fdir, File fdest) {
		try {
			int bufflength = 2048;
			if (!fdest.exists()) fdest.mkdirs();
			if (!fdest.isDirectory()) return false;
			for (File f : fdir.listFiles()) {
				if (f.getName().startsWith("META-INF")) continue;
				if (f.isDirectory()) {
					File f2 = new File(fdest, f.getName());
					f2.mkdirs();
					if (!copyContentsOfFolder(f, f2)) return false;
					continue;
				}
				if (new File(fdest, f.getName()).exists()) continue;
		        InputStream in = new FileInputStream(f);
		        OutputStream out = new FileOutputStream(new File(fdest, f.getName()));
		        // Copy the bytes from instream to outstream
		        byte[] buf = new byte[bufflength];
		        int len;
		        while ((len = in.read(buf)) > 0) {
		        	out.write(buf, 0, len);
		        }
		        in.close();
		        out.close();
			}
			return true;
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}
	
    public static byte[] createChecksum(File f) throws Exception {
    	InputStream fis = new FileInputStream(f);
    	
    	byte[] buffer = new byte[1024];
    	MessageDigest complete = MessageDigest.getInstance("MD5");
    	int numRead;
    	
    	do {
    		numRead = fis.read(buffer);
    		if (numRead > 0) {
    			complete.update(buffer, 0, numRead);
    		}
    	} while (numRead != -1);
    	
    	fis.close();
    	return complete.digest();
    }

    public static String getMD5Checksum(File f) throws Exception {
    	if (f.isDirectory()) return getMD5FolderChecksum(f, true);
    	byte[] b = createChecksum(f);
    	String result = "";
    	
    	for (int i = 0; i < b.length; i++) {
    		result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
    	}
    	
    	return result;
    }
    
    public static String getMD5FolderChecksum(File f, boolean header) throws Exception {
    	String result = "";
    	if (header) result = "#MD5 List of folder "+f.getName()+":\n";
    	
    	for (File f2 : f.listFiles()) {
    		if (f2.isDirectory()) {
    			result += getMD5FolderChecksum(f2, false);
    			continue;
    		}
    		result += f2.getParentFile().getName()+"/"+f2.getName()+" = "+getMD5Checksum(f2)+"\n";
    	}
    	
    	return result;
    }
    
    public static boolean getBoolean(String s)
    {
        return Boolean.parseBoolean(s);
    }
    
    public static String loadTextFile(File f)  {
    	try {
            if(!f.exists())
            {
            	f.getParentFile().mkdirs();
            	f.createNewFile();
                return "";
            }
            String s = "";
            BufferedReader bufferedreader = new BufferedReader(new FileReader(f));
            for(String l = ""; (l = bufferedreader.readLine()) != null;)
            {
                s += l+"\n";
            }
            bufferedreader.close();
            return s;
        } catch(Exception e) {
        	return "";
        }
    }
    
    public static boolean saveTextFile(String s, File f) {
        try {
        	if (f.exists()) {
        		f.delete();
        	}
        	if (!f.exists()) {
        		f.getParentFile().mkdirs();
        		f.createNewFile();
        	}
            PrintWriter printwriter = new PrintWriter(new FileWriter(f));
            for (String l : s.split("\n")) {
            	printwriter.println(l);
            }
            printwriter.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static File urlToFile(URL url) throws IllegalArgumentException {
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            try {
                uri = new URI(url.getProtocol(), url.getUserInfo(), url
                        .getHost(), url.getPort(), url.getPath(), url
                        .getQuery(), url.getRef());
            } catch (URISyntaxException e1) {
                throw new IllegalArgumentException("invalid URL: " + url);
            }
        }
        return new File(uri);
    }
    
    public static URL fileToUrl(File f) throws MalformedURLException {
        return f.toURI().toURL();
    }
    
	public static File getResource(String filename) {
		try {
			return urlToFile(RES.class.getResource(filename));
		} catch (Exception e1) {
			e1.printStackTrace();
			try {
				return new File(getExternalPath(Resources.class), filename);
			} catch (Exception e2) {
				e2.printStackTrace();
				return null;
			}
		}
	}

	public static InputStream getResourceAsStream(String filename) {
		try {
			return RES.class.getResourceAsStream(filename);
		} catch (Exception e1) {
			e1.printStackTrace();
			try {
				return new FileInputStream(new File(getExternalPath(Resources.class), filename));
			} catch (Exception e2) {
				e2.printStackTrace();
				return null;
			}
		}
	}
	
	public static File getExternalResource(String filename) {
		try {
			return new File(getExternalPath(Resources.class), filename);
		} catch (Exception e2) {
			e2.printStackTrace();
			return null;
		}
	}
	
	public static ResourceLocation getResourceLocation() {
		ResourceLocation rl = new ResourceLocation() {

			@Override
			public InputStream getResourceAsStream(String ref) {
				return Resources.getResourceAsStream(ref);
			}

			@Override
			public URL getResource(String ref) {
				try {
					return Resources.getResource(ref).toURI().toURL();
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return null;
				}
			}
			
		};
		return rl;
	}
	
}
