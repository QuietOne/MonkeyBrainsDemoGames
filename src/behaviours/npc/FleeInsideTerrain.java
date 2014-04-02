package behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.FleeBehaviour;
import com.jme3.math.Vector3f;

/**
 *
 * @author Tihomir Radosavljević
 */
public class FleeInsideTerrain extends FleeBehaviour{

    public FleeInsideTerrain(Agent agent, Agent target) {
        super(agent, target);
    }

    @Override
    protected void controlUpdate(float tpf) {
        Vector3f oldPos = agent.getLocalTranslation().clone();
        super.controlUpdate(tpf);
        if (agent.getLocalTranslation().x > 80 || agent.getLocalTranslation().z > 80
                || agent.getLocalTranslation().x < -80 || agent.getLocalTranslation().z < -80) {
            agent.setLocalTranslation(oldPos);
        }
    }

    
}
