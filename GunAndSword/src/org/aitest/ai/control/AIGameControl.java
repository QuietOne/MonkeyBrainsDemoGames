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
package org.aitest.ai.control;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.Team;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.ai.agents.util.control.GameControl;
import com.jme3.app.Application;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.aitest.ai.behaviors.npc.AIMainBehavior;
import org.aitest.ai.behaviors.player.PlayerMainBehavior;
import org.aitest.ai.model.AIModel;
import org.aitest.ai.utils.AIGameSpatials;
import org.aitest.physics.AIStaticObjectControl;
import org.aitest.physics.AIStaticObjectType;

/**
 *
 * @author Tihomir Radosavljević
 * @version 1.0.0
 */
public class AIGameControl implements GameControl {

    private InputManager inputManager;
    private DesktopAssetManager dasm;
    private Application app;
    private Node rootNode;
    private Node sceneNode;
    private boolean gameDebug;

    public AIGameControl() {
        inputManager = MonkeyBrainsAppState.getInstance().getApp().getInputManager();
        app = MonkeyBrainsAppState.getInstance().getApp();
        dasm = (DesktopAssetManager) app.getAssetManager();
        rootNode = MonkeyBrainsAppState.getInstance().getRootNode();
        sceneNode = new Node("Scene");
        gameDebug = false;
        rootNode.attachChild(sceneNode);
    }

    public void setInputManagerMapping() {
        inputManager.addMapping("gunFired", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("swordStrike", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_D));
    }

    public void addMoveListener(InputListener behaviour) {
        inputManager.addListener(behaviour, "forward", "left", "backward", "right");
    }

    public void addGunAttackListener(InputListener behaviour) {
        inputManager.addListener(behaviour, "gunFired");
    }

    public void addSwordAttackListener(InputListener behaviour) {
        inputManager.addListener(behaviour, "swordStrike");
    }

    public void setCameraSettings(Camera cam) {
        cam.setLocation(new Vector3f(6.7807055f, 22.863451f, 19.72432f));
        cam.setRotation(new Quaternion(-0.08801406f, 0.9110843f, -0.26273727f, -0.30520147f));
    }

    public void setFlyCameraSettings(FlyByCamera flyCam) {
        flyCam.setZoomSpeed(20f);
        flyCam.setMoveSpeed(50f);
        flyCam.setDragToRotate(true);
    }

    public boolean finish() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean win(Agent agent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void restart() {
        app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().removeAll(rootNode);
        sceneNode.detachAllChildren();
        rootNode.detachAllChildren();
        // attach it again
        rootNode.attachChild(sceneNode);
        MonkeyBrainsAppState.getInstance().getAgents().clear();
        MonkeyBrainsAppState.getInstance().getGameEntities().clear();
        loadScene();
        MonkeyBrainsAppState.getInstance().start();
    }

    public void spawn(GameEntity gameObject, Vector3f... area) {
        //for this game it is implemented in Blender
    }

    public void loadScene() {
        //loading scene
        Node sceneBase = (Node) dasm.loadModel("Models/Demo_01/Scene_01/scene_01.blend");
        //adding player
        Node playerNode = (Node) dasm.loadModel("Models/Demo_01/characters/character_01/character_01.j3o");
        Agent<AIModel> player = new Agent<AIModel>("Player", playerNode);
        AIModel model = new AIModel(player);
        player.setModel(model);
        model.setGraphicModel();
        player.setMainBehaviour(new PlayerMainBehavior(player));

        AIGameSpatials.getInstance().attachCameraTo(player, MonkeyBrainsAppState.getInstance().getApp().getCamera());
        MonkeyBrainsAppState.getInstance().addAgent(player);
        Team team = new Team("Enemy");
        int i = 1;
        for (Spatial sp : sceneBase.getChildren()) {
            if (sp.getName().indexOf("characterMan") == 0) {
                //adding enemies to the game
                //loading spatials for agent
                Node enemyNode = (Node) dasm.loadModel("Models/Demo_01/characters/character_01/character_01.j3o");
                enemyNode.setLocalTransform(sp.getLocalTransform());
                //creating agent
                Agent<AIModel> enemyAgent = new Agent<AIModel>("Enemy" + i, enemyNode);
                i++;
                //setting model
                model = new AIModel(enemyAgent);
                model.setViewDirection(enemyNode.getLocalRotation().mult(Vector3f.UNIT_Z).normalizeLocal());
                enemyAgent.setModel(model);
                model.setGraphicModel();
                enemyAgent.setMainBehaviour(new AIMainBehavior(enemyAgent));
                enemyAgent.setTeam(team);
                //adding it to game
                MonkeyBrainsAppState.getInstance().addAgent(enemyAgent);

            } else {
                //adding static objects
                CollisionShape cShape = CollisionShapeFactory.createMeshShape(sp);
                AIStaticObjectType objType;
                if (sp.getName().equals("floor")) {
                    objType = AIStaticObjectType.Floor;
                } else {
                    objType = AIStaticObjectType.Obstacle;
                }
                AIStaticObjectControl rg = new AIStaticObjectControl(objType, cShape, 0f);
                sp.addControl(rg);
                app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(rg);
                sceneNode.attachChild(sp);
            }
        }
        sceneBase.removeFromParent();
    }

    public void setGameDebug(boolean setDebug) {
        BulletAppState bullet = app.getStateManager().getState(BulletAppState.class);
        if (setDebug) {
            bullet.setDebugEnabled(true);
            gameDebug = true;
        } else {
            bullet.setDebugEnabled(false);
            gameDebug = false;
        }
    }

    public boolean isGameDebug() {
        return gameDebug;
    }
}
