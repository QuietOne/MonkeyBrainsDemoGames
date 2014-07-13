package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.ai.agents.events.GameObjectSeenEvent;
import com.jme3.ai.agents.events.GameObjectSeenListener;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AIAttackBehaviour extends Behaviour implements GameObjectSeenListener{

    public AIAttackBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void handleGameObjectSeenEvent(GameObjectSeenEvent event) {
    }
    
}
