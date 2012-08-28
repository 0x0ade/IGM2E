package main;

import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;

public class SoundBank {
	private static ConcurrentHashMap<String, Sound> sounds = new ConcurrentHashMap<String, Sound>();
	private static ConcurrentHashMap<String, Music> musics = new ConcurrentHashMap<String, Music>();
	
	public static void addSound(String savename, Sound sound) {
		sounds.put(savename, sound);
	}
	
	public static Sound getSound(String savename) {
		return sounds.get(savename);
	}
	
	public static void addMusic(String savename, Music music) {
		musics.put(savename, music);
	}
	
	public static Music getMusic(String savename) {
		return musics.get(savename);
	}
	
}
