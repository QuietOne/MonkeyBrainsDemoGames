package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.WanderBehaviour;
import com.jme3.math.Vector3f;
import org.aitest.ai.model.AIModel;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AIWanderBehaviour extends WanderBehaviour{

    public AIWanderBehaviour(Agent agent) {
        super(agent);
        //set area of wandering
        setArea(null, null);
    }

    @Override
    protected void controlUpdate(float tpf) {
        changeTargetPosition(tpf);
        Vector3f vel = calculateNewVelocity().mult(tpf);
        ((AIModel) agent.getModel()).setWalkDirection(vel);
        rotateAgent(tpf);
    }
    
    
}
