package net.message;

public class LocalIDMessage extends Message {
	
	public int localID;
	
	public LocalIDMessage() {
		this(-1);
	}
	
	public LocalIDMessage(int id) {
		localID = id;
	}
	
}
