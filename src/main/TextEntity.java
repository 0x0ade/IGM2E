package main;

import java.awt.Font;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.opengl.Texture;

public class TextEntity extends Entity {
	
	public String text = "";
	protected String bb = "";
	public String bold = "";
	protected String ss = "";
	public String size = "";
	
	public TextEntity(LevelLayer layer, int x, int y) {
		super(layer, x, y);
	}
	
	UnicodeFont font;
	
	@Override
	public void tick() {
		if (	!bb.equals(bold)||
				!ss.equals(size)) {
			Font base = Fonts.normal.getFont();
			if (bold.equals("true")) {
				base = Fonts.bold.getFont();
			}
			base.deriveFont(Float.parseFloat(size));
			font = new UnicodeFont(base);
			bb = bold;
			ss = size;
		}
	}
	
	@Override
	public void render(int xo, int yo) {
		font.drawString(x+xo, y+yo, text);
	}

	@Override
	public Image getImage() {
		try {
			return new Image(font.getWidth(text), font.getHeight(text));
		} catch (SlickException e) {
			e.printStackTrace();
			return ImageBank.getImage("empty");
		}
	}
	
	@Override
	public void checkCollide() {
	}
	
	@Override
	public boolean collides(Entity e) {
		return false;
	}

}
