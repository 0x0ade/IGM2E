package etc;

import java.io.InputStream;

/**
 *	This class represents a file stored somewhere, pre-read.
 */
public final class StoredFile {
	
	private String path = "";
	private byte[] raw;
	
	public StoredFile(String path, byte[] raw) {
		this.path = path;
		this.raw = raw;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getName() {
		return path.substring(path.lastIndexOf("/"));
	}
	
	public byte[] getData() {
		//Copy data to prevent writing on already stored file
		byte[] b = new byte[raw.length];
		for (int i = 0; i < b.length; i++) {
			b[i] = raw[i];
		}
		return b;
	}
	
	public InputStream getAsStream() {
		//Copy data to prevent writing on already stored file by (impossible) reverse-engineering
		byte[] b = new byte[raw.length];
		for (int i = 0; i < b.length; i++) {
			b[i] = raw[i];
		}
		InputStream is = new StoredInputStream(b);
		return is;
	}
	
}
