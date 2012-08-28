package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
	public Texture bg;
	public Texture og;
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
						o = LevelUtil.getTile(this, itsid, type, ts, x*16, y*16, layern, layer.name);
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
					o = LevelUtil.getObject(this, obj, map, groupn, n);
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
	}

	public void cacheBackground() {
		try {
			String bgs = tm.getMapProperty("background", "cave_16");
			BufferedImage bgi = TextureBank.getImage(bgs);
			
			int w = 256;
			int h = 256;
			
			final BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D) bi2.getGraphics();
			final BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) bi.getGraphics();
			g.setColor(new Color(0, 0, 0));
			g.fillRect(0, 0, w, h);
			for (int x = 0; x < w; x += bgi.getWidth()) {
				for (int y = 0; y < h; y += bgi.getHeight()) {
					g.drawImage(bgi, x, y, null);
				}
			}
			g.dispose();
			g2.setColor(new Color(0, 0, 0));
			g2.fillRect(0, 0, w, h);
			g2.drawImage(bi, 0, 0, null);
			g2.dispose();
			g = (Graphics2D) bi.getGraphics();
			g.setColor(new Color(0, 0, 0, 127));
			g.fillRect(0, 0, w, h);
			g.dispose();
			
			if (Thread.currentThread().getId() != IGM2E.threadId) {
				LTCommand cmd = new LTCommand() {
					@Override
					public String name() {
						return "Converting Texture for Level BG and outside";
					}
					
					@Override
					public void handle() {
						bg = TextureUtil.convert(bi);
						og = TextureUtil.convert(bi2);
					}

					@Override
					public String loadText() {
						return "Converting Texture for Level BG and outside";
					}
				};
				LTCThread t = new LTCThread(cmd);
				IGM2E.ltcmds.add(t);
				t.join();
			} else {
				bg = TextureUtil.convert(bi);
				og = TextureUtil.convert(bi2);
			}
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	@Override
	public void render() {
		
		updateVP();
		
		int xoffs = xo % 16;
		int yoffs = yo % 16;
		
		int ww = 256;
		int hh = 256;
		
		//BG LAYER 0 : MAIN
		for (int x = -ww*2; x < wo+ww*2; x += ww) {
			for (int y = -hh*2; y < ho+hh*2; y += hh) {
				int xx = x - (int)((xo % ww) * 0.5);
				int yy = y - (int)((yo % hh) * 0.5);
				IGM2E.render(bg, xx, yy);
			}
		}
		
		//OUTSIDE LEFT
		for (int x = -ww; x > xo - ww*4 - wo; x -= ww) {
			for (int y = -((ho - yo) - ((ho-yo) % 32)); y < yo + ho; y += hh) {
				int xx = x - xo;
				int yy = y - yo;
				IGM2E.render(og, xx, yy);
			}
		}
		
		//OUTSIDE RIGHT
		for (int x = w; x < w + xo + ww*4 + wo; x += ww) {
			for (int y = -((ho - yo) - ((ho-yo) % 32)); y < yo + ho; y += hh) {
				int xx = x - xo;
				int yy = y - yo;
				IGM2E.render(og, xx, yy);
			}
		}
		
		//OUTSIDE TOP
		for (int x = xo - (xo % 32); x < xo + wo; x += ww) {
			for (int y = -hh; y > yo - hh*4 - ho; y -= hh) {
				int xx = x - xo;
				int yy = y - yo;
				IGM2E.render(og, xx, yy);
			}
		}
		
		//OUTSIDE BOTTOM
		for (int x = xo - (xo % 32); x < xo + wo; x += ww) {
			for (int y = h; y < h + yo + hh*4 + ho; y += hh) {
				int xx = x - xo;
				int yy = y - yo;
				IGM2E.render(og, xx, yy);
			}
		}
		
		tm.render(-xoffs, -yoffs, xo/16, yo/16, wo/16+16, ho/16+16, "rendertop", "false", "false");
		
		for (Tile rr : tiles) {
			if (rr.isStatic()) continue;
			rr.render(-xo, -yo);
		}
		
		for (Entity rr : ents) {
			rr.render(-xo, -yo);
		}
		
		tm.render(-xoffs, -yoffs, xo/16, yo/16, wo/16+16, ho/16+16, "rendertop", "true", "false");
		
		for (Tile rr : tiles) {
			if (rr.isStatic()) continue;
			rr.renderTop(-xo, -yo);
		}
		
		for (Entity rr : ents) {
			rr.renderTop(-xo, -yo);
		}
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
