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
package fps.robotfight.control;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.ai.agents.util.control.HitPointsControl;
import java.util.List;

/**
 *
 * @author Tihomir Radosavljević
 * @version 1.0.0
 */
public class RobotFightHitPointsControl implements HitPointsControl {

    public void decreaseHitPoints(GameEntity targetedEntity, float damage) {
        //finding agent and decreasing his healthbar
        List<Agent> agents = MonkeyBrainsAppState.getInstance().getAgents();
        Agent agent = (Agent) targetedEntity;
        for (int i = 0; i < agents.size(); i++) {
            if (agents.get(i).equals(agent)) {
                agents.get(i).getHitPoints().decreaseHitPoints(damage);
                if (!agents.get(i).isEnabled()) {
                    agents.get(i).stop();
                    agents.get(i).getSpatial().removeFromParent();
                }
                break;
            }
        }
    }
}
