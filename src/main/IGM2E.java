package main;

import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.SlickException;
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
import etc.JarStorage;
import etc.ResourceUtil;
import etc.StoredFile;

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
	
	public static main.Mouse mouse = new main.Mouse();
	public static InputHandler input;
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
	
	public static Image buffer;
	public static Graphics gbuffer;
	
	public void loop() throws SlickException {
		Log.setVerbose(false);
		Image backbuffer = new Image(w, h);
		buffer = new Image(w, h);
		
		// Loop thread's run
		
		// Get this thread's id for checks if ltcmd needed to perform OpenGL / OpenAL / ... operation or not .
		threadId = Thread.currentThread().getId();
		
		long lastTime = System.nanoTime();
		double unprocessed = 0;
		double nsPerTick = 1000000000.0 / 60;
		int frames = 0;
		int ticks = 0;
		long lastTimer1 = System.currentTimeMillis();
		
		while (!Display.isCloseRequested() && dotick) {
			
			if (Display.wasResized()) {
				w = Display.getWidth();
				h = Display.getHeight();
				backbuffer = new Image(w, h);
				buffer = new Image(w, h);
			}
			
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
			
			ArrayList<LTCThread> ltcmdsclone = (ArrayList<LTCThread>) ltcmds.clone();
			
			ltcmds.clear();
			
			for (LTCThread cmd : ltcmdsclone) {
				cmd.start();
			}
			
			while (unprocessed >= 1) {
				ticks++;
				tick();
				unprocessed -= 1;
				shouldRender = true;
			}
			if (shouldRender) {
				frames++;
				gbuffer = buffer.getGraphics();
				gbuffer.setColor(new Color(0f, 0f, 0f, 0f));
				gbuffer.clear();
				renderInit();
				gbuffer.flush();
				gbuffer = backbuffer.getGraphics();
				gbuffer.drawImage(buffer, 0, 0, new Color(1f, 1f, 1f, 1f));
				gbuffer.flush();
				
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
				GL11.glViewport(0, 0, w, h);
				
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();
				GL11.glOrtho(0, w, h, 0, -10,10);
				
				backbuffer.draw(0, 0, w, h);
				
				Display.update();
			}
			
			AudioLoader.update();
			
			if (System.currentTimeMillis() - lastTimer1 > 1000) {
				lastTimer1 += 1000;
				Display.setTitle(name+" (FPS:"+frames+") (Ticks:"+ticks+")");
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
		
		if (input != null) {
			input.tick();
		}
		mouse.tick();
		
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
		// Special key handles , like pause
		if (InputHandler.isDown(InputHandler.MENU)) {
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
		//SGL renderer = Renderer.get();
		//renderer.glClear(SGL.GL_COLOR_BUFFER_BIT | SGL.GL_DEPTH_BUFFER_BIT);
		
		/*
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		GL11.glViewport(0, 0, w, h);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, w, h, 0, -10,10);
		*/
		
		render();
		
		renderOverlay();
		
		mouse.render();
	}

	private static void render() {
		bg.render();
		if (level != null) {
			level.render();
			if (!(level instanceof TitleLevel)) {
				hud.render();
			}
		}
	}
		
	private static void renderOverlay() {
		Image i = ImageBank.getImage("overlay");
		
		if (i == null) return;
		
		i.draw(0, 0);
		
		//render(t, 0, 0);
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
	
	public static void main(String[] args) {
		try {
			new IGM2E();
		} catch (LWJGLException e) {
			e.printStackTrace();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	private IGM2E() throws LWJGLException, SlickException {
	//public init() throws LWJGLException {
		
	    final String nativeLibDir = Options.getNAppDir("IGM2E")
			.getAbsolutePath().toString()
			+ File.separator
			+ "share"
			+ File.separator
			+ "bin"
			+ File.separator
			+ "natives"
//			+ File.separator
			;
	    
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
		
		Display.setDisplayMode(new DisplayMode(size.width, size.height));
		Display.setTitle(name+" (FPS: "+0+")");
		Display.create();
		
		initGl();
		
		ResourceLoader.addResourceLocation(new ResourceLocation() {
			@Override
			public InputStream getResourceAsStream(String ref) {
				//return IGM2E.class.getResourceAsStream(ref);
				for (StoredFile sf : JarStorage.storage) {
					if (sf.getPath().equals(ref)) {
						return sf.getAsStream();
					}
				}
				return null;
			}
			@Override
			public URL getResource(String ref) {
				return IGM2E.class.getResource(ref);
			}
		});
		
		ResourceLoader.addResourceLocation(new ResourceLocation() {
			@Override
			public InputStream getResourceAsStream(String ref) {
				String jarpath = ResourceUtil.getJarPath();
				File f = new File(jarpath, ref);
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return fis;
			}
			@Override
			public URL getResource(String ref) {
				String jarpath = ResourceUtil.getJarPath();
				File f = new File(jarpath, ref);
				try {
					return f.toURI().toURL();
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return null;
				}
			}
		});
		
		ImageLoader.initImages();
		SoundLoader.initSounds();
		
		try {
			MapLoader.initLevels();
		} catch (Exception e) {
			throw new RuntimeException("Coudln't load maps!", e);
		}
		
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
		//SGL renderer = Renderer.get();
		//renderer.initDisplay(w, h);
		//renderer.enterOrtho(w, h);
		//renderer.glScalef(2f, 2f, 1f);
		//renderer.glTranslatef(-w/4, -h/4, 0);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glClearAccum(0f,0f,0f,1f);
		GL11.glClear(GL11.GL_ACCUM_BUFFER_BIT);
		
	}
	
	public static void stop() {
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
				return "Initalizing Input Handler";
			}
			
			@Override
			public void handle() {
				input = new InputHandler();
			}

			@Override
			public String loadText() {
				return "Setting up Input";
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