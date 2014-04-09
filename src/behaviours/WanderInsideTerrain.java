/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behaviours;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.WanderBehaviour;
import com.jme3.math.Vector3f;

/**
 *
 * @author Tihomir Radosavljević
 */
public class WanderInsideTerrain extends WanderBehaviour {

    private float terrainSize;

    public WanderInsideTerrain(Agent agent, float terrainSize) {
        super(agent);
        setArea(new Vector3f(terrainSize * 2 - 5, 0, terrainSize * 2 - 5),
                new Vector3f(-terrainSize * 2 + 5, 0, -terrainSize * 2 + 5));
        this.terrainSize = terrainSize;
    }

    @Override
    protected void controlUpdate(float tpf) {
        Vector3f oldPos = agent.getLocalTranslation().clone();
        super.controlUpdate(tpf);
        if (agent.getLocalTranslation().x > terrainSize * 2 || agent.getLocalTranslation().z > terrainSize * 2
                || agent.getLocalTranslation().x < -terrainSize * 2 || agent.getLocalTranslation().z < -terrainSize * 2) {
            agent.setLocalTranslation(oldPos);
        }
    }
}
