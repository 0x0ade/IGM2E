package main;

import java.io.IOException;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;

public class Background implements RRenderer, Tickable {
	
	public int color;
	public int w;
	public int h;
	
	public Background(int w, int h) {
		this.color = 0xffffffff;
		this.w = w;
		this.h = h;
	}
	
	public Background(int color, int w, int h) {
		this.color = color;
		this.w = w;
		this.h = h;
	}
	
	@Override
	public void tick() {
		w = IGM2E.w;
		h = IGM2E.h;
	}

	@Override
	public void render() {
		Image i = ImageBank.getImage("white");
		i.draw(0, 0, w, h, new Color(color));
	}

}
