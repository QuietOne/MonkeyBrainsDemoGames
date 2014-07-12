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
public class PlayerMoveBehaviour extends Behaviour implements AnalogListener{

    AIModel model;
    
    public PlayerMoveBehaviour(Agent agent) {
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
        if (name.equals("AKeyChar")
                && !model.isDoShoot() && !model.isDoStrike()) {
            model.setDoRotate(true);
            model.setRotateLeft(true);
        } else if (name.equals("DKeyChar")
                && !model.isDoShoot() && !model.isDoStrike()) {
            model.setDoRotate(true);
            model.setRotateLeft(false);
        } else if (name.equals("WKeyChar")
                && !model.isDoShoot() && !model.isDoStrike()) {
            model.setDoMove(true);
            model.setMoveForward(true);
        } else if (name.equals("SKeyChar")
                && !model.isDoShoot() && !model.isDoStrike()) {
            model.setDoMove(true);
            model.setMoveForward(false);
        } 
    }
    
}
