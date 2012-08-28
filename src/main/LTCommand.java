package main;

public abstract class LTCommand {
	
	public boolean started = false;
	public boolean finished = false;
	public boolean inThread = false;
	
	public abstract String name();
	
	protected abstract void handle();
	
	public abstract String loadText();
	
	public void start() {
		started = true;
		handle();
		finished = true;
	}
}
