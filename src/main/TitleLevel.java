package main;

import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.opengl.Texture;

public class TitleLevel extends Level {
	
	public static final int maxBGids = 2;
	
	public float titleoffs = -40f;
	public float tosm = 1.5f;
	public float tos = tosm;
	
	public HashMap<String, TitleMenuBase> menus = new HashMap<String, TitleMenuBase>();
	public MenuBase menu;
	
	public int bg_id;
	
	public ArrayList<Float> bgoffs = new ArrayList<Float>();
	public ArrayList<Float> bgtos = new ArrayList<Float>();
	public ArrayList<Float> bgmaxo = new ArrayList<Float>();
	public ArrayList<Float> bgmaxt = new ArrayList<Float>();
	
	public Level paused;
	
	public TitleLevel(Level paused) {
		super(IGM2E.size.width, IGM2E.size.height);
		
		this.paused = paused;
		
		if (paused == null) {
			IGM2E.playBGM("title");
		}
		
		newBG();
		
		// x 510 y 300 
		
		menus.put("main", new TitleMenuBase() {
			
			public String[] list = new String[] {"Game", "DLC Center", "Options", "Quit"};
			
			@Override
			public void handleItem() {
				switch (item+1) {
				case 1:
					menu = menus.get("game");
					break;
				case 2:
					IGM2E.dlcCenter();
					break;
				case 3:
					menu = menus.get("options");
					break;
				case 4:
					IGM2E.dotick = false;
					return;
				default:
					break;
				}
			}
			
			@Override
			public String[] getList() {
				return list;
			}

			@Override
			public int getMaxItems() {
				return 4;
			}

			@Override
			public int getX() {
				return 510;
			}

			@Override
			public int getY() {
				return 300;
			}
			
		});
		
		menus.put("game", new TitleMenuBase() {
			
			public String[] list1 = new String[] {"New", "Back"};
			public String[] list2 = new String[] {"Load", "New", "Back"};
			
			@Override
			public void handleItem() {
				if (!IGM2E.canContinue()) {
					switch (item+1) {
					case 1:
						IGM2E.newGame();
						break;
					case 2:
						menu = menus.get("main");
						break;
					default:
						break;
					}
				} else {
					switch (item+1) {
					case 1:
						IGM2E.loadGame();
						break;
					case 2:
						IGM2E.newGame();
						break;
					case 3:
						menu = menus.get("main");
						break;
					default:
						break;
					}
				}
			}
			
			@Override
			public String[] getList() {
				return (!IGM2E.canContinue())?list1:list2;
			}

			@Override
			public int getMaxItems() {
				return (!IGM2E.canContinue())?2:3;
			}

			@Override
			public int getX() {
				return 510;
			}

			@Override
			public int getY() {
				return 300;
			}
			
		});
		
		menus.put("options", new TitleMenuBase() {
			
			public String[] list = new String[] {"Sounds", "Graphics", "Back"};
			
			@Override
			public void handleItem() {
				switch (item+1) {
				case 1:
					menu = menus.get("sounds");
					break;
				case 2:
					menu = menus.get("gfx");
					break;
				case 3:
					menu = menus.get("main");
					break;
				default:
					break;
				}
			}
			
			@Override
			public String[] getList() {
				return list;
			}

			@Override
			public int getMaxItems() {
				return 3;
			}

			@Override
			public int getX() {
				return 510;
			}

			@Override
			public int getY() {
				return 300;
			}
			
		});
		
		menus.put("sounds", new TitleMenuBase() {
			
			public MenuSlider[] sliders = new MenuSlider[] {
					new MenuSlider("Volume", "vol_sound", false),
					new MenuSlider("Effects", "vol_sfx", false),
					new MenuSlider("Background", "vol_bgm", false)
			};
			
			@Override
			public void handleItem() {
				switch (item+1) {
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					menu = menus.get("options");
					break;
				default:
					break;
				}
			}
			
			@Override
			public void tick() {
				super.tick();
				
				if (IGM2E.keys.left.wasPressed()) {
					switch (item+1) {
					case 1:
					case 2:
					case 3:
						sliders[item].subtract();
						break;
					case 4:
						break;
					default:
						break;
					}
				}
				
				if (IGM2E.keys.right.wasPressed()) {
					switch (item+1) {
					case 1:
					case 2:
					case 3:
						sliders[item].add();
						break;
					case 4:
						break;
					default:
						break;
					}
				}
			}
			
			@Override
			public String[] getList() {
				return new String[] {sliders[0].getMenuValue(), sliders[1].getMenuValue(), sliders[2].getMenuValue(), "Back"};
			}

			@Override
			public int getMaxItems() {
				return 4;
			}

			@Override
			public int getX() {
				return 510;
			}

			@Override
			public int getY() {
				return 300;
			}
			
		});
		
		menus.put("gfx", new TitleMenuBase() {
			
			public MenuSlider[] sliders = new MenuSlider[] {
					new MenuSlider("Fancy Menus", 0, 1, 1, "menus_fancy", true),
					new MenuSlider("V-Sync", 0, 1, 1, "gfx_vsync", true),
			};
			
			@Override
			public void handleItem() {
				switch (item+1) {
				case 1:
				case 2:
					sliders[item].setValue((sliders[item].getValue() == 0)?1:0);
					Options.set(sliders[item].option, (sliders[item].getValue()==sliders[item].maxval)+"");
					break;
				case 3:
					menu = menus.get("options");
					break;
				default:
					break;
				}
			}
			
			@Override
			public void tick() {
				super.tick();
				
				if (IGM2E.keys.left.wasPressed()) {
					switch (item+1) {
					case 1:
					case 2:
						sliders[item].subtract();
						break;
					case 3:
						break;
					default:
						break;
					}
				}
				
				if (IGM2E.keys.right.wasPressed()) {
					switch (item+1) {
					case 1:
					case 2:
						sliders[item].add();
						break;
					case 3:
						break;
					default:
						break;
					}
				}
			}
			
			@Override
			public String[] getList() {
				return new String[] {sliders[0].getMenuBoolean(), sliders[1].getMenuBoolean(), "Back"};
			}

			@Override
			public int getMaxItems() {
				return 3;
			}

			@Override
			public int getX() {
				return 510;
			}

			@Override
			public int getY() {
				return 300;
			}
			
		});
		
		
		menus.put("pause", new TitleMenuBase() {
			
			public String[] list = new String[] {"Continue", "Save", "Load", "Main Menu"};
			
			@Override
			public void handleItem() {
				switch (item+1) {
				case 1:
					IGM2E.unpause();
					break;
				case 2:
					IGM2E.saveGame();
					break;
				case 3:
					IGM2E.loadGame();
					break;
				case 4:
					TitleLevel.this.prepareSave();
					TitleLevel.this.paused.uninit();
					TitleLevel.this.paused = null;
					IGM2E.paused = false;
					newBG();
					menu = menus.get("main");
					IGM2E.playBGM("title");
					return;
				default:
					break;
				}
			}
			
			@Override
			public String[] getList() {
				return list;
			}

			@Override
			public int getMaxItems() {
				return 4;
			}

			@Override
			public int getX() {
				return 510;
			}

			@Override
			public int getY() {
				return 300;
			}
			
		});
		
		
		if (paused == null) {
			menu = menus.get("main");
		} else {
			menu = menus.get("pause");
		}
		
	}
	
	public void newBG() {
		bg_id = IGM2E.rand.nextInt(maxBGids)+1;
		bg_id = 1; // NOTE : bg_id 2 checks are only used for later-on implementations of animated backgrounds .
		
		if (bg_id == 2) {
			for (int i = 0; i < 3; i++) {
				bgoffs.add(0f);
				bgtos.add(1f);
			}
			
			bgmaxo.add(16f);
			bgmaxo.add(8f);
			bgmaxo.add(6f);
			
			bgmaxt.add(1f);
			bgmaxt.add(0.5f);
			bgmaxt.add(0.5f);
		}
	}

	@Override
	public void tick() {
		titleoffs += tos;
		
		if (titleoffs <= 0) {
			tos += 0.125f;
		}
		
		if (titleoffs >= 8) {
			tos -= 0.125f;
		}
		
		if (tos < -tosm) tos = -tosm;
		if (tos > tosm) tos = tosm;
		
		if (bg_id == 2) {
			for (int i = 0; i < 3; i++) {
				bgoffs.set(i, bgoffs.get(i)+bgtos.get(i));
				
				if (bgoffs.get(i) <= 0) {
					bgtos.set(i, bgtos.get(i)+0.125f);
				}
				
				if (bgoffs.get(i) >= bgmaxo.get(i)) {
					bgtos.set(i, bgtos.get(i)-0.125f);
				}
				
				if (bgtos.get(i) < -bgmaxt.get(i)) bgtos.set(i, -bgmaxt.get(i));
				if (bgtos.get(i) > bgmaxt.get(i)) bgtos.set(i, bgmaxt.get(i));
				
			}
		}
		
		menu.tick();
		
	}
	
	@Override
	public void render() {
		
		if (!Options.getAsBoolean("menus_fancy")) {
			IGM2E.render(TextureBank.getTexture("titlemenu_"+bg_id), 0, 0);
		} else {
			switch (bg_id) {
			case 1:
				IGM2E.render(TextureBank.getTexture("titlemenu_"+bg_id), 0, 0);
				break;
			case 2:
				IGM2E.render(TextureBank.getTexture("titlemenu_2_bg"), 0, 0);
				IGM2E.render(TextureBank.getTexture("titlemenu_2_l3"), 0, bgoffs.get(2));
				IGM2E.render(TextureBank.getTexture("titlemenu_2_l2"), 0, bgoffs.get(1));
				IGM2E.render(TextureBank.getTexture("titlemenu_2_l1"), 0, bgoffs.get(0));
				IGM2E.render(TextureBank.getTexture("titlemenu_2_overlay"), 0, 0);
				break;
			default:
				IGM2E.render(TextureBank.getTexture("titlemenu_"+bg_id), 0, 0);
				break;
			}
		}
		
		super.render();
		
		menu.render();
		
		IGM2E.render(TextureBank.getTexture("title"), 250, 4 + titleoffs);
	}
	
}
