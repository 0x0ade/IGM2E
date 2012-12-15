package main;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.tiled.TileSet;

public class PixelTile extends Tile {
	
	public int c;
	
	public PixelTile(LevelLayer layer, int x, int y, TileSet tset, int id, int tlayer, String layername, int c) {
		super(layer, x, y, tset, id, tlayer, layername);
		this.c = c;
		for (Tile tt : layer.tiles) {
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
	public Image getImage() {
		Color cc = new Color(c);
		Image i = ImageBank.getImage("white").getScaledCopy(1f);
		i.setImageColor(cc.r, cc.g, cc.b);
		return i;
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
