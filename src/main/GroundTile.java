package main;

import java.awt.image.BufferedImage;

import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMapPlus;

public class GroundTile extends Tile {
	
	public String types = "";
	public Texture fallbackt;
	public boolean renderTop = false;
	
	public GroundTile(LevelLayer layer, int x, int y, TileSet tset, int id, int tlayer, String layername) {
		super(layer, x, y, tset, id, tlayer, layername);
		
		//types = tset.name+"_"+(tset.getTileX(id) + tset.getTileY(id) * (tset.tilesAcross));
		int tsetid = 0;
		//Seems unproper but tilesets with just one line do work well .
		tsetid = (tset.getTileX(id) + tset.getTileY(id) * (tset.tilesAcross));
		if (tsetid < 1) {
			tsetid = tset.tilesAcross * tset.tilesDown;
		}
		if (tsetid > tset.tilesAcross * tset.tilesDown) {
			tsetid = tset.tilesAcross * tset.tilesDown;
		}
		types = tset.name+"_"+tsetid;
	}

	@Override
	public void tick() {
	}

	@Override
	public Image getImage() {
		Image i = ImageBank.getImage(types);
		return i;
	}
	
	@Override
	public void collide(Entity e) {
		if (!(e instanceof Player)) return;
		/*
		if (overlay == Overlay.crack) {
			e.level.remove(this);
			TBOTW.instance.soundPlayer.playSound(Resources.Sounds.boom, x+w/2, y+h/2);
		}
		*/
	}
	
}
