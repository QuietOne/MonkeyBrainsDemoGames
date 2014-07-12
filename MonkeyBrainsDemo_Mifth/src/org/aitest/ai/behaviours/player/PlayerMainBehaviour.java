package org.aitest.ai.behaviours.player;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import org.aitest.ai.character.AIModel;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class PlayerMainBehaviour extends Behaviour {

    PlayerAttackBehaviour attackBehaviour;
    PlayerMoveBehaviour moveBehaviour;
    AIModel model;
    
    public PlayerMainBehaviour(Agent agent) {
        super(agent);
        attackBehaviour = new PlayerAttackBehaviour(agent);
        moveBehaviour = new PlayerMoveBehaviour(agent);
        model = (AIModel) agent.getModel();
    }

    @Override
    protected void controlUpdate(float tpf) {
        model.update(tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
