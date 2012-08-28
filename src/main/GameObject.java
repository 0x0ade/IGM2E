package main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import org.newdawn.slick.opengl.Texture;

public abstract class GameObject implements RRenderable, RTRenderable, Tickable, Serializable, Cloneable {
	
	private int cloned = 0;
	
	public Level level;
	public int x;
	public int y;
	
	public abstract Texture getTexture();
	
	public BufferedImage getImage() {
		return TextureUtil.convert(getTexture());
	}

	public BufferedImage getCollisionImage() {
		return getImage();
	}
	
	public boolean damages(Entity e) {
		return false;
	}
	
	public float getDamage(Entity e) {
		return 0.75f;
	}
	
	@Override
	public void renderTop(int xo, int yo) {
	}
	
	public void reset() {
	}
	
	public void prepareSave() {
	}
	
	public void unprepareSave() {
	}
	
	@Override
	public GameObject clone() {
		try {
			GameObject clone = (GameObject) super.clone();
			clone.cloned++;
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
