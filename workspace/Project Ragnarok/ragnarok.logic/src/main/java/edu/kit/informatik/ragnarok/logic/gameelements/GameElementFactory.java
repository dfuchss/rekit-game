package edu.kit.informatik.ragnarok.logic.gameelements;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import edu.kit.informatik.ragnarok.logic.gameelements.entities.type.Enemy;
import edu.kit.informatik.ragnarok.logic.gameelements.entities.type.Pickup;
import edu.kit.informatik.ragnarok.logic.gameelements.inanimate.Inanimate;
import edu.kit.informatik.ragnarok.logic.scene.Scene;
import edu.kit.informatik.ragnarok.primitives.Vec;

public class GameElementFactory {

	private static Scene scene;

	public static void init(Scene scene) {
		GameElementFactory.scene = scene;
		GameElementFactory.load();
		GameElementFactory.RNG = new Random();
	}

	/**
	 * A HashMap containing every spawnable GameElement sorted by its type. -1
	 * is reserved for enemies, -2 is reserver for pickups.
	 */
	private static HashMap<Integer, GameElement[]> prototypeTypes;

	/**
	 * A HashMap containing every spawnable GameElement mapped from their
	 * respective getId().
	 */
	private static HashMap<Integer, GameElement> prototypes;

	private static Random RNG;

	public static void generate(int id, int x, int y) {
		GameElement element;
		if (id > 0) {
			element = GameElementFactory.prototypes.get(id);
		} else {
			// if this type of GameElement is not defined
			if (!GameElementFactory.prototypeTypes.containsKey(id)) {
				return;
			}
			GameElement[] elemArray = GameElementFactory.prototypeTypes.get(id);
			element = elemArray[GameElementFactory.RNG.nextInt(elemArray.length)];
		}
		if (element != null) {
			// Add enemy to model
			GameElementFactory.scene.addGameElement(element.create(new Vec(x, y)));
		}

	}

	private final synchronized static void load() {
		if (GameElementFactory.prototypes != null) {
			return;
		}
		HashMap<Integer, GameElement> protos = new HashMap<>();
		HashMap<Integer, GameElement[]> protoTypes = new HashMap<>();

		// Put Inanimates in collection
		protos.put(1, Inanimate.getPrototype());

		// Put Enemies in collection and in separate array
		Set<Enemy> enemyPrototypes = Enemy.getEnemyPrototypes();
		Enemy[] enemyCollection = new Enemy[enemyPrototypes.size()];
		int i = 0;
		for (Enemy e : enemyPrototypes) {
			protos.put(e.getID(), e);
			enemyCollection[i++] = e;
		}
		protoTypes.put(-1, enemyCollection);

		// Put Pickups in collection and in separate array
		Set<Pickup> pickupPrototypes = Pickup.getPickupPrototypes();
		Pickup[] pickupCollection = new Pickup[pickupPrototypes.size()];
		i = 0;
		for (Pickup e : pickupPrototypes) {
			protos.put(e.getID(), e);
			pickupCollection[i++] = e;
		}
		protoTypes.put(-2, pickupCollection);

		// Save local data structures in static attributes
		GameElementFactory.prototypes = protos;
		GameElementFactory.prototypeTypes = protoTypes;
	}
}
