package main;

import java.awt.Font;
import java.awt.image.BufferedImage;

import org.newdawn.slick.opengl.Texture;

public class LoadingLevelAnimation extends Background {
	
	int tick2 = 0;
	
	public LoadThread lt;
	public boolean done = false;
	
	public LoadingLevelAnimation(LoadThread lt, int w, int h) {
		super(0xff000000, w, h);
		this.lt = lt;
		
		this.lt.start();
	}
	
	@Override
	public void render() {
		try {
			
			Texture bg = TextureUtil.colorTexture(0xffffff, w, h);
			
			IGM2E.render(bg, 0, 0);
			
			Texture tex = TextureBank.getTexture("miniload_"+tick2);
			
			int x2 = 8;
			int y2 = h - tex.getTextureHeight() - 8;
			
			IGM2E.render(tex, x2, y2);
			
			LTCommand cmd = lt.getCurrent();
			String text = null;
			if (cmd != null) text = cmd.loadText();
			if (text == null) text = "Loading";
			Font font = Resources.Fonts.ubuntul.deriveFont(10f);
			Texture texttex = TextFactory.toTexture(text, 0xff113344, font, true);
			
			FontRenderContext frc = new FontRenderContext();
			int textw = (int)(font.getStringBounds(text, frc).getWidth());
			
			IGM2E.render(texttex, x2+16+8, y2);
		} catch (Exception e) {
		}
	}

	@Override
	public void tick() {
		lt.tick();
		if (lt.done()) this.done = true;
		//if (done) return;
		tick2 += 1;
		if (tick2 > 16) {
			tick2 = 1;
		}
	}

}
