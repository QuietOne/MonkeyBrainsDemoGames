package org.aitest.ai.utils;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.AIAppState;
import com.jme3.ai.agents.util.weapons.AbstractWeapon;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.List;
import org.aitest.ai.model.AIModel;

/**
 *
 * @author Tihomir Radosavljevic
 * @author mifth
 * @version 1.0.1
 */
public class Sword extends AbstractWeapon {

    public Sword(Agent agent) {
        this.agent = agent;
        name = "sword";
        cooldown = 0.6f;
        attackDamage = 30f;
        minAttackRange = 0;
        maxAttackRange = 1f;
        mass = 3;
    }

    @Override
    public void attack(Vector3f targetPosition, float tpf) {
        for (PhysicsCollisionObject physObj : spatial.getControl(GhostControl.class).getOverlappingObjects()) {
            Spatial spObj = (Spatial) physObj.getUserObject();
            AIModel aiModel = spObj.getControl(AIModel.class);

            //if somebody is being hit and that one is not me
            if (aiModel != null && !aiModel.equals((AIModel) agent.getModel())) {
                AIAppState.getInstance().agentAttack(agent, aiModel.getAgent(), this);
                if (!aiModel.getAgent().isEnabled()) {
                    //remove agent from physic space
                    AIAppState.getInstance().getApp().getStateManager().getState(BulletAppState.class).getPhysicsSpace().remove(aiModel);
                    //remove agent's sword from physics space
                    AIAppState.getInstance().getApp().getStateManager().getState(BulletAppState.class).getPhysicsSpace()
                            .remove(((Inventory) aiModel.getAgent().getInventory()).getSword().getSpatial());
                }
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
