package main;

import java.awt.Font;
import java.awt.image.BufferedImage;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;
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
			
			Image bg = ImageBank.getImage("white");
			
			bg.draw(0, 0, w, h, new Color(1f, 1f, 1f));
			
			Image tex = ImageBank.getImage("miniload_"+tick2);
			
			int x2 = 8;
			int y2 = h - tex.getHeight() - 8;
			
			tex.draw(x2, y2);
			
			LTCommand cmd = lt.getCurrent();
			String text = null;
			if (cmd != null) text = cmd.loadText();
			if (text == null) text = "Loading";
			
			UnicodeFont f = Fonts.resizeFont(Fonts.normal, 10);
			f = Fonts.refilter(f, Fonts.newGradient(0x113344));
			
			int textw = (int)(f.getWidth(text));
			f.drawString(x2+16+8, y2, text);
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
