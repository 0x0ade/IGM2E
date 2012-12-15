package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.openal.AL10;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.tiled.GroupObject;
import org.newdawn.slick.tiled.Layer;
import org.newdawn.slick.tiled.ObjectGroup;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.tiled.TiledMapPlus;
import org.newdawn.slick.util.BufferedImageUtil;

public class LoadedLevel extends Level {

	public TiledMapPlus tm;
	public Image bg;
	public Image og;
	public String savename;
	
	public LoadedLevel(TiledMapPlus map, String savename) {
		super((int)map.getWidth()*16, (int)map.getHeight()*16);
		this.savename = savename;
		tm = map;
		
		for (int layern = 0; layern < map.getLayerCount(); layern++) {
			Layer layer = map.getLayer(layern);
			
			for (int x = 0; x < w/16; x++) {
				for (int y = 0; y < h/16; y++) {
					
					int id = map.getTileId(x, y, layern);
					if (id <= 0) continue;
					
					Image img = map.getTileImage(x, y, layern);
					
					TileSet ts = map.getTileSetByGID(id);
					if (ts.name == null) System.out.println("ts name is null !!!");
					
					int itsid = layer.data[x][y][2];
					
					String type = map.getLayerProperty(layern, "type", "tile");
					
					Object o = null;
					try {
						o = LevelUtil.getTile(this, itsid, type, ts, x*16, y*16, layern, layer.name, map.getLayerProperty(layern, "layer", "0"));
					} catch (Throwable e) {
						e.printStackTrace();
						break;
					}
					
					if (o == null) continue;
					
					if (o instanceof Entity) {
						Entity e = (Entity)o;
						add(e);
						layer.removeTile(x, y);
					}
					if (o instanceof Tile) {
						Tile t = (Tile)o;
						if (t instanceof NextLevelTile) {
							((NextLevelTile)t).nextmap = map.getMapProperty("nextlevel", "editor_base");
						}
						if (map.getLayerProperty(layern, "rendertop", "false").toLowerCase().equals("true")) {
							t.renderTop = true;
						}
						add(t);
						if (!t.isStatic()) layer.removeTile(x, y);
					}
				}
			}
			
		}
		
		ArrayList<ObjectGroup> objectgroups = map.getObjectGroups();
		
		for (int groupn = 0; groupn < map.getObjectGroupCount(); groupn++) {
			ObjectGroup group = objectgroups.get(groupn);
			ArrayList<GroupObject> objects = group.getObjects();
			
			for (int n = 0; n < map.getObjectCount(groupn); n++) {
				GroupObject obj = objects.get(n);
				
				Object o = null;
				try {
					o = LevelUtil.getObject(this, obj, map, groupn, n, map.getObjectProperty(groupn, n, "layer", "0"));
				} catch (Throwable e) {
					e.printStackTrace();
					break;
				}
				
				if (o == null) continue;
				
				if (o instanceof GameObject) {
					GameObject go = (GameObject)o;
					go.level = this;
				}
				
				if (o instanceof Entity) {
					Entity e = (Entity)o;
					if (e instanceof TextEntity) {
						((TextEntity)e).text = map.getObjectProperty(groupn, n, "text", "<ERROR>");
						((TextEntity)e).bold = map.getObjectProperty(groupn, n, "bold", "false");
						((TextEntity)e).size = map.getObjectProperty(groupn, n, "size", "12");
					}
					add(e);
				}
				if (o instanceof Tile) {
					Tile t = (Tile)o;
					if (t instanceof NextLevelTile) {
						((NextLevelTile)t).nextmap = map.getMapProperty("nextlevel", "editor_base");
					}
					add(t);
				}
			}
		}
		
		String bgm = map.getMapProperty("bgm", "");
		if (!bgm.equals("")) {
			IGM2E.playBGM(bgm);
		}
		
		cacheBackground();
		
		cam = new LoadedLevelCam(this);
	}

	public void cacheBackground() {
		final int w = 256;
		final int h = 256;
		
		OpenGLHelper.run(new OpenGLHelper.Action() {
			@Override
			public Object run() throws Throwable {
				try {
					String bgs = tm.getMapProperty("background", "cave_16");
					Image bgi = ImageBank.getImage(bgs);
					
					og = new Image(w, h);
					Graphics ogg = og.getGraphics();
					for (int x = 0; x < w; x += bgi.getWidth()) {
						for (int y = 0; y < h; y += bgi.getHeight()) {
							ogg.drawImage(bgi, x, y);
						}
					}
					ogg.flush();
					bg = new Image(w, h);
					Graphics bgg = bg.getGraphics();
					for (int x = 0; x < w; x += bgi.getWidth()) {
						for (int y = 0; y < h; y += bgi.getHeight()) {
							bgg.drawImage(bgi, x, y, new Color(0.5f, 0.5f, 0.5f));
						}
					}
					bgg.flush();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}
	
	@Override
	public void render() {
		cam.render();
	}
	
	@Override
	public void uninit() {
		tm = null;
		bg = null;
		og = null;
		
		super.uninit();
	}
	
	@Override
	public void reinit() {
		tm = MapBank.getLevel(savename);
		cacheBackground();

		super.reinit();
	}
}
