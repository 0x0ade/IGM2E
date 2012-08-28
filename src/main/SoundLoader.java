package main;

import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;

public class SoundLoader {
	
	public static void initSounds() {
		//FX
		loadSound("/res/sounds/JUMP.wav", "jump");
		
		//BGM
		loadMusic("/res/sounds/BGM.ogg", "bgm");
		loadMusic("/res/sounds/TITLE.ogg", "title");
		loadMusic("/res/sounds/LEVEL1.ogg", "level1");
	}
	
	public static void loadSound(String filename, String savename) {
		try {
			Sound s = new Sound(filename);
			SoundBank.addSound(savename, s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadMusic(String filename, String savename) {
		try {
			Music m = new Music(filename);
			SoundBank.addMusic(savename, m);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
