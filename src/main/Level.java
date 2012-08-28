package main;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Level extends MenuBase implements Cloneable, Serializable {
	
	private int cloned = 0;
	
	public int w = 0;
	public int h = 0;
	
	public int xo = 0;
	public int yo = 0;
	public int wo = 0;
	public int ho = 0;
	
	public ArrayList<Tile> tiles = new ArrayList<Tile>();
	public ArrayList<Entity> ents = new ArrayList<Entity>();
	public ArrayList<Tile> tilesrem = new ArrayList<Tile>();
	public ArrayList<Entity> entsrem = new ArrayList<Entity>();
	public ArrayList<Tile> tilesadd = new ArrayList<Tile>();
	public ArrayList<Entity> entsadd = new ArrayList<Entity>();
	public float friction = 0.75f;
	public float gravity = 0.25f;
	public float jumpf = 1f;
	
	public Player player;
	
	public ViewportBox vp = new ViewportBox(IGM2E.size);
	public ViewportBox vpin = new ViewportBox(512, 512);
	
	public Level(int w, int h) {
		this.w = w;
		this.h = h;
	}
	
	/**
	 * Neutralizing constructor <p>
	 * Resets any subclass of Level into the Level class , usefull for saving 
	 */
	public Level(Level level) {
		w = level.w;
		h = level.h;
		
		xo = level.xo;
		yo = level.yo;
		wo = level.wo;
		ho = level.ho;
		
		tiles = level.tiles;
		ents = level.ents;
		tilesrem = level.tilesrem;
		entsrem = level.entsrem;
		tilesadd = level.tilesadd;
		entsadd = level.entsadd;
		friction = level.friction;
		gravity = level.gravity;
		jumpf = level.jumpf;
		
		player = level.player;
		
		vp = level.vp;
		vpin = level.vpin;
	}
	
	public Level() {
		this(0, 0);
	}

	public void add(Tile t) {
		tiles.add(t);
	}
	
	public void add(Entity e) {
		ents.add(e);
		if (e instanceof Player) if (player == null) player = (Player)e;
	}
	
	public void setPlayer(Player p) {
		player = p;
	}
	
	@Override
	public void render() {		
		
		updateVP();
		
		for (Entity rr : ents) {
			rr.render(-xo, -yo);
		}
		
		for (Tile rr : tiles) {
			rr.render(-xo, -yo);
		}
		
		for (Entity rr : ents) {
			rr.renderTop(-xo, -yo);
		}
		
		for (Tile rr : tiles) {
			rr.renderTop(-xo, -yo);
		}
	}
	
	public void updateVP() {
		if (player != null) {
			vpin.setPlayerPos(player);
			
			vp.setPlayerPos(player);
			
			// FIXME : Do viewport updating when vpin no more inside of vp - scroll ... 
			
		} else {
		}
		xo = vp.x;
		yo = vp.y;
		wo = vp.width;
		ho = vp.height;
	}
	
	@Override
	public void tick() {
		
		for (Tile tt : tiles) {
			tt.tick();
		}
		
		for (Entity tt : ents) {
			tt.tick();
		}
		
		for (Tile tt : tilesrem) {
			tiles.remove(tt);
		}
		tilesrem.clear();
		
		for (Entity tt : entsrem) {
			ents.remove(tt);
		}
		entsrem.clear();
		
		for (Tile tt : tilesadd) {
			tiles.add(tt);
		}
		tilesadd.clear();
		
		for (Entity tt : entsadd) {
			ents.add(tt);
		}
		entsadd.clear();
		
	}
	
	public void add2(Tile t) {
		tilesadd.add(t);
	}
	
	public void add2(Entity e) {
		entsadd.add(e);
	}
	
	public void remove(Entity e) {
		entsrem.add(e);
	}
	
	public void remove(Tile t) {
		tilesrem.add(t);
	}
	
	public void resetMap() {
		for (Tile rr : tiles) {
			rr.reset();
		}
		
		for (Entity rr : ents) {
			rr.reset();
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
		
		i = 0;
		for (Tile rr : tiles) {
			rr.level = null;
			tiles.set(i, rr);
			i++;
		}
		
		i = 0;
		for (Entity rr : ents) {
			rr.level = null;
			ents.set(i, rr);
			i++;
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
		
		i = 0;
		for (Tile rr : tiles) {
			rr.level = this;
			tiles.set(i, rr);
			i++;
		}
		
		i = 0;
		for (Entity rr : ents) {
			rr.level = this;
			ents.set(i, rr);
			i++;
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
		
		i = 0;
		for (Tile rr : tiles) {
			rr.prepareSave();
			tiles.set(i, rr);
			i++;
		}
		
		i = 0;
		for (Entity rr : ents) {
			rr.prepareSave();
			ents.set(i, rr);
			i++;
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
		
		i = 0;
		for (Tile rr : tiles) {
			rr.unprepareSave();
			tiles.set(i, rr);
			i++;
		}
		
		i = 0;
		for (Entity rr : ents) {
			rr.unprepareSave();
			ents.set(i, rr);
			i++;
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
