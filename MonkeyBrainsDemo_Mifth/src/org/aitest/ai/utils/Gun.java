package org.aitest.ai.utils;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.AbstractBullet;
import com.jme3.ai.agents.util.AbstractWeapon;
import com.jme3.math.Vector3f;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class Gun extends AbstractWeapon {

    public Gun(Agent agent) {
        this.agent = agent;
        name = "Gun";
        cooldown = 0.4f;
        attackDamage = 20f;
        numberOfBullets = -1;
        /*
         * Needed for AI calculations.
         * maxAttackRange
         * minAttackRange
         */
    }
    
    @Override
    protected AbstractBullet controlAttack(Vector3f direction, float tpf) {
        return new Bullet(this, direction, AIGameSpatials.getInstance().createBullet(this));
    }
}
