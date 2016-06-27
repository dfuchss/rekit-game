package edu.kit.informatik.ragnarok.logic.parallax;

import java.util.LinkedList;
import java.util.List;

import edu.kit.informatik.ragnarok.primitives.Vec;
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
		this.elems = new LinkedList<>();
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
				Vec pos = new Vec(randX, randY);

				// Calc Size
				float randW = HeapLayer.calcWithVariance(this.getPrototype().elemWidthMu(), this.getPrototype().elemWidthSigma());
				float randH = HeapLayer.calcWithVariance(this.getPrototype().elemHeightMu(), this.getPrototype().elemHeightSigma());
				Vec size = new Vec(randW, randH);

				// Calc Color
				int randR = (int) HeapLayer.calcWithVariance(this.getPrototype().elemColRMu(), this.getPrototype().elemColRSigma());
				int randG = (int) HeapLayer.calcWithVariance(this.getPrototype().elemColGMu(), this.getPrototype().elemColGSigma());
				int randB = (int) HeapLayer.calcWithVariance(this.getPrototype().elemColBMu(), this.getPrototype().elemColBSigma());
				int randA = (int) HeapLayer.calcWithVariance(this.getPrototype().elemColAMu(), this.getPrototype().elemColASigma());
				RGBAColor col = new RGBAColor(randR, randG, randB, randA);

				// Create actual HeapElem object and add it
				HeapElement elem = this.getPrototype().clone(this, pos, size, col);
				elem.backgroundZ -= i;
				this.scene.addGameElement(elem);
			}
		}
	}

	public static float calcWithVariance(float mu, float sigma) {
		return mu + (ParallaxLayer.RNG.nextFloat() * 2 * sigma) - sigma;
	}
}
