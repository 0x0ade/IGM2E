package main;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

public class Keys {
	
	public class Key {
		
		public final String name;
		public int keyCode;
		public boolean nextState;
		public boolean isDown;
		public boolean wasDown;
		public int pressedByKeyboard = 0;
		
		public Key(String name, int keyCode) {
			this.name = name;
			this.keyCode = keyCode;
			all.add(this);
		}
		
		public void tick() {
			if (pressedByKeyboard > 0 && nextState)
				pressedByKeyboard = 5;
			else
				pressedByKeyboard -= 1;
			if (pressedByKeyboard < 0) pressedByKeyboard = 0;
			
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
			if (nextState == down) return;
			nextState = down;
			pressedByKeyboard = 5;
		}
	}

	private List<Key> all = new ArrayList<Key>();
	
	public Key up = new Key("up", Keyboard.KEY_UP);
	public Key down = new Key("down", Keyboard.KEY_DOWN);
	public Key left = new Key("left", Keyboard.KEY_LEFT);
	public Key right = new Key("right", Keyboard.KEY_RIGHT);
	public Key enter = new Key("enter", Keyboard.KEY_RETURN);
	
	public Key save = new Key("save", Keyboard.KEY_F1);
	public Key load = new Key("load", Keyboard.KEY_F2);
	
	public Key pause = new Key("pause", Keyboard.KEY_ESCAPE);
	
	public void tick() {
		for (Key key : all)
			key.tick();
	}

	public void release() {
		for (Key key : all)
			key.release();
	}

	public List<Key> getAll() {
		return all;
	}
	
}
