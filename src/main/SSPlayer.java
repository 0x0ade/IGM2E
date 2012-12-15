package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;

public class SSPlayer extends Player {
	public SSPlayer(LevelLayer layer, int x, int y) {
		super(layer, x, y);
	}
	
	@Override
	public void tick() {
		IGM2E.sound_x = x;
		IGM2E.sound_y = y;
		
		if (InputHandler.isDown(InputHandler.JUMP) && canJump) {
			jump();
		}
		//if (IGM2.instance.keys.left.isDown) gx -= speed;
		//if (IGM2.instance.keys.right.isDown) gx += speed;
		if (InputHandler.isDown(InputHandler.LEFT)) donespeed = -speed;
		else if (InputHandler.isDown(InputHandler.RIGHT)) donespeed = speed;
		else donespeed = 0;
		
		gx += donespeed;
		
		super.tick();
	}
	
}
