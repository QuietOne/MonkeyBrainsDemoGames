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
package org.aitest.ai.behaviors.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.SimpleLookBehavior;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.LinkedList;
import java.util.List;

/**
 * Behavior for scanning environment.
 *
 * @author Tihomir Radosavljević
 * @version 1.0.1
 */
public class AILookBehavior extends SimpleLookBehavior {

    private MonkeyBrainsAppState brainsAppState;

    public AILookBehavior(Agent agent) {
        super(agent);
        brainsAppState = MonkeyBrainsAppState.getInstance();
        typeOfWatching = TypeOfWatching.AGENT_WATCHING;
    }

    @Override
    protected List<GameEntity> look(Agent agent, float viewAngle) {
        List<GameEntity> temp = super.look(agent, viewAngle);
        //is there obstacle between agent and observer
        Vector3f vecStart = agent.getLocalTranslation().clone().setY(1);
        BulletAppState bulletState = brainsAppState.getApp().getStateManager().getState(BulletAppState.class);
        for (int i = 0; i < temp.size(); i++) {
            GameEntity agentInRange = temp.get(i);
            Vector3f vecEnd = agentInRange.getLocalTranslation().clone().setY(1);
            //what has bullet hit
            List<PhysicsRayTestResult> rayTest = bulletState.getPhysicsSpace().rayTest(vecStart, vecEnd);

            float distance = vecEnd.length();
            PhysicsCollisionObject o = null;
            if (rayTest.size() > 0) {
                for (PhysicsRayTestResult getObject : rayTest) {
                    //distance to next collision
                    float fl = getObject.getHitFraction();
                    PhysicsCollisionObject collisionObject = getObject.getCollisionObject();
                    //bullet does not is not supposed to be seen
                    if (collisionObject instanceof GhostControl) {
                        continue;
                    }
                    Spatial thisSpatial = (Spatial) collisionObject.getUserObject();
                    // Get the Enemy to kill
                    if (fl < distance && !thisSpatial.equals(agentInRange.getSpatial())) {
                        temp.remove(agentInRange);
                        o = collisionObject;
                    }
                    
                }
            }
        }
        return temp;
    }
}