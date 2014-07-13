package org.aitest.ai.utils;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import org.aitest.ai.model.AIModel;

/**
 * Spatials needed for game.
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AIGameSpatials {

    private AssetManager assetManager;

    private AIGameSpatials() {
    }

    public static AIGameSpatials getInstance() {
        return AIGameSpatialHolder.INSTANCE;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    private static class AIGameSpatialHolder {

        private static final AIGameSpatials INSTANCE = new AIGameSpatials();
    }

    /**
     * Attaching camera to agent.
     *
     * @param agent
     * @param cam
     */
    public void attachCameraTo(Agent agent, Camera cam) {
        //create the camera Node
        CameraNode camNode = new CameraNode("Camera Node", cam);
        //this mode means that camera copies the movements of the target
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        //attach the camNode to the target
        ((Node) agent.getSpatial()).attachChild(camNode);
        //move camNode, e.g. behind and above the target
        camNode.setLocalTranslation(new Vector3f(0, 6, -18));
        //rotate the camNode to look at the target
        camNode.lookAt(agent.getSpatial().getLocalTranslation(), Vector3f.UNIT_Y);
        camNode.setLocalTranslation(new Vector3f(0, 12, -22));
        agent.setCamera(cam);
    }

    public void setGameLighting() {
        //setting ambiental lighting
        AmbientLight amb = new AmbientLight();
        amb.setColor(new ColorRGBA(0.7f, 0.8f, 1.0f, 1f));
        Game.getInstance().getRootNode().addLight(amb);

        //setting directional lighting
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.5501984f, -0.6679371f, 0.5011405f));
        dl.setColor(new ColorRGBA(1.0f, 1.0f, 0.7f, 1f));
        Game.getInstance().getRootNode().addLight(dl);
    }

    public void prepareModel(Agent agent, Node agentNode) {
        //for each part of agent spatial
        for (Spatial spatialPart : agentNode.getChildren()) {
            //make animation
            AnimControl animation = spatialPart.getControl(AnimControl.class);
            //there is no animation and there are more parts
            if (animation == null && spatialPart instanceof Node) {
                prepareModel(agent, (Node) spatialPart);
            } else {
                //there is some animation
                if (animation != null) {
                    //get skeleton control
                    SkeletonControl skeletonControl = spatialPart.getControl(SkeletonControl.class);
                    // PERFORMANCE IS MUCH MUCH BETTER WITH HW SKINNING
                    skeletonControl.setHardwareSkinningPreferred(true); 

                    //why load it first
                    if (swordModel == null) {
                        createSword(skeletonControl);
                    }
                    
                    // return animation list?
                    if (!((AIModel) agent.getModel()).getAnimationList().contains(animation)) {
                        AnimChannel aniChannel = animation.createChannel();
                        aniChannel.setAnim("base_stand");
                        ((AIModel) agent.getModel()).getAnimationList().add(animation);
                    }
                }
            }
        }
    }

    public Node createSword(SkeletonControl skeletonControl, String name) {
        Node swordModel = new Node(name);

        GhostControl gh = new GhostControl(new BoxCollisionShape(new Vector3f(0.3f, 1f, 0.3f)));
        swordModel.addControl(gh);
        //add sword to physic space
        Game.getInstance().getApp().getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(gh);

        Node n = skeletonControl.getAttachmentsNode(name);
        n.attachChild(swordModel);
        return swordModel;
    }

    public Geometry createBullet(Gun gun) {
        Spatial spatial = gun.getAgent().getSpatial();
        Geometry newBullet = getBulletSpatial();
        newBullet.setLocalRotation(spatial.getLocalRotation().clone());
        newBullet.setLocalTranslation(spatial.getLocalRotation().clone().addLocal(Vector3f.UNIT_Y).addLocal(newBullet.getLocalRotation().mult(Vector3f.UNIT_Z)));
        newBullet.addControl(new Bullet(gun, newBullet.getLocalTranslation(), newBullet, Game.getInstance().getApp(), bulletDestruction));
        return newBullet;
    }

    public Geometry getBulletSpatial() {
        Box b = new Box(Vector3f.ZERO, 1f, 1f, 1f);
        Geometry geometry = new Geometry("Box", b);
        geometry.setLocalScale(0.1f, 0.1f, 0.5f);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.Red);
        geometry.setMaterial(material);
        return geometry;
    }
}
