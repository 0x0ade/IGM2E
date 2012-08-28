package main;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

import main.Keys.Key;
import main.Mouse.MouseButton;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL21;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.Sound;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.OpenALStreamPlayer;
import org.newdawn.slick.opengl.CursorLoader;
import org.newdawn.slick.opengl.ImageDataFactory;
import org.newdawn.slick.opengl.ImageIOImageData;
import org.newdawn.slick.opengl.LoadableImageData;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.tiled.TiledMapPlus;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.util.ResourceLocation;

import dlc.DLCUtil;

import paulscode.sound.Vector3D;

/*
 * Notes : 
 * <NeiloGD> just use powers of 2 for textures man
 * <NeiloGD> always!
 * 
 * 
 */

public class IGM2E {
	
	public static long time = System.currentTimeMillis();
	public static short framecount = 0;
	
	public static final String build = "1 beta 1";
	
	public static final String name = "IGM2E Base";
	public static final String name_short = "IGM2EBASE";
	public static final Dimension size = new Dimension(800, 608);
	public static int w = size.width;
	public static int h = size.height;
	
	public static Keys keys = new Keys();
	public static main.Mouse mouse = new main.Mouse();
	public static Cursor cursor;
	public static Random rand;
	
	public static MusicListener mlist;
	
	public static boolean dotick = true;	
	public static int fps = 0;
	
	public static Background bg;
	public static Level level = new Level(0, 0);
	public static Savegame savegame = new Savegame();
	public static HUD hud = new HUD();
	public static boolean paused = false;
	
	public static Applet applet;
	public static Thread appletThread;
	public static long threadId;
	
	public static ArrayList<LTCThread> ltcmds = new ArrayList<LTCThread>();
	public static ArrayList<LTCThread> ltcmdsr = new ArrayList<LTCThread>();
	
	public static float sound_x = 0f;
	public static float sound_y = 0f;
	
	public static int color_bg = 0xffffffff;
	public static int color_fps = 0xff000000;
	
	public static Music bgm;
	public static String songname = "";
	
	public static Cursor cursor() {
		if (cursor != null) return cursor;
		
		mouse.render = true;
		
		emptycursor();
		
		return cursor;
	}
	
	private static Cursor emptycursor() {
		try {
			Cursor emptycursor = new EmptyCursor();
			
			if (cursor == null) {
				cursor = emptycursor;
			}
			
			return emptycursor;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public void loop() {
		// Loop thread's run
		
		// Get this thread's id for checks if ltcmd needed to perform OpenGL / OpenAL / ... operation or not .
		threadId = Thread.currentThread().getId();
		
		long lastTime = System.nanoTime();
		double unprocessed = 0;
		double nsPerTick = 1000000000.0 / 60;
		int frames = 0;
		int ticks = 0;
		long lastTimer1 = System.currentTimeMillis();
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glClearAccum(0f,0f,0f,1f);
		GL11.glClear(GL11.GL_ACCUM_BUFFER_BIT);
		
		while (!Display.isCloseRequested() && dotick) {
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
			
			for (LTCThread cmd : ltcmds) {
				cmd.start();
			}
			
			for (LTCThread cmd : ltcmdsr) {
				IGM2E.ltcmds.remove(cmd);
			}
			ltcmdsr.clear();
			
			while (unprocessed >= 1) {
				ticks++;
				tick();
				unprocessed -= 1;
				shouldRender = true;
			}
			try {
				Thread.sleep(2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (shouldRender) {
				frames++;
				Display.update();
			}
			renderInit();
			
			AudioLoader.update();
			
			if (System.currentTimeMillis() - lastTimer1 > 1000) {
				lastTimer1 += 1000;
				Display.setTitle(name+" (FPS:"+frames+") "+"(Ticks:"+ticks+")");
				frames = 0;
				ticks = 0;
				shouldRender = true;
			}
			
			Display.sync(60);
			
			frames++;
			framecount++;
		}
		stop();
	}
	
	private static void tick() {
		
		handleKeys();
		handleMouse();
		
		keys.tick();
		mouse.tick();
		JoypadHandler.tick();
		
		if (level != null) {
			level.tick();
			if (!(level instanceof TitleLevel)) {
				hud.tick();
			}
		}
		bg.tick();
		
		DLCUtil.tick();
		
	}
	
	private static void handleKeys() {
		Keyboard.poll();
		for (Key k : keys.getAll()) {
			k.handle(Keyboard.isKeyDown(k.keyCode));
		}
		
		// Special key handles , like pause
		if (keys.pause.wasPressed()) {
			if (!(level instanceof TitleLevel)) {
				pause();
			}
		}
	}
	
	private static void handleMouse() {
		Mouse.poll();
		mouse.handle(Mouse.getX(), h - Mouse.getY(), Mouse.getDX(), -Mouse.getDY());
		for (MouseButton mb : mouse.getAll()) {
			mb.handle(Mouse.isButtonDown(mb.buttonCode));
		}
	}
	
	private static void renderInit() {
		SGL renderer = Renderer.get();
		renderer.glClear(SGL.GL_COLOR_BUFFER_BIT | SGL.GL_DEPTH_BUFFER_BIT);
		
		render();
		
		renderOverlay();
		
		mouse.render();
		
		renderer.glColor4f(Options.getAsFloat("gfx_red"),
				Options.getAsFloat("gfx_green"),
				Options.getAsFloat("gfx_blue"),
				Options.getAsFloat("gfx_alpha"));
		
		
		
	}

	private static void render() {
		bg.render();
		if (level != null) {
			level.render();
			if (!(level instanceof TitleLevel)) {
				hud.render();
			}
		}
		renderFPS(color_fps);
	}
		
	private static void renderFPS(int c) {
		Font f = new Font("Tahoma", 0, 12); // Tahoma
		Texture fpst = TextFactory.toTexture(fps+"", c, f, false);
		render(fpst, 0, 0);
	}
	
	private static void renderOverlay() {
		Texture t = TextureBank.getTexture("overlay");
		
		if (t == null) return;
		
		render(t, 0, 0);
		/*t.bind();
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		glBegin(GL_QUADS);
		
		glTexCoord2f(0, 0);
		glVertex2f(0, 0);
		glTexCoord2f(1, 0);
		glVertex2f(1200, 0);
		glTexCoord2f(1, 1);
		glVertex2f(1200, 1100);
		glTexCoord2f(0, 1);
		glVertex2f(0, 1100);
		
		glEnd();*/
	}
	
	public static void render(Texture t, float x, float y) {
		if (t == null) return;
		
		int w = t.getTextureWidth();
		int h = t.getTextureHeight();
		
		render(t, x, y, w, h);
	}
	
	public static void render(Texture t, float x, float y, float w, float h) {
		if (t == null) return;
		
		SGL renderer = Renderer.get();
		
		t.bind();
		
		renderer.glBegin(SGL.GL_QUADS);
		
		renderer.glTexCoord2f(0, 0);
		renderer.glVertex2f(x , y);
		renderer.glTexCoord2f(1, 0);
		renderer.glVertex2f(x + w, y);
		renderer.glTexCoord2f(1, 1);
		renderer.glVertex2f(x + w, y + h);
		renderer.glTexCoord2f(0, 1);
		renderer.glVertex2f(x, y + h);
		
		renderer.glEnd();
	}
	
	public static void main(String[] args) {
		initalize(null);
	}
	
	public static void initalize(final EngineApplet ea) {
		if (ea == null) {
			try {
				new IGM2E(ea);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
		} else {
			appletThread = new Thread("IGM2E.AppletLoopThread") {
				@Override
				public void run() {
					try {
						new IGM2E(ea);
					} catch (LWJGLException e) {
						e.printStackTrace();
					}
				}
			};
			appletThread.start();
		}
	}
	
	private IGM2E(EngineApplet ea) throws LWJGLException {
	//public init() throws LWJGLException {
		
		Resources.copyLibs();
		
	    final String nativeLibDir = Options.getDir()
			.getAbsolutePath().toString()
			+ File.separator
			+ "bin"
			+ File.separator
			+ "natives"
			+ File.separator;
	    
	    try {
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			String[] paths = (String[])field.get(null);
			for (int i = 0; i < paths.length; i++) {
				if (nativeLibDir.equals(paths[i])) {
					return;
				}
			}
			String[] tmp = new String[paths.length+1];
			System.arraycopy(paths,0,tmp,0,paths.length);
			tmp[paths.length] = nativeLibDir;
			field.set(null,tmp);
			System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + nativeLibDir);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	    
	    System.setProperty("org.lwjgl.librarypath", nativeLibDir);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
	    catch (Exception localException) {
	    }
		
		Options.load(Options.optionsFile);
		Options.setup();
		Options.save(Options.optionsFile);
		
		rand = new Random();
		rand = new Random(rand.nextLong());
		
		//bg = new Background(0, 0, getWidth(), getHeight());
		bg = new LoadingAnimation(initLoadThread(), w, h);
		bg.color = color_bg;
		
		if (ea == null) {
			Display.setDisplayMode(new DisplayMode(size.width, size.height));
			Display.setTitle(name+" (FPS: "+0+")");
			Display.create();
		} else {
			Display.setParent(ea.canvas);
			Display.create();
			
			applet = ea;
		}
		
		initGl();
		
		ResourceLoader.addResourceLocation(Resources.getResourceLocation());
		
		TextureLoader.initTextures();
		SoundLoader.initSounds();
		
		MapLoader.initLevels();
		
		mlist = new MusicListener() {

			@Override
			public void musicEnded(Music music) {
			}

			@Override
			public void musicSwapped(Music music, Music newMusic) {
			}
			
		};
		
		if (!AL.isCreated()) AL.create();
		
		Cursor cursor = cursor();
		
		Mouse.create();
		Mouse.setNativeCursor(cursor);
		
		Keyboard.create();
		
		loop();
	}
	
	public static void initGl() {
		SGL renderer = Renderer.get();
		renderer.initDisplay(w, h);
		renderer.enterOrtho(w, h);
	}
	
	public static void stop() {
		JoypadHandler.stop();
		AL.destroy();
		Display.destroy();
		DLCUtil.stopAll();
		Options.save(Options.optionsFile);
		System.exit(0);
	}
	
	private static UncaughtExceptionHandler getFatalHandler() {
		UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
			} 
		};
		return handler; 
	}
	
	public static LoadThread emptyLoadThread() {
		ArrayList<LTCommand> ltcl = new ArrayList<LTCommand>();
		LoadThread lt = new LoadThread(ltcl);
		return lt;
	}
	
	public LoadThread initLoadThread() {
		ArrayList<LTCommand> ltcl = new ArrayList<LTCommand>();
		
		ltcl.add(new LTCommand() {
			@Override
			public String name() {
				return "Load DLC files";
			}
			
			@Override
			public void handle() {
				DLCUtil.injectDLCs(Options.getExClassesDir());
			}

			@Override
			public String loadText() {
				return "Loading DLCs";
			}
		});
		
		ltcl.add(new LTCommand() {
			@Override
			public String name() {
				return "Initalizing Joypad Handler";
			}
			
			@Override
			public void handle() {
				JoypadHandler.init();
			}

			@Override
			public String loadText() {
				return "Setting up Joysticks";
			}
		});
		
		ltcl.add(new LTCommand() {
			@Override
			public String name() {
				return "Initalize Main Menu";
			}
			
			@Override
			public void handle() {
				Level level_t = new TitleLevel(null);
				bg = new Background(w, h);
				level = level_t;
			}

			@Override
			public String loadText() {
				return "Loading Main Menu";
			}
		});
		
		LoadThread lt = new LoadThread(ltcl);
		return lt;
	}

	public static void playSound(String name, float x, float y) {
		String relative = "/res/sounds/"+name+".wav";
		try {
			Sound s = SoundBank.getSound(name);
			s.playAt(1, Options.getAsFloat("vol_sound") * Options.getAsFloat("vol_sfx"), (x-sound_x)/16, (y-sound_y)/16, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void playBGM(String name) {
		try {
			songname = name;
			savegame.song = name;
			Music m = SoundBank.getMusic(name);
			m.addListener(mlist);
			m.loop(1.0f, Options.getAsFloat("vol_sound") * Options.getAsFloat("vol_bgm"));
			bgm = m;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void startApplet(EngineApplet ea) {
		System.out.println("Initalizing from applet");
		initalize(ea);
	}

	public static void stopApplet(EngineApplet ea) {
		try {
			dotick = false;
			appletThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void removeLTCMD(LTCThread cmd) {
		ltcmdsr.add(cmd);
	}

	public static void dlcCenter() {
		ArrayList<LTCommand> ltcl = new ArrayList<LTCommand>();
		
		ltcl.add(new LTCommand() {
			@Override
			public String name() {
				return "DLC Center ( GAME BREAK !!! )";
			}
			
			@Override
			public void handle() {
				Level level_t = new TitleLevel(null);
				bg = new Background(w, h);
				level = level_t;
			}

			@Override
			public String loadText() {
				return "Loading DLC Center";
			}
		});
		
		LoadThread lt = new LoadThread(ltcl);
		
		level = new Level(0, 0);
		bg = new LoadingAnimation(lt, w, h);
		bg.color = color_bg;
		
	}
	
	public static void showError(final String error) {
		Thread t = new Thread("showError.\""+error+"\"") {
			public void run() {
				JOptionPane.showMessageDialog(null, error, name, JOptionPane.OK_OPTION);
			}
		};
		t.setDaemon(true);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
		}
	}
	
	public static void showJarQuestion() {
		Thread t = new Thread("jarQuestion") {
			public void run() {
				int i = JOptionPane.showConfirmDialog(null, "Is this the path to the .jar of the game ?\n"+Resources.getJarPath(), name, JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.NO_OPTION) System.exit(0);
			}
		};
		t.setDaemon(true);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
		}
	}

	public static void handleOptionChange(String key, boolean save) {
		if (save) {
			Options.save(Options.optionsFile); 
		}
		if (key.equals("vol_sound") || key.equals("vol_bgm")) {
			if (bgm != null) {
				bgm.setVolume(Options.getAsFloat("vol_sound") * Options.getAsFloat("vol_bgm"));
			}
		}
		if (key.equals("gfx_vsync")) {
			try {
				Display.setVSyncEnabled(Options.getAsBoolean("gfx_vsync"));
			} catch (NullPointerException e) {
				// FIXME LWJGL OPENGL fails here ... 
			}
		}
	}

	public static void newGame() {
		try {
			ArrayList<LTCommand> ltcl = new ArrayList<LTCommand>();
			
			ltcl.add(new LTCommand() {
				@Override
				public String name() {
					return "Loading level";
				}
				
				@Override
				public void handle() {
					try {
						loadLevel(new LoadedLevel(MapBank.getLevel("test"), "test"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public String loadText() {
					return "Searching map";
				}
			});
			
			LoadThread lt = new LoadThread(ltcl);
			
			level = new Level(0, 0);
			bg = new LoadingLevelAnimation(lt, w, h);
			bg.color = color_bg;
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static void threadedLoadMap(final String map) {
		ArrayList<LTCommand> ltcl = new ArrayList<LTCommand>();
		
		ltcl.add(new LTCommand() {
			@Override
			public String name() {
				return "Loading level";
			}
			
			@Override
			public void handle() {
				loadLevel(new LoadedLevel(MapBank.getLevel(map), map));
			}

			@Override
			public String loadText() {
				return "Searching map";
			}
		});
		
		LoadThread lt = new LoadThread(ltcl);
		
		level = new Level(0, 0);
		bg = new LoadingLevelAnimation(lt, w, h);
		bg.color = color_bg;
		
	}
	
	public static void loadLevel(Level loadedlevel) {
		level = loadedlevel;
		bg = new Background(w, h);
	}
	
	public static void pause() {
		Level level_t = new TitleLevel(level);
		bg = new Background(w, h);
		level = level_t;
		paused = true;
	}

	public static void unpause() {
		if (level instanceof TitleLevel && paused) {
			level = ((TitleLevel)level).paused;
			paused = false;
		} else {
			throw new RuntimeException("Can not unpause game while in game !");
		}
	}

	public static void saveGame() {
		Level level = null;
		if (paused) {
			level = ((TitleLevel)(IGM2E.level)).paused;
		} else {
			level = IGM2E.level;
		}
		
		savegame.saveLevel(level);
		try {
			savegame.saveGame(Savegame.name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadGame() {
		try {
			savegame = Savegame.loadGame(Savegame.name);
			level = savegame.loadLevel();
			paused = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean canContinue() {
		FilenameFilter lvlfilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".lvl");
			}
		};
		
		FilenameFilter savfilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".sav");
			}
		};
		
		boolean c1 = false;
		for (File f : Options.getSavesDir().listFiles(lvlfilter)) {
			if (f.getName().toLowerCase().equals(Savegame.name+".lvl")) {
				c1 = true;
				break;
			}
		}
		boolean c2 = false;
		for (File f : Options.getSavesDir().listFiles(savfilter)) {
			if (f.getName().toLowerCase().equals(Savegame.name+".sav")) {
				c1 = true;
				break;
			}
		}
		boolean can = c1 && c2;
		return can;
	}
	
}