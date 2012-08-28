package main;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;

public class EmptyCursor extends org.lwjgl.input.Cursor {
	
	public EmptyCursor()
			throws LWJGLException {
		//super(width, height, xHotspot, yHotspot, numImages, images, delays);
		super(1, 1, 0, 0, 1, createECBuffer(), null);
	}

	private static IntBuffer createECBuffer() {
		IntBuffer buf = BufferUtils.createIntBuffer(1);
		buf = buf.put(0, 0xffffffff);
		return buf;
	}
	
}
