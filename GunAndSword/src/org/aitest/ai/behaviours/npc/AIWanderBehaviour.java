package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.WanderBehaviour;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.aitest.ai.model.AIModel;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AIWanderBehaviour extends WanderBehaviour{

    AIModel model;
    
    public AIWanderBehaviour(Agent agent) {
        super(agent);
        model = (AIModel) agent.getModel();
        timeInterval = 1f;
    }

    @Override
    protected void controlUpdate(float tpf) {
        System.out.println("Area: " + area[0] + " "+ area[1]);
        System.out.println("Target" + targetPosition);
        changeTargetPosition(tpf);
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
