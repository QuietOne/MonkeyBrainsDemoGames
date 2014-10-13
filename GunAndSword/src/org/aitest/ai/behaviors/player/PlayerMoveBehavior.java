/**
 * Copyright (c) 2014, jMonkeyEngine All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.aitest.ai.behaviors.player;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.Behavior;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.List;
import org.aitest.ai.model.AIModel;

/**
 *
 * @author Tihomir Radosavljević
 * @version 1.0.0
 */
public class PlayerMoveBehavior extends Behavior implements ActionListener, AnalogListener {

    private AIModel model;
    private List<AnimControl> animationList;
    /**
     * This is used for calculating new moving vector when agent is moving and
     * rotating. When calculating new vector, this is the information that
     * enables us to know if agent is moving forward or backward, before
     * rotation was applied.
     */
    private boolean forward;
    private boolean moving;

    public PlayerMoveBehavior(Agent agent) {
        super(agent);
        model = (AIModel) agent.getModel();
        animationList = model.getAnimationList();
        forward = true;
        moving = false;
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("left")) {
            //rotation to the left
            Quaternion rotQua = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * agent.getRotationSpeed(), Vector3f.UNIT_Y);
            rotQua = spatial.getLocalRotation().mult(rotQua);
            model.setViewDirection(rotQua.mult(Vector3f.UNIT_Z).normalizeLocal());
        } else {
            if (name.equals("right")) {
                //rotation to the right
                Quaternion rotQua = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * agent.getRotationSpeed(), Vector3f.UNIT_Y).inverse();
                rotQua = spatial.getLocalRotation().mult(rotQua);
                model.setViewDirection(rotQua.mult(Vector3f.UNIT_Z).normalizeLocal());
            }
        }
        if (moving) {
            //if agent is already moving, calculate his new moving direction
            Vector3f walkDir = model.getViewDirection().mult(agent.getMoveSpeed());
            //if moving backward
            if (!forward) {
                walkDir.negateLocal();
            }
            model.setWalkDirection(walkDir);
        }
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        //forward button
        if (name.equals("forward") && isPressed) {
            Vector3f walkDir = model.getViewDirection().mult(agent.getMoveSpeed());
            model.setWalkDirection(walkDir);
            moving = true;
            forward = true;
        } else {
            if (name.equals("backward") && isPressed) {
                Vector3f walkDir = model.getViewDirection().mult(agent.getMoveSpeed()).negate();
                model.setWalkDirection(walkDir);
                moving = true;
                forward = false;
            } else {
                if (!name.equals("left") && !name.equals("right")) {
                    moving = false;
                }
            }

        }
        //add animation to it
        if (moving) {
            //animation for moving
            for (AnimControl animation : animationList) {
                if (!animation.getChannel(0).getAnimationName().equals("run_01")) {
                    animation.getChannel(0).setAnim("run_01", 0.3f);
                    animation.getChannel(0).setSpeed(1f);
                    animation.getChannel(0).setLoopMode(LoopMode.Loop);
                }
            }
        } else {
            //agent will stop moving if it attacks
            model.setWalkDirection(Vector3f.ZERO);
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
