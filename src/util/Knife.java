package util;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.AbstractWeapon;
import com.jme3.ai.agents.util.AbstractBullet;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 * Weapon for killing instantly agents that is near by. Made for testing steering
 * behaviours.
 * @author Tihomir Radosavljević
 */
public class Knife extends AbstractWeapon{

     public Knife(String name, Agent agent){
        this.name = name;
        this.agent = agent;
        this.maxAttackRange = 20f;
        this.minAttackRange = 0;
        this.attackDamage = agent.getMaxHitPoint();
        this.numberOfBullets = -1;
        cooldown = 0.02f;
    }
    
    //@Override
    protected AbstractBullet controlAttack(Agent target, float tpf) {
        Game game = Game.getInstance();
        //this the part where it hurts
        if (hurts(target) && !agent.equals(target)) {
            game.agentAttack(agent, target);
            ((Quad) ((Geometry) ((Node) target.getSpatial()).getChild("healthbar")).getMesh()).updateGeometry(target.getHitPoint() / 100 * 4, 0.2f);
        }
        return null;
    }

    @Override
    protected AbstractBullet controlAttack(Vector3f direction, float tpf) {
        Game game = Game.getInstance();
        //this the part where it hurts
        for (Agent target : game.getAgents()) {
            if (hurts(target) && !agent.equals(target)) {
                game.agentAttack(agent, target);
                ((Quad) ((Geometry) ((Node) target.getSpatial()).getChild("healthbar")).getMesh()).updateGeometry(target.getHitPoint() / 100 * 4, 0.2f);
            }
        }
        return null;
    }

    private boolean hurts(Agent agent) {
        if (!Game.getInstance().isFriendlyFire() && this.agent.isSameTeam(agent)) {
            return false;
        }
        if (this.agent.getSpatial().getLocalTranslation().distance(agent.getLocalTranslation()) < 5 && agent.isEnabled()) {
            return true;
        }
        return false;
    }
}
