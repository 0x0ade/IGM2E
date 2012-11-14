package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.imageio.ImageIO;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.GroupObject;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.tiled.TiledMapPlus;
import org.newdawn.slick.util.ResourceLoader;

public class LevelUtil {
	
	public static Object getTile(Level level, int id, String type, TileSet ts, int x, int y, int layer, String layername)
			throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		
		if ((""+ts.name).equals("null")) System.out.println("getTile says ts.name is null !!!");
		
		Object o = getTileFor(level, x, y, ts, id, layer, layername, type);
		
		return o;
	}
	
	public static Object getObject(Level level, GroupObject obj, TiledMapPlus map, int groupn, int n) {
		Object o = null;
		
		Class c = getObjectFor(obj, map, groupn, n);
		
		if (c == null) return null;
		
		Constructor[] constructs = c.getDeclaredConstructors();
		Constructor constr = null;
		for (int i = 0; i < constructs.length; i++) {
			constr = constructs[i];
			if (constr.getGenericParameterTypes().length == 0) {
				//break;
			}
			try {
				o = constr.newInstance(level, obj.x - obj.getImage().getWidth(), obj.y - obj.getImage().getHeight());
				break;
			} catch (Throwable e) {
				try {
					o = constr.newInstance(level, obj.x - obj.getImage().getWidth(), obj.y - obj.getImage().getHeight(), null, 0, 0, "");
					break;
				} catch (Throwable e2) {
					if (e2.getCause() == null) {
						e2.initCause(e);
					}
					e2.printStackTrace();
				}
			}
		}
		
		return o;
	}

	public static Object getTileFor(Level level, int x, int y, TileSet tset, int id, int layer, String layername, String type) {
		Object o = null;
		
		if (type.equals("tile")) {
			//TILES
			if (tset.name.equals("exit_tile")) {
				o = new NextLevelTile(level, x, y, tset, id, layer, layername);
			} else {
				o = new GroundTile(level, x, y, tset, id, layer, layername);
			}
			
		} else if (type.equals("bg")) {
			//BACKGROUNDS
			
		} else if (type.equals("entity")) {
			//ENTITIES
			
			System.out.println(id);
			switch (id) {
			case 33:
				o = new SSPlayer(level, x, y);
				break;
			case 35:
				o = new Blob(level, x, y);
				break;
			default:
				break;
			}
		}
		
		return o;
	}
	
	public static Class getObjectFor(GroupObject obj, TiledMapPlus map, int groupn, int n) {
		Class c = null;
		String type = map.getObjectType(groupn, n);
		String name = map.getObjectName(groupn, n);
		
		if (type.equals("tile")) {
			//TILES
			if (name.equals("exit_tile")) {
				c = NextLevelTile.class;
			} else {
				c = GroundTile.class;
			}
			
		} else if (type.equals("bg")) {
			//BACKGROUNDS
			
		} else if (type.equals("entity")) {
			//ENTITIES
			
			if (name.equals("text")) {
				c = TextEntity.class;
			} else if (name.equals("player")) {
				c = SSPlayer.class;
			} else if (name.equals("blob")) {
				c = Blob.class;
			}
		}
		
		return c;
	}

	public static TiledMapPlus getTMXLevel(String name) {
		try {
			TiledMapPlus map = new TiledMapPlus(ResourceLoader.getResourceAsStream("/res/levels/"+name+".tmx"), "/res/levels");
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
