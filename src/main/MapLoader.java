package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.tiled.TiledMapPlus;
import org.newdawn.slick.util.BufferedImageUtil;

public class MapLoader {
	
	public static void initLevels() {
		//loadLevel("test", "test");
		
		//loadLevel("intro", "intro");
		
		File folder = Resources.getResource("/res/levels/");
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
