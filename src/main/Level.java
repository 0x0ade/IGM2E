package main;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.tiled.Layer;

public class Level extends MenuBase implements Cloneable, Serializable {
	
	private int cloned = 0;
	
	public int w = 0;
	public int h = 0;
	
	public HashMap<Integer, LevelLayer> layers = new HashMap<Integer, LevelLayer>();
	{
		for (int i = -100; i < 100; i++) {
			layers.put(i, new LevelLayer(this));
		}
	}
	public float friction = 0.75f;
	public float gravity = 0.25f;
	public float jumpf = 1f;
	
	public Player player;
	public Camera cam;
	
	public Level(int w, int h) {
		this.w = w;
		this.h = h;
		
		cam = new Camera(this);
	}
	
	/**
	 * Neutralizing constructor <p>
	 * Resets any subclass of Level into the Level class , usefull for saving 
	 */
	public Level(Level level) {
		w = level.w;
		h = level.h;
		
		layers = (HashMap<Integer, LevelLayer>) level.layers.clone();
		friction = level.friction;
		gravity = level.gravity;
		jumpf = level.jumpf;
		
		player = level.player;
		
		cam = level.cam;
	}
	
	public Level() {
		this(0, 0);
	}

	public void add(Tile t) {
		t.layer.tiles.add(t);
	}
	
	public void add(Entity e) {
		e.layer.ents.add(e);
		if (e instanceof Player) if (player == null) player = (Player)e;
	}
	
	public void setPlayer(Player p) {
		player = p;
	}
	
	@Override
	public void render() {
		cam.render();
	}
	
	@Override
	public void tick() {
		for (LevelLayer layer : layers.values()) {
			for (Tile tt : layer.tiles) {
				tt.tick();
			}
			
			for (Entity tt : layer.ents) {
				tt.tick();
			}
			
			for (Tile tt : layer.tilesrem) {
				layer.tiles.remove(tt);
			}
			layer.tilesrem.clear();
			
			for (Entity tt : layer.entsrem) {
				layer.ents.remove(tt);
			}
			layer.entsrem.clear();
			
			for (Tile tt : layer.tilesadd) {
				layer.tiles.add(tt);
			}
			layer.tilesadd.clear();
			
			for (Entity tt : layer.entsadd) {
				layer.ents.add(tt);
			}
			layer.entsadd.clear();
		}
		
	}
	
	public void add2(Tile t) {
		t.layer.tilesadd.add(t);
	}
	
	public void add2(Entity e) {
		e.layer.entsadd.add(e);
	}
	
	public void remove(Entity e) {
		e.layer.entsrem.add(e);
	}
	
	public void remove(Tile t) {
		t.layer.tilesrem.add(t);
	}
	
	public void resetMap() {
		for (LevelLayer layer : layers.values()) {
			for (Tile rr : layer.tiles) {
				rr.reset();
			}
			
			for (Entity rr : layer.ents) {
				rr.reset();
			}
		}
	}
	
	/**
	 * Ran before saving with Savegame or exiting to main menu due to various factors , examples : 
	 * - TiledMap in LoadedLevel not serializable 
	 * - Images or Textures not serializable 
	 * @see Savegame
	 * @see reinit
	 * @see prepareSave
	 * @see unprepareSave
	 */
	public void uninit() {
		int i = 0;
		for (LevelLayer layer : layers.values()) {
			i = 0;
			for (Tile rr : layer.tiles) {
				rr.level = null;
				i++;
			}
			
			i = 0;
			for (Entity rr : layer.ents) {
				rr.level = null;
				i++;
			}
		}
		
		if (player != null) {
			player.level = null;
		}
		
	}
	
	/**
	 * Ran after loading with Savegame due to various factors , examples : 
	 * - TiledMap in LoadedLevel not serializable 
	 * - Images or Textures not serializable 
	 * @see Savegame
	 * @see uninit
	 * @see prepareSave
	 * @see unprepareSave
	 */
	public void reinit() {
		int i = 0;
		for (LevelLayer layer : layers.values()) {
			i = 0;
			for (Tile rr : layer.tiles) {
				rr.level = this;
				i++;
			}
			
			i = 0;
			for (Entity rr : layer.ents) {
				rr.level = this;
				i++;
			}
		}
		
		if (player != null) {
			player.level = this;
		}
		
	}
	
	/**
	 * Ran before saving with Savegame or exiting to main menu due to various factors , examples : 
	 * - LogicActions need to be unhandled , then rehandled due to map behaviour
	 * @see Savegame
	 * @see reinit
	 * @see uninit
	 * @see unprepareSave
	 */
	public void prepareSave() {
		int i = 0;
		for (LevelLayer layer : layers.values()) {
			i = 0;
			for (Tile rr : layer.tiles) {
				rr.prepareSave();
				i++;
			}
			
			i = 0;
			for (Entity rr : layer.ents) {
				rr.prepareSave();
				i++;
			}
		}
		
	}
	
	/**
	 * Ran after loading with Savegame due to various factors , examples : 
	 * - LogicActions need to be unhandled , then rehandled due to map behaviour
	 * @see Savegame
	 * @see reinit
	 * @see uninit
	 * @see prepareSave
	 */
	public void unprepareSave() {
		int i = 0;
		for (LevelLayer layer : layers.values()) {
			i = 0;
			for (Tile rr : layer.tiles) {
				rr.unprepareSave();
				i++;
			}
			
			i = 0;
			for (Entity rr : layer.ents) {
				rr.unprepareSave();
				i++;
			}
		}
		
	}
	
	@Override
	public Level clone() {
		try {
			Level clone = (Level) super.clone();
			clone.cloned++;
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
