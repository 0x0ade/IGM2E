package main;

import java.awt.Color;
import java.awt.Font;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.Effect;
import org.newdawn.slick.font.effects.GradientEffect;

public class Fonts
{
	public static final String name_normal = "ubuntufontfamily/Ubuntu-L";
	public static final UnicodeFont normal = loadFontFromFile(name_normal, 12);
	
	public static final String name_bold = "ubuntufontfamily/Ubuntu-B";
	public static final UnicodeFont bold = loadFontFromFile(name_bold, 12);
	
	public static UnicodeFont resizeFont(final UnicodeFont f, final int size) {
		return (UnicodeFont) OpenGLHelper.run(new OpenGLHelper.Action() {
			@Override
			public Object run() throws Throwable {
				UnicodeFont font = new UnicodeFont(f.getFont().deriveFont((float)size), size, f.getFont().isBold(), f.getFont().isItalic());
				for (int i = 0; i < f.getEffects().size(); i++) {
					font.getEffects().add(f.getEffects().get(i));
				}
				font.addGlyphs(0, 255);
				try {
				    font.loadGlyphs();
				} catch (SlickException ex) {
				   ex.printStackTrace();
				}
				return font;
			}
		});
	}
	
	public static UnicodeFont refilter(final UnicodeFont f, final Effect... fx) {
		return (UnicodeFont) OpenGLHelper.run(new OpenGLHelper.Action() {
			@Override
			public Object run() throws Throwable {
				UnicodeFont font = new UnicodeFont(f.getFont());
				for (int i = 0; i < fx.length; i++) {
					font.getEffects().add(fx[i]);
				}
				font.addGlyphs(0, 255);
				try {
				    font.loadGlyphs();
				} catch (SlickException ex) {
				   ex.printStackTrace();
				}
				return font;
			}
		});
	}
	
	public static GradientEffect newGradient(int top) {
		Color c1 = new Color(top);
		Color c2 = new Color(
				(int)(c1.getRed()*0.75f),
				(int)(c1.getGreen()*0.75f),
				(int)(c1.getBlue()*0.75f),
				(int)(c1.getAlpha())
				);
		GradientEffect grad = new GradientEffect(c1, c2, 1f);
		return grad;
	}
	
	public static UnicodeFont loadFont(String name, int size)
	{
		UnicodeFont font = new UnicodeFont(new Font(name,0,size));
		//font.getEffects().add(new ColorEffect(java.awt.Color.white));
		font.getEffects().add(newGradient(0xffffff));
		//font.addAsciiGlyphs();
		font.addGlyphs(0, 255);
		try {
		    font.loadGlyphs();
		} catch (SlickException ex) {
		   ex.printStackTrace();
		}
		return font;
	}
	
	public static UnicodeFont loadFontFromFile(String path, int size)
	{
		UnicodeFont font;
		try {
			font = new UnicodeFont("/res/ui/"+path+".ttf", size, false, false);
		} catch (SlickException e) {
			e.printStackTrace();
			return null;
		}
		//font.getEffects().add(new ColorEffect(java.awt.Color.white));
		font.getEffects().add(newGradient(0xffffff));
		//font.addAsciiGlyphs();
		font.addGlyphs(0, 255);
		try {
		    font.loadGlyphs();
		} catch (SlickException ex) {
		   ex.printStackTrace();
		}
		return font;
	}
}
