package main;

import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.Image;

public class ImageBank {
	private static ConcurrentHashMap<String, Image> images = new ConcurrentHashMap<String, Image>();
	
	public static void addImage(String savename, Image i) {
		images.put(savename, i);
	}
	
	public static Image getImage(String savename) {
		return images.get(savename);
	}
}
