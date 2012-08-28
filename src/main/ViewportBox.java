package main;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.Serializable;

public class ViewportBox extends Rectangle implements Serializable {
	
	public boolean moved = false;
	
	public ViewportBox(int w, int h) {
		this(0, 0, w, h);
	}
	
	public ViewportBox(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	public ViewportBox(Dimension size) {
		this(size.width, size.height);
	}
	
	public void setPlayerPos(Player p) {
		x = p.x - width/2;
		y = p.y - height/2;
		moved = true;
	}

	public int containsX(Rectangle r) {
		if (r.x < x) {
			return Dir4.left;
		} else if (r.x + r.width > x + width) {
			return Dir4.right;
		}
		return Dir4.unknown;
	}
	
	public int containsY(Rectangle r) {
		if (r.y < y) {
			return Dir4.up;
		} else if (r.y + r.height > y + height) {
			return Dir4.down;
		}
		return Dir4.unknown;
	}
	
}
