package org.aitest.ai.control;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.ai.agents.util.control.AIAppState;
import com.jme3.ai.agents.util.control.AIHPControl;
import java.util.List;

/**
 *
 * @author Tihomir Radosavljević
 * @version 1.0.0
 */
public class HPControl implements AIHPControl{

    public void decreaseHP(Agent agent, float damage) {
        //finding agent and decreasing his healthbar
        List<Agent> agents = AIAppState.getInstance().getAgents();
            for (int i=0; i < agents.size(); i++) {
                if (agents.get(i).equals(agent)) {
                    agents.get(i).getHpSystem().decreaseHP(damage);
                    if (!agents.get(i).isEnabled()) {
                        agents.get(i).stop();
                        agents.get(i).getSpatial().removeFromParent();
                    }
                    break;
                }
            }
        
    }

    public void decreaseHP(GameEntity gameEntity, float dasmage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
