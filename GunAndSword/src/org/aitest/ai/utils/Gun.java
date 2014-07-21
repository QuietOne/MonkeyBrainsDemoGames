package org.aitest.ai.utils;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.AbstractBullet;
import com.jme3.ai.agents.util.AbstractWeapon;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.math.Vector3f;
import java.util.List;
import org.aitest.ai.model.AIModel;

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
        maxAttackRange = 1000f;
        minAttackRange = 3f;
    }

    @Override
    protected AbstractBullet controlAttack(Vector3f direction, float tpf) {
        List<AnimControl> animationList = ((AIModel) agent.getModel()).getAnimationList();
        //get animation for fired gun
        for (AnimControl animation : animationList) {
            if (!animation.getChannel(0).getAnimationName().equals("shoot")) {
                animation.getChannel(0).setAnim("shoot", 0.1f);
                animation.getChannel(0).setSpeed(1.5f);
                animation.getChannel(0).setLoopMode(LoopMode.DontLoop);
            }
        }
        return new Bullet(this, agent.getLocalTranslation());
    }
}
