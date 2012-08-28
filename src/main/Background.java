package main;

import java.io.IOException;

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
		try {
			Texture t = TextureUtil.colorTexture(color, w, h);
			IGM2E.render(t, 0, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
