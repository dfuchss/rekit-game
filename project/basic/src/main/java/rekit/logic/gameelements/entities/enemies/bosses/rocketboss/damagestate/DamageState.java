package rekit.logic.gameelements.entities.enemies.bosses.rocketboss.damagestate;

import rekit.logic.gameelements.entities.enemies.bosses.rocketboss.RocketBoss;
import rekit.util.state.State;

public abstract class DamageState extends State {

	/**
	 * Reference to the parenting {@link RocketBoss}.
	 */
	protected RocketBoss parentBoss;

	public void enter(RocketBoss parent) {
		super.enter(parent.getMachine());
		this.parentBoss = parent;
	}

	public void addDamage(int points) {
		this.parent.nextState();
	}

	@Override
	public void logicLoop() {
		// Do nothing to remove time-dependent functionality
	}

	@Override
	public abstract DamageState getNextState();

	@Override
	public long getTimerTime() {
		// Not required, since time-functionality disabled
		return 0;
	}

	/**
	 * Getter for a multiplier that will be used to increase the
	 * {@link RocketBoss RocketBosses} movement speed, the mouth movement, and
	 * the delta between arm-actions.
	 * 
	 * @return the factor for the {@link RocketBoss RocketBosses} time
	 */
	public abstract float getTimeFactor();
	
	public abstract float getMouthAmplitude();

	/**
	 * Getter for the source path of the image for the boss.
	 * 
	 * @return the source path of the {@link RocketBoss RocketBosses} image.
	 */
	public abstract String getHeadImgSrc();

}
