package ragnarok.logic;

import java.util.Map;
import java.util.function.Consumer;

import ragnarok.core.CameraTarget;
import ragnarok.core.Team;
import ragnarok.logic.gameelements.GameElement;
import ragnarok.logic.gameelements.entities.Player;
import ragnarok.logic.gui.GuiElement;
import ragnarok.logic.gui.menu.MenuItem;
import ragnarok.logic.scene.Scenes;

/**
 * This is the public interface of all Scenes.
 *
 * @author Dominik Fuchss
 *
 */
public interface IScene {
	/**
	 * Get the current player of {@code null} if none set.
	 *
	 * @return the current player of {@code null}
	 */
	Player getPlayer();

	/**
	 * Get the current score of the player.
	 *
	 * @return the current score or 0 if none available
	 */
	int getScore();

	/**
	 * Get the current high score for the current level.
	 *
	 * @return the current high score or 0 if none available
	 */
	int getHighScore();

	/**
	 * Adds a GameElement to the Model. The elements will not directly be added
	 * to the internal data structure to prevent concurrency errors. Instead
	 * there is an internal list to hold all waiting GameElements that will be
	 * added in the next call of logicLoop
	 *
	 * @param e
	 *            the GameElement to add
	 */
	void addGameElement(GameElement e);

	/**
	 * Removes a GameElement from the Model The elements will not directly be
	 * removed from the internal data structure to prevent concurrency errors.
	 * Instead there is an internal list to hold all waiting GameElements that
	 * will be removed in the next call of logicLoop
	 *
	 * @param e
	 *            the GameElement to remove
	 */
	void markForRemove(GameElement e);

	/**
	 * Adds a GuiElement to the GameModel.
	 *
	 * @param e
	 *            the GuiElement to add
	 */
	void addGuiElement(GuiElement e);

	/**
	 * Removes a GuiElement to the GameModel.
	 *
	 * @param e
	 *            the GuiElement to remove
	 */
	void removeGuiElement(GuiElement e);

	/**
	 * Set the camera target.
	 *
	 * @param cameraTarget
	 *            the camera target
	 */
	void setCameraTarget(CameraTarget cameraTarget);

	/**
	 * Get the current camera offset.
	 *
	 * @return the current camera offset
	 */
	float getCameraOffset();

	/**
	 * Apply function on game elements (non-neutral).
	 *
	 * @param function
	 *            the function
	 * @see Team#isNeutral()
	 */
	void applyToNonNeutralGameElements(Consumer<GameElement> function);

	/**
	 * Apply function on all game elements.
	 *
	 * @param function
	 *            the function
	 */
	void applyToGameElements(Consumer<GameElement> function);

	/**
	 * Apply function on game elements.
	 *
	 * @param function
	 *            the function
	 */
	void applyToGuiElements(Consumer<GuiElement> function);

	/**
	 * End a Game/Scene.
	 *
	 * @param won
	 *            indicates whether successful or died
	 */
	void end(boolean won);

	/**
	 * Get the model of the MVC.
	 *
	 * @return the model
	 */
	Model getModel();

	/**
	 * Invoke logic.
	 *
	 */
	void logicLoop();

	/**
	 * Initialize the scene. e.g. build Level/GUI so Scene is ready to be drawn
	 * Must be called on restart.
	 */
	void init();

	/**
	 * Start the scene. Begin drawing and Player/Enemies will begin to move.
	 */
	default void start() {
	}

	/**
	 * Stop the scene. End drawing and Player/Enemies will end to move.
	 */
	default void stop() {
	}

	/**
	 * Restart the scene.
	 */
	default void restart() {
		this.init();
		this.start();
	}

	/**
	 * Get a map of duration-time of elements.
	 *
	 * @return the duration-time of elements by class
	 */
	Map<String, Long> getGameElementDurations();

	/**
	 * Get the amount of elements in the scene.
	 *
	 * @return the amount of elements
	 */
	int getGameElementCount();

	/**
	 * Get the associated Root-MenuItem.
	 *
	 * @return the root-menuItem or {@code null} if no {@link Scenes#MENU}
	 */
	MenuItem getMenu();

	/**
	 * Toggle pause.
	 */
	void togglePause();

	/**
	 * Indicates whether game is paused.
	 *
	 * @return {@code true} if paused, {@code false} otherwise
	 */
	boolean isPaused();

}