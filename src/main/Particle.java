package main;

import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;

public class Particle extends Entity {
	
	public String type;
	public int tick = 0;
	public int tickchange = 0;
	public int duration;
	public int id;
	
	public Particle(Level level, int x, int y, String type, int duration) {
		super(level, x, y);
		this.type = type;
		this.duration = duration;
		this.id = IGM2E.rand.nextInt(8)+1;
	}

	@Override
	public Image getImage() {
		return ImageBank.getImage("particles_"+type+"_"+id);
	}
	
	@Override
	public void tick() {
		if (tick > duration) {
			level.remove(this);
		}
		
		if (tickchange > 2) {
			id = IGM2E.rand.nextInt(8)+1;
			tickchange = 0;
		}
		
		tick++;
		tickchange++;
	}
	
}
