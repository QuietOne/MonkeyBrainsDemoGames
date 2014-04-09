/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.WanderBehaviour;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.Random;

/**
 *
 * @author Tihomir Radosavljević
 */
public class WanderInsideTerrain extends WanderBehaviour{

    public WanderInsideTerrain(Agent agent, float terrainSize) {
        super(agent);
        setArea(new Vector3f(terrainSize * 2- 5, 0, terrainSize * 2 - 5),
                    new Vector3f(-terrainSize * 2 + 5, 0, -terrainSize * 2 + 5));
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
