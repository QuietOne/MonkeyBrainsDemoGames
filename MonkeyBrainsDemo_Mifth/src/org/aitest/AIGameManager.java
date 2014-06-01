/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aitest;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.aitest.character.AICharacterControl;
import org.aitest.character.AIMainCharacterController;
import org.aitest.gui.AIGuiManager;
import org.aitest.physics.AIStaticObjectControl;
import org.aitest.physics.AIStaticObjectType;

/**
 *
 * @author mifthbeat
 */
public class AIGameManager extends AbstractAppState {

    private DesktopAssetManager dasm;
    private Application app;
    private Node root;
    private Node sceneNode = new Node("Scene");
    private long lastFrame;
    private double lastTimeFromPrevious;
    private float currentTpf;
    private boolean update, gameDebug = false;

    public AIGameManager(DesktopAssetManager dsm, Application app) {
        this.dasm = dsm;
        this.app = app;

        lastFrame = System.nanoTime();
        lastTimeFromPrevious = 0.0;
        currentTpf = 0f;

        root = (Node) app.getViewPort().getScenes().get(0);
        root.attachChild(sceneNode);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

    }

    public void loadScene() {

        Node sceneBase = (Node) dasm.loadModel("Models/Demo_01/Scene_01/scene_01.blend");

        for (Spatial sp : sceneBase.getChildren()) {

            if (sp.getName().indexOf("characterMan") == 0) {
                // enemy
                Node enemyNode = (Node) dasm.loadModel("Models/Demo_01/characters/character_01/character_01.j3o");
                enemyNode.setLocalTransform(sp.getLocalTransform());
                AICharacterControl enemyChar = new AICharacterControl(app, enemyNode, true);
                enemyChar.setViewDirection(enemyNode.getLocalRotation().mult(Vector3f.UNIT_Z).normalizeLocal());
                sceneNode.attachChild(enemyChar.getCharNode());
            } else {
                CollisionShape cShape = CollisionShapeFactory.createMeshShape(sp);
                AIStaticObjectType objType = AIStaticObjectType.Obstacle;
                if (sp.getName().equals("floor")) {
                    objType = AIStaticObjectType.Floor;
                }

                AIStaticObjectControl rg = new AIStaticObjectControl(objType, cShape, 0f);
                sp.addControl(rg);
                app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(rg);
                sceneNode.attachChild(sp);
            }
        }

        // removeloaded model
        sceneBase.removeFromParent();
        sceneBase = null;

        // mainCharacter
        AICharacterControl characterr = new AICharacterControl(app, (Node) dasm.loadModel("Models/Demo_01/characters/character_01/character_01.j3o"), true);
        AIMainCharacterController mainCharState = new AIMainCharacterController(characterr);
        app.getStateManager().attach(mainCharState);
        sceneNode.attachChild(characterr.getCharNode());

    }
    
    public void reloadScene() {
        app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().removeAll(root);
        sceneNode.detachAllChildren();
        root.detachAllChildren();
        root.attachChild(sceneNode); // attach it again
        app.getStateManager().detach(app.getStateManager().getState(AIMainCharacterController.class));
//        app.getStateManager().detach(app.getStateManager().getState(AIGuiManager.class));
        
        loadScene();
    }

    public DesktopAssetManager getDEsktopAssetManager() {
        return dasm;
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

    @Override
    public void update(float tpf) {

        // Use our own tpf calculation in case frame rate is
        // running away making this tpf unstable
        long time = System.nanoTime();

        long delta = time - lastFrame;

        double seconds = delta / 1000000000.0;

        // Clamp frame time to no bigger than a certain amount 60fps
        if (seconds + lastTimeFromPrevious >= 1.0 / 60.0) {
            lastFrame = time;
//            System.out.println(seconds);
            update = true;

            lastTimeFromPrevious = (seconds + lastTimeFromPrevious) - (1.0 / 60.0);
            currentTpf = (float) seconds;

        } else {
            update = false;
        }

    }

    @Override
    public void cleanup() {
        super.cleanup();

    }

    public boolean IsUpdate() {
        return update;
    }

    public boolean isGameDebug() {
        return gameDebug;
    }

    public float getCurrentTpf() {
        return currentTpf;
    }

    public void setCurrentTpf(float currentTpf) {
        this.currentTpf = currentTpf;
    }
}
