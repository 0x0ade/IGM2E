package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

public class TextureUtil {
	
	public static Texture mirrorY(Texture t) {
		String id = t.getTextureID() + "";
		if (TextureBank.getTexture("mirror_y_"+id) != null) return TextureBank.getTexture("mirror_y_"+id);
		
		int w = t.getImageWidth();
		int h = t.getImageHeight();
		
		int tw = t.getTextureWidth();
		int th = t.getTextureHeight();
		
		byte data[] = t.getTextureData();
		
		ImageBuffer ib = new ImageBuffer(w, h); 
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int r = data[(x+y*w)];
				int g = data[(x+y*w)+1];
				int b = data[(x+y*w)+2];
				int a = data[(x+y*w)+3];
				ib.setRGBA(x, h-y-1, r, g, b, a); 
			}
		}
		Image img = ib.getImage();
		
		Texture t2 = img.getTexture();
		
		TextureBank.addTexture("mirror_y_"+id, t2);
		
		return t2;
	}
	
	public static int getPixelDP(Texture t, int x, int y) {
		int w = (int) t.getTextureWidth();
		
		byte data[] = t.getTextureData();
		
		int r = data[(x+y*w)*4];
		int g = data[(x+y*w)*4+1];
		int b = data[(x+y*w)*4+2];
		int a = data[(x+y*w)*4+3];
		
		int c = new java.awt.Color(r, g, b, a).getRGB();
		
		return c;
	}
	
	public static byte[] toARGB(byte[] olddata) {
		if (olddata.length != 4) {
			byte[] data = olddata;
			for (int segn = 0; segn < data.length; segn += 4) {
				byte[] seg = new byte[4];
				seg[0] = data[segn+0];
				seg[1] = data[segn+1];
				seg[2] = data[segn+2];
				seg[3] = data[segn+3];
				
				seg = toARGB(seg);
				
				data[segn+0] = seg[0];
				data[segn+1] = seg[1];
				data[segn+2] = seg[2];
				data[segn+3] = seg[3];
			}
			return data;
		}
		byte[] data = new byte[4];
		
		byte r = olddata[0];
		byte g = olddata[1];
		byte b = olddata[2];
		byte a = olddata[3];
		
		data[0] = a;
		data[1] = r;
		data[2] = g;
		data[3] = b;
		
		return data;
	}

	public static Texture colorTexture(int c, int w, int h) throws IOException {
		Texture tfall = TextureBank.getTexture("color_"+c+"_"+w+"_"+h);
		
		if (tfall != null) return tfall;
		
		BufferedImage bi = colorImage(c, w, h);
		
		Texture t = BufferedImageUtil.getTexture("PNG", bi);
		TextureBank.addTexture("color_"+c+"_"+w+"_"+h, t);
		return t;
	}
	
	public static BufferedImage colorImage(int c, int w, int h) {
		BufferedImage bifall = TextureBank.getImage("color_"+c+"_"+w+"_"+h);
		
		if (bifall != null) return bifall;
		
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = bi.getGraphics();
		g.setColor(new Color(c));
		g.fillRect(0, 0, w, h);
		g.dispose();
		
		TextureBank.addImage("color_"+c+"_"+w+"_"+h, bi);
		return bi;
	}
	
	public static Texture gradientTexture(int c1, int c2, int w, int h, int dir) throws IOException {
		Texture tfall = TextureBank.getTexture("grad_"+c1+"_"+c2+"_"+w+"_"+h+"_"+dir);
		
		if (tfall != null) return tfall;
		
		BufferedImage bi = gradientImage(c1, c2, w, h, dir);
		
		Texture t = BufferedImageUtil.getTexture("PNG", bi);
		TextureBank.addTexture("grad_"+c1+"_"+c2+"_"+w+"_"+h+"_"+dir, t);
		return t;
	}
	
	public static BufferedImage gradientImage(int c1, int c2, int w, int h, int dir) {
		BufferedImage bifall = TextureBank.getImage("grad_"+c1+"_"+c2+"_"+w+"_"+h+"_"+dir);
		
		if (bifall != null) return bifall;
		
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = (Graphics2D) bi.getGraphics();
		if (dir == 1) {
			g.setPaint(new java.awt.GradientPaint(0, 0, new Color(c1), w, 0, new Color(c2)));
		} else if (dir == 2) {
			g.setPaint(new java.awt.GradientPaint(0, 0, new Color(c1), 0, h, new Color(c2)));
		}
		g.fillRect(0, 0, w, h);
		g.dispose();
		
		TextureBank.addImage("grad_"+c1+"_"+c2+"_"+w+"_"+h+"_"+dir, bi);
		return bi;
	}

	public static BufferedImage convert(Texture t) {
		if (TextureBank.getImage("convert_"+t.getTextureID()) != null) return TextureBank.getImage("convert_"+t.getTextureID());
		
		int w = t.getTextureWidth();
		int h = t.getTextureHeight();
		
		byte[] data = t.getTextureData();
		
		data = toARGB(data);
		
		ByteBuffer buffer = ByteBuffer.wrap(data);
		
		boolean check = true;
		int[] rgbd = new int[data.length/4];
		int i = 0;
		while (check) {
			try {
				rgbd[i] = buffer.getInt();
				i++;
			} catch (Exception e) {
				check = false;
			}
		}
		
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		bi.setRGB(0, 0,w, h, rgbd, 0, w);
		
		TextureBank.addImage("convert_"+t.getTextureID(), bi);
		
		return bi;
	}
	
	public static Texture convert(BufferedImage bi) {
		String id = bi.toString();
		if (TextureBank.getTexture("convert_bi_"+id) != null) return TextureBank.getTexture("convert_bi_"+id);
		
		int w = bi.getWidth();
		int h = bi.getHeight();
		
		int tw = CMath.power2(w);
		int th = CMath.power2(h);
		
		int[] pixel;
		
		ImageBuffer ib = new ImageBuffer(tw, th); 
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				pixel = bi.getRaster().getPixel(x, y, new int[4]);
				int r = pixel[0];
				int g = pixel[1];
				int b = pixel[2];
				int a = pixel[3];
				if (a == 255) {
					try {
						a = bi.getAlphaRaster().getPixel(x, y, new int[1])[0];
					} catch (Throwable t) {
					}
				}
				ib.setRGBA(x, y, r, g, b, a); 
			}
		}
		Image img = ib.getImage();
		
		Texture t = img.getTexture();
		
		TextureBank.addTexture("convert_bi_"+id, t);
		
		return t;
	}
	
	public static Texture shadeTexture(int c, int w, int h) throws IOException {
		Texture tfall = TextureBank.getTexture("shade_"+c+"_"+w+"_"+h);
		
		if (tfall != null) return tfall;
		
		BufferedImage bi = shadeImage(c, w, h);
		
		Texture t = BufferedImageUtil.getTexture("PNG", bi);
		TextureBank.addTexture("shade_"+c+"_"+w+"_"+h, t);
		return t;
	}

	public static BufferedImage shadeImage(int c, int w, int h) {
		BufferedImage bifall = TextureBank.getImage("shade_"+c+"_"+w+"_"+h);
		
		if (bifall != null) return bifall;
		
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = bi.getGraphics();
		Color cc = new Color(c);
		int df = 8;
		for (int y = 0; y < h + (h%16); y+=16) {
			g.setColor(cc);
			g.fillRect(0, y, w, 16);
			int cr = cc.getRed()-df;
			if (cr > 255) cr = 255;
			if (cr < 0) cr = 0;
			int cg = cc.getGreen()-df;
			if (cg > 255) cr = 255;
			if (cg < 0) cr = 0;
			int cb = cc.getBlue()-df;
			if (cb > 255) cr = 255;
			if (cb < 0) cr = 0;
			cc = new Color(cr, cg, cb, cc.getAlpha());
		}
		g.dispose();
		
		TextureBank.addImage("shade_"+c+"_"+w+"_"+h, bi);
		return bi;
	}
	
}
