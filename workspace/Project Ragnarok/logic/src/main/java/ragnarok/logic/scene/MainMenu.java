package ragnarok.logic.scene;

import ragnarok.config.GameConf;
import ragnarok.logic.GameModel;
import ragnarok.logic.gui.menu.BoolSetting;
import ragnarok.logic.gui.menu.MenuActionItem;
import ragnarok.logic.gui.menu.MenuGrid;
import ragnarok.logic.gui.menu.MenuItem;
import ragnarok.logic.gui.menu.MenuList;
import ragnarok.logic.gui.menu.SubMenu;
import ragnarok.logic.gui.menu.TextMenu;
import ragnarok.logic.level.LevelManager;
import ragnarok.primitives.geometry.Vec;

/**
 *
 * This class realizes the static part of the main menu of the game.
 *
 */
final class MainMenu extends Scene {
	/**
	 * The menu.
	 */
	private SubMenu menu;

	/**
	 * Create the main menu.
	 *
	 * @param model
	 *            the model
	 */
	public MainMenu(GameModel model) {
		super(model);
	}

	/**
	 * Create method of the scene.
	 *
	 * @param model
	 *            the model
	 * @param options
	 *            the options
	 * @return a new arcade scene.
	 */
	public static Scene create(GameModel model, String[] options) {
		return new MainMenu(model);
	}

	@Override
	public void init() {
		super.init();

		this.menu = new MenuList(this, "Main Menu");
		this.menu.setPos(new Vec(GameConf.PIXEL_W / 2f, GameConf.PIXEL_H / 2f));

		MenuList play = new MenuList(this, "Play");

		MenuActionItem inf = new MenuActionItem(this, "Infinite Fun", () -> this.getModel().switchScene(Scenes.INFINITE));
		MenuActionItem lod = new MenuActionItem(this, "Level of the Day", () -> this.getModel().switchScene(Scenes.LOD));
		MenuGrid arcade = new MenuGrid(this, "Arcade Mode", 6, 100, 100);

		for (int i = 0; i < LevelManager.getNumberOfArcadeLevels(); i++) {
			final int id = i;
			MenuActionItem button = new MenuActionItem(this, new Vec(80, 80), String.valueOf(id + 1),
					() -> this.getModel().switchScene(Scenes.ARCADE, String.valueOf(id)));
			arcade.addItem(button);
		}

		MenuList modPlay = new MenuList(this, "Mod Scenes");

		modPlay.addItem(new MenuActionItem(this, "no Mod Scenes :(", () -> {
		}));

		play.addItem(inf, lod, arcade, modPlay);

		MenuList settings = new MenuList(this, "Settings");
		settings.addItem(new BoolSetting(this, "Debug Mode", "DEBUG"));

		MenuList about = new MenuList(this, "About");
		about.addItem(new TextMenu(this, GameConf.ABOUT));

		this.menu.addItem(play, settings, about);

		this.addGuiElement(this.menu);
		this.menu.select();
	}

	@Override
	public MenuItem getMenu() {
		return this.menu;
	}

	@Override
	public int getScore() {
		throw new UnsupportedOperationException("No Score in MenuScene");
	}

	@Override
	public int getHighScore() {
		throw new UnsupportedOperationException("No HighScore in MenuScene");
	}

}