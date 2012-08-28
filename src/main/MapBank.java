package main;

import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.tiled.Layer;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.tiled.TiledMapPlus;

public class MapBank {
	private static ConcurrentHashMap<String, TiledMapPlus> levels = new ConcurrentHashMap<String, TiledMapPlus>();
	
	public static void addLevel(String savename, TiledMapPlus map) {
		for (int layern = 0; layern < map.getLayerCount(); layern++) {
			Layer layer = map.getLayer(layern);
			
			String type = map.getLayerProperty(layern, "type", "tile");
			
			if (type.equals("entity")) {
				layer.opacity = 0f;
			}
			
		}
		
		levels.put(savename, map);
	}
	
	public static TiledMapPlus getLevel(String savename) {
		return levels.get(savename).clone();
	}
}
