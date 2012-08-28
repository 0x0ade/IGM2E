package main;

public class TickThread extends Thread implements Tickable {
	
	public boolean dotick = true;
	public boolean tick = false;
	
	@Override
	public void run() {
		while (dotick) {
			if (tick) {
				ticki();
			}
		}
	}
	
	@Override
	public void tick() {
		if (!dotick) interrupt();
		tick = true;
	}
	
	public void ticki() {
		tick = false;
	}
	
}
