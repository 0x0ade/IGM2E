package main;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.renderer.SGL;

public class Mouse implements Tickable, RRenderer {
	
	public class MouseButton {
		public final String name;
		public int buttonCode;
		public boolean nextState;
		public boolean isDown;
		public boolean wasDown;
		
		public MouseButton(String name, int buttonCode) {
			this.name = name;
			this.buttonCode = buttonCode;
			all.add(this);
		}
		
		public void tick() {
			wasDown = isDown;
			isDown = nextState;
		}

		public boolean wasPressed() {
			return !wasDown && isDown;
		}

		public boolean wasReleased() {
			return wasDown && !isDown;
		}

		public void release() {
			nextState = false;
		}
		
		public void handle(boolean down) {
			nextState = down;
		}
	}
	
	private List<MouseButton> all = new ArrayList<MouseButton>();
	public MouseButton left = new MouseButton("left", 0);
	public MouseButton right = new MouseButton("right", 1);
	public MouseButton mid = new MouseButton("mid", 2);
	
	public int x;
	public int y;
	private long tick[];
	private int dx = 0;
	private int dy = 0;
	public boolean draw = false;
	public boolean render = false;
	public int drawxo = 10;
	public int drawyo = 7;
	
	public Image cursor = null;
	
	public Mouse() {
		tick = new long[3];
		for (int i = 0; i < tick.length; i++) tick[i] = 0;
	}
	
	public void tick() {
		for (MouseButton button : all)
			button.tick();
		if (org.lwjgl.input.Mouse.isInsideWindow()) {
			draw = true;
		} else {
			draw = false;
		}
	}

	public void release() {
		for (MouseButton button : all)
			button.release();
	}

	public List<MouseButton> getAll() {
		return all;
	}
	
	public void handle(int x, int y, int dx, int dy) {
		draw = true;
		this.x = x;
		this.y = y;
		if (dx != 0) this.dx = dx;
		if (dy != 0) this.dy = dy;
		if (x < 0 || x >= IGM2E.w ||
				y < 0 || y >= IGM2E.h) draw = false;
	}

	@Override
	public void render() {
		if (draw && render) {
			if (cursor == null) {
				cursor = ImageBank.getImage("mouse");
			}
			cursor.draw(x - drawxo, y - drawyo);
		}
	}
	
}
