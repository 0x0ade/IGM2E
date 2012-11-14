package main;

import java.awt.image.BufferedImage;

import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;

public class LoadingAnimation extends Background {
	
	int tick2 = 0;
	
	public LoadThread lt;
	public boolean done = false;
	
	public LoadingAnimation(LoadThread lt, int w, int h) {
		super(0xff000000, w, h);
		this.lt = lt;
		
		this.lt.start();
	}
	
	@Override
	public void render() {
		try {
			
			Image bg = ImageBank.getImage("loading");
			
			bg.draw(0, 0);
			
			Image tex = ImageBank.getImage("load_"+tick2);
			
			int x2 = w/2 - tex.getWidth()/2;
			int y2 = h/2 - tex.getHeight()/2;
			
			tex.draw(x2, y2);
			
			LTCommand cmd = lt.getCurrent();
			String text = null;
			if (cmd != null) text = cmd.loadText();
			if (text == null) text = "Loading";
			//Texture texttex = TextFactory.toTexture(text, 0xff444444, Resources.Fonts.ubuntul, true);
			Font f = Fonts.normal;
			int textw = (int)(f.getWidth(text));
			//IGM2E.render(texttex, w/2 - textw/2, y2 + tex.getTextureHeight() + 4);
			f.drawString(w/2 - textw/2, y2 + 24 + 4, text);
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
