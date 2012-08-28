package net.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.util.ArrayList;

import net.NetUtil;

/**
 *	Special message type for transfering messages over the network . Based on RequestClasMessage . <p>
 *	
 *	Constructors are protected because you create a FileMessage[] with {@link #convert} .
 */
public class FileMessage {
	
	
	public File file;
	public byte[] data = new byte[0];
	/**
	 * Used for avoiding buffer overflows while sending .
	 */
	public int offset = 0;
	/**
	 * Used for avoiding buffer overflows while sending .
	 */
	public int length = 0;
	/**
	 * Used for avoiding buffer overflows while sending .
	 */
	public static final int maxlength = 1024;
	
	/**
	 * Serialization constructor
	 */
	protected FileMessage() {
	}
	
	protected FileMessage(File file) {
		this(file, new byte[0]);
	}
	
	protected FileMessage(File file, byte[] data) {
		if (data.length > maxlength) {
			throw new IllegalArgumentException("When sending the data with that lenght the connection may close ! Limit it to "+maxlength+" !", new BufferOverflowException());
		}
		this.file = file;
		this.data = data;
	}
	
	/**
	 * Creates an array of file messages from the file f .
	 * @param f File to send
	 * @return FileMessage[] containing byte[]s containing the file f
	 * @throws IOException something inside happened ... wow ... ask Java why 
	 */
	public static final FileMessage[] convert(File f) throws IOException {
		ArrayList<FileMessage> fma = new ArrayList<FileMessage>();
		
		ArrayList<Byte> ba = new ArrayList<Byte>();
		FileInputStream fis = new FileInputStream(f);
		int r = 0;
		for (int i = 0; r != -1; i++) {
			r = fis.read();
			if (r == -1) break;
			ba.add((byte)r);
		}
		Byte[] bb2 = ba.toArray(new Byte[] {});
		byte[] b2 = new byte[bb2.length];
		for (int i = 0; i < bb2.length; i++) {
			b2[i] = bb2[i];
		}
		fis.close();
		byte[] data = b2;
		
		int limit = maxlength;
		byte[] datal = new byte[0];
		for (int i = 0; i < data.length; i += limit) {
			int i2 = 0;
			for (int i3 = limit; true; i3--) {
				i2 = i3;
				try {
					datal = new byte[i2];
					System.arraycopy(data, i, datal, 0, i2);
					// When not over limit , quit loop , else continue .
					break;
				} catch (Exception e) {
					continue;
				}
			}
			FileMessage msg = new FileMessage(f, datal);
			msg.offset = i;
			msg.length = data.length;
			fma.add(msg);
		}
		FileMessage[] fms = fma.toArray(new FileMessage[] {});
		return fms;
	}
	
	/**
	 * Creates an file from the recieved array of FileMessages and saves it .
	 * @param array File that got recieved .
	 * @param f File path to save the recieved file .
	 * @throws IOException something inside happened ... wow ... ask Java why 
	 */
	public static final void convert(FileMessage[] array, File f) throws IOException {
		if (!f.exists()) {
			f.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(f);
		for (int i = 0; i < array.length; i++) {
			FileMessage fm = array[i];
			for (int i2 = 0; i2 < fm.data.length; i2++) {
				fos.write(fm.data[i2]);
			}
		}
		fos.close();
	}
	
}
