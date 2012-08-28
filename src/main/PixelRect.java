package main;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Pixel Rectangle class , used for per-pixel collision checkings .
 * @author AngelDE98
 *
 */
public class PixelRect extends Rectangle {
	
	public PixelRect(int x, int y) {
		super(x, y, 1, 1);
	}
	
	public static ArrayList<PixelRect> getPixelsFromRect(Rectangle r) {
		ArrayList<PixelRect> prs = new ArrayList<PixelRect>();
		for (int x = 0; x < r.getWidth(); x++) {
			for (int y = 0; y < r.getHeight(); y++) {
				prs.add(new PixelRect((int)(x + r.getX()), (int)(y + r.getY())));
			}
		}
		return prs;
	}
	
}
