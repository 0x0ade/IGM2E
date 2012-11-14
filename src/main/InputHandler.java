package main;

import java.util.ArrayList;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.Control;
import org.newdawn.slick.command.ControllerButtonControl;
import org.newdawn.slick.command.ControllerDirectionControl;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.KeyControl;

public class InputHandler implements InputProviderListener {
	
	/**
	 * The input abstractor ("provider")
	 */
	public static InputProvider provider;
	/**
	 * The input (Keyboard, Joystick, Mouse, ...)
	 */
	public static Input input = new Input(IGM2E.h);
	
	public static final Command ENTER = new BasicCommand("enter");
	public static final Command UP = new BasicCommand("up");
	public static final Command DOWN = new BasicCommand("down");
	public static final Command LEFT = new BasicCommand("left");
	public static final Command RIGHT = new BasicCommand("right");
	//The following 4 controlls were made for WASD steering, CAN be used SEPERATELY to the previous 4.
	public static final Command W = new BasicCommand("w");
	public static final Command A = new BasicCommand("a");
	public static final Command S = new BasicCommand("s");
	public static final Command D = new BasicCommand("d");
	public static final Command JUMP = new BasicCommand("jump");
	public static final Command MENU = new BasicCommand("menu");
	/**
	 * @deprecated Use MENU instead. Links to it.
	 */
	public static final Command ESC = MENU;
	public static final Command SPACE = new BasicCommand("space");
	
	InputHandler() {
		reload();
	}
	
	/**
	 * Resets all the key bindings.
	 */
	public static void resetBinds() {
		ArrayList<Command> al = (ArrayList<Command>) provider.getUniqueCommands();
		for (int i = 0; i < al.size(); i++) {
			provider.clearCommand(al.get(i));
		}
		provider.bindCommand(new KeyControl(Input.KEY_ESCAPE), ESC);
		provider.bindCommand(new KeyControl(Input.KEY_ESCAPE), MENU);
		provider.bindCommand(new KeyControl(Input.KEY_ENTER), ENTER);
		provider.bindCommand(new KeyControl(Input.KEY_UP), UP);
		provider.bindCommand(new KeyControl(Input.KEY_DOWN), DOWN);
		provider.bindCommand(new KeyControl(Input.KEY_LEFT), LEFT);
		provider.bindCommand(new KeyControl(Input.KEY_RIGHT), RIGHT);
		provider.bindCommand(new KeyControl(Input.KEY_W), W);
		provider.bindCommand(new KeyControl(Input.KEY_A), A);
		provider.bindCommand(new KeyControl(Input.KEY_S), S);
		provider.bindCommand(new KeyControl(Input.KEY_D), D);
		provider.bindCommand(new KeyControl(Input.KEY_UP), JUMP);
		provider.bindCommand(new KeyControl(Input.KEY_W), JUMP);
		provider.bindCommand(new KeyControl(Input.KEY_SPACE), SPACE);
	}
	
	/**
	 * checkForOfficialJoysticks runs some checks for checking if one of the following conditions is met: 
	 * <br> - Linux-like behaviour on PS3 Controllers with delivered firmware (e.g. Ubuntu 12.04)
	 * <br>
	 * After these checks were ran special mappings will be enabled. You may try them out on your own.
	 */
	private static void checkForOJ() throws SlickException {
		input.initControllers();
		
		int count = Controllers.getControllerCount();
		
		for (int i = 0; i < count; i++) {
			Controller controller = Controllers.getController(i);
			
			if ((controller.getButtonCount() >= 3) && (controller.getButtonCount() < 100)) {
				String name = controller.getName();
				int id = controller.getIndex();
				switch (Options.getOs()) {
				case windows:
					break;
				case linux:
					checkPS3L(id, name, controller);
					break;
				case macos:
					checkPS3L(id, name, controller);
					break;
				case solaris:
					checkPS3L(id, name, controller);
					break;
				case unknown:
					checkPS3L(id, name, controller);
					break;
				default:
					break;
				}
			}
		}
		
	}
	
	private static int pinkie = 0; //Number of PS3 controllers without 3rd party driver / firmware on Linux
	
	/**
	 * Checks for the controller support of the PS3 controller on Linux- or Unix-like systems.
	 */
	private static void checkPS3L(int id, String name, Controller c) {
		if (name.trim().equals("Sony PLAYSTATION(R)3 Controller") && c.getButtonName(5).trim().equals("Pinkie")) {
			pinkie++;
			if (!Float.toString(pinkie/2f).endsWith(".0")) {
				return; //Ignore every second "pinkie" controller (ghost)
			}
			
			//Sony PS3 Sixaxis (or Sixaxis Dualshock 3) with delivered, BUGGY driver
			System.out.println("Found PS3 controller! INDEX: "+id+"; NAME: "+name);
			System.out.println("Note: You should better use a 3rd party driver / firmware. Why? HOME, X, O and triangle don't work.");
			System.out.println("Proof: Button count: "+c.getButtonCount()+"; usually 17-4-4-2=7 (4 dir, 4 X O tri squ and L2 R2 because these are usually axes)");
			provider.bindCommand(new ControllerDirectionControl(id, ControllerDirectionControl.LEFT), LEFT);
			provider.bindCommand(new ControllerDirectionControl(id, ControllerDirectionControl.RIGHT), RIGHT);
			provider.bindCommand(new ControllerDirectionControl(id, ControllerDirectionControl.UP), UP);
			provider.bindCommand(new ControllerDirectionControl(id, ControllerDirectionControl.DOWN), DOWN);
			//4 u 5 r 6 d 7 l
			//Note: Use ID+1
			provider.bindCommand(new ControllerButtonControl(id, 5), UP);
			provider.bindCommand(new ControllerButtonControl(id, 6), RIGHT);
			provider.bindCommand(new ControllerButtonControl(id, 7), DOWN);
			provider.bindCommand(new ControllerButtonControl(id, 8), LEFT);
			provider.bindCommand(new ControllerButtonControl(id, 13), JUMP); //Square ("DEAD")
			provider.bindCommand(new ControllerButtonControl(id, 4), ENTER); //Start
			provider.bindCommand(new ControllerButtonControl(id, 1), ESC); //Select
			provider.bindCommand(new ControllerButtonControl(id, 1), MENU); //Select
		}
	}
	
	
	/**
	 * Adds a new listener to the input provider.
	 */
	public static void addListener(InputProviderListener listener) {
		provider.addListener(listener);
	}
	
	/**
	 * Removes a listener from the input provider.
	 */
	public static void removeListener(InputProviderListener listener) {
		provider.removeListener(listener);
	}
	
	/**
	 * Adds a listener to the input directly. Mainly for custom input implementations.
	 */
	public static void addListener(InputListener listener) {
		input.addListener(listener);
	}
	
	/**
	 * Removes a listener from the input directly.
	 */
	public static void removeListener(InputListener listener) {
		input.removeListener(listener);
	}
	
	/**
	 * Adds a command that will be handled.
	 */
	public static void addCommand(Control control, Command cmd) {
		provider.bindCommand(control, cmd);
	}

	static ArrayList<Command> down_1 = new ArrayList<Command>();
	static ArrayList<Command> down = new ArrayList<Command>();
	
	@Override
	public void controlPressed(Command cmd) {
		down.add(cmd);
		//System.out.println(cmd+" pressed");
	}
	
	@Override
	public void controlReleased(Command cmd) {
		down.remove(cmd);
		//System.out.println(cmd+" released");
	}
	
	public static boolean isDown(Command cmd) {
		return down.contains(cmd);
	}
	
	public static boolean wasDown(Command cmd) {
		return down.contains(cmd) && !down_1.contains(cmd);
	}
	
	public static boolean wasPressed(Command cmd) {
		return !down.contains(cmd) && down_1.contains(cmd);
	}
	
	public void tick() {
		down_1 = (ArrayList<Command>) down.clone();
		input.poll(IGM2E.w, IGM2E.h);
	}
	
	/**
	 * Reloads the input engine from an important option change or beginning.
	 */
	public void reload() {
		down.clear();
		if (provider != null) {
			provider.setActive(false);
			input = new Input(IGM2E.h);
		}
		
		provider = new InputProvider(input);
		provider.addListener(this);
		resetBinds();
		
		try {
			checkForOJ();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
