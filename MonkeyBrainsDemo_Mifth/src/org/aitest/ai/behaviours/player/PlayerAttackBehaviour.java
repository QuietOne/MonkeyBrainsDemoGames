package org.aitest.ai.behaviours.player;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import java.util.List;
import org.aitest.ai.model.AIModel;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class PlayerAttackBehaviour extends Behaviour implements AnalogListener {

    AIModel model;
    List<AnimControl> animationList;

    public PlayerAttackBehaviour(Agent agent) {
        super(agent);
        model = (AIModel) agent.getModel();
        animationList = model.getAnimationList();
    }

    @Override
    protected void controlUpdate(float tpf) {
        //Framework updates weapon that is attached directly to agent
        //rest of the weapons needs to be manualy updated
        model.getSword().update(tpf);
        model.getGun().update(tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("gunFired")) {
            model.getGun().attack(Vector3f.ZERO, tpf);
        } else {
            //only listeners for this behaviour are gunFired and swordStrike
            model.getSword().attack(Vector3f.ZERO, tpf);
        }
    }
}
