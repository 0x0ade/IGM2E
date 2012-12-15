package main;

import java.util.ArrayList;

public class LevelLayer {
	
	public Level level;
	
	public LevelLayer(Level level) {
		this.level = level;
	}
	
	public ArrayList<Tile> tiles = new ArrayList<Tile>();
	public ArrayList<Entity> ents = new ArrayList<Entity>();
	public ArrayList<Tile> tilesrem = new ArrayList<Tile>();
	public ArrayList<Entity> entsrem = new ArrayList<Entity>();
	public ArrayList<Tile> tilesadd = new ArrayList<Tile>();
	public ArrayList<Entity> entsadd = new ArrayList<Entity>();
	
}
