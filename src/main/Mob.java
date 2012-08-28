package main;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

public abstract class Mob extends Entity {
	
	public Mob(Level level, int x, int y) {
		super(level, x, y);
		
		donespeed = speed;
		jumpf = 5f;
	}
	
	@Override
	public void tick() {
		if (level == null) level = IGM2E.level;
		
		handleAI(level);
		gx += donespeed;
		
		super.tick();
	}
	
	public void handleAI(Level l) {
		aijump = true;
		aijump2 = false;
		aichangedir = false;
		aiothers = new ArrayList<Mob>();
		
		for (Tile t : l.tiles) {
			checkAI(t);
		}
		for (Entity e : l.ents) {
			checkAI(e);
		}
		
		if (aijump2) {
			jump();
		}
		
		if (aichangedir) {
			donespeed = -donespeed;
		}
	}
	
	public void checkAI(Tile t) {
		if (!t.collides(this)) return;
		
		int tx = t.x;
		int ty = t.y;
		int tw = t.w;
		int th = t.h;
		
		checkAI(t, tx, ty, tw, th, t.canPass(this));
	}
	
	public void checkAI(Entity e) {
		if (!e.collides(this) || e == this || e.equals(this)) return;
		
		int ex = e.x;
		int ey = e.y;
		int ew = (int) e.getTexture().getImageWidth();
		int eh = (int) e.getTexture().getImageHeight();
		
		checkAI(e, ex, ey, ew, eh, false);
	}
	
	boolean aijump;
	boolean aijump2;
	boolean aijumping = false;
	boolean aichangedir;
	public int checkw = 16;
	public int checkw2 = 16;
	public int checkh = 16*2;
	public int checkh2 = 16;
	public ArrayList<Mob> aiothers = new ArrayList<Mob>();
	
	public void checkAI(GameObject o, int ox, int oy, int ow, int oh, boolean canpass) {
		int w = (int) getTexture().getImageWidth();
		int h = (int) getTexture().getImageHeight();
		
		boolean addw = (donespeed>=0);
		
		Rectangle ery = new Rectangle(x+1-(!addw?checkw2:0), y-checkh, w-2+(addw?checkw2:0), checkh);
		if (level.gravity < 0) {
			ery.y = y+h;
		}
		Rectangle erye = new Rectangle(ery.x+ery.width-checkw2, ery.y+ery.height-checkh2, checkw2, checkh2);
		Rectangle ery2 = new Rectangle(x+1, y-1, w-2, h+2);
		Rectangle erx = new Rectangle(x-(!addw?checkw:0), y+1, w+(addw?checkw:0), h-2);
		Rectangle erx2 = new Rectangle(x, y+1, w, h-2);
		if (donespeed > 0) {
			erx2.width = erx2.width+checkw;
		} else if (donespeed < 0) {
			erx2.x = erx2.x-checkw;
		}
		
		Rectangle or = new Rectangle(ox, oy, ow, oh);
		
		if (ery.intersects(or)) {
			if (!canpass) {
				if (!erye.intersects(or)) {
					aijump = false;
					aijump2 = false; //Aborts any current jump task sheudle...
				}
			}
		}
		
		if (ery2.intersects(or)) {
			if (!canpass) {
				aijumping = false;
			}
		}
		
		if (erx.intersects(or)) {
			if (!canpass) {
				if (aijump) {
					if (o instanceof Mob) {
						if (((Mob)o).aiothers.contains(this)) {
							aichangedir = true;
							return;
						} else {
							aiothers.add((Mob)o);
						}
 					}
					aijump2 = true;
					aijumping = true;
				} else if (!aijumping) {
					aichangedir = true;
				}
			}
		}
		
	}
	
}
