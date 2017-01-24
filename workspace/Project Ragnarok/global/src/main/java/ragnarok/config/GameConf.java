package ragnarok.config;

import java.util.Random;

import org.apache.log4j.Logger;

import home.fox.visitors.Visitable;
import home.fox.visitors.annotations.AfterVisit;
import home.fox.visitors.annotations.NoVisit;
import home.fox.visitors.annotations.VisitInfo;
import home.fox.visitors.parser.Parser;
import ragnarok.primitives.geometry.Vec;
import ragnarok.primitives.image.RGBColor;
import ragnarok.util.ReflectUtils;
import ragnarok.util.TextOptions;

/**
 * Configuration class that holds static options.
 *
 * @author Angelo Aracri
 * @author Dominik Fuchss
 * @version 1.1
 */
@VisitInfo(res = "conf/game", visit = true)
public final class GameConf implements Visitable {
	/**
	 * Prevent instantiation.
	 */
	private GameConf() {
	}

	/**
	 * The search path for dynamically loaded classes; see {@link ReflectUtils}.
	 */
	@NoVisit
	public static final String SEARCH_PATH = "ragnarok";
	/**
	 * The GameWide randomness source.
	 */
	@NoVisit
	public static final Random PRNG = new Random();

	/**
	 * The about text.
	 */
	public static String ABOUT;
	/**
	 * The text options for {@link #ABOUT}.
	 */
	@NoVisit
	public static TextOptions ABOUT_TEXT;
	/**
	 * The file location of the level manager file.
	 */
	public static String LVL_MGMT_FILE;
	/**
	 * This boolean indicates whether the game is in debug mode.
	 */
	@NoVisit
	public static boolean DEBUG = true;

	/**
	 * The Name of the Game.
	 */
	public static String NAME;

	/**
	 * The version of the Game.
	 */
	public static String VERSION;

	/**
	 * The used version of the StandardWidgetToolkit.
	 */
	public static String SWT_VERSION;

	/**
	 * Size of one in-game unit in pixels. So a Vec2D(1, 0) will be projected to
	 * (pxPerUnit, 0)
	 */
	public static int PX_PER_UNIT;
	/**
	 * Grid width. Determines how broad the window game will be
	 */
	public static int GRID_W;
	/**
	 * Grid height. Determines how high the window game will be
	 */
	public static int GRID_H;

	/**
	 * Width of the window.<br>
	 * Calculated by {@link GameConf#GRID_W} * {@link GameConf#PX_PER_UNIT}.
	 */
	@NoVisit
	public static int PIXEL_W;
	/**
	 * Height of the window.<br>
	 * Calculated by {@link GameConf#GRID_H} * {@link GameConf#PX_PER_UNIT}.
	 */
	@NoVisit
	public static int PIXEL_H;

	/**
	 * Time in milliseconds to wait after each renderLoop, that refreshes all
	 * graphical elements.
	 */
	public static int RENDER_DELTA;
	/**
	 * Time in milliseconds to wait after each logicLoop, that simulates physics
	 * changes positions, detects collisions, ...
	 */
	public static int LOGIC_DELTA;

	/**
	 * Gravitational constant g in pxPerUnit/s^2. Determines how fast something
	 * accelerates upon falling.
	 */
	public static float G;
	/**
	 * The default camera offset for a player.
	 */
	public static float PLAYER_CAMERA_OFFSET;
	/**
	 * The player's walk acceleration.
	 */
	public static float PLAYER_WALK_ACCEL;
	/**
	 * The player's stop acceleration.
	 */
	public static float PLAYER_STOP_ACCEL;
	/**
	 * The player's walk max speed.
	 */
	public static float PLAYER_WALK_MAX_SPEED;
	/**
	 * The player's jump boost.
	 */
	public static float PLAYER_JUMP_BOOST;
	/**
	 * The player's boost upon jumping on an enemy.
	 */
	public static float PLAYER_KILL_BOOST;
	/**
	 * The player's maximum jump time in millis.
	 */
	public static long PLAYER_JUMP_TIME;
	/**
	 * The player's bottom boost (used when colliding from bottom).
	 */
	public static float PLAYER_BOTTOM_BOOST;
	/**
	 * The player's default amount of lives.
	 */
	public static int PLAYER_LIVES;

	/**
	 * The menu's background color.
	 */
	public static RGBColor MENU_BACKGROUND_COLOR;
	/**
	 * The menu's box color (default).
	 */
	public static RGBColor MENU_BOX_COLOR;
	/**
	 * The menu's box color (selected).
	 */
	public static RGBColor MENU_BOX_SELECT_COLOR;
	/**
	 * The menu's box color (option).
	 */
	public static RGBColor MENU_BOX_OPTION_COLOR;
	/**
	 * The menu's text color.
	 */
	public static RGBColor MENU_TEXT_COLOR;
	/**
	 * The menu's text font.
	 */
	public static String MENU_TEXT_FONT;
	/**
	 * The menu's text size.
	 */
	public static int MENU_TEXT_SIZE;
	/**
	 * The game's background color.
	 */
	public static RGBColor GAME_BACKGROUD_COLOR;
	/**
	 * The game's text color.
	 */
	public static RGBColor GAME_TEXT_COLOR;
	/**
	 * The game's text font.
	 */
	public static String GAME_TEXT_FONT;
	/**
	 * The game's text size.
	 */
	public static int GAME_TEXT_SIZE;
	/**
	 * The game's debug text color.
	 */
	@NoVisit
	public static RGBColor DEBUG_TEXT_COLOR;
	/**
	 * The game's text options.
	 */
	@NoVisit
	public static TextOptions DEFAULT_TEXT;
	/**
	 * The game's text options (menu).
	 */
	@NoVisit
	public static TextOptions MENU_TEXT;
	/**
	 * The game's text options (hints).
	 */
	@NoVisit
	public static TextOptions HINT_TEXT;
	/**
	 * The Global GAME_LOGGER.
	 */
	@NoVisit
	public static final Logger GAME_LOGGER = Logger.getLogger(GameConf.class);

	/**
	 * Set values which cannot be loaded by {@link Parser Parsers}.
	 */
	@AfterVisit
	public static void afterVisit() {
		GameConf.PIXEL_W = GameConf.GRID_W * GameConf.PX_PER_UNIT;
		GameConf.PIXEL_H = GameConf.GRID_H * GameConf.PX_PER_UNIT;

		GameConf.DEFAULT_TEXT = new TextOptions(new Vec(-1, 0), GameConf.GAME_TEXT_SIZE, GameConf.GAME_TEXT_COLOR, GameConf.GAME_TEXT_FONT, 0);
		GameConf.MENU_TEXT = new TextOptions(new Vec(-0.5F, -0.65f), GameConf.MENU_TEXT_SIZE, GameConf.MENU_TEXT_COLOR, GameConf.MENU_TEXT_FONT, 0);
		GameConf.DEBUG_TEXT_COLOR = new RGBColor(255, 255, 255);
		GameConf.HINT_TEXT = GameConf.DEFAULT_TEXT.clone().setHeight(GameConf.GAME_TEXT_SIZE - 8).setColor(GameConf.DEBUG_TEXT_COLOR);
		GameConf.ABOUT_TEXT = GameConf.HINT_TEXT.clone().setAlignmentLeft(new Vec());
	}
}