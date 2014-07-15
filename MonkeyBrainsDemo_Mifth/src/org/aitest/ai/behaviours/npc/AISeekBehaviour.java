package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.SeekBehaviour;
import com.jme3.ai.agents.events.GameObjectSeenEvent;
import com.jme3.ai.agents.events.GameObjectSeenListener;
import com.jme3.math.Vector3f;
import org.aitest.ai.model.AIModel;

/**
 * Behaviour for coming closer to enemy.
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AISeekBehaviour extends SeekBehaviour implements GameObjectSeenListener {

    public AISeekBehaviour(Agent agent) {
        super(agent, null);
    }

    public void handleGameObjectSeenEvent(GameObjectSeenEvent event) {
        if (event.getGameObjectSeen() instanceof Agent) {
            Agent targetAgent = (Agent) event.getGameObjectSeen();
            if (agent.isSameTeam(targetAgent)) {
                return;
            }
            setTarget(targetAgent);
            enabled = true;
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        //FIXME: needed interaction with walls.
        Vector3f vel = calculateNewVelocity().mult(tpf);
        ((AIModel) agent.getModel()).setWalkDirection(vel);
        rotateAgent(tpf);
    }
}
