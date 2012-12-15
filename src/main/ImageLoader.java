package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.BufferedImageUtil;
import org.newdawn.slick.util.ResourceLoader;

public class ImageLoader {
	
	public static void initImages() {
		//UI
		loadImage("/res/ui/empty.png", "empty");
		loadImage("/res/ui/titlemenu_1.png", "titlemenu_1");
		loadImage("/res/ui/loading.png", "loading");
		loadImage("/res/ui/overlay.png", "overlay");
		loadImage("/res/ui/cursor.png", "cursor");
		loadImage("/res/ui/mouse.png", "mouse");
		loadImage("/res/ui/title.png", "title");
		loadSpritesheet("/res/ui/load.png", "load", 64, 64);
		loadSpritesheet("/res/ui/miniload.png", "miniload", 16, 16);
		
		//TILES
		loadImage("/res/levels/tiles/exit_tile.png", "exit_tile");
		
		loadSpritesheet("/res/levels/tiles/cave.png", "cave", 16, 16);
		loadSpritesheet("/res/levels/tiles/cave_grass.png", "cave_grass", 16, 16);
		loadSpritesheet("/res/levels/tiles/cave2.png", "cave2", 16, 16);
		loadSpritesheet("/res/levels/tiles/cave_bg.png", "cave_bg", 16, 16);
		
		loadSpritesheet("/res/levels/tiles/spikes.png", "spikes", 16, 16);
		
		//ENTITIES / MOBS
		loadSpritesheet("/res/game/player.png", "player", 16, 16);
		loadSpritesheet("/res/game/blob.png", "blob", 16, 16);
		
		//ETC
		try {
			Image white = new Image(1, 1);
			Graphics whiteg = white.getGraphics();
			whiteg.setColor(new Color(Color.white));
			whiteg.setBackground(new Color(Color.white));
			whiteg.clear();
			whiteg.flush();
			ImageBank.addImage("white", white);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * @deprecated Use {@link #loadSpritesheet(String filename, String savename, int w, int h)} instead. <br>
	 * <b>WARNING!</b> Will be removed soon!
	 */
	private static void loadEntity(String filename, String savename, int w, int h) {
		try {
			Image i = new Image(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(filename),GL11.GL_NEAREST));
			
			int iy = 1;
			for (int y = 0; y < i.getHeight(); y += h) {
				int ix = 1;
				for (int x = 0; x < i.getWidth(); x += w) {
					Image sub = i.getSubImage(x, y, w, h);
					ImageBank.addImage(savename+"_"+iy+"_"+ix, sub);
					
					ix++;
				}
				iy++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @deprecated Use {@link #loadSpritesheet(String filename, String savename, int w, int h)} instead. <br>
	 * <b>WARNING!</b> Will be removed soon!
	 */
	private static void loadTilesetFrom(String filename, String savename, int w, int h) {
		try {
			Image i = new Image(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(filename),GL11.GL_NEAREST));
			
			int ii = 1;
			for (int y = 0; y < i.getHeight(); y += h) {
				for (int x = 0; x < i.getWidth(); x += w) {
					Image sub = i.getSubImage(x, y, w, h);
					ImageBank.addImage(savename+"_"+ii, sub);
					
					ii++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadSpritesheet(String filename, String savename, int h, int w) {
		try {
			Image i = new Image(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(filename),GL11.GL_NEAREST));
			SpriteSheet s = new SpriteSheet(i, w, h);
			ImageBank.addImage(savename, s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadImage(String filename, String savename, int x, int y, int h, int w) {
		try {
			Image i = new Image(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(filename),GL11.GL_NEAREST));
			i = i.getSubImage(x, y, w, h);
			ImageBank.addImage(savename, i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadImage(String filename, String savename) {
		try {
			Image i = new Image(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(filename),GL11.GL_NEAREST));
			ImageBank.addImage(savename, i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
