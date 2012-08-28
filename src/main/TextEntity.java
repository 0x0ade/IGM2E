package main;

import org.newdawn.slick.opengl.Texture;

public class TextEntity extends Entity {
	
	public String text;
	public String bold;
	public String size;
	
	public TextEntity(Level level, int x, int y) {
		super(level, x, y);
	}

	@Override
	public void render(int xo, int yo) {
		IGM2E.render(getTexture(), xo+x, yo+y);
	}

	@Override
	public Texture getTexture() {
		if (bold.equals("true")) {
			return TextFactory.toTexture(text, 0xffffffff, Resources.Fonts.ubuntub.deriveFont(Float.parseFloat(size)), true);
		} else {
			return TextFactory.toTexture(text, 0xffffffff, Resources.Fonts.ubuntul.deriveFont(Float.parseFloat(size)), true);
		}
		
		//return TextFactory.toTexture(text, 0xffffffff, true);
	}
	
	@Override
	public void checkCollide(Level l) {
	}
	
	@Override
	public boolean collides(Entity e) {
		return false;
	}

}
