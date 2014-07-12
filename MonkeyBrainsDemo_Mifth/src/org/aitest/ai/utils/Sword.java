package org.aitest.ai.utils;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.AbstractBullet;
import com.jme3.ai.agents.util.AbstractWeapon;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.math.Vector3f;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class Sword extends AbstractWeapon {

    public Sword(Agent agent) {
        this.agent = agent;
        name = "Sword";
        cooldown = 0.6f;
        attackDamage = 30f;
        numberOfBullets = -1;
        /*
         * Needed for AI calculations.
         * maxAttackRange
         * minAttackRange
         */      
              
    }

    @Override
    protected AbstractBullet controlAttack(Vector3f direction, float tpf) {
        Game game = Game.getInstance();
        //this the part where it hurts
        for (Agent target : game.getAgents()) {
            if (hurts(target) && !agent.equals(target)) {
                game.agentAttack(agent, target);
            }
        }
        return null;
    }

    /**
     * Method to determinate if sword had touched some agent.
     *
     * @param agent
     * @return
     */
    private boolean hurts(Agent agent) {
        if (!Game.getInstance().isFriendlyFire() && this.agent.isSameTeam(agent)) {
            return false;
        }
        //TODO: add what determines if somebody has been hit
        return false;
    }
}
