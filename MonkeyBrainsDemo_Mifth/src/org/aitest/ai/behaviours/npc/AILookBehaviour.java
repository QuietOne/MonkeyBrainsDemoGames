package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleLookBehaviour;
import com.jme3.ai.agents.util.GameObject;
import java.util.List;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AILookBehaviour extends SimpleLookBehaviour {

    public AILookBehaviour(Agent agent) {
        super(agent);
    }

    @Override
    protected List<GameObject> look(Agent agent, float viewAngle) {
        throw new UnsupportedOperationException("I don't know how to get obstacles");
    }
}
