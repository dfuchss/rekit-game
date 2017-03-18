package rekit.gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import rekit.config.GameConf;
import rekit.core.GameTime;
import rekit.logic.IScene;
import rekit.logic.Model;
import rekit.logic.gameelements.GameElement;
import rekit.logic.gui.GuiElement;
import rekit.primitives.geometry.Vec;
import rekit.util.InputHelper;
import rekit.util.ThreadUtils;
import rekit.util.Utils;

/**
 * Main class of the View. Manages the window and a canvas an periodically
 * renders all GameElements that GameModel.getGameElementIterator() provides
 *
 * @author Angelo Aracri
 * @author Dominik Fuchss
 *
 * @version 1.1
 */
class GameView implements View {

	/**
	 * The location of the icon of the game.
	 */
	private static final String ICON_LOCATION = "/images/icon.png";

	/**
	 * Reference to the model, that holds all information that are required for
	 * rendering.
	 */
	private Model model;

	/**
	 * The last render time.
	 */
	private long lastRenderTime;
	/**
	 * The queue used for fps calculation.
	 */
	private Queue<Long> fpsQueue = new ArrayDeque<>();
	/**
	 * The last calculated fps-sum value.
	 */
	private long lastFpsSum = -1;
	/**
	 * Amount of points in time to calculate FPS.
	 */
	private static final int FPS_COUNTER = 500;
	/**
	 * Max tries of {@link #getGameIcon(int)}.
	 */
	private static final int MAX_TRIES = 5;

	/**
	 * The Field that manages the graphic context.
	 */
	private GameGridImpl grid;
	/**
	 * The frame.
	 */
	private final JFrame frame;
	/**
	 * The canvas in the {@link #frame}.
	 */
	private final Canvas canvas;
	/**
	 * The Buffer of the {@link #canvas}.
	 */
	private final BufferStrategy bufferStrategy;

	/**
	 * Constructor that creates a new window with a canvas and prepares all
	 * required attributes.
	 *
	 * @param model
	 *            the model
	 */
	public GameView(Model model) {
		this.model = model;
		// Create window
		this.frame = new JFrame(GameConf.NAME + " (" + GameConf.VERSION + ")");
		this.frame.setIconImage(this.getGameIcon());
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.frame.setResizable(false);
		this.frame.setSize(GameConf.PIXEL_W, GameConf.PIXEL_H);
		Utils.center(this.frame);
		this.frame.setLayout(new BorderLayout());

		// Create and position a canvas
		this.canvas = new Canvas();
		this.canvas.setPreferredSize(new Dimension(GameConf.PIXEL_W, GameConf.PIXEL_H));
		this.canvas.setIgnoreRepaint(true);
		this.canvas.setBackground(Utils.calcRGB(GameConf.GAME_BACKGROUD_COLOR));
		this.frame.add(this.canvas, BorderLayout.CENTER);
		this.frame.pack();

		this.canvas.createBufferStrategy(3);
		this.bufferStrategy = this.canvas.getBufferStrategy();
		this.frame.setVisible(true);

		// Create Graphic context
		this.grid = new GameGridImpl();
	}

	/**
	 * Get the game icon.
	 *
	 * @return the game icon
	 */
	private Image getGameIcon() {
		return this.getGameIcon(0);
	}

	/**
	 * Try to get icon multiple times, as sometimes stream will closes (don't
	 * know why).
	 *
	 * @param nTry
	 *            the number of the try
	 * @return hopefully the game icon
	 */
	private Image getGameIcon(final int nTry) {
		if (nTry > GameView.MAX_TRIES) {
			return null;
		}
		DefaultResourceLoader resolv = new DefaultResourceLoader();
		try {
			Resource icon = resolv.getResource(GameView.ICON_LOCATION);
			if (!icon.exists()) {
				GameConf.GAME_LOGGER.error("Icon does not exist.");
				return null;
			}
			// Read data to local buffer.
			ByteArrayInputStream is = new ByteArrayInputStream(IOUtils.toByteArray(icon.getInputStream()));
			return ImageIO.read(is);
		} catch (IOException e) {
			GameConf.GAME_LOGGER.debug(e + ", Icon does not exist. Try " + nTry);
			return this.getGameIcon(nTry + 1);
		}
	}

	/**
	 * Starts the View by periodically invoking renderLoop().
	 */
	@Override
	public void start() {
		ThreadUtils.runDaemon("GameView", this::update);
	}

	/**
	 * "Update-Thread" content.
	 */
	private void update() {
		while (this.frame.isVisible()) {
			this.renderLoop();
			ThreadUtils.sleep(GameConf.RENDER_DELTA);
		}
	}

	/**
	 * Games main render loop that is periodically called. It updates the canvas
	 * by iterating over all GameElements that GameMode.getGameElementIterator()
	 * supplies and invoking each render()
	 */
	public void renderLoop() {
		IScene scene = this.model.getScene();
		if (this.model.filterChanged()) {
			this.grid.setFilter(this.model.getFilter());
		}

		// Create temporary GC on new Image and let field draw on that
		// Double buffering reduces flickering
		Graphics2D graphics = (Graphics2D) this.bufferStrategy.getDrawGraphics();

		// set current camera position
		this.grid.setGraphics(graphics);
		this.grid.setCurrentOffset(scene.getCameraOffset());
		this.grid.setBackground(GameConf.GAME_BACKGROUD_COLOR);

		this.drawElements(scene);
		this.drawDebug();

		// draw temporary image on actual cavans
		graphics.dispose();
		this.bufferStrategy.show();
	}

	/**
	 * Draw {@link GuiElement GuiElements} and {@link GameElement GameElements}.
	 *
	 * @param scene
	 *            the current scene
	 */
	private void drawElements(IScene scene) {
		scene.applyToGameElements(e -> e.render(this.grid));
		scene.applyToGuiElements(e -> e.render(this.grid));
	}

	/**
	 * If {@link GameConf#DEBUG} is set this method will be used for drawing
	 * stats.
	 */
	private void drawDebug() {
		if (!GameConf.DEBUG) {
			return;
		}
		// draw FPS
		String debugInfo = "FPS: " + this.getFPS();
		this.grid.drawText(new Vec(GameConf.PIXEL_W - 10, GameConf.PIXEL_H - 60), debugInfo, GameConf.HINT_TEXT, false);

		Map<String, Integer> classCounter = new TreeMap<>();

		this.model.getScene().applyToGameElements(e -> {
			String className = e.getClass().getSimpleName();
			if (classCounter.containsKey(className)) {
				classCounter.put(className, classCounter.get(className) + 1);
			} else {
				classCounter.put(className, 1);
			}
		});

		StringBuilder resultName = new StringBuilder().append("GameElements\n");
		StringBuilder resultNum = new StringBuilder();
		StringBuilder resultDur = new StringBuilder().append("\n");

		resultNum.append(this.model.getScene().getGameElementCount()).append("\n");

		Map<String, Long> durations = this.model.getScene().getGameElementDurations();
		classCounter.entrySet().forEach((e) -> {
			resultName.append(e.getKey());
			resultName.append("\n");
			resultNum.append(e.getValue());
			resultNum.append("\n");
			Long dur = durations.get(e.getKey());
			resultDur.append(dur == null ? "-" : dur);
			resultDur.append("\n");
		});

		this.grid.drawText(new Vec(GameConf.PIXEL_W - 55, GameConf.PIXEL_H / 4f), resultName.toString(), GameConf.HINT_TEXT, false);
		this.grid.drawText(new Vec(GameConf.PIXEL_W - 30, GameConf.PIXEL_H / 4f), resultNum.toString(), GameConf.HINT_TEXT, false);
		this.grid.drawText(new Vec(GameConf.PIXEL_W - 5, GameConf.PIXEL_H / 4f), resultDur.toString(), GameConf.HINT_TEXT, false);
	}

	/**
	 * Get the latest FPS.
	 *
	 * @return the FPS
	 */
	private long getFPS() {
		if (GameTime.isPaused()) {
			return 1000L * this.fpsQueue.size() / this.lastFpsSum;
		}

		long thisTime = GameTime.getTime();
		long deltaTime = thisTime - this.lastRenderTime;
		this.lastRenderTime = GameTime.getTime();

		if (this.fpsQueue.size() > GameView.FPS_COUNTER && this.lastFpsSum != -1) {
			// Queue filled & fps set --> Speedup
			long fpsDelete = this.fpsQueue.remove();
			this.fpsQueue.add(deltaTime);
			this.lastFpsSum -= fpsDelete;
			this.lastFpsSum += deltaTime;
			return 1000 * GameView.FPS_COUNTER / this.lastFpsSum;

		}

		if (this.lastFpsSum == -1) {
			// Fill Queue
			this.fpsQueue.add(deltaTime);
		}
		if (this.fpsQueue.size() > GameView.FPS_COUNTER) {
			this.lastFpsSum = this.fpsQueue.stream().mapToLong(Long::longValue).sum();
		}

		return 1000L * this.fpsQueue.size() / this.fpsQueue.stream().mapToLong(Long::longValue).sum();
	}

	@Override
	public void attachMe(InputHelper inputHelper) {
		// Add our custom KeyListener to an object
		KeyAdapter adapter = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				inputHelper.press(e.getKeyCode());
			}

			@Override
			public void keyReleased(KeyEvent e) {
				inputHelper.release(e.getKeyCode());
			}
		};
		this.frame.addKeyListener(adapter);

	}

}