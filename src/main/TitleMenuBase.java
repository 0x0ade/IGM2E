package main;

import org.newdawn.slick.opengl.Texture;

public abstract class TitleMenuBase extends MenuBase {
	
	public int item = 0;
	public int iw = 128;
	public int ih = 32;
	public int cw = 32;
	public int ch = 32;
	
	public float cx = 0;
	public float cy = 0;
	public float cgtx = 0;
	public float cgty = 0;
	public float cgx = 0;
	public float cgy = 0;
	
	@Override
	public void tick() {
		
		float maxx = 0;
		float maxy = ih*getMaxItems();
		
		if (IGM2E.keys.enter.wasPressed()) {
			handleItem();
			cx = cgtx;
			cy = cgty;
			cgx = 0;
			cgy = 0;
			return;
		}
		if (IGM2E.keys.up.wasPressed()) {
			cgty = cgty - ih;
		}
		if (IGM2E.keys.down.wasPressed()) {
			cgty = cgty + ih;
		}
		if (IGM2E.keys.left.wasPressed()) {
			cgtx = cgtx - iw;
		}
		if (IGM2E.keys.right.wasPressed()) {
			cgtx = cgtx + iw;
		}
		
		if (cgtx < 0) cgtx = 0;
		if (cgtx > maxx) cgtx = maxx;
		if (cgty < 0) cgty = 0;
		if (cgty > maxy-ih) cgty = maxy-ih;
		
		cx += cgx;
		cy += cgy;
		
		if (cx < 0) cx = 0;
		if (cx > maxx) cx = maxx;
		if (cy < 0) cy = 0;
		if (cy > maxy) cy = maxy;
		
		cgx = cgtx - cx;
		cgy = cgty - cy;
		cgx = cgx * 0.25f;
		cgy = cgy * 0.25f;
		
		item = (int)((cgty / ih) + ((cgtx / iw)*getMaxItems()));
	}
	
	@Override
	public void render() {
		float maxx = 0;
		float maxy = ih*getMaxItems();
		
		IGM2E.render(TextureBank.getTexture("cursor"), getX()+cx, getY()+cy);
		
		String[] list = getList();
		
		for (int i = 0; i < list.length; i++) {
			int yy = (i * ih);
			int xx = (int)((int)(yy / maxy)%maxx);
			yy = (int)(yy%maxy);
			xx = xx + cw + 4;
			Texture t = TextFactory.toTexture(list[i], 0xffffffff, Resources.Fonts.ubuntub.deriveFont(24f), true);
			IGM2E.render(t, getX() + xx, getY() + yy);
		}
	}
	
	public abstract void handleItem();
	
	public abstract String[] getList();
	
	public abstract int getMaxItems();
	
	public abstract int getX();
	
	public abstract int getY();
	
}
