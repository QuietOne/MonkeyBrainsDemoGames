package org.aitest.ai.behaviours.player;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.ai.agents.util.control.Game;
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
        ((AIGameControl) Game.getInstance().getGameControl()).addGunAttackListener(attackBehaviour);
        ((AIGameControl) Game.getInstance().getGameControl()).addSwordAttackListener(attackBehaviour);
        ((AIGameControl) Game.getInstance().getGameControl()).addMoveListener(moveBehaviour);
        //this has been added, so the agent will stop moving if he is attacking
        ((AIGameControl) Game.getInstance().getGameControl()).addGunAttackListener(moveBehaviour);
        ((AIGameControl) Game.getInstance().getGameControl()).addSwordAttackListener(moveBehaviour);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //decreasing timer for moving
        moveBehaviour.update(tpf);
        attackBehaviour.update(tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
