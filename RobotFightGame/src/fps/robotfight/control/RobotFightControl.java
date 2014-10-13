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
package fps.robotfight.control;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.Behavior;
import com.jme3.ai.agents.behaviors.npc.SimpleAttackBehavior;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.ai.agents.util.control.GameControl;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Tihomir Radosavljević
 * @version 1.0.2
 */
public class RobotFightControl implements GameControl {

    MonkeyBrainsAppState brainsAppState;
    InputManager inputManager;

    public RobotFightControl() {
        brainsAppState = MonkeyBrainsAppState.getInstance();
        inputManager = brainsAppState.getApp().getInputManager();
    }

    public void setInputManagerMapping() {
        inputManager.addMapping("moveForward", new KeyTrigger(KeyInput.KEY_UP), new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("moveBackward", new KeyTrigger(KeyInput.KEY_DOWN), new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("moveRight", new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("moveLeft", new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Switch", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
    }

    public void addMoveListener(Agent agent, AnalogListener behaviour) {
        inputManager.addListener(behaviour, "moveForward", "moveBackward", "moveRight", "moveLeft");
    }

    public void addAttackListener(Agent agent, ActionListener behaviour) {
        inputManager.addListener(behaviour, "Shoot");
    }

    public void addSwitchListener(Agent agent, ActionListener behaviour) {
        inputManager.addListener(behaviour, "Switch");
    }

    public HashMap<String, Behavior> getPlayerMoveSupportedOperations(Agent agent) {
        HashMap<String, Behavior> supportedOperations = new HashMap<String, Behavior>();
        supportedOperations.put("moveForward", new Behavior(agent) {
            @Override
            protected void controlUpdate(float tpf) {
                Vector3f oldPos = agent.getLocalTranslation().clone();
                agent.getSpatial().move(agent.getLocalRotation().mult(new Vector3f(0, 0, agent.getMoveSpeed() * tpf)));
                if (agent.getLocalTranslation().x > 40 * 2 || agent.getLocalTranslation().z > 40 * 2
                        || agent.getLocalTranslation().x < -40 * 2 || agent.getLocalTranslation().z < -40 * 2) {
                    agent.setLocalTranslation(oldPos);
                }
                enabled = false;
            }
        });
        supportedOperations.put("moveBackward", new Behavior(agent) {
            @Override
            protected void controlUpdate(float tpf) {
                Vector3f oldPos = agent.getLocalTranslation().clone();
                agent.getSpatial().move(agent.getLocalRotation().mult(new Vector3f(0, 0, -agent.getMoveSpeed() * tpf)));
                if (agent.getLocalTranslation().x > 40 * 2 || agent.getLocalTranslation().z > 40 * 2
                        || agent.getLocalTranslation().x < -40 * 2 || agent.getLocalTranslation().z < -40 * 2) {
                    agent.setLocalTranslation(oldPos);
                }
                enabled = false;
            }
        });
        supportedOperations.put("moveRight", new Behavior(agent) {
            @Override
            protected void controlUpdate(float tpf) {
                agent.getSpatial().rotate(0, -(FastMath.DEG_TO_RAD * tpf) * agent.getRotationSpeed(), 0);
                enabled = false;
            }
        });
        supportedOperations.put("moveLeft", new Behavior(agent) {
            @Override
            protected void controlUpdate(float tpf) {
                agent.getSpatial().rotate(0, (FastMath.DEG_TO_RAD * tpf) * agent.getRotationSpeed(), 0);
                enabled = false;
            }
        });
        return supportedOperations;
    }

    public HashMap<String, Behavior> getPlayerAttackSupportedOperations(Agent agent) {
        HashMap<String, Behavior> supportedOperations = new HashMap<String, Behavior>();
        supportedOperations.put("Shoot", new SimpleAttackBehavior(agent));
        return supportedOperations;
    }

    public boolean finish() {
        //checking if one team left if it has, then brainsAppState over
        int i = 0;
        List<Agent> agents = brainsAppState.getAgents();
        //find first alive agent in list
        while (i < agents.size() && !agents.get(i).isEnabled()) {
            i++;
        }
        int j = i + 1;
        while (j < agents.size()) {
            //finding next agent that is alive
            if (!agents.get(j).isEnabled()) {
                j++;
                continue;
            }
            //if agent is not in the same team, brainsAppState is certainly not over
            if (!agents.get(i).isSameTeam(agents.get(j))) {
                break;
            }
            j++;
        }
        if (j >= agents.size()) {
            brainsAppState.stop();
        }
        return !brainsAppState.isInProgress();
    }

    public boolean win(Agent agent) {
        if (!brainsAppState.isInProgress()) {
            if (agent.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    public void restart() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void spawn(GameEntity gameObject, Vector3f... area) {
        Random random = new Random();
        float x, z;
        int distance = (int) FastMath.abs(area[1].x - area[0].x);
        x = random.nextInt(distance / 2);
        if (random.nextBoolean()) {
            x *= -1;
        }
        distance = (int) FastMath.abs(area[1].z - area[0].z);
        z = random.nextInt(distance / 2);
        if (random.nextBoolean()) {
            z *= -1;
        }
        gameObject.setLocalTranslation(x, 0, z);
        if (gameObject instanceof Agent) {
            brainsAppState.addAgent((Agent) gameObject);
        } else {
            brainsAppState.addGameEntity(gameObject);
        }
    }

    public void setCameraSettings(Camera camera) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setFlyCameraSettings(FlyByCamera flyCam) {
        flyCam.setMoveSpeed(20);
        //disable the default flyby cam
        flyCam.setEnabled(false);
    }
}
