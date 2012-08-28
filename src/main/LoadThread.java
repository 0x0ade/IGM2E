package main;

import java.util.ArrayList;

public class LoadThread extends TickThread {
	
	private boolean done = false;
	private ArrayList<LTCommand> commandlist = new ArrayList<LTCommand>();
	private int currentn = 0;
	private LTCommand cmd;
	
	public LoadThread(ArrayList<LTCommand> list) {
		super();
		commandlist = list;
	}
	
	@Override
	public void ticki() {
		super.ticki();
		if (done) {
			dotick = false;
			return;
		}
		cmd = commandlist.get(currentn);
		System.out.println(cmd.name());
		cmd.handle();
		currentn++;
		if (currentn == commandlist.size()) {
			dotick = false;
			done = true;
		}
	}
	
	public boolean done() {
		return done;
	}

	public LTCommand getCurrent() {
		return cmd;
	}

}
