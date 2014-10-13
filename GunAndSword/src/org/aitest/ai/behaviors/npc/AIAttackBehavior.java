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
import com.jme3.ai.agents.behaviors.npc.SimpleAttackBehavior;
import com.jme3.ai.agents.events.GameEntitySeenEvent;
import java.util.Random;
import org.aitest.ai.utils.GunAndSwordInventory;

/**
 * Behavior for attacking opponent if opponent is seen.
 *
 * @author Tihomir Radosavljević
 * @version 1.0.1
 */
public class AIAttackBehavior extends SimpleAttackBehavior {

    private GunAndSwordInventory weapons;
    /**
     * Bigger value means easier game, if it is 1, then agent will never miss.
     * Must be greater or equal to 1.
     */
    private final int simplicity = 60;
    /**
     * To add some randomness to game.
     */
    private Random random;

    public AIAttackBehavior(Agent agent) {
        super(agent);
        weapons = (GunAndSwordInventory) agent.getInventory();
        random = new Random();
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (targetPosition != null) {
            weapons.getGun().attack(targetedEntity, tpf);
            targetPosition = null;
            //is he supossed to miss next time
            missOrNot((Agent) targetedEntity);
        } else {
            //if target is seen
            if (targetedEntity != null && targetedEntity.isEnabled()) {
                //attack with all weapon at disposal

                //if target is in range of sword strike, strike him
                if (weapons.getSword().isInRange(targetedEntity)) {
                    weapons.getSword().attack(targetedEntity, tpf);
                }
                //if target is in range of gun, fire him
                if (weapons.getGun().isInRange(targetedEntity)) {
                    weapons.getGun().attack(targetedEntity, tpf);
                }
                //is he supossed to miss next time
                missOrNot((Agent) targetedEntity);
            }
        }
    }

    @Override
    public void handleGameEntitySeenEvent(GameEntitySeenEvent event) {
        if (event.getGameEntitySeen() instanceof Agent) {
            Agent targetAgent = (Agent) event.getGameEntitySeen();
            if (agent.isSameTeam(targetAgent)) {
                return;
            }
            targetedEntity = targetAgent;
            missOrNot(targetAgent);
            enabled = true;
        }
    }

    private void missOrNot(Agent agent) {
        if (agent == null) {
            targetPosition = null;
        } else {
            if (simplicity > 1) {
                int number = random.nextInt(simplicity);
                if (number > 1) {
                    targetPosition = agent.getLocalTranslation().clone().mult(1.1f);
                }
            }
        }
    }
}
