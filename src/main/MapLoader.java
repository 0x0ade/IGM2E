package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.tiled.TiledMapPlus;
import org.newdawn.slick.util.BufferedImageUtil;
import org.newdawn.slick.util.ResourceLoader;

public class MapLoader {
	
	public static void initLevels() throws URISyntaxException {
		//loadLevel("test", "test");
		
		//loadLevel("intro", "intro");
		
		File folder = new File(ResourceLoader.getResource("/res/levels/").toURI());
		for (File child : folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".tmx") && !file.isDirectory();
			}
		})) {
			loadLevel(child.getName().replaceAll(".tmx", ""), child.getName().replaceAll(".tmx", ""));
		}
		
	}
	
	public static void loadLevel(String filename, String savename) {
		try {
			TiledMapPlus map = LevelUtil.getTMXLevel(filename);
			MapBank.addLevel(savename, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
