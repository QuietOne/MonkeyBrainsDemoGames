package org.aitest.ai.behaviours.player;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.ai.agents.util.control.AIAppState;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import org.aitest.ai.control.AIGameControl;
import org.aitest.ai.model.AIModel;

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
        //game model that will be updated
        model = (AIModel) agent.getModel();
        //adding listeners to appropriate behaviours
        ((AIGameControl) AIAppState.getInstance().getGameControl()).addGunAttackListener(attackBehaviour);
        ((AIGameControl) AIAppState.getInstance().getGameControl()).addSwordAttackListener(attackBehaviour);
        ((AIGameControl) AIAppState.getInstance().getGameControl()).addMoveListener(moveBehaviour);
    }

    @Override
    protected void controlUpdate(float tpf) {
        attackBehaviour.update(tpf);
        //moving is updated only by inputs
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
