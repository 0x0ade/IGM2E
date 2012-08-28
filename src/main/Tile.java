package main;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.tiled.TileSet;

public abstract class Tile extends GameObject {
	
	public int w = 16;
	public int h = 16;
	public int id;
	public int layer;
	public String layername;
	
	public boolean renderTop = false;
	
	private boolean overcanpass = false;
	private boolean canpass = false;
	private boolean overcanpasslogic = false;
	private boolean canpasslogic = false;
	
	public Tile(Level level, int x, int y, TileSet tset, int id, int layer, String layername) {
		this.level = level;
		this.x = x;
		this.y = y;
		this.id = id;
		this.layer = layer;
		this.layername = layername;
	}
	
	@Override
	public void render(int xo, int yo) {
		if (!renderTop) IGM2E.render(getTexture(), x+xo, y+yo);
	}
	
	@Override
	public void renderTop(int xo, int yo) {
		if (renderTop) IGM2E.render(getTexture(), x+xo, y+yo);
	}
	
	public boolean collides(Entity e) {
		return true;
	}
	
	public final boolean canPass(Entity e) {
		return (overcanpasslogic)?canpasslogic:((overcanpass)?canpass:canPassOrig(e));
	}
	
	public boolean canPassOrig(Entity e) {
		return false;
	}

	public void overrideCanPass(boolean b) {
		overcanpass = b;
	}
	
	public void setCanPass(boolean b) {
		canpass = b;
	}
	
	public void overrideCanPassByLogic(boolean b) {
		overcanpasslogic = b;
	}
	
	public void setCanPassByLogic(boolean b) {
		canpasslogic = b;
	}
	
	public void collide(Entity e) {
	}
	
	public void left(Entity e) {
	}
	
	public boolean isStatic() {
		return true;
	}
	
}
