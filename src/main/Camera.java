/**
 * 
 */
package main;

import org.newdawn.slick.opengl.renderer.Renderer;

public class Camera implements RRenderer {
	
	public Level level;
	
	public int xo = 0;
	public int yo = 0;
	public int wo = 0;
	public int ho = 0;
	
	public ViewportBox vp = new ViewportBox(IGM2E.size);
	public ViewportBox vpin = new ViewportBox(512, 512);
	
	public GameObject allocated;
	
	public Camera(Level level) {
		this.level = level;
	}
	
	@Override
	public final void render() {
		if (allocated == null) {
			allocated = level.player;
		}
		if (allocated != null) {
			vpin.setCenter(allocated);
			
			vp.setCenter(allocated);
			
		} else {
		}
		xo = vp.x;
		yo = vp.y;
		wo = vp.width;
		ho = vp.height;
		
		renderImpl();
	}
	
	public void renderImpl() {
		for (LevelLayer layer : level.layers.values()) {
			for (Entity rr : layer.ents) {
				rr.render(-xo, -yo);
			}
			
			for (Tile rr : layer.tiles) {
				rr.render(-xo, -yo);
			}
			
			for (Entity rr : layer.ents) {
				rr.renderTop(-xo, -yo);
			}
			
			for (Tile rr : layer.tiles) {
				rr.renderTop(-xo, -yo);
			}
		}
	}

}
