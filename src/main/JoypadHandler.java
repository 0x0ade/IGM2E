package main;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

/**
 * Class to change Gamepad / Joystick input to Keys .
 */
public class JoypadHandler {
	
	public final static class Button {
		public final String name;
		public final Controller controller;
		public final int id;
		public Keys.Key simulKey;
		public boolean nextState = false;
		public boolean wasDown = false;
		public boolean isDown = false;

		public Button(String name, Controller controller, int id) {
			this.name = name;
			this.controller = controller;
			this.id = id;
			
			String skn = Options.get("joyb_"+name);
			if (skn != null) {
				for (Keys.Key key : IGM2E.keys.getAll()) {
					if (simulKey != null) continue;
					if (key.name.equals(skn)) {
						simulKey = key;
					}
				}
			}
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
	}
	
	public final static class Axis {
		public final String name;
		public final Controller controller;
		public final int id;
		public float firstState = -2;
		public float nextState = 0;
		public float currState = 0;
		public float lastState = 0;

		public Axis(String name, Controller controller, int id) {
			this.name = name;
			this.controller = controller;
			this.id = id;
		}

		public void tick() {
			lastState = currState;
			currState = nextState;
		}

		public boolean wasAt(float f) {
			return currState != f && lastState == f;
		}
		
		public boolean isAt(float f) {
			return currState == f && lastState != f;
		}

		public void update(float f) {
			nextState = f;
		}
	}
	
	public Controller controller;

	public ArrayList<Object> butaxes;

	public int buttonCount;
	public int axisCount;
	public int itemCount;
	
	public JoypadHandler(int index) {
		controller = Controllers.getController(index);

		buttonCount = controller.getButtonCount();
		axisCount = controller.getAxisCount();
		itemCount = controller.getButtonCount() + controller.getAxisCount() + 2;

		for (int i=0;i<controller.getButtonCount();i++) {
			addButton(new Button(controller.getButtonName(i), controller, i));
		}
		for (int i=buttonCount;i<buttonCount+controller.getAxisCount();i++) {
			addAxis(new Axis(controller.getAxisName(i-buttonCount), controller, i));
		}

		int i = itemCount - 2;
		addAxis(new Axis("POV X", controller, i));

		i = itemCount - 1;
		addAxis(new Axis("POV Y", controller, i));
	}

	public void updateDetails() {
		for (int i=0;i<buttonCount;i++) {
			if (controller.isButtonPressed(i)) {
				if (butaxes.get(i) instanceof Button) {
					toggleButton((Button)butaxes.get(i), true);
				}
			} else if (!controller.isButtonPressed(i)) {
				if (butaxes.get(i) instanceof Button) {
					toggleButton((Button)butaxes.get(i), false);
				}
			}
		}
		for (int i=buttonCount;i<buttonCount+controller.getAxisCount();i++) {
			if (butaxes.get(i) instanceof Axis) {
				toggleAxis((Axis)butaxes.get(i), controller.getAxisValue(i-buttonCount));
			}
		}

		toggleAxis((Axis)butaxes.get(itemCount-2), controller.getPovX());
		toggleAxis((Axis)butaxes.get(itemCount-1), controller.getPovY());
	}
	
	public void addButton(Button b) {
		addButton(b, b.id);
	}
	
	public void addButton(Button b, int i) {
		if (butaxes == null) {
			butaxes = new ArrayList<Object>(itemCount);
		}
		butaxes.add(i, b);
	}
	
	public void toggleButton(Button b, boolean s) {
		b.nextState = s;
		
		if (b.simulKey == null) return;
		
		if (s) {
			toggleJoypad(b.simulKey, true);
		} else {
			toggleJoypad(b.simulKey, false);
		}
	}
	
	public void addAxis(Axis a) {
		addAxis(a, a.id);
	}
	
	public void addAxis(Axis a, int i) {
		if (butaxes == null) {
			butaxes = new ArrayList<Object>(itemCount);
		}
		butaxes.add(i, a);
	}
	
	public void toggleAxis(Axis a, float f) {
		if (a.firstState == -2) {
			a.firstState = f;
		}
		
		if (a.firstState == f) {
			a.nextState = 0;
			return;
		} else {
			a.firstState = -3;
		}
		
		a.nextState = f;
		
		Keys keys = IGM2E.keys;
		
		if (a.name.equals(walkXA.substring(walkXA.indexOf(":")+1)) && !((a.controller.getIndex()+"").equals(walkXA.substring(walkXA.indexOf(":")+1)))) {
			if (f > 0) {
				toggleJoypad(keys.right, true);
			} else {
				toggleJoypad(keys.right, false);
			}
			if (f < 0) {
				toggleJoypad(keys.left, true);
			} else {
				toggleJoypad(keys.left, false);
			}
		}
		if (a.name.equals(walkYA.substring(walkYA.indexOf(":")+1)) && !((a.controller.getIndex()+"").equals(walkYA.substring(walkYA.indexOf(":")+1)))) {
			if (f > 0) {
				toggleJoypad(keys.down, true);
			} else {
				toggleJoypad(keys.down, false);
			}
			if (f < 0) {
				toggleJoypad(keys.up, true);
			} else {
				toggleJoypad(keys.up, false);
			}
		}
		
	}
	
	//Index:AxisName
	//3:X axis
	public static String walkXA = "-1:NONE";
	public static String walkYA = "-1:NONE";
	
	public static JoypadHandler handlers[];
	public static int count;
	
	public static void init() {
		try {
			Controllers.create();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		count = Controllers.getControllerCount();
		System.out.println(count+" Controllers Found");
		for (int i=0;i<count;i++) {
			Controller controller = Controllers.getController(i);
			System.out.println(controller.getIndex()+" : "+controller.getName());
		}

		if (count == 0) {
			return;
		}

		handlers  = new JoypadHandler[count];
		for (int i=0;i<count;i++) {
			handlers[i] = new JoypadHandler(i);
		}
		
		if (Options.get("joya_walkXA") != null) {
			walkXA = Options.get("joya_walkXA");
		}
		if (Options.get("joya_walkYA") != null) {
			walkYA = Options.get("joya_walkYA");
		}
	}
	
	public static void tick() {
		if (count == 0) {
			return;
		}
		
		Controllers.poll();

		/*while (Controllers.next()) {
			System.out.println("Event Fired: ");
			System.out.println("\t"+Controllers.getEventNanoseconds());
			System.out.println("\t"+Controllers.getEventSource()+":"+Controllers.getEventControlIndex()+":"+Controllers.isEventButton());
			System.out.println("\t"+Controllers.isEventXAxis()+":"+Controllers.isEventYAxis());
		}*/

		for (int i=0;i<count;i++) {
			if (handlers != null) {
				if (handlers[i] != null) {
					handlers[i].updateDetails();
				}
			}
		}
	}
	
	public static void toggleJoypad(Keys.Key key, boolean state) {
		if (key == null) return;
		if (key.pressedByKeyboard == 0) {
			key.nextState = state;
		}
	}

	public static void stop() {
		count = 0;
	}
	
}
