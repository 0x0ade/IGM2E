package main;

public class Dir4 {
	
	public static final int unknown = 0;
	public static final int up = 1;
	public static final int down = 2;
	public static final int left = 3;
	public static final int right = 4;
	
	public static int getDir(String st) {
		String s = st.toLowerCase();
		if (s.equals("up")) return up;
		if (s.equals("down")) return down;
		if (s.equals("left")) return left;
		if (s.equals("right")) return right;
		return unknown;
	}
	
}
