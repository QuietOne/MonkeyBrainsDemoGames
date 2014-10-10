package behaviours;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.steering.FleeBehavior;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Tihomir Radosavljević
 */
public class FleeInsideTerrain extends FleeBehavior{

    private float terrainSize;

    public FleeInsideTerrain(float terrainSize, Agent agent, Agent target) {
        super(agent, target);
        this.terrainSize = terrainSize;
    }

    public FleeInsideTerrain(float terrainSize, Agent agent, Agent target, Spatial spatial) {
        super(agent, target, spatial);
        this.terrainSize = terrainSize;
    }
    
    public FleeInsideTerrain(Agent agent, Agent target) {
        super(agent, target);
    }

    @Override
    protected void controlUpdate(float tpf) {
        Vector3f oldPos = agent.getLocalTranslation().clone();
        super.controlUpdate(tpf);
        if (agent.getLocalTranslation().x > terrainSize*2 || agent.getLocalTranslation().z > terrainSize*2
                || agent.getLocalTranslation().x < -terrainSize*2 || agent.getLocalTranslation().z < -terrainSize*2) {
            agent.setLocalTranslation(oldPos);
        }
    }

    
}
