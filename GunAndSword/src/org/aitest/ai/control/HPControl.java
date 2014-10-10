package org.aitest.ai.control;

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
public class HPControl implements HitPointsControl {

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
