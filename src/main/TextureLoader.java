package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

public class TextureLoader {
	
	public static void initTextures() {
		//UI
		loadTexture("/res/ui/empty.png", "empty");
		loadTexture("/res/ui/titlemenu_1.png", "titlemenu_1");
		loadTexture("/res/ui/loading.png", "loading");
		loadTexture("/res/ui/overlay.png", "overlay");
		loadTexture("/res/ui/cursor.png", "cursor");
		loadTexture("/res/ui/mouse.png", "mouse");
		loadTexture("/res/ui/title.png", "title");
		loadTilesetFrom("/res/ui/load.png", "load", 64, 64);
		loadTilesetFrom("/res/ui/miniload.png", "miniload", 16, 16);
		
		//TILES
		loadTexture("/res/levels/tiles/exit_tile.png", "exit_tile");
		
		loadTilesetFrom("/res/levels/tiles/cave.png", "cave", 16, 16);
		loadTilesetFrom("/res/levels/tiles/cave_grass.png", "cave_grass", 16, 16);
		loadTilesetFrom("/res/levels/tiles/cave2.png", "cave2", 16, 16);
		loadTilesetFrom("/res/levels/tiles/cave_bg.png", "cave_bg", 16, 16);
		
		loadTilesetFrom("/res/levels/tiles/spikes.png", "spikes", 16, 16);
		
		//ENTITIES / MOBS
		loadEntity("/res/game/player.png", "player", 16, 16);
		loadEntity("/res/game/blob.png", "blob", 16, 16);
		
	}
	
	private static void loadEntity(String filename, String savename, int w, int h) {
		try {
			InputStream is = Resources.getResourceAsStream(filename);
			BufferedImage all = ImageIO.read(is);
			
			int iy = 1;
			for (int y = 0; y < all.getHeight(); y += h) {
				int ix = 1;
				for (int x = 0; x < all.getWidth(); x += w) {
					BufferedImage bi = all.getSubimage(x, y, w, h);
					TextureBank.addImage(savename+"_"+iy+"_"+ix, bi);
					Texture t = BufferedImageUtil.getTexture("PNG", bi);
					TextureBank.addTexture(savename+"_"+iy+"_"+ix, t);
					
					ix++;
				}
				iy++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void loadTilesetFrom(String filename, String savename, int w, int h) {
		try {
			InputStream is = Resources.getResourceAsStream(filename);
			BufferedImage all = ImageIO.read(is);
			
			int i = 1;
			for (int y = 0; y < all.getHeight(); y += h) {
				for (int x = 0; x < all.getWidth(); x += w) {
					BufferedImage bi = all.getSubimage(x, y, w, h);
					TextureBank.addImage(savename+"_"+i, bi);
					Texture t = BufferedImageUtil.getTexture("PNG", bi);
					TextureBank.addTexture(savename+"_"+i, t);
					
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadTexture(String filename, String savename, int x, int y, int h, int w) {
		try {
			InputStream is = Resources.getResourceAsStream(filename);
			BufferedImage bi = ImageIO.read(is);
			bi = bi.getSubimage(x, y, w, h);
			TextureBank.addImage(savename, bi);
			Texture t = BufferedImageUtil.getTexture("PNG", bi);
			TextureBank.addTexture(savename, t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadTexture(String filename, String savename) {
		try {
			InputStream is = Resources.getResourceAsStream(filename);
			BufferedImage bi = ImageIO.read(is);
			TextureBank.addImage(savename, bi);
			Texture t = BufferedImageUtil.getTexture("PNG", bi);
			TextureBank.addTexture(savename, t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
