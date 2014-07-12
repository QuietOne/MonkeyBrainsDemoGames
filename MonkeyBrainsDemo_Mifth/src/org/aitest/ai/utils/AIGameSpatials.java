package org.aitest.ai.utils;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
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
    
    private void prepareModel(Agent agent, Node nd) {
        for (Spatial sp : nd.getChildren()) {
            AnimControl aniControl = sp.getControl(AnimControl.class);

            if (aniControl == null && sp instanceof Node) {
                prepareModel((Node) sp);
            } else if (aniControl != null) {
                SkeletonControl skeletonControl = sp.getControl(SkeletonControl.class);
                skeletonControl.setHardwareSkinningPreferred(true); // PERFORMANCE IS MUCH MUCH BETTER WITH HW SKINNING

                if (swordModel == null) {
                    createSword(skeletonControl);
                }

                if (!animLst.contains(aniControl)) {
                    AnimChannel aniChannel = aniControl.createChannel();
                    aniChannel.setAnim("base_stand");

                    animLst.add(aniControl);
                }

            }
        }
    }

    private void createSword(SkeletonControl skeletonControl) {
        swordModel = new Node("sword");

        GhostControl gh = new GhostControl(new BoxCollisionShape(new Vector3f(0.3f, 1f, 0.3f)));
        swordModel.addControl(gh);
        physics.add(gh);

        Node n = skeletonControl.getAttachmentsNode("sword");
        n.attachChild(swordModel);
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
