package main;

public abstract class MenuBase implements Tickable, RRenderer {
	
	public int x = 0;
	public int y = 0;
	public int w;
	public int h;
	
	@Override
	public void tick() {
		w = IGM2E.w;
		h = IGM2E.h;
	}
	
}
