package main;

/**
 *	Camera class specially made for LoadedLevel render events.
 *	
 */
public class LoadedLevelCam extends Camera {
	
	public LoadedLevelCam(LoadedLevel level) {
		super(level);
	}
	
	@Override
	public void renderImpl() {
		LoadedLevel level = (LoadedLevel)this.level;
		
		if (level == null) throw new NullPointerException("Level == null");
		
		int xoffs = xo % 16;
		int yoffs = yo % 16;
		
		int ww = 256;
		int hh = 256;
		
		//BG LAYER 0 : MAIN
		for (int x = -ww*2; x < wo+ww*2; x += ww) {
			for (int y = -hh*2; y < ho+hh*2; y += hh) {
				int xx = x - (int)((xo % ww) * 0.5);
				int yy = y - (int)((yo % hh) * 0.5);
				level.bg.draw(xx, yy);
			}
		}
		
		//OUTSIDE LEFT
		for (int x = -ww; x > xo - ww*4 - wo; x -= ww) {
			for (int y = -((ho - yo) - ((ho-yo) % 32)); y < yo + ho; y += hh) {
				int xx = x - xo;
				int yy = y - yo;
				level.og.draw(xx, yy);
			}
		}
		
		//OUTSIDE RIGHT
		for (int x = level.w; x < level.w + xo + ww*4 + wo; x += ww) {
			for (int y = -((ho - yo) - ((ho-yo) % 32)); y < yo + ho; y += hh) {
				int xx = x - xo;
				int yy = y - yo;
				level.og.draw(xx, yy);
			}
		}
		
		//OUTSIDE TOP
		for (int x = xo - (xo % 32); x < xo + wo; x += ww) {
			for (int y = -hh; y > yo - hh*4 - ho; y -= hh) {
				int xx = x - xo;
				int yy = y - yo;
				level.og.draw(xx, yy);
			}
		}
		
		//OUTSIDE BOTTOM
		for (int x = xo - (xo % 32); x < xo + wo; x += ww) {
			for (int y = level.h; y < level.h + yo + hh*4 + ho; y += hh) {
				int xx = x - xo;
				int yy = y - yo;
				level.og.draw(xx, yy);
			}
		}
		
		level.tm.render(-xoffs, -yoffs, xo/16, yo/16, wo/16+16, ho/16+16, "rendertop", "false", "false");
		
		for (Tile rr : level.tiles) {
			if (rr.isStatic()) continue;
			rr.render(-xo, -yo);
		}
		
		for (Entity rr : level.ents) {
			rr.render(-xo, -yo);
		}
		
		level.tm.render(-xoffs, -yoffs, xo/16, yo/16, wo/16+16, ho/16+16, "rendertop", "true", "false");
		
		for (Tile rr : level.tiles) {
			if (rr.isStatic()) continue;
			rr.renderTop(-xo, -yo);
		}
		
		for (Entity rr : level.ents) {
			rr.renderTop(-xo, -yo);
		}
	}
	
}
