package rekit.logic.gameelements.entities.enemies.bosses.rocketboss.arm.armstate;

import rekit.logic.gameelements.GameElement;
import rekit.logic.gameelements.entities.enemies.Rocket;
import rekit.logic.gameelements.entities.enemies.bosses.rocketboss.RocketBoss;
import rekit.logic.gameelements.entities.enemies.bosses.rocketboss.arm.Arm;
import rekit.logic.gameelements.entities.enemies.bosses.rocketboss.arm.armaction.ArmAction;
import rekit.logic.gameelements.entities.enemies.bosses.rocketboss.arm.armaction.ArmActionRocketLauncher;
import rekit.primitives.geometry.Vec;
import rekit.util.state.State;
import rekit.util.state.TimeStateMachine;

public class ArmActionState extends ArmState {

	public ArmActionState(Arm parentArm) {
		super(parentArm);
	}

	@Override
	public void enter(TimeStateMachine parent) {
		super.enter(parent);
		
		Vec spawnPos = this.getParentArm().getHandPos();
		
		GameElement rocket = new Rocket().create(spawnPos, new String[]{});
		this.getParentArm().getParent().getScene().addGameElement(rocket);
	}

	@Override
	public State getNextState() {
		return new ArmUnbuildState(getParentArm());
	}

	@Override
	public long getTimerTime() {
		return RocketBoss.ARM_STATE_TIME_ACTION;
	}

	public float getSegmentAmount() {
		// Return 1 for all segments
		return 1;
	}

}