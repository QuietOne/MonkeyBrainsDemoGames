package fps.robotfight.util;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.AIAppState;
import com.jme3.ai.agents.util.weapons.AbstractWeapon;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 * Weapon for killing instantly agents that is near by. Made for testing
 * steering behaviours.
 *
 * @author Tihomir Radosavljević
 */
public class Knife extends AbstractWeapon {

    public Knife(String name, Agent agent) {
        this.name = name;
        this.agent = agent;
        this.maxAttackRange = 20f;
        this.minAttackRange = 0;
        this.attackDamage = agent.getHitPoints().getMaxHP();
        this.cooldown = 0.02f;
    }

    @Override
    public void attack(Vector3f targetPosition, float tpf) {
        AIAppState game = AIAppState.getInstance();
        //this the part where it hurts
        for (Agent target : game.getAgents()) {
            if (hurts(target) && !agent.equals(target)) {
                game.agentAttack(agent, target, this);
                ((Quad) ((Geometry) ((Node) target.getSpatial()).getChild("healthbar")).getMesh()).updateGeometry(target.getHitPoints().getCurrentHP() / 100 * 4, 0.2f);
            }
        }
    }

    private boolean hurts(Agent agent) {
        if (!AIAppState.getInstance().isFriendlyFire() && this.agent.isSameTeam(agent)) {
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
