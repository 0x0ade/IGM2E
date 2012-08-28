package main;

import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.opengl.Texture;

public class TextureBank {
	private static ConcurrentHashMap<String, Texture> textures = new ConcurrentHashMap<String, Texture>();
	private static ConcurrentHashMap<String, BufferedImage> images = new ConcurrentHashMap<String, BufferedImage>();
	
	public static void addTexture(String savename, Texture texture) {
		textures.put(savename, texture);
	}
	
	public static Texture getTexture(String savename) {
		return textures.get(savename);
	}

	public static void addImage(String savename, BufferedImage bi) {
		images.put(savename, bi);
	}
	
	public static BufferedImage getImage(String savename) {
		return images.get(savename);
	}
}
