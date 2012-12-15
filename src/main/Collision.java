package main;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.Image;

public class Collision {
	
	public static boolean debug = false;
	public static boolean fast = false;
	
	/**
	 * Complicated PPC ( Per-Pixel-Collision ) checking -.-** 
	 * Hence " ME " and " MY " as side of the last named GameObject
	 * Example : " Me " means the GameObject , " My Width " means it's width , ... 
	 * @param level Level in which it happens 
	 * 	@param me Me ..............
	 * @param er My Rectangle 
	 * @param erx My checking Rectangle 
	 * @param or Other's checking Rectangle
	 * @param x My X
	 * @param y My Y
	 * @param w My Width
	 * @param h My Height 
	 * @param o Another GameObject 
	 * @param ox Another's X
	 * @param oy Another's Y
	 * @param ow Another's W
	 * @param oh Another's H
	 * @return true if they collide ( collision images ) , else false 
	 */
	public static boolean overlapps(LevelLayer layer, GameObject me, Rectangle er, Rectangle erx, Rectangle or, int x, int y, int w, int h, GameObject o, int ox, int oy, int ow, int oh) {
		if (fast) {
		} else {
			overlapps2(layer, me, er, erx, or, x, y, w, h, o, ox, oy, ow, oh);
		}
		Rectangle ix = erx.intersection(or);
		
		Image bi = o.getCollisionImage();
		
		//int xoffs = (int) ix.getX();
		//int yoffs = (int) ix.getY();
		
		int xoffs = ox;
		int yoffs = oy;
		
		ArrayList<PixelRect> prl1 = PixelRect.getPixelsFromRect(ix);
		ArrayList<PixelRect> prl = new ArrayList<PixelRect>();
		
		for (PixelRect pr : prl1) {
			int xx = (int)(pr.getX() - xoffs);
			int yy = (int)(pr.getY() - yoffs);
			
			if (bi.getColor(xx, yy).a == 0) {
				if (debug) {
					layer.level.add2(new PixelTile(layer, (int)(pr.getX()), (int)(pr.getY()), null, 0, 0, "", 0xffffff));
				}
				continue;
			}
			
			if (debug) {
				layer.level.add2(new PixelTile(layer, (int)(pr.getX()), (int)(pr.getY()), null, 0, 0, "", 0x000000));
			}
			
			prl.add(pr);
		}
		
		for (PixelRect pr : prl) {
			if (er.intersects(pr)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Complicated PPC ( Per-Pixel-Collision ) checking -.-**
	 * Hence " ME " and " MY " as side of the last named GameObject
	 * Example : " Me " means the GameObject , " My Width " means it's width , ... 
	 * @param level Level in which it happens 
	 * @param me Me ..............
	 * @param er My Rectangle 
	 * @param erx My checking Rectangle 
	 * @param or Other's checking Rectangle
	 * @param x My X
	 * @param y My Y
	 * @param w My Width
	 * @param h My Height 
	 * @param o Another GameObject 
	 * @param ox Another's X
	 * @param oy Another's Y
	 * @param ow Another's W
	 * @param oh Another's H
	 * @return true if they collide ( collision images ) , else false 
	 */
	public static boolean overlapps2(LevelLayer layer, GameObject me, Rectangle er, Rectangle erx, Rectangle or, int x, int y, int w, int h, GameObject o, int ox, int oy, int ow, int oh) {
		Rectangle ix = erx.intersection(or);
		
		Image bi = o.getCollisionImage();
		
		//int xoffs = (int) ix.getX();
		//int yoffs = (int) ix.getY();
		
		int xoffs = ox;
		int yoffs = oy;
		
		ArrayList<PixelRect> prl1 = PixelRect.getPixelsFromRect(ix);
		ArrayList<PixelRect> prl = new ArrayList<PixelRect>();
		
		for (PixelRect pr : prl1) {
			int xx = (int)(pr.getX() - xoffs);
			int yy = (int)(pr.getY() - yoffs);
			
			if (bi.getColor(xx, yy).a == 0) {
				if (debug) {
					layer.level.add2(new PixelTile(layer, (int)(pr.getX()), (int)(pr.getY()), null, 0, 0, "", 0xffffffff));
				}
				continue;
			}
			
			if (debug) {
				layer.level.add2(new PixelTile(layer, (int)(pr.getX()), (int)(pr.getY()), null, 0, 0, "", 0xff000000));
			}
			
			prl.add(pr);
		}
		
		Image mbi = me.getCollisionImage();
		
		ArrayList<PixelRect> prl2 = PixelRect.getPixelsFromRect(ix);
		
		int mxoffs = x;
		int myoffs = y;
		
		for (PixelRect pr : prl2) {
			int xx = (int)(pr.getX() - xoffs);
			int yy = (int)(pr.getY() - yoffs);
			
			if (mbi.getColor(xx, yy).a == 0) {
				if (debug) {
					layer.level.add2(new PixelTile(layer, (int)(pr.getX()), (int)(pr.getY()), null, 0, 0, "", 0xffffff));
				}
				continue;
			}
			
			if (debug) {
				layer.level.add2(new PixelTile(layer, (int)(pr.getX()), (int)(pr.getY()), null, 0, 0, "", 0x000000));
			}
			
			for (PixelRect pr2 : prl) {
				if (er.intersects(pr2)) {
					if (pr.intersects(pr2)) {
						return true;
					}
				}
			}
			
		}
		return false;
	}
	
}
