package edu.kit.informatik.ragnarok.logic.parallax;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.kit.informatik.ragnarok.logic.gameelements.Field;
import edu.kit.informatik.ragnarok.primitives.Vec2D;
import edu.kit.informatik.ragnarok.util.RGBAColor;

public class HeapLayer extends ParallaxLayer {

	/**
	 * Prototype for cloning new HeapElements
	 */
	private HeapElement prototype;

	/**
	 * Holds a list of all heapElements to render
	 */
	protected List<HeapElement> elems;

	public HeapLayer(HeapElement prototype, float distanceFromFront) {
		super(distanceFromFront);
		this.elems = new LinkedList<HeapElement>();
		this.prototype = prototype;
	}

	protected HeapElement getPrototype() {
		return this.prototype;
	}

	@Override
	public void logicLoop(float currentOffset) {
		// calculates this.x and this.generateUntil
		super.logicLoop(currentOffset);

		// while we need to generate
		while (this.currentlyGeneratedUntil + this.fieldXtoLayerX(this.x) < this.generateUntil) {
			float randDist = HeapLayer.calcWithVariance(this.getPrototype().heapDistanceMu(), this.getPrototype().heapDistanceSigma());
			float randAmount = HeapLayer.calcWithVariance(this.getPrototype().elemNumMu(), this.getPrototype().elemNumSigma());

			this.currentlyGeneratedUntil += randDist;

			for (int i = 0; i < randAmount; i++) {
				// Calc Position (relative to screen!)
				float randX = this.currentlyGeneratedUntil
						+ HeapLayer.calcWithVariance(this.getPrototype().elemXMu(), this.getPrototype().elemXSigma());
				float randY = HeapLayer.calcWithVariance(this.getPrototype().elemYMu(), this.getPrototype().elemYSigma());
				Vec2D pos = new Vec2D(randX, randY);

				// Calc Size
				float randW = HeapLayer.calcWithVariance(this.getPrototype().elemWidthMu(), this.getPrototype().elemWidthSigma());
				float randH = HeapLayer.calcWithVariance(this.getPrototype().elemHeightMu(), this.getPrototype().elemHeightSigma());
				Vec2D size = new Vec2D(randW, randH);

				// Calc Color
				int randR = (int) HeapLayer.calcWithVariance(this.getPrototype().elemColRMu(), this.getPrototype().elemColRSigma());
				int randG = (int) HeapLayer.calcWithVariance(this.getPrototype().elemColGMu(), this.getPrototype().elemColGSigma());
				int randB = (int) HeapLayer.calcWithVariance(this.getPrototype().elemColBMu(), this.getPrototype().elemColBSigma());
				int randA = (int) HeapLayer.calcWithVariance(this.getPrototype().elemColAMu(), this.getPrototype().elemColASigma());
				RGBAColor col = new RGBAColor(randR, randG, randB, randA);

				// Create actual HeapElem object and (blockingly) add it
				HeapElement elem = this.getPrototype().clone(this, pos, size, col);
				synchronized (this.synchronize()) {
					this.elems.add(elem);
				}
			}

		}
	}

	@Override
	public void render(Field f) {
		// call super method before this (drawing white rectangle)
		super.render(f);
		synchronized (this.synchronize()) {
			Iterator<HeapElement> it = this.elems.iterator();
			while (it.hasNext()) {
				HeapElement e = it.next();
				if (e.getFieldPos().getX() + e.getSize().getX() < this.x) {
					it.remove();
				} else {
					e.render(f);
				}
			}
		}
	}

	@Override
	public int getElementCount() {
		return this.elems.size();
	}

	public static float calcWithVariance(float mu, float sigma) {
		return mu + (ParallaxLayer.RNG.nextFloat() * 2 * sigma) - sigma;
	}
}