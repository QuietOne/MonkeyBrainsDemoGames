package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.SeekBehaviour;
import com.jme3.ai.agents.events.GameObjectSeenEvent;
import com.jme3.ai.agents.events.GameObjectSeenListener;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AISeekBehaviour extends SeekBehaviour implements GameObjectSeenListener{

    public AISeekBehaviour(Agent agent) {
        super(agent, null);
    }

    public void handleGameObjectSeenEvent(GameObjectSeenEvent event) {
        setTarget((Agent) event.getGameObjectSeen());
    }

    @Override
    protected void controlUpdate(float tpf) {
        super.controlUpdate(tpf);
        //FIXME: needed interaction with walls.
    }
    
    
    
}
