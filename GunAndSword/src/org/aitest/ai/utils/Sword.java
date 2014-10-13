/**
 * Copyright (c) 2014, jMonkeyEngine All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.aitest.ai.utils;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
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
 * @author Tihomir Radosavljević
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
        MonkeyBrainsAppState brainsAppState = MonkeyBrainsAppState.getInstance();
        for (PhysicsCollisionObject physObj : spatial.getControl(GhostControl.class).getOverlappingObjects()) {
            Spatial spObj = (Spatial) physObj.getUserObject();
            AIModel aiModel = spObj.getControl(AIModel.class);

            //if somebody is being hit and that one is not me
            if (aiModel != null && !aiModel.equals((AIModel) agent.getModel())) {
                brainsAppState.decreaseHitPoints(aiModel.getAgent(), this);
                if (!aiModel.getAgent().isEnabled()) {
                    //remove agent from physic space
                    brainsAppState.getApp().getStateManager().getState(BulletAppState.class).getPhysicsSpace().remove(aiModel);
                    //remove agent's sword from physics space
                    brainsAppState.getApp().getStateManager().getState(BulletAppState.class).getPhysicsSpace()
                            .remove(((GunAndSwordInventory) aiModel.getAgent().getInventory()).getSword().getSpatial());
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
