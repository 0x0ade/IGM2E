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
		IGM2E.removeLTCMD(this);
		done = true;
	}
	
	public void join() {
		try {
			while (!done) {
				Thread.sleep(1l);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
