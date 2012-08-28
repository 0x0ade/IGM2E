package main;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.tiled.TileSet;

public class PixelTile extends Tile {
	
	public Texture t;
	
	public PixelTile(Level level, int x, int y, TileSet tset, int id, int layer, String layername, Texture t) {
		super(level, x, y, tset, id, layer, layername);
		this.t = t;
		for (Tile tt : level.tiles) {
			if (!(tt instanceof PixelTile)) continue;
			if (tt.x == x && tt.y == y) level.remove(tt);
		}
		w = 1;
		h = 1;
	}
	
	@Override
	public void tick() {
	}
	
	@Override
	public Texture getTexture() {
		return t;
	}
	
	@Override
	public boolean collides(Entity e) {
		return false;
	}
	
	@Override
	public boolean isStatic() {
		return false;
	}
	
}
