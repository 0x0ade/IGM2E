package main;

import java.awt.image.BufferedImage;

import org.newdawn.slick.opengl.Texture;

public abstract class Player extends Entity {
	
	public Player(Level level, int x, int y) {
		super(level, x, y);
	}

	@Override
	public void tick() {
		super.tick();
	}
	
	@Override
	public Texture getTexture() {
		String dir = "2_";
		if (donespeed < 0) dir = "1_";
		if (donespeed > 0) dir = "3_";
		String add = dir+((int)walktick+1);
		return TextureBank.getTexture("player_"+add);
	}
	
	@Override
	public BufferedImage getImage() {
		String dir = "2_";
		if (donespeed < 0) dir = "1_";
		if (donespeed > 0) dir = "3_";
		String add = dir+((int)walktick+1);
		return TextureBank.getImage("player_"+add);
	}
	
}
