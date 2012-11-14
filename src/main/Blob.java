package main;

import java.awt.image.BufferedImage;

import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;

public class Blob extends Mob {
	
	public Blob(Level level, int x, int y) {
		super(level, x, y);
		
		System.out.println("Hi, I am BLOB_"+x+"_"+y+" !");
	}

	@Override
	public void tick() {
		super.tick();
	}
	
	@Override
	public Image getImage() {
		String dir = "2_";
		if (donespeed < 0) dir = "1_";
		if (donespeed > 0) dir = "3_";
		String add = dir+((int)walktick+1);
		return ImageBank.getImage("blob_"+add);
	}
	
}
