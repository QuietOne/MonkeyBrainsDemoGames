package org.aitest.ai.utils;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.AbstractBullet;
import com.jme3.ai.agents.util.AbstractWeapon;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;
import org.aitest.ai.model.AIModel;

/**
 *
 * @author Tihomir Radosavljevic
 * @author mifth
 * @version 1.0
 */
public class Sword extends AbstractWeapon {

    public Sword(Agent agent) {
        this.agent = agent;
        name = "sword";
        cooldown = 0.6f;
        attackDamage = 30f;
        numberOfBullets = -1;
        minAttackRange = 0;
        //CHECK: if number is correct.
        maxAttackRange = 1f;
    }

    @Override
    protected AbstractBullet controlAttack(Vector3f direction, float tpf) {
        for (PhysicsCollisionObject physObj : spatial.getControl(GhostControl.class).getOverlappingObjects()) {
            Spatial spObj = (Spatial) physObj.getUserObject();
            AIModel aiModel = spObj.getControl(AIModel.class);

            //if somebody is being hit and that one is not me
            if (aiModel != null && !aiModel.equals((AIModel) agent.getModel())) {
                Game.getInstance().agentAttack(agent, aiModel.getAgent(), this);
                break;
            }
        }
        //get animation for sword strike
        List<AnimControl> animationList = ((AIModel) agent.getModel()).getAnimationList();
        for (AnimControl animation : animationList) {
            if (!animation.getChannel(0).getAnimationName().equals("strike_sword")) {
                animation.getChannel(0).setAnim("strike_sword", 0.2f);
                animation.getChannel(0).setSpeed(1.0f);
                animation.getChannel(0).setLoopMode(LoopMode.DontLoop);
            }
        }
        return null;
    }
}
