package main;

public class LTCThread {
	
	public LTCommand cmd;
	public boolean done = false;
	
	public LTCThread(LTCommand cmd) {
		this.cmd = cmd;
	}
	
	public void start() {
		if (done) return;
		cmd.start();
		done = true;
	}
	
	public void join() {
		while (!done) {
		}
	}
	
}
