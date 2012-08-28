package main;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Frame;

public class EngineApplet extends Applet {
	
	public Canvas canvas;
	
	@Override
	public void init() {
		setLayout(new BorderLayout());
		setSize(IGM2E.size);
		try {
			canvas = new Canvas() {
				@Override
				public final void addNotify() {
					super.addNotify();
					IGM2E.startApplet(EngineApplet.this);
				}
				@Override
				public final void removeNotify() {
					IGM2E.stopApplet(EngineApplet.this);
					super.removeNotify();
				}
			};
			canvas.setSize(IGM2E.size);
			add(canvas);
			canvas.setFocusable(true);
			canvas.requestFocus();
			canvas.setIgnoreRepaint(true);
			canvas.setVisible(true);
			setVisible(true);
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException("Failed loading applet's display", e);
		}
	}

	@Override
	public void start() {
		 
	}
	
	@Override
	public void stop() {
		
	}
	
	@Override
	public void destroy() {
		remove(canvas);
		super.destroy();
	}
}
