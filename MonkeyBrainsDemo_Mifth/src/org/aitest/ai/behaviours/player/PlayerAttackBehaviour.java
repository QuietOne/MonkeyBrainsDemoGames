package org.aitest.ai.behaviours.player;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.input.controls.AnalogListener;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import org.aitest.ai.character.AIModel;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class PlayerAttackBehaviour extends Behaviour implements AnalogListener{

    AIModel model;
    
    public PlayerAttackBehaviour(Agent agent) {
        super(agent);
        model = (AIModel) agent.getModel();
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("mouseLeftClick")) {
            model.setDoMove(false);
            model.setDoRotate(false);
            model.setDoShoot(true);
            model.setDoStrike(false);
        } else if (name.equals("mouseRightClick")
                && !model.isDoShoot()) {
            model.setDoMove(false);
            model.setDoRotate(false);
            model.setDoStrike(true);
//            charCtrl.setMoveForward(false);
        }
    }
    
}
