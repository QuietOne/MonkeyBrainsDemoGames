package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.SeekBehaviour;
import com.jme3.ai.agents.events.GameObjectSeenEvent;
import com.jme3.ai.agents.events.GameObjectSeenListener;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.aitest.ai.model.AIModel;

/**
 * Behaviour for coming closer to enemy.
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AISeekBehaviour extends SeekBehaviour implements GameObjectSeenListener {

    AIModel model;
    
    public AISeekBehaviour(Agent agent) {
        super(agent, null);
        model = (AIModel) agent.getModel();
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
        Vector3f vel = calculateNewVelocity();
        model.setWalkDirection(vel);
        rotateAgent(tpf);
    }

    @Override
    protected void rotateAgent(float tpf) {
        Quaternion q = new Quaternion();
        q.lookAt(velocity, new Vector3f(0, 1, 0));
        agent.getLocalRotation().slerp(q, agent.getRotationSpeed());
        model.setViewDirection(agent.getLocalRotation().mult(Vector3f.UNIT_Z).normalize());
    }
}
