package org.aitest.ai.behaviours.player;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.input.controls.AnalogListener;
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
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("gunFired")) {
           // model.getGun().attack(Vector3f.ZERO, tpf);
            //get animation for fired gun
            for (AnimControl animation : animationList) {
                if (!animation.getChannel(0).getAnimationName().equals("shoot")) {
                    animation.getChannel(0).setAnim("shoot", 0.1f);
                    animation.getChannel(0).setSpeed(1.5f);
                    animation.getChannel(0).setLoopMode(LoopMode.DontLoop);
                }
            }
        } else {
            if (name.equals("swordStrike")) {
               // model.getSword().attack(Vector3f.ZERO, tpf);
                //get animation for sword strike
                for (AnimControl animation : animationList) {
                    if (!animation.getChannel(0).getAnimationName().equals("strike_sword")) {
                        animation.getChannel(0).setAnim("strike_sword", 0.2f);
                        animation.getChannel(0).setSpeed(1.0f);
                        animation.getChannel(0).setLoopMode(LoopMode.DontLoop);
                    }
                }
            }
        }
    }
}
