package main;

import java.awt.image.BufferedImage;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.Texture;

public class Blob extends Mob {
	
	public Blob(LevelLayer layer, int x, int y) {
		super(layer, x, y);
		
		System.out.println("Hi, I am BLOB_"+x+"_"+y+" !");
	}

	@Override
	public void tick() {
		super.tick();
	}
	
	@Override
	public Image getImage() {
		int dir = 1;
		if (donespeed < 0) dir = 0;
		if (donespeed > 0) dir = 1;
		int frame = (int) walktick;
		SpriteSheet sheet = (SpriteSheet) ImageBank.getImage("blob");
		return sheet.getSubImage(frame, dir);
	}
	
}
