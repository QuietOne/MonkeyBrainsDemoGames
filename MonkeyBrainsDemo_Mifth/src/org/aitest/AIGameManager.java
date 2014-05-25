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
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.aitest.character.AICharacter;

/**
 *
 * @author mifthbeat
 */
public class AIGameManager extends AbstractAppState {

    private DesktopAssetManager dasm;
    private Application app;
    private Node root;
    private Node sceneNode = new Node("Scene");

    public AIGameManager(DesktopAssetManager dsm, Application app) {
        this.dasm = dsm;
        this.app = app;

        root = (Node) app.getViewPort().getScenes().get(0);
        root.attachChild(sceneNode);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

    }

    public void loadScene() {

        Node sceneBase = (Node) dasm.loadModel("Models/Demo_01/Scene_01/scene_01.j3o");

        for (Spatial sp : sceneBase.getChildren()) {

            if (sp.getName().indexOf("characterMan") == 0) {
                // enemy
                Node enemyNode = (Node) dasm.loadModel("Models/Demo_01/characters/character_01/character_01.j3o");
                enemyNode.setLocalTransform(sp.getLocalTransform());
                AICharacter enemyChar = new AICharacter(app, enemyNode);
                sceneNode.attachChild(enemyChar.getCharNode());
            } else {
                CollisionShape cShape = CollisionShapeFactory.createMeshShape(sp);
                RigidBodyControl rg = new RigidBodyControl(cShape, 0f);
                sp.addControl(rg);
                app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(rg);
                sceneNode.attachChild(sp);
            }
            

        }

        // removeloaded model
        sceneBase.removeFromParent();
        sceneBase = null;

        // mainCharacter
        AICharacter characterr = new AICharacter(app, (Node) dasm.loadModel("Models/Demo_01/characters/character_01/character_01.j3o"));
        sceneNode.attachChild(characterr.getCharNode());

    }

    public DesktopAssetManager getDEsktopAssetManager() {
        return dasm;
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void cleanup() {
        super.cleanup();

    }
}
