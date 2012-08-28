package main;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMapPlus;

public class NextLevelTile extends Tile {
	
	public String nextmap;
	
	private int tick = 0;
	
	public NextLevelTile(Level level, int x, int y, TileSet tset, int id, int layer, String layername) {
		super(level, x, y, tset, id, layer, layername);
	}

	@Override
	public void tick() {
		
		if (tick > 8) {
			int px = x + IGM2E.rand.nextInt(17)-4;
			int py = y + IGM2E.rand.nextInt(17)-4;
			level.add2(new Particle(level, px, py, "gold", 10));
			
			tick = 0;
		} else {
			tick++;
		}
		
	}

	@Override
	public Texture getTexture() {
		return TextureBank.getTexture("exit_tile");
	}
	
	@Override
	public void collide(Entity e) {
		if (e instanceof Player) {
			level.resetMap();
			Player p = (Player)e;
			IGM2E.threadedLoadMap(nextmap);
		}
	}
	
}
