package fps.robotfight.util;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.ai.agents.util.weapons.AbstractWeapon;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 * Weapon for slowly killing agents that are near by. Made for testing steering
 * behaviors.
 *
 * @author Tihomir Radosavljević
 * @version 1.0.0
 */
public class Knife extends AbstractWeapon {

    public Knife(String name, Agent agent) {
        this.name = name;
        this.agent = agent;
        this.maxAttackRange = 20f;
        this.minAttackRange = 0;
        this.attackDamage = 1;
        this.cooldown = 0.02f;
    }

    @Override
    public void attack(Vector3f targetPosition, float tpf) {
        MonkeyBrainsAppState brainsAppState = MonkeyBrainsAppState.getInstance();
        //this the part where it hurts
        for (Agent target : brainsAppState.getAgents()) {
            if (hurts(target) && !agent.equals(target)) {
                brainsAppState.decreaseHitPoints(target, this);
                ((Quad) ((Geometry) ((Node) target.getSpatial()).getChild("healthbar"))
                        .getMesh()).updateGeometry(target.getHitPoints().getCurrentHitPoints() / 100 * 4, 0.2f);
            }
        }
    }

    private boolean hurts(Agent agent) {
        if (!MonkeyBrainsAppState.getInstance().isFriendlyFire() && this.agent.isSameTeam(agent)) {
            return false;
        }
        if (this.agent.getSpatial().getLocalTranslation().distance(agent.getLocalTranslation()) < 5 && agent.isEnabled()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    protected boolean isUnlimitedUse() {
        return true;
    }

    @Override
    protected void useWeapon() {
    }
}
