package main;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public final class OpenGLHelper {
	private OpenGLHelper() {}
	
	public static Object run(final Action action) {
		if (Thread.currentThread().getId() == IGM2E.threadId) {
			try {
				return action.run();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			return null;
		}
		final Object[] ret = new Object[1];
		LTCThread ltct = new LTCThread(new LTCommand() {
			@Override
			public void handle() {
				try {
					ret[0] = action.run();
				} catch (ThreadDeath e) {
					//Pass thru
					throw e;
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public String name() {
				return "Running OpenGL context action";
			}

			@Override
			public String loadText() {
				return "";
			}
		});
		
		IGM2E.ltcmds.add(ltct);
		
		ltct.join();
		
		return ret[0];
	}
	
	public static interface Action {
		public Object run() throws Throwable;
	}
}
