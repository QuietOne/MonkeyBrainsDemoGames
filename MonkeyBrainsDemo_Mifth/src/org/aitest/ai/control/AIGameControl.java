package org.aitest.ai.control;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.GameObject;
import com.jme3.ai.agents.util.control.Game;
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
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.aitest.ai.behaviours.npc.AIMainBehaviour;
import org.aitest.ai.behaviours.player.PlayerMainBehaviour;
import org.aitest.ai.character.AIModel;
import org.aitest.ai.utils.AIGameSpatials;
import org.aitest.physics.AIStaticObjectControl;
import org.aitest.physics.AIStaticObjectType;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AIGameControl implements GameControl {

    private InputManager inputManager;
    private DesktopAssetManager dasm;
    private Application app;
    private Node rootNode;
    private Node sceneNode;
    private boolean gameDebug;

    public AIGameControl() {
        inputManager = Game.getInstance().getApp().getInputManager();
        app = Game.getInstance().getApp();
        dasm = (DesktopAssetManager) app.getAssetManager();
        rootNode = Game.getInstance().getRootNode();
        sceneNode = new Node("Scene");
        gameDebug = false;
        rootNode.attachChild(sceneNode);
    }

    public void setInputManagerMapping() {
        inputManager.addMapping("mouseLeftClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("mouseRightClick", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("WKeyChar", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("AKeyChar", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("SKeyChar", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("DKeyChar", new KeyTrigger(KeyInput.KEY_D));
    }

    public void addMoveListener(Agent agent, AnalogListener behaviour) {
        inputManager.addListener(behaviour, "WKeyChar", "AKeyChar", "SKeyChar", "DKeyChar");
    }

    public void addGunAttackListener(Agent agent, ActionListener behaviour) {
        inputManager.addListener(behaviour, "mouseLeftClick");
    }

    public void addSwordAttackListener(Agent agent, ActionListener behaviour) {
        inputManager.addListener(behaviour, "mouseRightClick");
    }

    public void setCameraSettings(Camera cam) {
        cam.setLocation(new Vector3f(6.7807055f, 22.863451f, 19.72432f));
        cam.setRotation(new Quaternion(-0.08801406f, 0.9110843f, -0.26273727f, -0.30520147f));
    }

    public void setFlyCameraSettings(FlyByCamera flyCam) {
        flyCam.setZoomSpeed(0f);
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
//        app.getStateManager().detach(app.getStateManager().getState(AIMainCharacterController.class));
        app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().removeAll(rootNode);
        sceneNode.detachAllChildren();
        rootNode.detachAllChildren();
        rootNode.attachChild(sceneNode); // attach it again
        loadScene();
    }

    public void spawn(GameObject gameObject, Vector3f... area) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void loadScene() {
        //adding player
        Agent<AIModel> player = new Agent<AIModel>("player");
        player.setModel(new AIModel(player));
        player.setMainBehaviour(new PlayerMainBehaviour(player));
        AIGameSpatials.getInstance().attachCameraTo(player, Game.getInstance().getApp().getCamera());
        Game.getInstance().addAgent(player);
        
        Node sceneBase = (Node) dasm.loadModel("Models/Demo_01/Scene_01/scene_01.blend");
        for (Spatial sp : sceneBase.getChildren()) {
            if (sp.getName().indexOf("characterMan") == 0) {
                //adding enemies
                Node enemyNode = (Node) dasm.loadModel("Models/Demo_01/characters/character_01/character_01.j3o");
                enemyNode.setLocalTransform(sp.getLocalTransform());
//                AICharacterControl enemyChar = new AICharacterControl(app, enemyNode, true);
//                enemyChar.setViewDirection(enemyNode.getLocalRotation().mult(Vector3f.UNIT_Z).normalizeLocal());
//                sceneNode.attachChild(enemyChar.getCharNode());
                Agent<AIModel> enemyAgent = new Agent<AIModel>("Enemy", enemyNode);
                enemyAgent.setModel(new AIModel(enemyAgent));
                enemyAgent.setMainBehaviour(new AIMainBehaviour(enemyAgent));
                Game.getInstance().addAgent(enemyAgent);
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
        // remove loaded model
        sceneBase.removeFromParent();
        sceneBase = null;
        // mainCharacter
//        AICharacterControl characterr = new AICharacterControl(app, (Node) dasm.loadModel("Models/Demo_01/characters/character_01/character_01.j3o"), true);
//        AIMainCharacterController mainCharState = new AIMainCharacterController(characterr);
//        app.getStateManager().attach(mainCharState);
//        sceneNode.attachChild(characterr.getCharNode());
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
