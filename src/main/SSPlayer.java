package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;

public class SSPlayer extends Player {
	public SSPlayer(Level level, int x, int y) {
		super(level, x, y);
	}
	
	@Override
	public void tick() {
		IGM2E.sound_x = x;
		IGM2E.sound_y = y;
		
		if (IGM2E.keys.up.wasPressed() && canJump) {
			jump();
		}
		//if (IGM2.instance.keys.left.isDown) gx -= speed;
		//if (IGM2.instance.keys.right.isDown) gx += speed;
		if (IGM2E.keys.left.isDown) donespeed = -speed;
		else if (IGM2E.keys.right.isDown) donespeed = speed;
		else donespeed = 0;
		
		gx += donespeed;
		
		super.tick();
	}
	
}
