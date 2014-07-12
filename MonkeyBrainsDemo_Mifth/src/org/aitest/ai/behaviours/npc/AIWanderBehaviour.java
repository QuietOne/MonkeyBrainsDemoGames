package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.WanderBehaviour;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AIWanderBehaviour extends WanderBehaviour{

    public AIWanderBehaviour(Agent agent) {
        super(agent);
    }

    @Override
    protected void controlUpdate(float tpf) {
        super.controlUpdate(tpf);
        //FIXME: I need interaction with walls.
    }
    
    
}
