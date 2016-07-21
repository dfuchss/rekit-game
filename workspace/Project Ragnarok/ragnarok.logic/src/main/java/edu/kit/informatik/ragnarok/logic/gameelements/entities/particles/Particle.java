package edu.kit.informatik.ragnarok.logic.gameelements.entities.particles;

import edu.kit.informatik.ragnarok.logic.Field;
import edu.kit.informatik.ragnarok.logic.gameelements.GameElement;
import edu.kit.informatik.ragnarok.logic.gameelements.Team;
import edu.kit.informatik.ragnarok.primitives.geometry.Direction;
import edu.kit.informatik.ragnarok.primitives.geometry.Polygon;
import edu.kit.informatik.ragnarok.primitives.geometry.Vec;
import edu.kit.informatik.ragnarok.primitives.image.RGBAColor;
import edu.kit.informatik.ragnarok.primitives.time.Progress;
import edu.kit.informatik.ragnarok.primitives.time.Timer;

/**
 * A simple GameElement with no collision and only with graphical effects. Every
 * Particle has a fixed lifeTime after which it removes itself.
 * <p>
 * Sad life.
 * </p>
 * Can be given many options as to how to draw the particle and how it changes
 * these options over time.
 *
 * @author Angelo Aracri
 * @version 1.0
 */
public class Particle extends GameElement {
	/**
	 * The initial polygon
	 */
	private Polygon initialPolygon;
	/**
	 * The current polygon
	 */
	private Polygon polygon;
	/**
	 * Red Channel
	 */
	private Progress colorR;
	/**
	 * Green Channel
	 */
	private Progress colorG;
	/**
	 * Blue Channel
	 */
	private Progress colorB;
	/**
	 * Alpha Channel
	 */
	private Progress colorA;
	/**
	 * The speed of the particle
	 */
	private Progress speed;
	/**
	 * The angle of the particle
	 */
	private Progress angle;
	/**
	 * The rotation of the particle
	 */
	private Progress rotation;
	/**
	 * The scale of the particle
	 */
	private Progress scale;
	/**
	 * The lifetime timer of the particle
	 */
	private Timer timer;
	/**
	 * The current color
	 */
	private RGBAColor currentCol = new RGBAColor(0, 0, 0, 0);
	/**
	 * The current movement vector
	 */
	private Vec movementVec = null;

	/**
	 * Constructor that takes all ProgressDependencies required for the Particle
	 * behavior
	 *
	 * @param polygon
	 *            the shape that the particle should be drawn with
	 * @param pos
	 *            the initial position of the particle
	 * @param time
	 *            the time in seconds for how the particle will be rendered
	 * @param size
	 *            the <i>ProgressDendency</i> for the polygons size
	 * @param speed
	 *            the <i>ProgressDendency</i> for the polygons movement speed
	 * @param angle
	 *            the <i>ProgressDendency</i> for the polygons movement angle
	 * @param colorR
	 *            the <i>ProgressDendency</i> for the polygons red color channel
	 * @param colorG
	 *            the <i>ProgressDendency</i> for the polygons green color
	 *            channel
	 * @param colorB
	 *            the <i>ProgressDendency</i> for the polygons blue color
	 *            channel
	 * @param colorA
	 *            the <i>ProgressDendency</i> for the polygons alpha color
	 *            channel
	 */
	public Particle(Polygon polygon, Vec pos, float lifeTime, Progress scale, Progress speed, Progress rotation, Progress angle,
			Progress colorR, Progress colorG, Progress colorB, Progress colorA) {
		super(pos, new Vec(), new Vec(1), Team.NEUTRAL);

		// clone polygon so we can work with it
		this.polygon = this.initialPolygon = polygon.clone();

		// set shape options
		this.scale = scale;
		this.rotation = rotation;
			
		// set movement options
		this.speed = speed;
		this.angle = angle;
		

		// set color options
		this.colorR = colorR;
		this.colorG = colorG;
		this.colorB = colorB;
		this.colorA = colorA;

		// create timer to get progress between 0 and 1 relative to time
		this.timer = new Timer(lifeTime);

		// set position
		this.setPos(pos.clone());
	}

	@Override
	public void logicLoop(float deltaTime) {
		// tell timer passed time
		this.timer.removeTime(deltaTime);

		// If time is up: kill yourself
		if (this.timer.timeUp()) {
			this.getScene().removeGameElement(this);
		} else {
			// get current progress of Particle
			float progress = this.timer.getProgress();
			this.currentCol = new RGBAColor(this.capColor(this.colorR.getNow(progress)), this.capColor(this.colorG.getNow(progress)),
					this.capColor(this.colorB.getNow(progress)), this.capColor(this.colorA.getNow(progress)));

			// get speed and angle relative to progress
			float speed = this.speed.getNow(progress);
			float angle = this.angle.getNow(progress);
			
			// get rotation and scale of polygon
			float rotation = this.rotation.getNow(progress);
			float scale = this.scale.getNow(progress);

			if (rotation != 0) {
				this.polygon = this.initialPolygon.rotate(rotation, this.getPos().add(new Vec(0.1f, 0.1f)));
			}
			
			if (scale != 1) {
				this.polygon = this.polygon.scale(scale);
			}
			

			// only recalculate movement vector if speed and angle are dynamic
			if (this.movementVec == null || !this.speed.isStatic() || !this.angle.isStatic()) {
				// get Einheitsvector in 0degrees
				this.movementVec = Direction.UP.getVector();

				// set Amount in units/time
				this.movementVec = this.movementVec.scalar(speed * deltaTime);

				// set Angle
				this.movementVec = this.movementVec.rotate(angle);
			}

			// apply distance-vector to position
			this.setPos(this.getPos().add(this.movementVec));
		}
	}

	@Override
	public int getOrderZ() {
		return 100;
	}

	@Override
	public void internalRender(Field f) {
		// float progress = timer.getProgress();
		this.polygon.moveTo(this.getPos());

		f.drawPolygon(this.polygon, this.currentCol, true);
	}

	/**
	 * Caps a color to make sure it is never smaller than 0 or greater than 255
	 *
	 * @param col
	 *            the color value to cap
	 * @return the capped color
	 */
	public int capColor(float col) {
		int intCol = (int) col;
		return intCol > 255 ? 255 : (intCol < 0 ? 0 : intCol);
	}

}
