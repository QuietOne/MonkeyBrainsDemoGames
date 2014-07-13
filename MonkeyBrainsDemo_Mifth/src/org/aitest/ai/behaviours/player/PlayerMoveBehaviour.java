package org.aitest.ai.behaviours.player;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
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
public class PlayerMoveBehaviour extends Behaviour implements AnalogListener {

    AIModel model;
    List<AnimControl> animationList;

    public PlayerMoveBehaviour(Agent agent) {
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
        boolean moving = true;
        if (name.equals("forward")) {
            Vector3f walkDir = model.getViewDirection().mult(agent.getMoveSpeed());
            model.setWalkDirection(walkDir);
        } else if (name.equals("backward")) {
            Vector3f walkDir = model.getViewDirection().mult(agent.getMoveSpeed()).negate();
            model.setWalkDirection(walkDir);
        } else if (name.equals("left")) {
            //rotation to the left
            Quaternion rotQua = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * agent.getRotationSpeed(), Vector3f.UNIT_Y);
            rotQua = spatial.getLocalRotation().mult(rotQua);
            model.setViewDirection(rotQua.mult(Vector3f.UNIT_Z).normalizeLocal());
        } else if (name.equals("right")) {
            //rotation to the right
            Quaternion rotQua = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * agent.getRotationSpeed(), Vector3f.UNIT_Y).inverse();
            rotQua = spatial.getLocalRotation().mult(rotQua);
            model.setViewDirection(rotQua.mult(Vector3f.UNIT_Z).normalizeLocal());
        } else {
            moving = false;
        }
        //add animation to it
        if (moving && !name.equals("gunFired") && !name.equals("swordStrike")) {
            //animation for moving
            for (AnimControl animation : animationList) {
                if (!animation.getChannel(0).getAnimationName().equals("run_01")) {
                    animation.getChannel(0).setAnim("run_01", 0.3f);
                    animation.getChannel(0).setSpeed(1f);
                    animation.getChannel(0).setLoopMode(LoopMode.Loop);
                }
            }
        } else {
            //animation for standing
            for (AnimControl animation : animationList) {
                if (!animation.getChannel(0).getAnimationName().equals("base_stand")) {
                    animation.getChannel(0).setAnim("base_stand", 0.3f);
                    animation.getChannel(0).setSpeed(1f);
                    animation.getChannel(0).setLoopMode(LoopMode.Loop);
                }
            }
        }
    }
}
