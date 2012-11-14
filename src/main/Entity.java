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

import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;

public abstract class Entity extends GameObject {
	
	public float walktick = 0;
	
	public float speed = 1.0f;
	public float donespeed = 0f;
	
	public int oldx;
	public int oldy;
	
	public float gx = 0f;
	public float gy = 0f;
	
	public float jumpf = 6.5f;
	public boolean doGrav = false;
	public boolean canJump = false;
	
	public float hTime = 10;
	public float hTick = 0;
	public float maxHealth;
	public float health;
	
	public Entity(Level level, int x, int y) {
		this.level = level;
		this.x = x;
		this.y = y;
		resetHealth(10);
	}
	
	@Override
	public void render(int xo, int yo) {
		if (level.gravity >= 0) {
			getImage().draw(xo+x, yo+y);
		} else {
			int w = getImage().getWidth();
			int h = getImage().getHeight();
			getImage().draw(xo+x, yo+y+h, w, -h);
		}
	}
	
	@Override
	public void tick() {
		if (level == null) level = IGM2E.level;
		
		gx = gx * level.friction;
		checkGravity();
		
		oldx = x;
		oldy = y;
		
		x += (int)gx;
		y += (int)gy;
		
		if (x != oldx) walktick += 0.25;
		if (walktick > 3) walktick = 0;
		
		checkCollide(level);
		
		if (x > level.w-16) {x = level.w-16; gx = 0;}
		if (x < 0) {x = 0; gx = 0;}
		if (y > level.h-16) {y = level.h-16; gy = 0;}
		if (y < 0) {y = 0; gy = 0;}
		
		if (health < maxHealth) {
			hTick++;
			if (hTick >= hTime) {
				hTick = 0;
				health += 1f;
			}
		} else {
			hTick = 0;
		}
		if (health > maxHealth) health = maxHealth;
	}
	
	public void checkCollide(Level l) {
		for (Tile t : l.tiles) {
			collide(t);
		}
		for (Entity e : l.ents) {
			collide(e);
		}
	}
	
	public void collide(Tile t) {
		if (!t.collides(this)) return;
		
		int tx = t.x;
		int ty = t.y;
		int tw = t.w;
		int th = t.h;
		
		collide(t, tx, ty, tw, th, t.canPass(this));
	}
	
	public void collide(Entity e) {
		if (!e.collides(this) || e == this || e.equals(this)) return;
		
		int ex = e.x;
		int ey = e.y;
		int ew = (int) e.getImage().getWidth();
		int eh = (int) e.getImage().getHeight();
		
		collide(e, ex, ey, ew, eh, false);
	}
	
	public void collideold(GameObject o, int ox, int oy, int ow, int oh, boolean canpass) {
		int w = (int) getImage().getWidth();
		int h = (int) getImage().getHeight();
		
		Rectangle ery = new Rectangle(x+1, y, w-2, h);
		Rectangle erx = new Rectangle(x, y+1, w, h-2);
		
		Rectangle or = new Rectangle(ox, oy, ow, oh);
		
		doGrav = true;
		
		if (erx.intersects(or)) {
			if (!canpass) {
				x = oldx;
				gx = 0;
			}
		}
		
		if (ery.intersects(or)) {
			if (!canpass) {
				y = oldy;
				gy = 0;
				doGrav = false;
			}
		}
		
	}
	
	//FIXME : Rewrite for feedback , not oldx and oldy ( then fix stutter ) and debug per-pixel-check
	public void collide(GameObject o, int ox, int oy, int ow, int oh, boolean canpass) {
		
		boolean hurt = false; // Fixes double-hit when X and Y collide the same hurting object .
		boolean tilech = false; // Fixes double-tile.collide(this); when X and Y collide the same tile .
		int tilelh = 0; // Needed for tile.left(this); to only happen when X and Y won't collide " this tile " .
		
		int w = (int) getImage().getWidth();
		int h = (int) getImage().getHeight();
		
		Rectangle er = new Rectangle(x, y, w, h);
		Rectangle ery = new Rectangle(x+1, y, w-2, h);
		Rectangle erx = new Rectangle(x, y+1, w, h-2);
		
		Rectangle or = new Rectangle(ox, oy, ow, oh);
		
		doGrav = true;
		
		if (erx.intersects(or)) {
			if (Collision.overlapps(level, this, er, erx, or, x, y, w, h, o, ox, oy, ow, oh)) {
				if (!canpass) {
					x = oldx;
					gx = 0;
				}
				
				if (o.damages(this)) {
					hurt(o);
					hurt = true;
				}
				
				if (o instanceof Tile) {
					((Tile)o).collide(this);
					tilech = true;
				}
			} else {
				tilelh++;
			}
		} else {
			tilelh++;
		}
		
		if (ery.intersects(or)) {
			if (Collision.overlapps(level, this, er, ery, or, x, y, w, h, o, ox, oy, ow, oh)) {
				if (!canpass) {
					y = oldy;
					gy = 0;
					doGrav = false;
					canJump = true;
				}
				
				if (!hurt) {
					if (o.damages(this)) {
						hurt(o);
					}
				}
				
				if (!tilech) {
					if (o instanceof Tile) {
						((Tile)o).collide(this);
						tilech = true;
					}
				}
			} else {
				tilelh++;
			}
		} else {
			tilelh++;
		}
		
		if (tilelh == 2) {
			if (o instanceof Tile) {
				((Tile)o).left(this);
			}
		}
		
	}
	
	public void checkGravity() {
		if (doGrav) {
			gy += level.gravity;
			doGrav = false;
		} else {
		}
	}
	
	public boolean collides(Entity entity) {
		return true;
	}
	
	public void hurt(GameObject go) {
		float damage = go.getDamage(this);
		this.health -= damage;
	}
	
	public void resetHealth(float h) {
		health = maxHealth = h;
	}
	
	public static Entity getBaseEntity() {
		Entity e = new Entity(IGM2E.level, -1, -1) {

			@Override
			public Image getImage() {
				return ImageBank.getImage("empty");
			}
			
		};
		return e;
	}
	
	public void jump() {
		if (canJump) {
			gy -= level.jumpf * jumpf;
			canJump = false;
			
			IGM2E.playSound("jump", x, y);
		}
	}
}
